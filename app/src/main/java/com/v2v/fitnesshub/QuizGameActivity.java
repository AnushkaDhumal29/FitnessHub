package com.v2v.fitnesshub;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizGameActivity extends AppCompatActivity {

    private TextView tvQuestion, tvScore;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;

    private List<Question> questionList;
    private int currentScore = 0;
    private Question currentQuestion;

    private Random random = new Random();
    private boolean isAnswered = false; // prevent double clicks

    // 🎵 Sound effects
    private MediaPlayer correctSound, wrongSound;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        // 🔊 Load sounds
        correctSound = MediaPlayer.create(this, R.raw.correct_answer);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);

        // Load questions
        questionList = getQuestions();
        Collections.shuffle(questionList);

        loadNewQuestion();

        View.OnClickListener optionClickListener = v -> {
            if (!isAnswered) {
                Button clicked = (Button) v;
                checkAnswer(clicked);
            }
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);
    }

    private void loadNewQuestion() {
        if (questionList.isEmpty()) {
            Toast.makeText(this, "🎉 Quiz Finished! Final Score: " + currentScore, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Pick a random question
        int index = random.nextInt(questionList.size());
        currentQuestion = questionList.remove(index);

        // Animate Question
        tvQuestion.setText(currentQuestion.getQuestionText());
        tvQuestion.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // Animate Options
        btnOption1.setText(currentQuestion.getOptions().get(0));
        btnOption2.setText(currentQuestion.getOptions().get(1));
        btnOption3.setText(currentQuestion.getOptions().get(2));
        btnOption4.setText(currentQuestion.getOptions().get(3));

        btnOption1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        btnOption2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        btnOption3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
        btnOption4.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));

        resetButtonColors();
        isAnswered = false;
    }

    private void checkAnswer(Button clickedButton) {
        isAnswered = true;

        int selectedIndex = -1;
        if (clickedButton == btnOption1) selectedIndex = 0;
        else if (clickedButton == btnOption2) selectedIndex = 1;
        else if (clickedButton == btnOption3) selectedIndex = 2;
        else if (clickedButton == btnOption4) selectedIndex = 3;

        if (selectedIndex == currentQuestion.getCorrectAnswerIndex()) {
            currentScore++;
            tvScore.setText("Score: " + currentScore);

            // ✅ Visual feedback
            clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(this, "✅ Correct Answer 🎉", Toast.LENGTH_SHORT).show();

            // Animation
            clickedButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.glow));

            // Checkmark icon
            clickedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.checkbox_on_background, 0);

            // 🔊 Play success sound
            correctSound.start();

        } else {
            clickedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            Toast.makeText(this, "❌ Wrong!", Toast.LENGTH_SHORT).show();
            showCorrectAnswer();

            // 🔊 Play error sound
            wrongSound.start();
        }

        // Load next question after delay
        new Handler().postDelayed(this::loadNewQuestion, 1500);
    }

    private void showCorrectAnswer() {
        int correctIndex = currentQuestion.getCorrectAnswerIndex();
        Button correctButton;

        switch (correctIndex) {
            case 0: correctButton = btnOption1; break;
            case 1: correctButton = btnOption2; break;
            case 2: correctButton = btnOption3; break;
            default: correctButton = btnOption4; break;
        }

        correctButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        correctButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.checkbox_on_background, 0);
    }

    private void resetButtonColors() {
        btnOption1.setBackgroundResource(R.drawable.bg_button_neon);
        btnOption2.setBackgroundResource(R.drawable.bg_button_neon);
        btnOption3.setBackgroundResource(R.drawable.bg_button_neon);
        btnOption4.setBackgroundResource(R.drawable.bg_button_neon);

        // Remove checkmark icons
        btnOption1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnOption2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnOption3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnOption4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    // 🎯 Question Bank
    // 🎯 Question Bank (30 Questions)
    private List<Question> getQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(new Question("Which exercise burns the most calories in 30 minutes?",
                List.of("Running", "Swimming", "Cycling", "Yoga"), 0));
        list.add(new Question("How many minutes of moderate exercise are recommended per week?",
                List.of("50", "100", "150", "200"), 2));
        list.add(new Question("Which vitamin is mainly produced when exposed to sunlight?",
                List.of("Vitamin A", "Vitamin B12", "Vitamin C", "Vitamin D"), 3));
        list.add(new Question("What is the primary muscle worked in squats?",
                List.of("Biceps", "Quadriceps", "Triceps", "Deltoids"), 1));
        list.add(new Question("Which nutrient is the body's main source of energy?",
                List.of("Protein", "Carbohydrates", "Fat", "Vitamins"), 1));
        list.add(new Question("How much of the human body is made up of water?",
                List.of("30%", "50%", "60-70%", "90%"), 2));
        list.add(new Question("Which organ detoxifies chemicals and metabolizes drugs?",
                List.of("Lungs", "Liver", "Kidneys", "Heart"), 1));
        list.add(new Question("What is the average resting heart rate for adults?",
                List.of("40-60 bpm", "60-100 bpm", "100-120 bpm", "120-140 bpm"), 1));
        list.add(new Question("Which mineral is essential for strong bones and teeth?",
                List.of("Iron", "Calcium", "Magnesium", "Potassium"), 1));
        list.add(new Question("Which exercise is best for strengthening the core?",
                List.of("Plank", "Bicep curls", "Lunges", "Push-ups"), 0));
        list.add(new Question("How many calories are in 1 gram of protein?",
                List.of("2", "4", "6", "9"), 1));
        list.add(new Question("What is the recommended daily steps goal for good health?",
                List.of("2000", "5000", "8000", "10000"), 3));
        list.add(new Question("Which system in the body controls hormones?",
                List.of("Digestive system", "Respiratory system", "Endocrine system", "Circulatory system"), 2));
        list.add(new Question("What type of fat is considered 'healthy'?",
                List.of("Trans fat", "Saturated fat", "Unsaturated fat", "Hydrogenated fat"), 2));
        list.add(new Question("What is the largest organ in the human body?",
                List.of("Heart", "Skin", "Liver", "Lungs"), 1));
        list.add(new Question("Which macronutrient helps build and repair muscles?",
                List.of("Carbohydrates", "Proteins", "Fats", "Vitamins"), 1));
        list.add(new Question("How many hours of sleep are recommended for adults?",
                List.of("4-5", "6-7", "7-9", "10+"), 2));
        list.add(new Question("Which type of exercise improves flexibility?",
                List.of("Yoga", "Running", "Weightlifting", "Swimming"), 0));
        list.add(new Question("What is the normal body temperature of a healthy person?",
                List.of("35°C", "36.5-37°C", "38°C", "39°C"), 1));
        list.add(new Question("Which blood type is known as the universal donor?",
                List.of("A", "B", "O-", "AB+"), 2));

        // Extra 10
        list.add(new Question("Which exercise is known as a full-body workout?",
                List.of("Burpees", "Crunches", "Bicep curls", "Calf raises"), 0));
        list.add(new Question("What does BMI stand for?",
                List.of("Body Mass Index", "Blood Muscle Indicator", "Bone Mass Input", "Basic Metabolic Indicator"), 0));
        list.add(new Question("Which organ pumps blood through the body?",
                List.of("Liver", "Heart", "Kidney", "Lungs"), 1));
        list.add(new Question("Which nutrient helps in oxygen transport in blood?",
                List.of("Calcium", "Iron", "Magnesium", "Zinc"), 1));
        list.add(new Question("What is the main benefit of cardiovascular exercise?",
                List.of("Improved digestion", "Stronger bones", "Heart health", "Flexibility"), 2));
        list.add(new Question("Which is the strongest muscle in the human body (by weight)?",
                List.of("Gluteus maximus", "Tongue", "Jaw (masseter)", "Quadriceps"), 2));
        list.add(new Question("Which drink is best for hydration during workouts?",
                List.of("Soda", "Water", "Energy drink", "Coffee"), 1));
        list.add(new Question("What is the main role of fiber in the diet?",
                List.of("Build muscles", "Aid digestion", "Increase energy", "Burn fat"), 1));
        list.add(new Question("Which part of the body has the most sweat glands?",
                List.of("Forehead", "Feet", "Hands", "Back"), 1));
        list.add(new Question("What is the medical term for high blood pressure?",
                List.of("Hypotension", "Hypertension", "Arrhythmia", "Anemia"), 1));

        return list;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctSound != null) correctSound.release();
        if (wrongSound != null) wrongSound.release();
    }
}

