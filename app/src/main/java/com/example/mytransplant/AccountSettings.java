package com.example.mytransplant;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountSettings extends AppCompatActivity {

    private static final String TAG = "AccountSettings";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhoneNumber;

    private EditText etNewUsername;
    private EditText etNewEmail;
    private EditText etNewPhoneNumber;

    private Button btnUpdate;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPhoneNumber = findViewById(R.id.etNewPhoneNumber);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEditTextFields();

                btnCancel.setVisibility(View.VISIBLE);
                // Change button text and behavior
                btnUpdate.setText("Confirm");
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        confirmUpdate();

                        resetUI();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideEditTextFields();

                btnCancel.setVisibility(View.GONE);

                btnUpdate.setText("Update");
                setUpdateClickListener();
            }
        });


        loadUserData();

        setUpdateClickListener();
    }


    private void hideEditTextFields() {

        etNewUsername.setVisibility(View.GONE);
        etNewEmail.setVisibility(View.GONE);
        etNewPhoneNumber.setVisibility(View.GONE);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                User user = document.toObject(User.class);
                                displayUserData(user);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }
    }

    private void displayUserData(User user) {
        if (user != null) {
            tvUsername.setText("Username: " + user.getUsername());
            tvEmail.setText("Email: " + user.getEmail());
            tvPhoneNumber.setText("Phone Number: " + user.getPhoneNumber());

            etNewUsername.setText(user.getUsername());
            etNewEmail.setText(user.getEmail());
            etNewPhoneNumber.setText(user.getPhoneNumber());
        }
    }

    private void showEditTextFields() {

        etNewUsername.setVisibility(View.VISIBLE);
        etNewEmail.setVisibility(View.VISIBLE);
        etNewPhoneNumber.setVisibility(View.VISIBLE);
    }

    private void confirmUpdate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            String newUsername = etNewUsername.getText().toString().trim();
            String newEmail = etNewEmail.getText().toString().trim();
            String newPhoneNumber = etNewPhoneNumber.getText().toString().trim();

            Map<String, Object> updatedFields = new HashMap<>();

            if (!TextUtils.isEmpty(newUsername)) {
                updatedFields.put("username", newUsername);
            }

            if (!TextUtils.isEmpty(newEmail)) {
                updatedFields.put("email", newEmail);
            }

            if (!TextUtils.isEmpty(newPhoneNumber)) {
                updatedFields.put("phoneNumber", newPhoneNumber);
            }

            db.collection("users").document(userId)
                    .update(updatedFields)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User data updated successfully!");

                        loadUserData();

                        resetUI();
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating user data", e));
        }
    }


    private void resetUI() {

        hideEditTextFields();

        btnCancel.setVisibility(View.GONE);
        btnUpdate.setText("Update");

        setUpdateClickListener();
    }

    private void setUpdateClickListener() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEditTextFields();

                btnCancel.setVisibility(View.VISIBLE);
                btnUpdate.setText("Confirm");
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        confirmUpdate();

                        resetUI();
                    }
                });
            }
        });
    }
}
