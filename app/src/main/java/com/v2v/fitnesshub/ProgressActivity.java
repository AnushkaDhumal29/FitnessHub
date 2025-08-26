package com.v2v.fitnesshub;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ProgressActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private EditText etPostText;
    private Uri imageUri = null;

    private static final int CAMERA_PERMISSION_CODE = 101;

    // ---- Camera Launcher ----
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (imageUri != null) {
                        ivPreview.setImageURI(imageUri);  // Full-resolution image
                    } else if (result.getData() != null && result.getData().getExtras() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        ivPreview.setImageBitmap(bitmap); // Fallback: thumbnail
                    }
                }
            });

    // ---- Gallery Launcher ----
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ivPreview.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_activity);

        ivPreview = findViewById(R.id.ivPreview);
        etPostText = findViewById(R.id.etPostText);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnGallery = findViewById(R.id.btnGallery);
        Button btnShare = findViewById(R.id.btnShare);

        // 📷 Camera button
        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        // 🖼️ Gallery button
        btnGallery.setOnClickListener(v -> openGallery());

        // 📤 Share button
        btnShare.setOnClickListener(v -> sharePost());

        // ✅ Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_progress); // highlight current tab

        // BottomNavigationView item selection
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this,MainScreenActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_plan) {
                Intent intent = new Intent(this,DietPlanActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_tutorials) {
                Toast.makeText(this, "Tutorials clicked", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    // ---- Open Camera ----
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    // ---- Open Gallery ----
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // ---- Share Post ----
    private void sharePost() {
        String text = etPostText.getText().toString().trim();

        if (imageUri == null && text.isEmpty()) {
            Toast.makeText(this, "Add image or text to share!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Save post locally
        savePostLocally(text, imageUri);

        // 🔗 Share outside app
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (imageUri != null) {
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // ---- Save post locally ----
    private void savePostLocally(String text, Uri imageUri) {
        long timestamp = System.currentTimeMillis();

        try {
            SharedPreferences prefs = getSharedPreferences("MyPosts", MODE_PRIVATE);
            String existingPosts = prefs.getString("posts", "[]");

            JSONArray postArray = new JSONArray(existingPosts);

            JSONObject newPost = new JSONObject();
            newPost.put("text", text);
            newPost.put("image", imageUri != null ? imageUri.toString() : null);
            newPost.put("time", timestamp);

            postArray.put(newPost);

            prefs.edit().putString("posts", postArray.toString()).apply();

            Toast.makeText(this, "Post saved locally!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving post!", Toast.LENGTH_SHORT).show();
        }
    }
}
