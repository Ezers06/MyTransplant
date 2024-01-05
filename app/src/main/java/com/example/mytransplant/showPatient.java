package com.example.mytransplant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class showPatient extends AppCompatActivity {

    private ListView listView;
    private PatientAdapter adapter;
    private List<String> documentIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);

        listView = findViewById(R.id.listViewPatient);
        documentIds = new ArrayList<>();
        adapter = new PatientAdapter(this, R.layout.item_patient, documentIds);
        listView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("request")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            documentIds.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                documentIds.add(documentId);
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDocumentId = documentIds.get(position);
            fetchAndDisplayDetails(position, view, selectedDocumentId);
        });
    }

    private void fetchAndDisplayDetails(int position, View itemView, String documentId) {
        FirebaseFirestore.getInstance().collection("request").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {

                                adapter.updateUIWithDetails(position, itemView, document);
                            }
                        }

                    }
                });
    }
}