package com.v2v.fitnesshub;

public class ExerciseItem {
    private final String name;
    private final String singleVideoId;
    private final String category;
    private final String thumbnailUrl;

    public ExerciseItem(String name, String singleVideoId, String category, String thumbnailUrl) {
        this.name = name;
        this.singleVideoId = singleVideoId;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public String getSingleVideoId() {
        return singleVideoId;
    }

    public String getCategory() {
        return category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
