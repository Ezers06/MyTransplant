package com.example.mytransplant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Assign extends AppCompatActivity {

    private ListView listViewDonater;
    private ListView listViewPatients;
    private AssignAdapter donaterAdapter;
    private AssignAdapter patientAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView textViewSelectedDonor;
    private TextView textViewSelectedPatient;
    private UserModel selectedDonor;
    private UserModel selectedPatient;
    private UserModel currentDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        textViewSelectedDonor = findViewById(R.id.textViewSelectedDonor);
        textViewSelectedPatient = findViewById(R.id.textViewSelectedPatient);

        listViewDonater = findViewById(R.id.listViewDonater);
        listViewPatients = findViewById(R.id.listViewPatients);

        donaterAdapter = new AssignAdapter(this);
        patientAdapter = new AssignAdapter(this);

        listViewDonater.setAdapter(donaterAdapter);
        listViewPatients.setAdapter(patientAdapter);

        fetchDonaterDataFromFirestore();
        fetchPatientDataFromFirestore();
        fetchDoctorDataForCurrentUser();

        listViewDonater.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDonor = (UserModel) donaterAdapter.getItem(position);
                updateSelectedDonorTextView();
            }
        });

        listViewPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPatient = (UserModel) patientAdapter.getItem(position);
                updateSelectedPatientTextView();
            }
        });

        Button btnMatch = findViewById(R.id.btnMatch);
        btnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMatching();
            }
        });
    }

    private void fetchDoctorDataForCurrentUser() {
        if (mAuth.getCurrentUser() != null) {
            String currentUserUid = mAuth.getCurrentUser().getUid();

            firestore.collection("doctors")
                    .document(currentUserUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                currentDoctor = new UserModel(document);

                                // Log success and details
                                Log.d("Firestore", "Doctor data retrieved successfully.");
                                Log.d("Firestore", "Doctor Name: " + currentDoctor.getName());
                                Log.d("Firestore", "Doctor Phone: " + currentDoctor.getPhoneNumber());
                                Log.d("Firestore", "Doctor Hospital: " + currentDoctor.getHospitalName());
                                Log.d("Firestore", "Doctor Address: " + currentDoctor.getHospitalAddress());

                                performMatching();
                            } else {
                                Log.d("Firestore", "Doctor data does not exist for the current user.");
                            }
                        } else {
                            Log.e("Firestore", "Error fetching doctor data: " + task.getException().getMessage());
                        }
                    });
        } else {
            Log.d("Firestore", "No authenticated user found. Unable to fetch doctor data.");
        }
    }

    private void performMatching() {
        if (selectedDonor != null && selectedPatient != null) {
            if (selectedDonor.getBloodType().equals(selectedPatient.getBloodType()) &&
                    selectedDonor.getOrganType().equals(selectedPatient.getOrganType())) {

                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

                CheckBox checkBox1 = dialogView.findViewById(R.id.checkboxOption1);
                CheckBox checkBox2 = dialogView.findViewById(R.id.checkboxOption2);
                CheckBox checkBox3 = dialogView.findViewById(R.id.checkboxOption3);
                Button btnSelectDateTime = dialogView.findViewById(R.id.btnSelectDateTime);
                TextView tvSelectedDateTime = dialogView.findViewById(R.id.tvSelectedDateTime);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(dialogView)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String selectedDateTime = tvSelectedDateTime.getText().toString();
                                boolean isCheckBox1Checked = checkBox1.isChecked();
                                boolean isCheckBox2Checked = checkBox2.isChecked();
                                boolean isCheckBox3Checked = checkBox3.isChecked();

                                if (isCheckBox1Checked && isCheckBox2Checked && isCheckBox3Checked && !selectedDateTime.isEmpty()) {
                                    if (currentDoctor != null) {

                                        Map<String, Object> appointmentData = new HashMap<>();
                                        appointmentData.put("donorName", selectedDonor.getName());
                                        appointmentData.put("donorICNumber", selectedDonor.getIdentityCard());
                                        appointmentData.put("donorPhoneNumber", selectedDonor.getPhoneNumber());
                                        appointmentData.put("donorBloodType", selectedDonor.getBloodType());
                                        appointmentData.put("donorOrganType", selectedDonor.getOrganType());

                                        appointmentData.put("patientName", selectedPatient.getName());
                                        appointmentData.put("patientICNumber", selectedPatient.getIdentityCard());
                                        appointmentData.put("patientPhoneNumber", selectedPatient.getPhoneNumber());
                                        appointmentData.put("patientBloodType", selectedPatient.getBloodType());
                                        appointmentData.put("patientOrganType", selectedPatient.getOrganType());

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                        String formattedDateTime = sdf.format(parseDateTime(selectedDateTime));

                                        appointmentData.put("appointmentDate", formattedDateTime);

                                        appointmentData.put("doctorName", currentDoctor.getName());
                                        appointmentData.put("doctorPhoneNumber", currentDoctor.getPhoneNumber());
                                        appointmentData.put("doctorHospitalName", currentDoctor.getHospitalName());
                                        appointmentData.put("doctorHospitalAddress", currentDoctor.getHospitalAddress());

                                        firestore.collection("appointment").add(appointmentData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("Firestore", "Appointment data added with ID: " + documentReference.getId());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("Firestore", "Error adding appointment data: " + e.getMessage());
                                                    }
                                                });
                                    }

                                    deleteDocument("donate", selectedDonor);
                                    deleteDocument("request", selectedPatient);

                                    Toast.makeText(Assign.this, "Matching Done!", Toast.LENGTH_SHORT).show();

                                    selectedDonor = null;
                                    selectedPatient = null;

                                    updateSelectedDonorTextView();
                                    updateSelectedPatientTextView();
                                } else {

                                    Toast.makeText(Assign.this, "Please select all checkboxes and choose a date & time.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                AlertDialog dialog = builder.create();

                btnSelectDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateTimePickerDialog(tvSelectedDateTime);
                    }
                });


                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setEnabled(false);
                    }
                });


                CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setEnabled(checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && !tvSelectedDateTime.getText().toString().isEmpty());
                    }
                };

                checkBox1.setOnCheckedChangeListener(checkBoxListener);
                checkBox2.setOnCheckedChangeListener(checkBoxListener);
                checkBox3.setOnCheckedChangeListener(checkBoxListener);

                dialog.show();

            } else {
                Toast.makeText(this, "Blood type or organ type mismatch. Please select matching donor and patient.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select a Donor and a Patient before matching.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDateTimePickerDialog(final TextView tvSelectedDateTime) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(Assign.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                        String formattedDateTime = sdf.format(calendar.getTime());

                                        tvSelectedDateTime.setText(formattedDateTime);
                                        tvSelectedDateTime.setVisibility(View.VISIBLE);
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private Date parseDateTime(String dateTimeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void deleteDocument(String collection, UserModel user) {
        firestore.collection(collection).document(user.getDocumentId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", collection + " data deleted successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error deleting " + collection + " data: " + e.getMessage());
                    }
                });
    }

    private void updateSelectedDonorTextView() {
        if (selectedDonor != null) {
            textViewSelectedDonor.setText("" + selectedDonor.getName());
        }
    }

    private void updateSelectedPatientTextView() {
        if (selectedPatient != null) {
            textViewSelectedPatient.setText("" + selectedPatient.getName());
        }
    }

    private void fetchDonaterDataFromFirestore() {
        firestore.collection("donate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserModel> donaterList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            UserModel donater = new UserModel(document);
                            donaterList.add(donater);
                        }
                        donaterAdapter.updateData(donaterList);
                    }
                });
    }

    private void fetchPatientDataFromFirestore() {
        firestore.collection("request")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserModel> patientList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            UserModel patient = new UserModel(document);
                            patientList.add(patient);
                        }
                        patientAdapter.updateData(patientList);
                    }
                });
    }

}
