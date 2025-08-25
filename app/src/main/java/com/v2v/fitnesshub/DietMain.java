package com.v2v.fitnesshub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class DietMain extends AppCompatActivity {

    private TextInputEditText etAge, etGender, etHeight, etWeight, etGoal,
            etPreference, etMealFreq, etPantry, etSupplements, etBudget, etAllergies;
    private Button btnGenerate;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diet_one);

        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etGoal = findViewById(R.id.etGoal);
        etPreference = findViewById(R.id.etPreference);
        etMealFreq = findViewById(R.id.etMealFreq);
        etPantry = findViewById(R.id.etPantry);
        etSupplements = findViewById(R.id.etSupplements);
        etBudget = findViewById(R.id.etBudget);
        etAllergies = findViewById(R.id.etAllergies);
        btnGenerate = findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(v -> {
            String prompt = buildPrompt();

            if (prompt.trim().isEmpty()) {
                Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "📝 Built Prompt:\n" + prompt);

            GeminiClient.generateDietPlan(prompt, new GeminiClient.PlanCallback() {
                @Override
                public void onSuccess(String planText) {
                    Log.d(TAG, "✅ API Success, plan received:\n" + planText);

                    Intent intent = new Intent(DietMain.this, PlanResultActivity.class);
                    intent.putExtra("plan", planText);
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "❌ API Error: " + message);
                    Toast.makeText(DietMain.this, "Error: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private String buildPrompt() {
        return "Generate a personalized diet plan for:\n" +
                "Age: " + etAge.getText() + " years\n" +
                "Gender: " + etGender.getText() + "\n" +
                "Height: " + etHeight.getText() + " cm\n" +
                "Weight: " + etWeight.getText() + " kg\n" +
                "Goal: " + etGoal.getText() + "\n" +
                "Diet Preference: " + etPreference.getText() + "\n" +
                "Meal Frequency: " + etMealFreq.getText() + " meals/day\n" +
                "Ingredients at home: " + etPantry.getText() + "\n" +
                "Supplements: " + etSupplements.getText() + "\n" +
                "Daily Budget: ₹" + etBudget.getText() + "\n" +
                "Allergies/Restrictions: " + etAllergies.getText();
    }
}
