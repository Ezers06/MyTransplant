package com.example.mytransplant;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PersonalInformation extends AppCompatActivity {

    private static final String TAG = "PersonalInformation";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Calendar dobCalendar;
    private boolean datePickerDialogShown = false;

    private String fullName;
    private String icNumber;
    private String dateOfBirth;
    private String bloodType;
    private String race;
    private String gender;
    private String address;
    private String rhFactor;
    private String combinedbloodType;
    private boolean userConfirmedChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dobCalendar = Calendar.getInstance();

        loadPersonalInfo();

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(view -> {

            fullName = getEditTextValue(R.id.editName);
            icNumber = getEditTextValue(R.id.editIC);
            dateOfBirth = getEditTextValue(R.id.editDOB);
            race = getEditTextValue(R.id.editRace);
            gender = ((Spinner) findViewById(R.id.spinnerSex)).getSelectedItem().toString();
            address = getEditTextValue(R.id.editAddress);
            bloodType = ((Spinner) findViewById(R.id.spinnerBloodType)).getSelectedItem().toString();
            rhFactor = ((Spinner) findViewById(R.id.spinnerRhFactor)).getSelectedItem().toString();
            combinedbloodType = bloodType + " " + rhFactor;

            saveChanges();
        });

        EditText editTextDateOfBirth = findViewById(R.id.editDOB);
        editTextDateOfBirth.setInputType(InputType.TYPE_NULL);
        editTextDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        EditText editTextICNumber = findViewById(R.id.editIC);
        editTextICNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        editTextICNumber.setFilters(new InputFilter[]{new ICNumberInputFilter()});

    }

    private class ICNumberInputFilter implements InputFilter {
        private static final String IC_REGEX = "^[0-9]{0,6}([\\-]{0,1}[0-9]{0,2}){0,1}([\\-]{0,1}[0-9]{0,4}){0,1}$";

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String prefix = dest.subSequence(0, dstart).toString();
            String inserted = source.subSequence(start, end).toString();
            String suffix = dest.subSequence(dend, dest.length()).toString();

            String newText = prefix + inserted + suffix;

            if (newText.matches(IC_REGEX)) {
                return null;
            } else {
                return "";
            }
        }
    }

    private void showDatePickerDialog() {
        if (!datePickerDialogShown) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    dateSetListener,
                    dobCalendar.get(Calendar.YEAR),
                    dobCalendar.get(Calendar.MONTH),
                    dobCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
            datePickerDialogShown = true;
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dobCalendar.set(Calendar.YEAR, year);
            dobCalendar.set(Calendar.MONTH, month);
            dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateOfBirthEditText();
            datePickerDialogShown = false;

            EditText editTextDateOfBirth = findViewById(R.id.editDOB);
            editTextDateOfBirth.setOnClickListener(null);
        }
    };

    private void updateDateOfBirthEditText() {
        EditText editTextDateOfBirth = findViewById(R.id.editDOB);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        editTextDateOfBirth.setText(sdf.format(dobCalendar.getTime()));
    }


    private void saveChanges() {

        String fullName = getEditTextValue(R.id.editName);
        String icNumber = getEditTextValue(R.id.editIC);
        String dateOfBirth = getEditTextValue(R.id.editDOB);
        String race = getEditTextValue(R.id.editRace);
        String address = getEditTextValue(R.id.editAddress);

        if (isEmpty(fullName) || isEmpty(icNumber) || isEmpty(dateOfBirth) || isEmpty(race) || isEmpty(address)) {
            showToast("All fields must be filled!");
            setEmptyFieldError(fullName, R.id.editName);
            setEmptyFieldError(icNumber, R.id.editIC);
            setEmptyFieldError(dateOfBirth, R.id.editDOB);
            setEmptyFieldError(bloodType, R.id.spinnerBloodType);
            setEmptyFieldError(race, R.id.editRace);
            setEmptyFieldError(address, R.id.editAddress);
            return;
        } else {

            clearFieldErrors();
        }

        if (!isValidICFormat(icNumber)) {
            showToast("Invalid IC number format. Please enter a valid IC number.");
            return;
        }

        if (!isValidAddressFormat(address)) {
            showToast("Invalid address format. Please enter a valid address.");
            setAddressFieldError("Address Format: [Street Name], [Postal Code], [City], [State]", R.id.editAddress);
            return;
        } else {

            setAddressFieldError(null, R.id.editAddress);
        }

        showConfirmationDialog();

        datePickerDialogShown = false;

    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        builder.setTitle("Confirm Changes");
        builder.setMessage("\nPlease double-check your information.\nOnce you click save, your information cannot be changed!\n");

        CheckBox checkBoxTick = new CheckBox(this);
        checkBoxTick.setText("I confirm that the information provided is correct");
        checkBoxTick.setChecked(false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER;
        checkBoxTick.setLayoutParams(layoutParams);

        linearLayout.addView(checkBoxTick);

        builder.setView(linearLayout);

        builder.setPositiveButton("Save", (dialog, which) -> {

            userConfirmedChanges = true;
            saveAdditionalInfoToFirestore(fullName, icNumber, dateOfBirth, race, gender, address, combinedbloodType);

            loadPersonalInfo();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            userConfirmedChanges = false;
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(checkBoxTick.isChecked());

            checkBoxTick.setOnCheckedChangeListener((buttonView, isChecked) -> {
                positiveButton.setEnabled(isChecked);
            });
        });
        alertDialog.show();
    }

    private void setAddressFieldError(String errorMessage, int editTextId) {
        EditText editText = findViewById(editTextId);
        editText.setError(errorMessage);
    }

    private boolean isValidICFormat(String icNumber) {
        String icRegex = "^\\d{6}-\\d{2}-\\d{4}$";
        return icNumber.matches(icRegex);
    }

    private boolean isValidAddressFormat(String address) {
        // Allow any characters, numbers, spaces, and commas in the address
        String regex = "^[\\p{L}0-9 ,.-]+$";
        return address.matches(regex);
    }


    private void setEmptyFieldError(String value, int editTextId) {
        EditText editText = findViewById(editTextId);
        if (isEmpty(value)) {
            editText.setError("This Field Required!");
        } else {
            editText.setError(null);
        }
    }

    private void clearFieldErrors() {
        int[] editTextIds = {
                R.id.editName,
                R.id.editIC,
                R.id.editDOB,
                R.id.editRace,
                R.id.editAddress
        };

        for (int editTextId : editTextIds) {
            EditText editText = findViewById(editTextId);
            editText.setError(null);
        }
    }

    private String getEditTextValue(int editTextId) {
        View view = findViewById(editTextId);

        if (view instanceof EditText) {
            return ((EditText) view).getText().toString().trim();
        } else {
            return "";
        }
    }


    private boolean isEmpty(String value) {
        return value.isEmpty();
    }

    private void loadPersonalInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String email = document.getString("email");
                                String phoneNumber = document.getString("phoneNumber");
                                String fullName = document.getString("fullName");
                                String icNumber = document.getString("icNumber");
                                String dateOfBirth = document.getString("dateOfBirth");
                                String bloodType = document.getString("bloodType");
                                String rhFactor = document.getString("rhFactor");
                                String race = document.getString("race");
                                String gender = document.getString("gender");
                                String address = document.getString("address");

                                updateEditTextFields(email, phoneNumber, fullName, icNumber, dateOfBirth, race, gender, address, bloodType, rhFactor);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                        }
                    });
        }
    }

    private void updateEditTextFields(String email, String phoneNumber, String fullName, String icNumber, String dateOfBirth, String race, String gender, String address, String  bloodType, String rhFactor) {
        EditText editTextEmail = findViewById(R.id.editEmail);
        EditText editTextTelNo = findViewById(R.id.editPhone);
        EditText editTextFullName = findViewById(R.id.editName);
        EditText editTextICNumber = findViewById(R.id.editIC);
        EditText editTextDateOfBirth = findViewById(R.id.editDOB);
        Spinner spinnerBloodType = findViewById(R.id.spinnerBloodType);
        Spinner spinnerRhFactor = findViewById(R.id.spinnerRhFactor);
        EditText editTextRace = findViewById(R.id.editRace);
        Spinner spinnerSex = findViewById(R.id.spinnerSex);
        EditText editTextAddress = findViewById(R.id.editAddress);

        if (email != null) {
            editTextEmail.setText(email);
            editTextEmail.setFocusable(false);
            editTextEmail.setCursorVisible(false);
            editTextEmail.setEnabled(false);
        }

        if (phoneNumber != null) {
            editTextTelNo.setText(phoneNumber);
            editTextTelNo.setFocusable(false);
            editTextTelNo.setCursorVisible(false);
            editTextTelNo.setEnabled(false);
        }

        if (fullName != null) {
            editTextFullName.setText(fullName);
            editTextFullName.setFocusable(false);
            editTextFullName.setCursorVisible(false);
            editTextFullName.setEnabled(false);
        }

        if (icNumber != null) {
            editTextICNumber.setText(icNumber);
            editTextICNumber.setFocusable(false);
            editTextICNumber.setCursorVisible(false);
            editTextICNumber.setEnabled(false);
        }

        if (dateOfBirth != null) {
            editTextDateOfBirth.setText(dateOfBirth);
            editTextDateOfBirth.setFocusable(false);
            editTextDateOfBirth.setCursorVisible(false);
            editTextDateOfBirth.setEnabled(false);
        }

        if (race != null) {
            editTextRace.setText(race);
            editTextRace.setFocusable(false);
            editTextRace.setCursorVisible(false);
            editTextRace.setEnabled(false);
        }

        if (bloodType != null) {
            setSpinnerSelection(spinnerBloodType, R.array.blood_type_options, bloodType);
            spinnerBloodType.setEnabled(false);

            setSpinnerSelection(spinnerRhFactor, R.array.rh_factor_options, rhFactor);
            spinnerRhFactor.setEnabled(false);
        } else {
            spinnerRhFactor.setEnabled(true);
        }

        if (gender != null) {

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_options, R.layout.spinner_layout);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSex.setAdapter(adapter);

            int position = adapter.getPosition(gender);
            spinnerSex.setSelection(position);

            spinnerSex.setEnabled(false);
        }

        if (address != null) {
            editTextAddress.setText(address);
        }
    }

    private void setSpinnerSelection(Spinner spinner, int arrayResource, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResource, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int position = getIndexForValue(adapter, value);
        if (position != -1) {
            spinner.setSelection(position);
        }
    }

    private int getIndexForValue(ArrayAdapter<CharSequence> adapter, String value) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }

    private void saveAdditionalInfoToFirestore(String fullName, String icNumber, String dateOfBirth, String race, String gender, String address, String combinedBloodType) {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("fullName", fullName);
                additionalInfo.put("icNumber", icNumber);
                additionalInfo.put("dateOfBirth", dateOfBirth);
                additionalInfo.put("race", race);
                additionalInfo.put("gender", gender);
                additionalInfo.put("address", address);
                additionalInfo.put("bloodType", combinedBloodType);

                db.collection("users").document(userId)
                        .update(additionalInfo)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Additional information updated successfully");
                            showToast("Information saved successfully");
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error updating additional information", e);
                            showToast("Failed to save information. Please try again.");
                        });
            }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

