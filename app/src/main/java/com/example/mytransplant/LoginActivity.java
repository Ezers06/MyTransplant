package com.example.mytransplant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        final ToggleButton togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        final EditText etPassword = findViewById(R.id.etPassword);

        // Set default transformation method to PasswordTransformationMethod
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Set OnCheckedChangeListener for the toggle button
        togglePasswordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle the password visibility
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        TextView btn = findViewById(R.id.textViewSignUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        TextView forgotPasswordText = findViewById(R.id.forgotPassword);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ForgotPassword activity
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        TextView doctorbtn = findViewById(R.id.doctorbutton);
        doctorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, DoctorRegister.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Override the back button to prevent navigating back to the app
        moveTaskToBack(true);
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Check the user's role or type
                            checkUserRole(user.getUid());
                        }
                    } else {
                        showToast("Login failed. Please check your credentials.");
                    }
                });
    }

    private void checkUserRole(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // This is a regular user
                        saveSession("user");
                        navigateToUserHomePage();
                    } else {
                        firestore.collection("doctors").document(userId).get()
                                .addOnSuccessListener(doctorDocumentSnapshot -> {
                                    if (doctorDocumentSnapshot.exists()) {
                                        // This is a doctor
                                        saveSession("doctor");
                                        navigateToDoctorHomePage();
                                    } else {
                                        showToast("Invalid user type.");
                                    }
                                })
                                .addOnFailureListener(e -> showToast("Error checking user type."));
                    }
                })
                .addOnFailureListener(e -> showToast("Error checking user type."));
    }

    private void saveSession(String userRole) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userRole", userRole);
        editor.apply();
    }

    private void navigateToUserHomePage() {
        Intent userHomeIntent = new Intent(LoginActivity.this, Homepage.class);
        startActivity(userHomeIntent);
        showToast("User login successful!");
        finish(); // Optional: Finish the login activity to prevent going back
    }

    private void navigateToDoctorHomePage() {
        Intent doctorHomeIntent = new Intent(LoginActivity.this, HomepageDoctor.class);
        startActivity(doctorHomeIntent);
        showToast("Doctor login successful!");
        finish(); // Optional: Finish the login activity to prevent going back
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
