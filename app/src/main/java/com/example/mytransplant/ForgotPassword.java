package com.example.mytransplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;

public class ForgotPassword extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final EditText etEmail = findViewById(R.id.etForgot);
        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkIfEmailExists(email);
            }
        });
    }

    private void checkIfEmailExists(final String email) {

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult() != null && !task.getResult().isEmpty()) {

                                sendPasswordResetEmail(email);
                            } else {

                                Toast.makeText(ForgotPassword.this, "Email not found. Please check your email address.", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(ForgotPassword.this, "Error checking email existence. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendPasswordResetEmail(final String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(ForgotPassword.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(ForgotPassword.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
