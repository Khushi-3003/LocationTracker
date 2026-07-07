package com.example.locationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.locationtracker.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "LocationTrackerPrefs";
    private static final String KEY_MY_PHONE = "my_phone";

    // 1. Registration views
    private LinearLayout layoutRegistration;
    private EditText etRegPhone;
    private Button btnRegister;

    // 2. Map views
    private View layoutMap;
    private MapView mapView;
    private EditText etPhone;
    private Button btnGetLocation;
    private TextView tvMyDeviceTitle, tvMyAddress, tvMyLat, tvMyLng, tvMyStatus;

    // Sharing logic variables
    private String myPhone;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker myLocationMarker;
    private boolean isFirstLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load osmdroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Bind layout views
        layoutRegistration = findViewById(R.id.layoutRegistration);
        etRegPhone = findViewById(R.id.etRegPhone);
        btnRegister = findViewById(R.id.btnRegister);

        layoutMap = findViewById(R.id.layoutMap);
        mapView = findViewById(R.id.mapView);
        etPhone = findViewById(R.id.etPhone);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvMyDeviceTitle = findViewById(R.id.tvMyDeviceTitle);
        tvMyAddress = findViewById(R.id.tvMyAddress);
        tvMyLat = findViewById(R.id.tvMyLat);
        tvMyLng = findViewById(R.id.tvMyLng);
        tvMyStatus = findViewById(R.id.tvMyStatus);

        // Initialize Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Local Map settings
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17.0);

        myLocationMarker = new Marker(mapView);
        myLocationMarker.setTitle("My Location");
        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(myLocationMarker);

        // Bind button actions
        btnRegister.setOnClickListener(v -> registerDevice());
        btnGetLocation.setOnClickListener(view -> getTargetLocation());

        // Initialize Location tracking components
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();

        // Check if device is already registered
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        myPhone = prefs.getString(KEY_MY_PHONE, null);

        if (myPhone == null || myPhone.isEmpty()) {
            // Show registration screen, hide map
            layoutRegistration.setVisibility(View.VISIBLE);
            layoutMap.setVisibility(View.GONE);
        } else {
            // Registration already complete, show map & start sharing
            enterAppMapFlow();
        }
    }

    private void registerDevice() {
        String inputPhone = etRegPhone.getText().toString().trim();
        if (inputPhone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number to register", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save phone number locally
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_MY_PHONE, inputPhone);
        editor.apply();

        myPhone = inputPhone;
        enterAppMapFlow();
    }

    private void enterAppMapFlow() {
        layoutRegistration.setVisibility(View.GONE);
        layoutMap.setVisibility(View.VISIBLE);
        tvMyDeviceTitle.setText("MY DEVICE LOCATION: " + myPhone);
        
        // Start live location updates and Firebase sharing
        checkPermissionsAndStartLocationUpdates();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateMyLocationAndShare(location);
                }
            }
        };
    }

    private void checkPermissionsAndStartLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            tvMyStatus.setText("GPS tracking active • Sharing real-time database");
        } catch (SecurityException e) {
            tvMyStatus.setText("Unable to track GPS (permission error)");
        }
    }

    private void updateMyLocationAndShare(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        long timestamp = System.currentTimeMillis();

        tvMyLat.setText(String.format(Locale.getDefault(), "%.6f", lat));
        tvMyLng.setText(String.format(Locale.getDefault(), "%.6f", lng));

        // Update Map marker
        GeoPoint myPoint = new GeoPoint(lat, lng);
        myLocationMarker.setPosition(myPoint);
        
        if (isFirstLocationUpdate) {
            mapView.getController().animateTo(myPoint);
            isFirstLocationUpdate = false;
        }
        mapView.invalidate();

        // Write my coordinates to Firebase Database under my registered number
        User myUserLocation = new User(myPhone, lat, lng, timestamp);
        databaseReference.child(myPhone).setValue(myUserLocation);

        // Perform reverse geocoding on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String addressStr = addresses.get(0).getAddressLine(0);
                    runOnUiThread(() -> {
                        tvMyAddress.setText(addressStr);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                        tvMyStatus.setText("Sharing live • Location synced: " + sdf.format(new Date()));
                    });
                } else {
                    runOnUiThread(() -> tvMyAddress.setText("Location place name not found"));
                }
            } catch (IOException e) {
                runOnUiThread(() -> tvMyAddress.setText("Unable to resolve location name (offline)"));
            }
        });
    }

    private void getTargetLocation() {
        final String phoneStr = etPhone.getText().toString().trim();
        if (phoneStr.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGetLocation.setEnabled(false);
        databaseReference.child(phoneStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                btnGetLocation.setEnabled(true);
                if (snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "Location Detected!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.putExtra("track_phone", phoneStr);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No location found for this phone number.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnGetLocation.setEnabled(true);
                Toast.makeText(MainActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                tvMyStatus.setText("Access denied • Enable location permissions in settings");
                Toast.makeText(this, "Location permissions are required to share your position.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (layoutMap.getVisibility() == View.VISIBLE) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (layoutMap.getVisibility() == View.VISIBLE) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}