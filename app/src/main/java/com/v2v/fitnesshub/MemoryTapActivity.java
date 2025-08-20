package com.v2v.fitnesshub;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class MemoryTapActivity extends AppCompatActivity {

    private GridLayout gridNumbers;
    private Button btnStart;
    private TextView tvScore, tvLevel, tvTimer;
    private FrameLayout backgroundGlow;

    private ArrayList<Integer> sequence = new ArrayList<>();
    private int userIndex = 0;
    private int score = 0;
    private int level = 1;
    private CountDownTimer countdownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorytap_activity);

        gridNumbers = findViewById(R.id.gridNumbers);
        btnStart = findViewById(R.id.btnStart);
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        tvTimer = findViewById(R.id.tvTimer);
        backgroundGlow = findViewById(R.id.backgroundGlow);

        startBackgroundGlow();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    // Animated background glow
    private void startBackgroundGlow() {
        final int colorFrom = Color.parseColor("#121212");
        final int colorTo = Color.parseColor("#1E88E5");

        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] toggle = {0};

        Runnable glowRunnable = new Runnable() {
            @Override
            public void run() {
                if (toggle[0] == 0) {
                    backgroundGlow.setBackgroundColor(colorTo);
                    toggle[0] = 1;
                } else {
                    backgroundGlow.setBackgroundColor(colorFrom);
                    toggle[0] = 0;
                }
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(glowRunnable);
    }

    private void startGame() {
        btnStart.setEnabled(false);
        sequence.clear();
        userIndex = 0;
        gridNumbers.removeAllViews();
        tvLevel.setText("Level: " + level);

        Random random = new Random();
        for (int i = 0; i < level + 2; i++) {
            sequence.add(random.nextInt(9) + 1);
        }

        createButtons();
        showSequence();
    }

    private void createButtons() {
        for (int i = 1; i <= 9; i++) {
            final Button btn = new Button(this);
            btn.setText(String.valueOf(i));
            btn.setTextSize(24f);
            btn.setTextColor(Color.WHITE);
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.neon_button));

            final int num = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkNumber(num);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 200;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);
            btn.setLayoutParams(params);

            gridNumbers.addView(btn);
        }
    }

    private void showSequence() {
        long delay = 500;
        Handler handler = new Handler(Looper.getMainLooper());
        for (final int num : sequence) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    highlightNumber(num);
                }
            }, delay);
            delay += 800;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetButtons();
                startCountdown();
            }
        }, delay);
    }

    private void highlightNumber(int num) {
        for (int i = 0; i < gridNumbers.getChildCount(); i++) {
            Button btn = (Button) gridNumbers.getChildAt(i);
            if (Integer.parseInt(btn.getText().toString()) == num) {
                btn.setScaleX(1.2f);
                btn.setScaleY(1.2f);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn.setScaleX(1f);
                        btn.setScaleY(1f);
                    }
                }, 400);
            }
        }
    }

    private void resetButtons() {
        for (int i = 0; i < gridNumbers.getChildCount(); i++) {
            Button btn = (Button) gridNumbers.getChildAt(i);
            btn.setScaleX(1f);
            btn.setScaleY(1f);
        }
    }

    private void flashButton(Button btn, int color) {
        btn.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void shakeButton(Button btn) {
        btn.animate().translationXBy(25).translationXBy(-25).setDuration(500).start();
    }

    // Green flash for each correct tap
    private void onCorrectTap(Button btn) {
        flashButton(btn, Color.GREEN);
        btn.setScaleX(1.2f);
        btn.setScaleY(1.2f);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.setScaleX(1f);
                btn.setScaleY(1f);
            }
        }, 300);
    }

    // Smooth appreciation animation after full correct sequence
    private void showAppreciation() {
        String[] emojis = {"✨", "🎉", "💫", "⭐"};
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            final TextView emojiView = new TextView(this);
            emojiView.setText(emojis[random.nextInt(emojis.length)]);
            emojiView.setTextSize(32 + random.nextInt(16));
            emojiView.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));

            emojiView.setX(gridNumbers.getWidth() / 2f - 40 + random.nextInt(80) - 40);
            emojiView.setY(gridNumbers.getHeight() / 2f - 40);

            backgroundGlow.addView(emojiView);

            emojiView.animate()
                    .translationYBy(-300 - random.nextInt(100))
                    .translationXBy(random.nextInt(120) - 60)
                    .alpha(0f)
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(1200 + random.nextInt(400))
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            backgroundGlow.removeView(emojiView);
                        }
                    })
                    .start();
        }

        Toast.makeText(this, "Awesome! Sequence Completed 🎉", Toast.LENGTH_SHORT).show();
    }

    private void checkNumber(int num) {
        Button btn = null;
        for (int i = 0; i < gridNumbers.getChildCount(); i++) {
            Button b = (Button) gridNumbers.getChildAt(i);
            if (Integer.parseInt(b.getText().toString()) == num) {
                btn = b;
                break;
            }
        }
        if (btn == null) return;

        if (num == sequence.get(userIndex)) {
            onCorrectTap(btn); // green flash
            userIndex++;

            if (userIndex == sequence.size()) {
                score++;
                level++;
                tvScore.setText("Score: " + score);
                showAppreciation(); // celebrate full sequence
                if (countdownTimer != null) countdownTimer.cancel();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                }, 1500);
            }
        } else {
            flashButton(btn, Color.RED);
            shakeButton(btn);
            if (countdownTimer != null) countdownTimer.cancel();
            Toast.makeText(this, "Wrong! Try Again.", Toast.LENGTH_SHORT).show();
            resetGame(); // restart on wrong guess
        }
    }

    private void startCountdown() {
        if (countdownTimer != null) countdownTimer.cancel();

        countdownTimer = new CountDownTimer(sequence.size() * 3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                Toast.makeText(MemoryTapActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                resetGame();
            }
        }.start();
    }

    private void resetGame() {
        userIndex = 0;
        level = 1;
        score = 0;
        tvScore.setText("Score: " + score);
        tvLevel.setText("Level: " + level);
        btnStart.setEnabled(true);
        tvTimer.setText("00:00");
    }
}
