package com.fenestra.kahvalti.database;

/**
 * Created by karim on 5/7/17.
 */

public class Score {

    private int id;
    private int score;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Score() {
    }

    public Score(int score) {
        this.score = score;
    }
}
