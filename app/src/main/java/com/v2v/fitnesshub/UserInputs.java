package com.v2v.fitnesshub;

public class UserInputs {
    public String preference;
    public int mealFrequency;
    public String pantryCSV;
    public String supplements;
    public String allergies;
    public int dailyBudget;
    public int age;
    public String gender;
    public int heightCm;
    public int weightKg;
    public String goal;

    // ✅ You can add a constructor for easy initialization
    public UserInputs(String preference, int mealFrequency, String pantryCSV,
                      String supplements, String allergies, int dailyBudget,
                      int age, String gender, int heightCm, int weightKg, String goal) {
        this.preference = preference;
        this.mealFrequency = mealFrequency;
        this.pantryCSV = pantryCSV;
        this.supplements = supplements;
        this.allergies = allergies;
        this.dailyBudget = dailyBudget;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.goal = goal;
    }
}
