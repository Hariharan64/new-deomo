package com.example.qrstaff;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private EditText editTextPhoneNumber;
    private Spinner spinnerCountryCode;
    private Button buttonVerify;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        buttonVerify = findViewById(R.id.buttonVerify);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        // Populate the Spinner with country codes
        String[] countryCodes = {"+1", "+44", "+91", "+61"}; // Add more as needed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);

        buttonVerify.setOnClickListener(v -> {
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String countryCode = spinnerCountryCode.getSelectedItem().toString();

            if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
                editTextPhoneNumber.setError("Please enter a valid phone number");
                editTextPhoneNumber.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            verifyPhoneNumber(countryCode + phoneNumber);
        });
    }

    private void verifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(MainActivity.this, OtpActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("verificationId", verificationId);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();
                        retrieveUserDetails(phoneNumber);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void retrieveUserDetails(String phoneNumber) {
        // Your implementation to retrieve and store user details
    }
}