package com.v2v.fitnesshub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DietPlanActivity extends AppCompatActivity {

    RecyclerView rvDietPlans;
    Button btnGoToAi;
    ArrayList<DietPlanModel> dietPlans;
    DietPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dietplan_activity);

        rvDietPlans = findViewById(R.id.rvDietPlans);
        btnGoToAi = findViewById(R.id.btnGoToAi);

        // Setup RecyclerView
        rvDietPlans.setLayoutManager(new GridLayoutManager(this, 2));

        // Predefined Indian Diet Plans
        dietPlans = new ArrayList<>();
        dietPlans.add(new DietPlanModel("Weight Loss Plan", R.drawable.ic_weightloss));
        dietPlans.add(new DietPlanModel("Muscle Gain Plan", R.drawable.ic_musclesgain));
        dietPlans.add(new DietPlanModel("Pregnancy Plan", R.drawable.ic_pregenencyplan));
        dietPlans.add(new DietPlanModel("Period Care Plan", R.drawable.ic_periodcareplan));
        dietPlans.add(new DietPlanModel("Diabetic Friendly", R.drawable.ic_diabeticplan));
        dietPlans.add(new DietPlanModel("Heart Healthy", R.drawable.ic_heartfriendlyplan));
        dietPlans.add(new DietPlanModel("Detox Diet", R.drawable.ic_detoxplan));
        dietPlans.add(new DietPlanModel("Vegetarian Balanced", R.drawable.ic_vegeterianplan));
        dietPlans.add(new DietPlanModel("Non-Veg Protein", R.drawable.ic_nonvegplan));
        dietPlans.add(new DietPlanModel("Ayurvedic Satvik", R.drawable.ic_aayurvedicplan));

        adapter = new DietPlanAdapter(dietPlans, this);
        rvDietPlans.setAdapter(adapter);

        // AI button → open AI Diet Plan Page
        btnGoToAi.setOnClickListener(v ->
                Toast.makeText(this, "Ai generated plan", Toast.LENGTH_SHORT).show()
        );
    }
}
