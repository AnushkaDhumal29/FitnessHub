package com.v2v.fitnesshub;

public class DietPlanModel {
    String title;
    int image;

    public DietPlanModel(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
