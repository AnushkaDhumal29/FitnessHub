package com.v2v.fitnesshub;
public class PromptBuilder {

    public static String buildPrompt(UserInputs ui) {
        return "You are a certified nutritionist AI. Create a 7-day personalized diet plan in India-friendly options. "
                + "Respect all constraints below and ONLY produce helpful, safe, practical guidance.\n\n"
                + "Constraints:\n"
                + "- Diet preference: " + ui.preference + "\n"
                + "- Meals per day: " + ui.mealFrequency + "\n"
                + "- Use mainly ingredients available at home: [" + ui.pantryCSV + "]\n"
                + "- Consider supplements already taken: " + (isEmpty(ui.supplements) ? "none" : ui.supplements) + "\n"
                + "- Max daily food budget: INR " + ui.dailyBudget + "\n"
                + "- Allergies/restrictions: " + (isEmpty(ui.allergies) ? "none" : ui.allergies) + "\n"
                + "- Include approximate per-day totals (Calories, Protein g, Carbs g, Fat g) and simple prep notes.\n"
                + "- Avoid unsafe crash diets; ensure balanced macros and adequate fiber.\n\n"
                + "User profile:\n"
                + "Age: " + ui.age + ", Gender: " + ui.gender + ", Height: " + ui.heightCm + " cm, Weight: " + ui.weightKg + " kg\n"
                + "Goal: " + ui.goal + "\n\n"
                + "Output format (strict):\n"
                + "WEEKLY PLAN\n"
                + "Day 1:\n- Breakfast: ...\n- Lunch: ...\n- Snack: ...\n- Dinner: ...\n"
                + "Approx totals: Calories ~____, Protein ~____g, Carbs ~____g, Fat ~____g\n"
                + "Day 2:\n...\n"
                + "Day 7:\n...\n"
                + "Shopping List (within budget): ...\n"
                + "Notes: ...";
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}




