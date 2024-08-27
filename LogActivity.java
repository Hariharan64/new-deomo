package com.example.qrstaff;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LogActivity extends AppCompatActivity {

    private EditText editTextPhoneNumber, editTextOTP;
    private Button buttonSendOTP, buttonVerifyOTP;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private String verificationId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);




    }
}
