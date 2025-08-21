package com.v2v.fitnesshub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DietPlanDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan_details);

        tvTitle = findViewById(R.id.tvPlanTitle);
        tvDetails = findViewById(R.id.tvPlanDetails);

        String title = getIntent().getStringExtra("title");
        if (title == null) {
            title = "Diet Plan";
        }
        tvTitle.setText(title);

        // Expanded plan details
        switch (title) {
            case "Weight Loss Plan":
                tvDetails.setText(
                        "⚡ Goal: Create a calorie deficit while staying energetic.\n\n" +
                                "🍽 Breakfast:\n• Poha with vegetables\n• 1 cup Green Tea\n\n" +
                                "🥗 Lunch:\n• 2 Multigrain Rotis\n• Dal (low oil)\n• Seasonal Veg Sabzi\n• Fresh Salad\n\n" +
                                "🌙 Dinner:\n• Vegetable Khichdi\n• Cucumber Raita\n\n" +
                                "☕ Snacks:\n• Sprouts Chaat\n• Coconut Water\n\n" +
                                "✅ Tip: Drink 2–3L water daily & avoid fried foods."
                );
                break;

            case "Muscle Gain Plan":
                tvDetails.setText(
                        "⚡ Goal: Muscle growth with high protein & balanced carbs.\n\n" +
                                "🍽 Breakfast:\n• Banana + Peanut Butter Shake\n• 3 Boiled Eggs\n\n" +
                                "🥗 Lunch:\n• Chicken Curry / Paneer Curry\n• Brown Rice (1 bowl)\n• Salad\n\n" +
                                "🌙 Dinner:\n• Paneer/Chicken Curry\n• 2 Chapatis\n• Green Vegetables\n\n" +
                                "☕ Snacks:\n• Protein Shake\n• Handful of Dry Fruits\n\n" +
                                "✅ Tip: Add strength training for best results."
                );
                break;

            case "Pregnancy Plan":
                tvDetails.setText(
                        "⚡ Goal: Balanced nutrition for mother & baby.\n\n" +
                                "🍽 Breakfast:\n• Oats with Milk & Dry Fruits\n• Fresh Orange Juice\n\n" +
                                "🥗 Lunch:\n• 2 Chapatis\n• Dal\n• Green Sabzi\n• Rice (small portion)\n\n" +
                                "🌙 Dinner:\n• Vegetable Khichdi\n• Curd\n\n" +
                                "☕ Snacks:\n• Seasonal Fruit Bowl\n• Handful of Nuts\n\n" +
                                "✅ Tip: Avoid raw fish & unpasteurized dairy. Consult doctor regularly."
                );
                break;

            case "Period Care Plan":
                tvDetails.setText(
                        "⚡ Goal: Reduce cramps & balance hormones.\n\n" +
                                "🍽 Breakfast:\n• Warm Haldi (Turmeric) Milk\n• Vegetable Upma\n\n" +
                                "🥗 Lunch:\n• 2 Roti\n• Rajma/Dal\n• Green Sabzi\n• Salad\n\n" +
                                "🌙 Dinner:\n• Moong Dal Khichdi\n• Beetroot Raita\n\n" +
                                "☕ Snacks:\n• Small piece Dark Chocolate\n• Herbal Tea\n\n" +
                                "✅ Tip: Stay hydrated & include iron-rich foods."
                );
                break;

            case "Diabetic Friendly":
                tvDetails.setText(
                        "⚡ Goal: Control blood sugar with low-GI foods.\n\n" +
                                "🍽 Breakfast:\n• Vegetable Daliya\n• Sugar-free Tea\n\n" +
                                "🥗 Lunch:\n• 2 Multigrain Chapatis\n• Dal + Sabzi\n• Salad\n\n" +
                                "🌙 Dinner:\n• Lauki Soup\n• 2 Roti + Sabzi\n\n" +
                                "☕ Snacks:\n• Roasted Chana\n• Sprouts Salad\n\n" +
                                "✅ Tip: Avoid sweets & monitor blood sugar regularly."
                );
                break;

            case "Heart Healthy":
                tvDetails.setText(
                        "⚡ Goal: Support heart health with omega-3 & fiber.\n\n" +
                                "🍽 Breakfast:\n• Oats with Flax Seeds\n• 1 cup Green Tea\n\n" +
                                "🥗 Lunch:\n• 1 Bowl Brown Rice\n• Dal + Sabzi\n• Salad\n\n" +
                                "🌙 Dinner:\n• Grilled Fish / Paneer\n• 2 Chapatis\n\n" +
                                "☕ Snacks:\n• Walnuts\n• Fruit Salad\n\n" +
                                "✅ Tip: Limit fried foods & add daily walking."
                );
                break;

            case "Detox Diet":
                tvDetails.setText(
                        "⚡ Goal: Cleanse body & improve digestion.\n\n" +
                                "🍽 Breakfast:\n• Warm Lemon Water\n• Fresh Fruit Smoothie\n\n" +
                                "🥗 Lunch:\n• Steamed Vegetables\n• Quinoa Salad\n\n" +
                                "🌙 Dinner:\n• Vegetable Soup\n• 1 Roti\n\n" +
                                "☕ Snacks:\n• Coconut Water\n• Green Tea\n\n" +
                                "✅ Tip: Drink herbal teas & avoid packaged foods."
                );
                break;

            case "Vegetarian Balanced":
                tvDetails.setText(
                        "⚡ Goal: Balanced vegetarian nutrition.\n\n" +
                                "🍽 Breakfast:\n• Vegetable Sandwich\n• 1 Glass Milk\n\n" +
                                "🥗 Lunch:\n• 2 Rotis\n• Dal\n• Seasonal Veg Sabzi\n• Salad\n\n" +
                                "🌙 Dinner:\n• Veg Pulao\n• Curd\n\n" +
                                "☕ Snacks:\n• Roasted Makhana\n• Fresh Fruit Bowl\n\n" +
                                "✅ Tip: Ensure protein from lentils, paneer & soy."
                );
                break;

            case "Non-Veg Protein":
                tvDetails.setText(
                        "⚡ Goal: High protein intake for strength.\n\n" +
                                "🍽 Breakfast:\n• Veg Omelette\n• Black Coffee\n\n" +
                                "🥗 Lunch:\n• Grilled Chicken/Fish\n• Brown Rice\n• Salad\n\n" +
                                "🌙 Dinner:\n• Egg Curry / Chicken Curry\n• 2 Rotis\n\n" +
                                "☕ Snacks:\n• Boiled Eggs\n• Peanut Chaat\n\n" +
                                "✅ Tip: Prefer grilled/boiled over fried meats."
                );
                break;

            case "Ayurvedic Satvik":
                tvDetails.setText(
                        "⚡ Goal: Light & sattvic food for body balance.\n\n" +
                                "🍽 Breakfast:\n• Warm Honey Water\n• Seasonal Fruits\n\n" +
                                "🥗 Lunch:\n• Moong Dal Khichdi with Ghee\n• Fresh Salad\n\n" +
                                "🌙 Dinner:\n• Light Vegetable Soup\n• 1 Roti\n\n" +
                                "☕ Snacks:\n• Herbal Tea\n• Handful of Dry Fruits (Almonds, Dates)\n\n" +
                                "✅ Tip: Avoid onions, garlic & heavy oily foods."
                );
                break;

            default:
                tvDetails.setText("Details coming soon...");
        }
    }
}
