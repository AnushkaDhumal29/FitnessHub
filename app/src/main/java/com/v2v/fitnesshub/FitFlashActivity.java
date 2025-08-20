package com.v2v.fitnesshub;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FitFlashActivity extends AppCompatActivity {

    private TextView exerciseText, scoreText, levelText;
    private ProgressBar timerBar;
    private List<Button> buttons;
    private FrameLayout confettiContainer;

    private List<String> allExercises = Arrays.asList("Push-up", "Jump", "Squat", "Lunge", "Burpee", "Plank");
    private List<String> currentExercises = new ArrayList<>();
    private String correctExercise = "";
    private int score = 0;
    private int level = 1;
    private long timePerRound = 3000;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitflash_activity);

        // Bind views
        exerciseText = findViewById(R.id.exerciseText);
        scoreText = findViewById(R.id.scoreText);
        levelText = findViewById(R.id.levelText);
        timerBar = findViewById(R.id.timerBar);
        confettiContainer = findViewById(R.id.confettiContainer);

        buttons = Arrays.asList(
                findViewById(R.id.button1),
                findViewById(R.id.button2),
                findViewById(R.id.button3),
                findViewById(R.id.button4),
                findViewById(R.id.button5),
                findViewById(R.id.button6)
        );

        for (Button btn : buttons) {
            btn.setOnClickListener(v -> checkAnswer((Button) v));
        }

        startNewRound();
    }

    private void startNewRound() {
        // Determine exercises count
        int exercisesCount;
        if (score < 5) exercisesCount = 3;
        else if (score < 10) exercisesCount = 4;
        else if (score < 15) exercisesCount = 5;
        else exercisesCount = 6;

        currentExercises = new ArrayList<>(allExercises.subList(0, exercisesCount));

        Random random = new Random();
        correctExercise = currentExercises.get(random.nextInt(currentExercises.size()));
        exerciseText.setText("Do: " + correctExercise + "!");

        // Shuffle and assign buttons
        List<String> shuffled = new ArrayList<>(currentExercises);
        Collections.shuffle(shuffled);

        for (int i = 0; i < buttons.size(); i++) {
            Button btn = buttons.get(i);
            if (i < shuffled.size()) {
                btn.setText(shuffled.get(i));
                btn.setVisibility(Button.VISIBLE);
                btn.setBackgroundColor(Color.parseColor("#6200EE"));
            } else {
                btn.setVisibility(Button.GONE);
            }
        }

        // Update score and level
        int previousLevel = level;
        level = (score / 5) + 1;
        scoreText.setText("Score: " + score);
        levelText.setText("Level: " + level);

        // Appreciate user if level increased
        if (level > previousLevel) {
            Toast.makeText(this, "🎉 Congrats! You reached Level " + level + "!", Toast.LENGTH_SHORT).show();
            animateLevelUp(levelText);
            showConfetti();
        }

        // Reduce time for higher levels
        timePerRound = Math.max(1000, 3000 - (level - 1) * 200); // min 1 sec

        // Timer
        timerBar.setMax((int) timePerRound);
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(timePerRound, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerBar.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Time's up!", Toast.LENGTH_SHORT).show();
                startNewRound();
            }
        }.start();
    }

    private void checkAnswer(Button selectedButton) {
        if (countDownTimer != null) countDownTimer.cancel();

        if (selectedButton.getText().toString().equals(correctExercise)) {
            score++;
            animateButton(selectedButton, true);
        } else {
            if (score > 0) score--;
            animateButton(selectedButton, false);
        }

        startNewRound();
    }

    private void animateButton(Button button, boolean correct) {
        int color = correct ? Color.GREEN : Color.RED;
        button.setBackgroundColor(color);

        ScaleAnimation anim = new ScaleAnimation(
                1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        button.startAnimation(anim);
    }

    private void animateLevelUp(TextView levelView) {
        // Scale animation
        ScaleAnimation scale = new ScaleAnimation(
                1f, 1.8f, 1f, 1.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(400);
        scale.setRepeatCount(1);
        scale.setRepeatMode(Animation.REVERSE);
        levelView.startAnimation(scale);

        // Color flash
        int originalColor = levelView.getCurrentTextColor();
        levelView.setTextColor(Color.YELLOW);
        levelView.postDelayed(() -> levelView.setTextColor(originalColor), 400);
    }

    private void showConfetti() {
        Random random = new Random();
        int[] colors = {Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED, Color.GREEN, Color.WHITE};
        final int confettiCount = 50 + level * 5; // more confetti for higher levels

        for (int i = 0; i < confettiCount; i++) {
            final TextView confetti = new TextView(this);
            String[] shapes = {"•", "★", "✦", "✨", "❇", "❉"};
            confetti.setText(shapes[random.nextInt(shapes.length)]);
            confetti.setTextSize(12 + random.nextInt(18));
            confetti.setTextColor(colors[random.nextInt(colors.length)]);
            confetti.setX(random.nextInt(confettiContainer.getWidth()));
            confetti.setY(-50);
            confetti.setAlpha(0.9f);

            confettiContainer.addView(confetti);

            confetti.animate()
                    .translationY(confettiContainer.getHeight() + 50)
                    .translationXBy((random.nextInt(200) - 100))
                    .rotationBy(random.nextInt(720))
                    .scaleX(0.5f + random.nextFloat())
                    .scaleY(0.5f + random.nextFloat())
                    .alpha(0f)
                    .setDuration(1200 + random.nextInt(1000))
                    .withEndAction(() -> confettiContainer.removeView(confetti))
                    .start();
        }
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }
}
