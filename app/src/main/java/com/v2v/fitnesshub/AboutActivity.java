package com.v2v.fitnesshub;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        VideoView videoView = findViewById(R.id.aboutVideo);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.about);
        videoView.setVideoURI(video);

        // Just restart or stay on the same activity after completion
        videoView.setOnCompletionListener(mp -> {
            // Option 1: Loop video
            videoView.start();

            // Option 2: Do nothing, stay on screen
            // (remove the line above if you want the video to stop once finished)
        });

        videoView.start();
    }
}
