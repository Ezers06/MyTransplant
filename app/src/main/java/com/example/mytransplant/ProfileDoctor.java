package com.example.mytransplant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ProfileDoctor extends AppCompatActivity {

    private static final String TAG = "ProfileDoctor";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhoneNumber;
    private TextView tvHospitalName;
    private TextView tvHospitalAddress;

    private EditText etNewUsername;
    private EditText etNewEmail;
    private EditText etNewPhoneNumber;

    private Button btnUpdate;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_doctor);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvHospitalAddress = findViewById(R.id.tvHospitalAddress);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPhoneNumber = findViewById(R.id.etNewPhoneNumber);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check the text of the button to determine the action
                if (btnUpdate.getText().toString().equals("Update")) {
                    // Show EditText and btnCancel, hide btnUpdate
                    etNewUsername.setVisibility(View.VISIBLE);
                    etNewEmail.setVisibility(View.VISIBLE);
                    etNewPhoneNumber.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnUpdate.setText("Confirm"); // Change button text to "Confirm"
                } else if (btnUpdate.getText().toString().equals("Confirm")) {
                    confirmUpdate();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide EditText and btnCancel, show btnUpdate
                etNewUsername.setVisibility(View.GONE);
                etNewEmail.setVisibility(View.GONE);
                etNewPhoneNumber.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnUpdate.setText("Update");
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("doctors").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Doctor doctor = document.toObject(Doctor.class);
                                displayUserData(doctor);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }
    }

    private void displayUserData(Doctor doctor) {
        if (doctor != null) {
            tvUsername.setText("Full Name: " + doctor.getFullName());
            tvEmail.setText("Email: " + doctor.getEmail());
            tvPhoneNumber.setText("Phone Number: " + doctor.getPhoneNumber());
            tvHospitalName.setText("Hospital Name: " + doctor.getHospitalName());
            tvHospitalAddress.setText("Hospital Address: " + doctor.getHospitalAddress());

            etNewUsername.setText(doctor.getFullName());
            etNewEmail.setText(doctor.getEmail());
            etNewPhoneNumber.setText(doctor.getPhoneNumber());
        }
    }

    private void confirmUpdate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("doctors").document(userId)
                    .update(
                            "fullName", etNewUsername.getText().toString(),
                            "email", etNewEmail.getText().toString(),
                            "phoneNumber", etNewPhoneNumber.getText().toString()

                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(ProfileDoctor.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            etNewUsername.setVisibility(View.GONE);
                            etNewEmail.setVisibility(View.GONE);
                            etNewPhoneNumber.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                            btnUpdate.setText("Update");
                            btnUpdate.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Update failed
                            Log.e(TAG, "Error updating profile", e);
                            Toast.makeText(ProfileDoctor.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
