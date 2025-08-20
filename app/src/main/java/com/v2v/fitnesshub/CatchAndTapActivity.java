package com.v2v.fitnesshub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CatchAndTapActivity extends AppCompatActivity {

    private FrameLayout gameLayout;
    private ImageView ball;
    private TextView scoreText;

    private int score = 0;
    private Handler handler = new Handler();
    private Random random = new Random();

    private int layoutWidth, layoutHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catchandtap_activity);

        gameLayout = findViewById(R.id.game_layout);
        ball = findViewById(R.id.ball);
        scoreText = findViewById(R.id.score_text);

        gameLayout.post(() -> {
            layoutWidth = gameLayout.getWidth();
            layoutHeight = gameLayout.getHeight();
            startGame();
        });

        ball.setOnClickListener(v -> {
            score++;
            scoreText.setText("Score: " + score);
            showAppreciation();
            moveBall();
        });
    }

    private void startGame() {
        handler.postDelayed(moveRunnable, 1000);
    }

    private Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            moveBall();
            handler.postDelayed(this, 1500);
        }
    };

    private void moveBall() {
        if (layoutWidth == 0 || layoutHeight == 0) return;

        int ballSize = ball.getWidth();
        int maxX = layoutWidth - ballSize;
        int maxY = layoutHeight - ballSize;

        int randomX = random.nextInt(Math.max(maxX, 1));
        int randomY = random.nextInt(Math.max(maxY, 1));

        ball.setX(randomX);
        ball.setY(randomY);
    }

    private void showAppreciation() {
        String message;
        if (score % 5 == 0) {
            message = "🎉 Amazing!";
        } else if (score % 3 == 0) {
            message = "👍 Nice!";
        } else {
            message = "😎 Great!";
        }

        // Create TextView dynamically
        TextView popup = new TextView(this);
        popup.setText(message);
        popup.setTextSize(18f);
        popup.setGravity(Gravity.CENTER);
        popup.setTextColor(getRandomColor());

        // Position above the ball
        int x = (int) ball.getX();
        int y = (int) ball.getY() - 100;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = x;
        params.topMargin = Math.max(y, 0);
        popup.setLayoutParams(params);
        gameLayout.addView(popup);

        // Animate: move up, fade out, and scale
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(popup, "translationY", 0f, -150f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(popup, "alpha", 1f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(popup, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(popup, "scaleY", 0f, 1.2f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveUp, fadeOut, scaleX, scaleY);
        animatorSet.setDuration(1000);
        animatorSet.start();

        // Remove TextView after animation
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                gameLayout.removeView(popup);
            }
        });
    }

    private int getRandomColor() {
        // Random bright colors
        int[] colors = {
                Color.RED, Color.GREEN, Color.BLUE,
                Color.MAGENTA, Color.CYAN, Color.YELLOW
        };
        return colors[random.nextInt(colors.length)];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(moveRunnable);
    }
}
