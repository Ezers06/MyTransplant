package com.example.mytransplant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;


public class Homepage extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView textViewUsername;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupProfileCard();
        setupOrganDetailCard();
        setupDonateCard();
        setupRequestCard();
        setupAboutCard();
        setupContactCard();

        textViewUsername = findViewById(R.id.textView);
        fetchAndDisplayUsername();
    }

    private void fetchAndDisplayUsername() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {

            String userId = currentUser.getUid();

            firestore.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    String username = document.getString("username");

                                    textViewUsername.setText("Hi, " + username);
                                }

                            }

                        }
                    });
        }

    }

    private void setupProfileCard() {
        CardView profileCard = findViewById(R.id.profileCard);
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
    }

    private void openProfile() {
        Intent profileIntent = new Intent(Homepage.this, Profile.class);
        startActivity(profileIntent);

    }

    private void setupContactCard() {
        CardView contactCard = findViewById(R.id.contactCard);
        contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContact();
            }
        });
    }

    private void openContact() {
        Intent contactIntent = new Intent(Homepage.this, Contact.class);
        startActivity(contactIntent);

    }

    private void setupAboutCard() {
        CardView aboutCard = findViewById(R.id.aboutCard);
        aboutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAbout();
            }
        });
    }

    private void openAbout() {
        Intent aboutIntent = new Intent(Homepage.this, About.class);
        startActivity(aboutIntent);

    }

    private void setupRequestCard() {
        CardView requestCard = findViewById(R.id.requestCard);
        requestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequest();
            }
        });
    }

    private void openRequest() {
        Intent requestIntent = new Intent(Homepage.this, Request.class);
        startActivity(requestIntent);

    }

    private void setupDonateCard() {
        CardView donateCard = findViewById(R.id.donateCard);
        donateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDonate();
            }
        });
    }

    private void openDonate() {
        Intent donateIntent = new Intent(Homepage.this, Donate.class);
        startActivity(donateIntent);

    }

    private void setupOrganDetailCard() {
        CardView organDetailCard = findViewById(R.id.infoCard);
        organDetailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrganDetail();
            }
        });
    }

    private void openOrganDetail() {
        Intent organDetailIntent = new Intent(Homepage.this, OrganDetail.class);
        startActivity(organDetailIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
