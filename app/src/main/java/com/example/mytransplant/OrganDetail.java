package com.example.mytransplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class OrganDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_detail);

        LinearLayout brainLayout = findViewById(R.id.brainLayout);
        LinearLayout heartLayout = findViewById(R.id.heartLayout);
        LinearLayout kidneyLayout = findViewById(R.id.kidneyLayout);
        LinearLayout lungsLayout = findViewById(R.id.lungsLayout);
        LinearLayout liverLayout = findViewById(R.id.liverLayout);

        brainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrganDetail.this, InformationBrain.class));
            }
        });

        heartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrganDetail.this, InformationHeart.class));
            }
        });

        kidneyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrganDetail.this, InformationKidney.class));
            }
        });

        lungsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrganDetail.this, InformationLungs.class));
            }
        });

        liverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrganDetail.this, InformationLiver.class));
            }
        });
    }
}
