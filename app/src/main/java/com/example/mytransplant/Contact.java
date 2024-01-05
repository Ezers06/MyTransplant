package com.example.mytransplant;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        TextView email1TextView = findViewById(R.id.email1);

        setUnderline(email1TextView);

        email1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = "nurameerrizal@gmail.com";
                composeEmail(new String[]{emailAddress});
            }
        });

        TextView email2TextView = findViewById(R.id.email2);

        setUnderline(email2TextView);

        email2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = "zairilhafizzie05@gmail.com";
                composeEmail(new String[]{emailAddress});
            }
        });

        // Find the TextView representing the phone number
        TextView phoneNum1TextView = findViewById(R.id.phoneNum1);

        setUnderline(phoneNum1TextView);


        // Set an OnClickListener for the phone number TextView
        phoneNum1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, open the phone dialer with the specified phone number
                String phoneNumber = "011-3706475"; // Change this to the actual phone number
                openPhoneDialer(phoneNumber);
            }
        });

        // Find the TextView representing the second phone number
        TextView phoneNum2TextView = findViewById(R.id.phoneNum2);

        setUnderline(phoneNum2TextView);

        // Set an OnClickListener for the second phone number TextView
        phoneNum2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, open the phone dialer with the specified phone number
                String phoneNumber = "013-2543740"; // Change this to the actual phone number
                openPhoneDialer(phoneNumber);
            }
        });
    }

    private void setUnderline(TextView textView) {
        // Create a SpannableString with an UnderlineSpan
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        // Set the SpannableString to the TextView
        textView.setText(content);
    }

    // Method to open the phone dialer with the specified phone number
    private void openPhoneDialer(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
