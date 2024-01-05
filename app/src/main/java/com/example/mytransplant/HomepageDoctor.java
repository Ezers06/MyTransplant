package com.example.mytransplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomepageDoctor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_doctor);

        CardView profileCard = findViewById(R.id.viewProfileCard);
        CardView viewDonorsCard = findViewById(R.id.viewDonorsCard);
        CardView viewPatientCard = findViewById(R.id.viewPatientCard);
        CardView transplantMatchCard = findViewById(R.id.transplantMatchCard);

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageDoctor.this, ProfileDoctor.class);
                startActivity(intent);
            }
        });

        viewDonorsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageDoctor.this, showDonate.class);
                startActivity(intent);
            }
        });

        viewPatientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageDoctor.this, showPatient.class);
                startActivity(intent);
            }
        });

        transplantMatchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageDoctor.this, Assign.class);
                startActivity(intent);
            }
        });
    }
}