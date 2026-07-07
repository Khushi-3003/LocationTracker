package com.example.locationtracker;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locationtracker.model.User;
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

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private TextView tvTargetPhone, tvTargetLat, tvTargetLng, tvLastUpdated, tvTargetAddress;
    private ProgressBar pbLoading;
    private ImageButton btnBack;

    private DatabaseReference databaseReference;
    private ValueEventListener trackingListener;
    private String trackPhone;

    private Marker targetMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load osmdroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        trackPhone = getIntent().getStringExtra("track_phone");
        if (trackPhone == null || trackPhone.isEmpty()) {
            Toast.makeText(this, "No phone number to track", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mapView = findViewById(R.id.mapView);
        tvTargetPhone = findViewById(R.id.tvTargetPhone);
        tvTargetLat = findViewById(R.id.tvTargetLat);
        tvTargetLng = findViewById(R.id.tvTargetLng);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        tvTargetAddress = findViewById(R.id.tvTargetAddress);
        pbLoading = findViewById(R.id.pbLoading);
        btnBack = findViewById(R.id.btnBack);

        tvTargetPhone.setText(trackPhone);

        // Configure map
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.5);

        // Initialize Marker
        targetMarker = new Marker(mapView);
        targetMarker.setTitle("Target: " + trackPhone);
        targetMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(targetMarker);

        btnBack.setOnClickListener(v -> finish());

        // Connect to Firebase and track location in real-time
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(trackPhone);
        startTracking();
    }

    private void startTracking() {
        pbLoading.setVisibility(View.VISIBLE);

        trackingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbLoading.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        double lat = user.getLatitude();
                        double lng = user.getLongitude();
                        long timestamp = user.getTimestamp();

                        tvTargetLat.setText(String.format(Locale.getDefault(), "%.6f", lat));
                        tvTargetLng.setText(String.format(Locale.getDefault(), "%.6f", lng));

                        if (timestamp > 0) {
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a, dd MMM yyyy", Locale.getDefault());
                            String dateStr = sdf.format(new Date(timestamp));
                            tvLastUpdated.setText("Last active: " + dateStr);
                        } else {
                            tvLastUpdated.setText("Active: Coordinates saved");
                        }

                        // Update Map
                        GeoPoint targetPoint = new GeoPoint(lat, lng);
                        targetMarker.setPosition(targetPoint);
                        mapView.getController().animateTo(targetPoint);
                        mapView.invalidate(); // Refresh map

                        // Geocode target coordinates to get address name in background
                        Executors.newSingleThreadExecutor().execute(() -> {
                            Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                                if (addresses != null && !addresses.isEmpty()) {
                                    String addressStr = addresses.get(0).getAddressLine(0);
                                    runOnUiThread(() -> {
                                        tvTargetAddress.setText(addressStr);
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        tvTargetAddress.setText("Location address name not found");
                                    });
                                }
                            } catch (IOException e) {
                                runOnUiThread(() -> {
                                    tvTargetAddress.setText("Unable to resolve address name (offline)");
                                });
                            }
                        });
                    }
                } else {
                    tvTargetLat.setText("--");
                    tvTargetLng.setText("--");
                    tvTargetAddress.setText("Location not found");
                    tvLastUpdated.setText("No location shared for this number.");
                    Toast.makeText(MapActivity.this, "This user is not sharing their location", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(MapActivity.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        databaseReference.addValueEventListener(trackingListener);
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
        if (databaseReference != null && trackingListener != null) {
            databaseReference.removeEventListener(trackingListener);
        }
    }
}
