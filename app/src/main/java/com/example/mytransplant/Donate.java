package com.example.mytransplant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Donate extends AppCompatActivity {

    private static final String TAG = "Donate";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private EditText editTextName;
    private EditText editTextIC;
    private EditText editTextDOB;
    private EditText editTextBloodType;
    private EditText editTextRace;
    private EditText editTextGender;
    private EditText editTextAddress;
    private EditText editTextPhoneNumber;
    private EditText editrelayName;
    private EditText editrelayRelation;
    private EditText editrelayNumber;
    private Spinner spinnerOrgan;
    private Button confirmDonate;
    private ProgressBar loadingProgressBar;
    private Button btnCancel;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.editName);
        editTextIC = findViewById(R.id.editIC);
        editTextDOB = findViewById(R.id.editDOB);
        editTextBloodType = findViewById(R.id.editBloodType);
        editTextRace = findViewById(R.id.editRace);
        editTextGender = findViewById(R.id.editGender);
        editTextAddress = findViewById(R.id.editAddress);
        editTextPhoneNumber = findViewById(R.id.editPhone);
        editrelayName = findViewById(R.id.editrelayName);
        editrelayRelation = findViewById(R.id.editrelayRelation);
        editrelayNumber = findViewById(R.id.editrelayNumber);
        spinnerOrgan = findViewById(R.id.spinnerOrgan);


        confirmDonate = findViewById(R.id.confirmDonate);

        loadUserInfo();

        confirmDonate.setOnClickListener(v -> {
            if (validateRelativesFields()) {

                showConfirmationDialog();
            } else {

                showToast("Please fill in all required fields.");
            }
        });
        checkDonorStatus();
    }

    private void checkDonorStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("donate").document(userId)
                    .get()
                    .addOnCompleteListener(taskDonate -> {
                        if (taskDonate.isSuccessful()) {
                            DocumentSnapshot documentDonate = taskDonate.getResult();
                            if (documentDonate.exists()) {

                                findViewById(R.id.imgverify).setVisibility(View.VISIBLE);
                                findViewById(R.id.textverifyView).setVisibility(View.VISIBLE);
                                findViewById(R.id.textverifyDonate).setVisibility(View.VISIBLE);
                                findViewById(R.id.fordisplay1).setVisibility(View.VISIBLE);
                                hideAllViews();
                            } else {

                                checkRequestStatus(userId);
                            }
                        } else {
                            Log.w(TAG, "Error checking donor status", taskDonate.getException());
                        }
                    });
        }
    }

    private void checkRequestStatus(String userId) {

        db.collection("request").document(userId)
                .get()
                .addOnCompleteListener(taskRequest -> {
                    if (taskRequest.isSuccessful()) {
                        DocumentSnapshot documentRequest = taskRequest.getResult();
                        if (documentRequest.exists()) {

                            findViewById(R.id.imgchecker).setVisibility(View.VISIBLE);
                            findViewById(R.id.textcheckerView).setVisibility(View.VISIBLE);
                            findViewById(R.id.textcheckerDonate).setVisibility(View.VISIBLE);
                            findViewById(R.id.fordisplay2).setVisibility(View.VISIBLE);
                            hideAllViews();
                        }
                    } else {
                        Log.w(TAG, "Error checking request status", taskRequest.getException());
                    }
                });
    }

    private void hideAllViews() {

        findViewById(R.id.textOrganDonationForm).setVisibility(View.GONE);
        findViewById(R.id.Title).setVisibility(View.GONE);
        findViewById(R.id.textName).setVisibility(View.GONE);
        findViewById(R.id.editName).setVisibility(View.GONE);
        findViewById(R.id.texttIC).setVisibility(View.GONE);
        findViewById(R.id.editIC).setVisibility(View.GONE);
        findViewById(R.id.textDOB).setVisibility(View.GONE);
        findViewById(R.id.editDOB).setVisibility(View.GONE);
        findViewById(R.id.textBloodType).setVisibility(View.GONE);
        findViewById(R.id.editBloodType).setVisibility(View.GONE);
        findViewById(R.id.textRace).setVisibility(View.GONE);
        findViewById(R.id.editRace).setVisibility(View.GONE);
        findViewById(R.id.textGender).setVisibility(View.GONE);
        findViewById(R.id.editGender).setVisibility(View.GONE);
        findViewById(R.id.textAddress).setVisibility(View.GONE);
        findViewById(R.id.editAddress).setVisibility(View.GONE);
        findViewById(R.id.textPhone).setVisibility(View.GONE);
        findViewById(R.id.editPhone).setVisibility(View.GONE);
        findViewById(R.id.secRelatives).setVisibility(View.GONE);
        findViewById(R.id.textrelayName).setVisibility(View.GONE);
        findViewById(R.id.editrelayName).setVisibility(View.GONE);
        findViewById(R.id.textrelayRelation).setVisibility(View.GONE);
        findViewById(R.id.editrelayRelation).setVisibility(View.GONE);
        findViewById(R.id.textrelayNumber).setVisibility(View.GONE);
        findViewById(R.id.editrelayNumber).setVisibility(View.GONE);
        findViewById(R.id.textDonation).setVisibility(View.GONE);
        findViewById(R.id.textorganDonation).setVisibility(View.GONE);
        findViewById(R.id.spinnerOrgan).setVisibility(View.GONE);
        findViewById(R.id.confirmDonate).setVisibility(View.GONE);
    }



    private boolean validateRelativesFields() {
        boolean isValid = true;

        String relayName = editrelayName.getText().toString().trim();
        String relayRelation = editrelayRelation.getText().toString().trim();
        String relayNumber = editrelayNumber.getText().toString().trim();

        if (relayName.isEmpty()) {
            editrelayName.setError("Required field");
            isValid = false;
        }

        if (relayRelation.isEmpty()) {
            editrelayRelation.setError("Required field");
            isValid = false;
        }

        if (relayNumber.isEmpty()) {
            editrelayNumber.setError("Required field");
            isValid = false;
        }

        return isValid;
    }

    private void showConfirmationDialog() {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_donate, null);


        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        CheckBox checkBox1 = dialogView.findViewById(R.id.checkBox1);
        CheckBox checkBox2 = dialogView.findViewById(R.id.checkBox2);
        CheckBox checkBox3 = dialogView.findViewById(R.id.checkBox3);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        loadingProgressBar = dialogView.findViewById(R.id.loadingProgressBar);

        dialogTitle.setText("Confirm Donation");
        dialogMessage.setText("Are you sure you want to be an organ donor?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnConfirm.setOnClickListener(v -> {

            setButtonsEnabled(false);
            showLoading();

            boolean isCheckBox1Checked = checkBox1.isChecked();
            boolean isCheckBox2Checked = checkBox2.isChecked();
            boolean isCheckBox3Checked = checkBox3.isChecked();

            new android.os.Handler().postDelayed(() -> {

                setButtonsEnabled(true);
                hideLoading();

                if (isCheckBox1Checked && isCheckBox2Checked && isCheckBox3Checked) {

                    sendDataToFirestore();
                } else {

                    showToast("Please check all checkboxes to confirm.");
                }
            }, 2000);
        });


        dialog.show();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnCancel.setEnabled(enabled);
        btnConfirm.setEnabled(enabled);
    }

    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void sendDataToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> donateData = new HashMap<>();
            donateData.put("fullName", editTextName.getText().toString());
            donateData.put("icNumber", editTextIC.getText().toString());
            donateData.put("dateOfBirth", editTextDOB.getText().toString());
            donateData.put("bloodType", editTextBloodType.getText().toString());
            donateData.put("race", editTextRace.getText().toString());
            donateData.put("gender", editTextGender.getText().toString());
            donateData.put("address", editTextAddress.getText().toString());
            donateData.put("phoneNumber", editTextPhoneNumber.getText().toString());
            donateData.put("relayName", editrelayName.getText().toString());
            donateData.put("relayRelation", editrelayRelation.getText().toString());
            donateData.put("relayNumber", editrelayNumber.getText().toString());
            donateData.put("organDonation", spinnerOrgan.getSelectedItem().toString());


            db.collection("donate").document(userId)
                    .set(donateData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Data successfully sent to Firestore");

                        showToast("You've become a donor");

                        navigateToHomepage();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error sending data to Firestore", e);

                    });
        }
    }

    private void navigateToHomepage() {
        Intent intent = new Intent(this, Homepage.class);
        startActivity(intent);
        finish();
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String fullName = document.getString("fullName");
                                String icNumber = document.getString("icNumber");
                                String dateOfBirth = document.getString("dateOfBirth");
                                String bloodType = document.getString("bloodType");
                                String race = document.getString("race");
                                String gender = document.getString("gender");
                                String address = document.getString("address");
                                String phoneNumber = document.getString("phoneNumber");

                                updateUI(fullName, icNumber, dateOfBirth, bloodType, race, gender, address, phoneNumber);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                        }
                    });
        }
    }

    private void updateUI(String fullName, String icNumber, String dateOfBirth, String bloodType, String race, String gender, String address, String phoneNumber) {
        editTextName.setText(fullName);
        editTextIC.setText(icNumber);
        editTextDOB.setText(dateOfBirth);
        editTextBloodType.setText(bloodType);
        editTextRace.setText(race);
        editTextGender.setText(gender);
        editTextAddress.setText(address);
        editTextPhoneNumber.setText(phoneNumber);


        setEditTextEnabled(false, editTextName, editTextIC, editTextDOB, editTextBloodType, editTextRace, editTextGender, editTextAddress, editTextPhoneNumber);
    }

    private void setEditTextEnabled(boolean isEnabled, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setEnabled(isEnabled);
            editText.setFocusable(false);
            editText.setCursorVisible(false);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}