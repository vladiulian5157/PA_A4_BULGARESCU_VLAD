package org.example;

public class Question {
    public String text;
    public String answer;
    public int timeLimit;

    public Question(String text, String answer, int timeLimit) {
        this.text = text;
        this.answer = answer;
        this.timeLimit = timeLimit;
    }
}