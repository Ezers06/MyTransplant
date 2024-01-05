package com.example.mytransplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class PatientAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> documentIds;

    public PatientAdapter(@NonNull Context context, int resource, @NonNull List<String> documentIds) {
        super(context, resource, documentIds);
        this.context = context;
        this.documentIds = documentIds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.item_patient, parent, false);
        }

        String documentId = documentIds.get(position);

        TextView userNameTextView = itemView.findViewById(R.id.userName1);
        TextView identityCardTextView = itemView.findViewById(R.id.identityCard1);
        TextView userAddressTextView = itemView.findViewById(R.id.userAddress1);
        TextView phoneNumberTextView = itemView.findViewById(R.id.phoneNumber1);
        TextView bloodTypeTextView = itemView.findViewById(R.id.bloodType1);
        TextView organTypeTextView = itemView.findViewById(R.id.organType1);

        fetchDetailsAndPopulateViews(documentId, userNameTextView, identityCardTextView,
                userAddressTextView, phoneNumberTextView, bloodTypeTextView, organTypeTextView);

        return itemView;
    }

    private void fetchDetailsAndPopulateViews(String documentId, TextView userNameTextView,
                                              TextView identityCardTextView, TextView userAddressTextView,
                                              TextView phoneNumberTextView, TextView bloodTypeTextView,
                                              TextView organTypeTextView) {
        FirebaseFirestore.getInstance().collection("request").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Retrieve data from Firestore
                                String fullName = document.getString("fullName");
                                String icNumber = document.getString("icNumber");
                                String address = document.getString("address");
                                String phoneNumber = document.getString("phoneNumber");
                                String bloodType = document.getString("bloodType");
                                String organType = document.getString("organRequest");

                                    userNameTextView.setText("Name: " + fullName);
                                identityCardTextView.setText("Identity Card: " + icNumber);
                                userAddressTextView.setText("Address: " + address);
                                phoneNumberTextView.setText("Phone: " + phoneNumber);
                                bloodTypeTextView.setText("Blood Type: " + bloodType);
                                organTypeTextView.setText("Organ: " + organType);
                            }

                        }

                    }
                });
    }

    public void updateUIWithDetails(int position, View itemView, DocumentSnapshot document) {
        TextView userNameTextView = itemView.findViewById(R.id.userName1);
        TextView identityCardTextView = itemView.findViewById(R.id.identityCard1);
        TextView userAddressTextView = itemView.findViewById(R.id.userAddress1);
        TextView phoneNumberTextView = itemView.findViewById(R.id.phoneNumber1);
        TextView bloodTypeTextView = itemView.findViewById(R.id.bloodType1);
        TextView organTypeTextView = itemView.findViewById(R.id.organType1);

        String fullName = document.getString("fullName");
        String icNumber = document.getString("icNumber");
        String address = document.getString("address");
        String phoneNumber = document.getString("phoneNumber");
        String bloodType = document.getString("bloodType");
        String organType = document.getString("organRequest");

        userNameTextView.setText("Name: " + fullName);
        identityCardTextView.setText("Identity Card: " + icNumber);
        userAddressTextView.setText("Address: " + address);
        phoneNumberTextView.setText("Phone: " + phoneNumber);
        bloodTypeTextView.setText("Blood Type: " + bloodType);
        organTypeTextView.setText("Organ: " + organType);
    }
}
