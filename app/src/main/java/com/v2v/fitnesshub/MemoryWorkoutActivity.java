package com.v2v.fitnesshub;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryWorkoutActivity extends AppCompatActivity {

    // UI
    private GridLayout gridLayout;
    private LinearLayout gameLayout;
    private LinearLayout gameOverLayout;
    private TextView scoreText, timerText, gameResultText;
    private Button restartButton, restartButtonOverlay;

    // Game state
    private final Handler handler = new Handler();
    private CountDownTimer countDownTimer;
    private int score = 0;
    private int pairsRemaining = 6;   // 3x4 grid => 6 pairs
    private boolean isBusy = false;   // lock while animating

    // Cards & mapping
    private final List<Integer> cardMap = new ArrayList<>(); // maps position -> imageIndex
    private final List<ImageView> cardViews = new ArrayList<>();
    private ImageView firstCard = null, secondCard = null;
    private int firstIdx = -1, secondIdx = -1;

    // Images (PUT THESE IN res/drawable/)
    private final int[] images = new int[]{
            R.drawable.ic_cycling,
            R.drawable.ic_jump_rope,
            R.drawable.ic_swimming,
            R.drawable.ic_meditation,
            R.drawable.ic_dumbbell,
            R.drawable.ic_running
    };
    private final int cardBack = R.drawable.card_back;

    // Grid config
    private static final int ROWS = 3;
    private static final int COLS = 4;
    private static final long ROUND_MILLIS = 60_000L; // 60 seconds
    private static final long FLIP_BACK_DELAY = 650L; // ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memoryworkout_activity); // <- use the XML with overlay

        bindViews();
        setupGridOnceMeasured();
        startNewGame();

        restartButton.setOnClickListener(v -> startNewGame());
        restartButtonOverlay.setOnClickListener(v -> startNewGame());
    }

    private void bindViews() {
        gameLayout = findViewById(R.id.gameLayout);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        gridLayout = findViewById(R.id.gridLayout);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        gameResultText = findViewById(R.id.gameResultText);
        restartButton = findViewById(R.id.restartButton);
        restartButtonOverlay = findViewById(R.id.restartButtonOverlay);

        gridLayout.setRowCount(ROWS);
        gridLayout.setColumnCount(COLS);
    }

    private void setupGridOnceMeasured() {
        // Wait until GridLayout is measured to size cards nicely
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        buildCardViews(); // create ImageViews sized to grid
                    }
                }
        );
    }

    private void buildCardViews() {
        gridLayout.removeAllViews();
        cardViews.clear();

        int gridW = gridLayout.getWidth();
        int gridH = gridLayout.getHeight();

        // Calculate square-ish card size with margins
        int hSpacing = dp(8);
        int vSpacing = dp(8);
        int cardW = (gridW - (COLS + 1) * hSpacing) / COLS;
        int cardH = (gridH - (ROWS + 1) * vSpacing) / ROWS;
        int size = Math.min(cardW, cardH);

        for (int i = 0; i < ROWS * COLS; i++) {
            ImageView card = new ImageView(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = size;
            lp.height = size;
            lp.setMargins(hSpacing, vSpacing, hSpacing, vSpacing);
            card.setLayoutParams(lp);
            card.setScaleType(ImageView.ScaleType.CENTER_CROP);
            card.setAdjustViewBounds(true);
            card.setBackgroundResource(cardBack);
            card.setImageDrawable(null); // show only back

            final int pos = i;
            card.setOnClickListener(v -> onCardClicked(pos));

            gridLayout.addView(card);
            cardViews.add(card);
        }
    }

    private void startNewGame() {
        // Reset overlays & buttons
        gameOverLayout.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);

        // Reset state
        score = 0;
        pairsRemaining = images.length;
        isBusy = false;
        firstCard = secondCard = null;
        firstIdx = secondIdx = -1;
        updateScore();

        // Build mapping: 6 image indexes, each twice → 12 positions
        cardMap.clear();
        for (int i = 0; i < images.length; i++) {
            cardMap.add(i);
            cardMap.add(i);
        }
        Collections.shuffle(cardMap);

        // Reset cards visuals
        for (ImageView card : cardViews) {
            card.setVisibility(View.VISIBLE);
            card.setBackgroundResource(cardBack);
            card.setImageDrawable(null);
            card.setEnabled(true);
        }

        // Start timer
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(ROUND_MILLIS, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long s = millisUntilFinished / 1000L;
                timerText.setText("⏳ Time: " + s + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("⏳ Time: 0s");
                endGame(false); // time’s up
            }
        };
        countDownTimer.start();
    }

    private void onCardClicked(int position) {
        if (isBusy) return;                // block while animating
        ImageView card = cardViews.get(position);
        if (card.getVisibility() != View.VISIBLE) return;

        int imgIndex = cardMap.get(position);
        revealWithFlip(card, images[imgIndex]);

        if (firstCard == null) {
            firstCard = card;
            firstIdx = imgIndex;
            return;
        }

        if (card == firstCard) return; // same card tapped twice

        secondCard = card;
        secondIdx = imgIndex;

        isBusy = true;

        // Check match after short delay so player can see second card
        handler.postDelayed(() -> {
            if (firstIdx == secondIdx) {
                // Match!
                score += 10;
                updateScore();
                animateMatchAndHide(firstCard, secondCard);
                pairsRemaining--;
                if (pairsRemaining == 0) {
                    endGame(true);
                }
            } else {
                // Not a match → flip both back
                hideWithFlip(firstCard);
                hideWithFlip(secondCard);
            }
            // reset selection
            firstCard = secondCard = null;
            firstIdx = secondIdx = -1;
            isBusy = false;
        }, 450);
    }

    private void revealWithFlip(ImageView card, int imageRes) {
        Animation flipOut = AnimationUtils.loadAnimation(this, R.anim.flip_out);
        Animation flipIn = AnimationUtils.loadAnimation(this, R.anim.flip_in);

        flipOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Front face
                card.setBackgroundColor(0xFF2C2C2C); // remove back graphic
                card.setImageResource(imageRes);
                card.startAnimation(flipIn);
            }
        });
        card.startAnimation(flipOut);
    }

    private void hideWithFlip(ImageView card) {
        Animation flipOut = AnimationUtils.loadAnimation(this, R.anim.flip_out);
        Animation flipIn = AnimationUtils.loadAnimation(this, R.anim.flip_in);

        flipOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Back face
                card.setImageDrawable(null);
                card.setBackgroundResource(cardBack);
                card.startAnimation(flipIn);
            }
        });
        // small delay so mismatched pair is visible briefly
        handler.postDelayed(() -> card.startAnimation(flipOut), FLIP_BACK_DELAY);
    }

    private void animateMatchAndHide(ImageView a, ImageView b) {
        // simple fade + scale for matched pair
        a.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.0f).setDuration(180).withEndAction(() -> {
            a.setVisibility(View.INVISIBLE);
            a.setScaleX(1f);
            a.setScaleY(1f);
            a.setAlpha(1f);
        }).start();

        b.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.0f).setDuration(180).withEndAction(() -> {
            b.setVisibility(View.INVISIBLE);
            b.setScaleX(1f);
            b.setScaleY(1f);
            b.setAlpha(1f);
        }).start();
    }

    private void endGame(boolean won) {
        // stop timer & disable taps
        if (countDownTimer != null) countDownTimer.cancel();
        for (ImageView card : cardViews) card.setEnabled(false);

        gameResultText.setText(won ? "🎉 You Won!" : "⌛ Time’s Up!");
        gameOverLayout.setVisibility(View.VISIBLE);
        restartButton.setVisibility(View.GONE); // bottom button hidden during overlay
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
