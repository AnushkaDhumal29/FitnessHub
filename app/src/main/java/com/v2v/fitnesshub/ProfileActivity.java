package com.v2v.fitnesshub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private Button btnSave;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private TextInputEditText etFullName, etUsername, etDob, etHeight, etWeight, etGoal, etMedical;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private Uri selectedImageUri = null; // To store chosen image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        ivProfile = findViewById(R.id.ivProfile);
        btnSave = findViewById(R.id.btnSave);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etDob = findViewById(R.id.etDob);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etGoal = findViewById(R.id.etGoal);
        etMedical = findViewById(R.id.etMedical);

        // Camera Button
        findViewById(R.id.btnCamera).setOnClickListener(v -> openCamera());

        // Gallery Button
        findViewById(R.id.btnGallery).setOnClickListener(v -> openGallery());

        // Remove Pic Button
        findViewById(R.id.btnRemovePic).setOnClickListener(v -> {
            ivProfile.setImageResource(R.drawable.ic_person);
            selectedImageUri = null;
        });

        // Date of Birth Picker
        etDob.setOnClickListener(v -> openDatePicker());

        // Save Button
        btnSave.setOnClickListener(v -> saveProfile());
    }

    // Open Camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    // Open Gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    // Handle Image Selection

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_CAMERA) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivProfile.setImageBitmap(photo);

                    // Save temporarily in MediaStore and get URI
                    String path = MediaStore.Images.Media.insertImage(
                            getContentResolver(), photo,
                            "profile_pic_" + System.currentTimeMillis(), null);
                    if (path != null) selectedImageUri = Uri.parse(path);

                } else if (requestCode == REQUEST_GALLERY) {
                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                this.getContentResolver(), selectedImageUri);
                        ivProfile.setImageBitmap(bitmap);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Date Picker for DOB
    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            etDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
        }, year, month, day);

        datePicker.show();
    }

    // Save Profile -> store in SharedPreferences and go to MainScreenActivity
    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String goal = etGoal.getText().toString().trim();
        String medical = etMedical.getText().toString().trim();

        String gender = "";
        if (rbMale.isChecked()) gender = "Male";
        else if (rbFemale.isChecked()) gender = "Female";

        // Save everything in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fullName", fullName);
        editor.putString("username", username);
        editor.putString("dob", dob);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("goal", goal);
        editor.putString("medical", medical);
        editor.putString("gender", gender);

        if (selectedImageUri != null) {
            editor.putString("profileUri", selectedImageUri.toString()); // save as string
        }
        editor.apply();

        // Go to MainScreen
        Intent intent = new Intent(ProfileActivity.this, MainScreenActivity.class);
        startActivity(intent);
        finish();
    }
}