package com.v2v.fitnesshub;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex; // 0,1,2,3

    public Question(String questionText, List<String> options, int correctAnswerIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}
