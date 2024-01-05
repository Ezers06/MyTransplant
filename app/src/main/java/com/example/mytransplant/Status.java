package com.example.mytransplant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Status extends AppCompatActivity {

    private static final String TAG = "StatusActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView userName1TextView, identityCard1TextView, phoneNumber1TextView, bloodType1TextView, organType1TextView;
    private TextView userName2TextView, identityCard2TextView, phoneNumber2TextView, bloodType2TextView, organType2TextView;
    private TextView userName3TextView, phoneNumber3TextView, hospitalNameTextView, hospitalAddressTextView;
    private TextView appointmentDateTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userName1TextView = findViewById(R.id.userName1);
        identityCard1TextView = findViewById(R.id.identityCard1);
        phoneNumber1TextView = findViewById(R.id.phoneNumber1);
        bloodType1TextView = findViewById(R.id.bloodType1);
        organType1TextView = findViewById(R.id.organType1);

        userName2TextView = findViewById(R.id.userName2);
        identityCard2TextView = findViewById(R.id.identityCard2);
        phoneNumber2TextView = findViewById(R.id.phoneNumber2);
        bloodType2TextView = findViewById(R.id.bloodType2);
        organType2TextView = findViewById(R.id.organType2);

        userName3TextView = findViewById(R.id.userName3);
        phoneNumber3TextView = findViewById(R.id.phoneNumber3);
        hospitalNameTextView = findViewById(R.id.hospitalName);
        hospitalAddressTextView = findViewById(R.id.hospitalAddress);

        appointmentDateTimeTextView = findViewById(R.id.appointmentDateTime);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            CollectionReference usersRef = db.collection("users");

            usersRef.document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {
                        CollectionReference appointmentRef = db.collection("appointment");

                        String fullName = userDocument.getString("fullName");
                        String icNumber = userDocument.getString("icNumber");

                        Query donorQuery = appointmentRef
                                .whereEqualTo("donorName", fullName)
                                .whereEqualTo("donorICNumber", icNumber);

                        Query patientQuery = appointmentRef
                                .whereEqualTo("patientName", fullName)
                                .whereEqualTo("patientICNumber", icNumber);

                        Task<QuerySnapshot> donorTask = donorQuery.get();
                        Task<QuerySnapshot> patientTask = patientQuery.get();

                        Tasks.whenAllComplete(donorTask, patientTask).addOnCompleteListener(taskResults -> {
                            if (taskResults.isSuccessful()) {
                                boolean donorQueryHasResults = !donorTask.getResult().getDocuments().isEmpty();
                                boolean patientQueryHasResults = !patientTask.getResult().getDocuments().isEmpty();

                                if (donorQueryHasResults || patientQueryHasResults) {
                                    DocumentSnapshot appointmentDocument;
                                    if (donorQueryHasResults) {
                                        appointmentDocument = donorTask.getResult().getDocuments().get(0);
                                    } else {
                                        appointmentDocument = patientTask.getResult().getDocuments().get(0);
                                    }

                                    String appointmentDateTime = appointmentDocument.getString("appointmentDate");
                                    String userName1 = appointmentDocument.getString("doctorName");
                                    String identityCard1 = appointmentDocument.getString("donorICNumber");
                                    String phoneNumber1 = appointmentDocument.getString("doctorPhoneNumber");
                                    String bloodType1 = appointmentDocument.getString("donorBloodType");
                                    String organType1 = appointmentDocument.getString("donorOrganType");

                                    String userName2 = appointmentDocument.getString("patientName");
                                    String identityCard2 = appointmentDocument.getString("patientICNumber");
                                    String phoneNumber2 = appointmentDocument.getString("patientPhoneNumber");
                                    String bloodType2 = appointmentDocument.getString("patientBloodType");
                                    String organType2 = appointmentDocument.getString("patientOrganType");

                                    String userName3 = appointmentDocument.getString("doctorName");
                                    String phoneNumber3 = appointmentDocument.getString("doctorPhoneNumber");
                                    String hospitalName = appointmentDocument.getString("doctorHospitalName");
                                    String hospitalAddress = appointmentDocument.getString("doctorHospitalAddress");

                                    appointmentDateTimeTextView.setText(appointmentDateTime);

                                    userName1TextView.setText(userName1);
                                    identityCard1TextView.setText(identityCard1);
                                    phoneNumber1TextView.setText(phoneNumber1);
                                    bloodType1TextView.setText(bloodType1);
                                    organType1TextView.setText(organType1);

                                    userName2TextView.setText(userName2);
                                    identityCard2TextView.setText(identityCard2);
                                    phoneNumber2TextView.setText(phoneNumber2);
                                    bloodType2TextView.setText(bloodType2);
                                    organType2TextView.setText(organType2);

                                    userName3TextView.setText(userName3);
                                    phoneNumber3TextView.setText(phoneNumber3);
                                    hospitalNameTextView.setText(hospitalName);
                                    hospitalAddressTextView.setText(hospitalAddress);

                                } else {
                                    Log.d(TAG, "No matching appointment found");

                                    appointmentDateTimeTextView.setVisibility(View.GONE);

                                    userName1TextView.setVisibility(View.GONE);
                                    identityCard1TextView.setVisibility(View.GONE);
                                    phoneNumber1TextView.setVisibility(View.GONE);
                                    bloodType1TextView.setVisibility(View.GONE);
                                    organType1TextView.setVisibility(View.GONE);

                                    userName2TextView.setVisibility(View.GONE);
                                    identityCard2TextView.setVisibility(View.GONE);
                                    phoneNumber2TextView.setVisibility(View.GONE);
                                    bloodType2TextView.setVisibility(View.GONE);
                                    organType2TextView.setVisibility(View.GONE);

                                    userName3TextView.setVisibility(View.GONE);
                                    phoneNumber3TextView.setVisibility(View.GONE);
                                    hospitalNameTextView.setVisibility(View.GONE);
                                    hospitalAddressTextView.setVisibility(View.GONE);

                                    TextView noAppointmentNoteTextView = findViewById(R.id.noAppointmentNote);
                                    noAppointmentNoteTextView.setVisibility(View.VISIBLE);

                                    CardView appMainLayout1 = findViewById(R.id.appMainLayout1);
                                    CardView appMainLayout2 = findViewById(R.id.appMainLayout2);
                                    CardView appMainLayout3 = findViewById(R.id.appMainLayout3);

                                    appMainLayout1.setVisibility(View.GONE);
                                    appMainLayout2.setVisibility(View.GONE);
                                    appMainLayout3.setVisibility(View.GONE);

                                    TextView textViewDonator = findViewById(R.id.textViewDonator);
                                    TextView textViewPatient = findViewById(R.id.textViewPatient);
                                    TextView textViewDoctor = findViewById(R.id.textViewDoctor);

                                    textViewDonator.setVisibility(View.GONE);
                                    textViewPatient.setVisibility(View.GONE);
                                    textViewDoctor.setVisibility(View.GONE);
                                }
                            } else {
                                Log.w(TAG, "Error getting appointment documents", taskResults.getException());
                            }
                        });
                    } else {
                        Log.d(TAG, "No such user document");
                    }
                } else {
                    Log.w(TAG, "Error getting user document", task.getException());
                }
            });
        }
    }
}
