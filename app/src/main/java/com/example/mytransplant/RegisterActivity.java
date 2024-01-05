package com.example.mytransplant;


import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Button btnRegister = findViewById(R.id.btnRegisterNew);
        final EditText etPassword = findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        final ToggleButton togglePasswordVisibility1 = findViewById(R.id.togglePasswordVisibility1);
        final ToggleButton togglePasswordVisibility2 = findViewById(R.id.togglePasswordVisibility2);

        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        togglePasswordVisibility1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        togglePasswordVisibility2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText etName = findViewById(R.id.etNewName);
                EditText etEmail = findViewById(R.id.etNewEmail);
                EditText etNumber = findViewById(R.id.etNewNumber);
                EditText etPassword = findViewById(R.id.etNewPassword);
                EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String username = etName.getText().toString().trim();
                String phoneNumber = etNumber.getText().toString().trim();

                etName.setError(null);
                etEmail.setError(null);
                etNumber.setError(null);
                etPassword.setError(null);
                etConfirmPassword.setError(null);

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("This Field Required");
                }
                if (TextUtils.isEmpty(username)) {
                    etName.setError("This Field Required");
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    etNumber.setError("This Field Required");
                }
                if (!TextUtils.isEmpty(email) && !email.endsWith("@gmail.com")) {
                    etEmail.setError("Invalid Email Format (must end with @gmail.com)");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)
                        || TextUtils.isEmpty(username) || TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!phoneNumber.matches("\\d{10,}")) {
                    etNumber.setError("Invalid phone number (must be at least 10 digits and contain only digits)");
                    return;
                }
                registerUser(email, password, username, phoneNumber);
            }
        });

        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void registerUser(final String email, final String password, final String username, final String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                User userDetails = new User(username, email, phoneNumber);

                                saveUserDetails(user.getUid(), userDetails);

                                Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed, Please try again. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    private void saveUserDetails(String userId, User user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully written
                        Log.d(TAG, "User details added to Firestore!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Log.w(TAG, "Error adding user details to Firestore", e);
                    }
                });
    }




}