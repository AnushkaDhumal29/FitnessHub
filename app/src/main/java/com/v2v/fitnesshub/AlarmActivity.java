package com.v2v.fitnesshub;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextView tvMessage;
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tvMessage = findViewById(R.id.tvMessage);
        btnStop = findViewById(R.id.btnStop);

        String msg = getIntent().getStringExtra("msg");
        tvMessage.setText(msg);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_tone); // add alarm_tone.mp3 in res/raw
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        btnStop.setOnClickListener(v -> {
            mediaPlayer.stop();
            finish();
        });
    }
}
