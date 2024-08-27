package com.example.qrstaff;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpActivity extends AppCompatActivity {

    private EditText editTextOtp;
    private Button buttonVerifyOtp;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private String verificationId;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        editTextOtp = findViewById(R.id.editTextOtp);
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        buttonVerifyOtp.setOnClickListener(v -> {
            String otp = editTextOtp.getText().toString().trim();

            if (TextUtils.isEmpty(otp)) {
                editTextOtp.setError("Please enter OTP");
                editTextOtp.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            verifyOtp(otp);
        });
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
                        retrieveUserDetails(phoneNumber);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveUserDetails(String phoneNumber) {
        databaseReference.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String designation = dataSnapshot.child("designation").getValue(String.class);
                    String employeeCode = dataSnapshot.child("employeeCode").getValue(String.class);

                    // Store user details in SharedPreferences
                    saveUserDetails(name, designation, employeeCode);

                    // Redirect to HomeActivity
                    Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OtpActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails(String name, String designation, String employeeCode) {
        // Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user details
        editor.putString("name", name);
        editor.putString("designation", designation);
        editor.putString("employeeCode", employeeCode);

        // Apply changes
        editor.apply();
    }

}