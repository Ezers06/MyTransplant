package com.example.mytransplant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Profile extends AppCompatActivity {

    private TextView textViewUsername;
    private TextView textViewEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);

        Button accountButton = findViewById(R.id.account);
        Button personalButton = findViewById(R.id.personal);
        Button statusButton = findViewById(R.id.status);
        Button logoutButton = findViewById(R.id.logout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(Profile.this, AccountSettings.class);
                startActivity(accountIntent);
            }
        });

        personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent personalIntent = new Intent(Profile.this, PersonalInformation.class);
                startActivity(personalIntent);
            }
        });

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent statusIntent = new Intent(Profile.this, Status.class);
                startActivity(statusIntent);
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();

            retrieveUserDataFromFirestore(userUid);
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        logoutUser();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void logoutUser() {
        mAuth.signOut();

        Intent loginIntent = new Intent(Profile.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void retrieveUserDataFromFirestore(String userUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                String username = document.getString("username");
                                String email = document.getString("email");

                                textViewUsername.setText(username);
                                textViewEmail.setText(email);
                            }

                        }

                    }
                });
    }
}


