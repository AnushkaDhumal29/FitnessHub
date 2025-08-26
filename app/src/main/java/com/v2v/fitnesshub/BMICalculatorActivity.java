package com.v2v.fitnesshub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BMICalculatorActivity extends AppCompatActivity {

    EditText editWeight, editHeight;
    Button btnCalculate;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            String weightStr = editWeight.getText().toString().trim();
            String heightStr = editHeight.getText().toString().trim();

            if (weightStr.isEmpty() || heightStr.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float weight = Float.parseFloat(weightStr);
            float heightCm = Float.parseFloat(heightStr);
            float heightM = heightCm / 100;

            float bmi = weight / (heightM * heightM);

            String category;
            if (bmi < 18.5) {
                category = "Underweight";
            } else if (bmi < 24.9) {
                category = "Normal weight";
            } else if (bmi < 29.9) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            tvResult.setText("Your BMI: " + String.format("%.2f", bmi) + "\nCategory: " + category);
        });
    }
}

