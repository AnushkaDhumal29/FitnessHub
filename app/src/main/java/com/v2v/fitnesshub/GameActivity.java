package com.v2v.fitnesshub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    LinearLayout gameBreathing, gameCatch, gameMemory,gameFitflash, gameMemoryTap, gameQuiz;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_activity);

        // Initialize all game layouts
        gameBreathing = findViewById(R.id.game_breathing);
        gameCatch = findViewById(R.id.game_catch);
        gameMemory = findViewById(R.id.game_memory);
        gameFitflash = findViewById(R.id.game_fitFlash);
        gameMemoryTap = findViewById(R.id.game_memorytap);
        gameQuiz = findViewById(R.id.game_quiz);

        // Set click listeners
        gameBreathing.setOnClickListener(v -> {
            Toast.makeText(this, "Breathing Bubble Game coming soon!", Toast.LENGTH_SHORT).show();
            // Example if you had activity:
             startActivity(new Intent(this, BreathingBubbleActivity.class));
        });

        gameCatch.setOnClickListener(v -> {
            Toast.makeText(this, "Catch and Tap Game coming soon!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CatchAndTapActivity.class));
        });

        gameMemory.setOnClickListener(v -> {
            Toast.makeText(this, "Memory Workout coming soon!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MemoryWorkoutActivity.class));
        });

        gameFitflash.setOnClickListener(v -> {
            Toast.makeText(this, "FitFlash coming soon!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FitFlashActivity.class));
        });

        gameMemoryTap.setOnClickListener(v -> {
            Toast.makeText(this, "Memory Tap Game coming soon!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MemoryTapActivity.class));
        });

        gameQuiz.setOnClickListener(v -> {
            Toast.makeText(this, "Quiz Game coming soon!", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this, QuizGameActivity.class));
        });
    }
}
