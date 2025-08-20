package com.v2v.fitnesshub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BreathingBubbleActivity extends AppCompatActivity {

    private ImageView breathingBubble;
    private TextView breathingText;
    private Handler handler = new Handler();
    private int cycleIndex = 0;

    private String[] phases = {"Inhale...", "Hold...", "Exhale...", "Hold..."};
    private int[] durations = {4000, 2000, 4000, 2000}; // ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breathingbubble_activity);

        breathingBubble = findViewById(R.id.breathingBubble);
        breathingText = findViewById(R.id.breathingText);

        startBreathingCycle();
    }

    private void startBreathingCycle() {
        runPhase();
    }

    private void runPhase() {
        breathingText.setText(phases[cycleIndex]);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX, scaleY;

        if (phases[cycleIndex].equals("Inhale...")) {
            scaleX = ObjectAnimator.ofFloat(breathingBubble, "scaleX", 1f, 1.5f);
            scaleY = ObjectAnimator.ofFloat(breathingBubble, "scaleY", 1f, 1.5f);
        } else if (phases[cycleIndex].equals("Exhale...")) {
            scaleX = ObjectAnimator.ofFloat(breathingBubble, "scaleX", 1.5f, 1f);
            scaleY = ObjectAnimator.ofFloat(breathingBubble, "scaleY", 1.5f, 1f);
        } else {
            // Hold phase → no scaling
            scaleX = ObjectAnimator.ofFloat(breathingBubble, "scaleX", breathingBubble.getScaleX(), breathingBubble.getScaleX());
            scaleY = ObjectAnimator.ofFloat(breathingBubble, "scaleY", breathingBubble.getScaleY(), breathingBubble.getScaleY());
        }

        scaleX.setDuration(durations[cycleIndex]);
        scaleY.setDuration(durations[cycleIndex]);
        scaleX.setInterpolator(new LinearInterpolator());
        scaleY.setInterpolator(new LinearInterpolator());

        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.start();

        handler.postDelayed(() -> {
            cycleIndex = (cycleIndex + 1) % phases.length;
            runPhase();
        }, durations[cycleIndex]);
    }
}
