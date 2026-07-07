package com.example.locationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    private EditText etPhone;
    private Button btnGetLocation;
    private DatabaseReference databaseReference;

    // Self-Location & Map variables
    private MapView mapView;
    private TextView tvMyAddress, tvMyLat, tvMyLng, tvMyStatus;
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
        etPhone = findViewById(R.id.etPhone);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        mapView = findViewById(R.id.mapView);
        tvMyAddress = findViewById(R.id.tvMyAddress);
        tvMyLat = findViewById(R.id.tvMyLat);
        tvMyLng = findViewById(R.id.tvMyLng);
        tvMyStatus = findViewById(R.id.tvMyStatus);

        // Initialize Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Map
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.5);
        myLocationMarker = new Marker(mapView);
        myLocationMarker.setTitle("My Location");
        myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        // Use custom color or icon for my location if desired (defaults to red pin)
        mapView.getOverlays().add(myLocationMarker);

        // Track target search button
        btnGetLocation.setOnClickListener(view -> getTargetLocation());

        // Initialize Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();
        checkPermissionsAndStartLocationUpdates();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateMyLocation(location);
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
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMyLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        tvMyLat.setText(String.format(Locale.getDefault(), "%.6f", lat));
        tvMyLng.setText(String.format(Locale.getDefault(), "%.6f", lng));

        // Update Map Marker
        GeoPoint myPoint = new GeoPoint(lat, lng);
        myLocationMarker.setPosition(myPoint);
        
        if (isFirstLocationUpdate) {
            mapView.getController().animateTo(myPoint);
            isFirstLocationUpdate = false;
        }

        mapView.invalidate();

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
                        tvMyStatus.setText("Tracking active • Updated: " + sdf.format(new Date()));
                    });
                } else {
                    runOnUiThread(() -> tvMyAddress.setText("Address name not found"));
                }
            } catch (IOException e) {
                runOnUiThread(() -> tvMyAddress.setText("Offline or unable to resolve address name"));
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
                Toast.makeText(this, "Permission denied. Cannot track device location.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}