package com.v2v.fitnesshub;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials); // create this XML with FrameLayout container

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new TutorialsFragment())
                .commit();
    }
}

