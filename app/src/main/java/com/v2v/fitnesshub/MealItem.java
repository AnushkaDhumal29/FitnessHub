package com.v2v.fitnesshub;

public class MealItem {
    private String title;
    private String description;

    public MealItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

