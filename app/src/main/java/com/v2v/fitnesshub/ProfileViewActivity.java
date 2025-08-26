package com.v2v.fitnesshub;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileViewActivity extends AppCompatActivity {

    private ImageView ivProfileview;
    private TextView tvName, tvUsername, tvGender, tvDob, tvHeight, tvWeight, tvGoal, tvMedical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view); // Make sure this file is present

        // IDs MUST match those in activity_profile_view.xml (provided below)
        ivProfileview = findViewById(R.id.ivProfileView);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvDob = findViewById(R.id.tvDob);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvGoal = findViewById(R.id.tvGoal);
        tvMedical = findViewById(R.id.tvMedical);
        tvGender = findViewById(R.id.tvGender);

        SharedPreferences preferences = getSharedPreferences("user_profile", MODE_PRIVATE);

        tvName.setText("Full Name: " + preferences.getString("fullName", ""));
        tvUsername.setText("Username: " + preferences.getString("username", ""));
        tvDob.setText("DOB: " + preferences.getString("dob", ""));
        tvHeight.setText("Height: " + preferences.getString("height", ""));
        tvWeight.setText("Weight: " + preferences.getString("weight", ""));
        tvGoal.setText("Goal: " + preferences.getString("goal", ""));
        tvMedical.setText("Medical Condition: " + preferences.getString("medical", ""));
        tvGender.setText("Gender: " + preferences.getString("gender", ""));

        String profileUri = preferences.getString("profileUri", null);

        if (profileUri != null) {
            try {
                Uri uri = Uri.parse(profileUri);
                ivProfileview.setImageURI(uri);

                // Fallback if URI is invalid or file was deleted
                if (ivProfileview.getDrawable() == null) {
                    ivProfileview.setImageResource(R.drawable.ic_person);
                }
            } catch (Exception e) {
                ivProfileview.setImageResource(R.drawable.ic_person);
                Toast.makeText(this, "Error loading profile image", Toast.LENGTH_SHORT).show();
            }
        } else {
            ivProfileview.setImageResource(R.drawable.ic_person);
        }


    }
}
