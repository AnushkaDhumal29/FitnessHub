package com.v2v.fitnesshub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlanResultActivity extends AppCompatActivity {

    private TextView tvPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diet_result);

        tvPlan = findViewById(R.id.tvPlan);

        String plan = getIntent().getStringExtra("plan");
        if (plan != null) {
            tvPlan.setText(plan);
        } else {
            tvPlan.setText("⚠️ No plan received.");
        }
    }
}

