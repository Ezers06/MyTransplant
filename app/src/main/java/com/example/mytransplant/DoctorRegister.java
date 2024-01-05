package com.example.mytransplant;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class DoctorRegister extends AppCompatActivity {

    private EditText etDoctorName, etDoctorEmail, etDoctorNumber, etDoctorPassword, etConfirmPassword;
    private Spinner spinnerHospital;
    private TextView etDoctorAddress;
    private ArrayAdapter<Hospital> hospitalAdapter;
    private Button btnRegisterDoctor;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etDoctorName = findViewById(R.id.etDoctorName);
        etDoctorEmail = findViewById(R.id.etDoctorEmail);
        etDoctorAddress = findViewById(R.id.etDoctorAddress);
        etDoctorNumber = findViewById(R.id.etDoctorNumber);
        etDoctorPassword = findViewById(R.id.etDoctorPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegisterDoctor = findViewById(R.id.btnRegisterDoctor);

        spinnerHospital = findViewById(R.id.spinnerHospital);
        hospitalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(hospitalAdapter);

        spinnerHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Hospital selectedHospital = hospitalAdapter.getItem(position);

                if (selectedHospital != null && !selectedHospital.getName().equals("Choose Your Hospital")) {
                    etDoctorAddress.setText(selectedHospital.getAddress());
                } else {
                    etDoctorAddress.setText("Hospital Address");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        retrieveHospitalData();

        final ToggleButton togglePasswordVisibility1 = findViewById(R.id.togglePasswordVisibility1);
        final ToggleButton togglePasswordVisibility2 = findViewById(R.id.togglePasswordVisibility2);

        etDoctorPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        togglePasswordVisibility1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etDoctorPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etDoctorPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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

        btnRegisterDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerDoctor();
            }
        });

        TextView alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(DoctorRegister.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void retrieveHospitalData() {
        firestore.collection("hospital")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        hospitalAdapter.clear();

                        Hospital defaultHospital = new Hospital("Choose Your Hospital", "");
                        hospitalAdapter.add(defaultHospital);

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String hospitalName = document.getString("name");
                            String hospitalAddress = document.getString("address");

                            Hospital hospital = new Hospital(hospitalName, hospitalAddress);
                            hospitalAdapter.add(hospital);
                        }

                        hospitalAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void registerDoctor() {
        String email = etDoctorEmail.getText().toString().trim();
        String password = etDoctorPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String fullName = etDoctorName.getText().toString().trim();
        String phoneNumber = etDoctorNumber.getText().toString().trim();

        etDoctorName.setError(null);
        etDoctorAddress.setError(null);
        etDoctorNumber.setError(null);

        boolean hasError = false;
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty()) {
            showToast("All fields are required.");
            hasError = true;
        }
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            etDoctorEmail.setError("Invalid Email Format (must end with @gmail.com)");
            hasError = true;
        }
        if (fullName.isEmpty()) {
            etDoctorName.setError("This Field Required");
            hasError = true;
        }
        if (!phoneNumber.matches("\\d{10,}")) {
            etDoctorNumber.setError("Invalid phone number (must be at least 10 digits and contain only digits)");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match.");
            return;
        }

        if (hasError) {
            return;
        }

        Hospital selectedHospital = (Hospital) spinnerHospital.getSelectedItem();

        if (selectedHospital == null || selectedHospital.getName().isEmpty() || selectedHospital.getAddress().isEmpty()) {
            showToast("Please select a valid hospital.");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                addUserDataToFirestore(user.getUid(), selectedHospital);
                                navigateToDoctorLogin();
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                e.printStackTrace();
                            }
                            showToast("Failed to Register. Please check your information.");
                        }
                    }
                });
    }

    private void navigateToDoctorLogin() {
        Intent loginIntent = new Intent(DoctorRegister.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void addUserDataToFirestore(String userId, Hospital selectedHospital) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", etDoctorName.getText().toString().trim());
        userData.put("email", etDoctorEmail.getText().toString().trim());
        userData.put("hospitalName", selectedHospital.getName());
        userData.put("hospitalAddress", selectedHospital.getAddress());
        userData.put("phoneNumber", etDoctorNumber.getText().toString().trim());

        if (firebaseAuth.getCurrentUser() != null) {
            firestore.collection("doctors").document(userId)
                    .set(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("Registration successful!");
                            } else {
                                Exception e = task.getException();
                                if (e != null) {
                                    e.printStackTrace();
                                }
                                showToast("Failed to add user data to Firestore. Please try again.");
                            }
                        }
                    });
        } else {
            showToast("User not authenticated. Please sign in and try again.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}