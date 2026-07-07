package com.example.locationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnGetLocation;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etPhone = findViewById(R.id.etPhone);
        btnGetLocation = findViewById(R.id.btnGetLocation);

        // Initialize Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnGetLocation.setOnClickListener(view -> getTargetLocation());
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
}