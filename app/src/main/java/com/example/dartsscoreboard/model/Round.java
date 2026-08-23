package com.example.dartsscoreboard.model;

import java.util.ArrayList;

public class Round {
    private String id;
    private String legId;

    private String throwsTotalScore;
    private int dartsCount;

    private ArrayList<Throw> throwsList  = new ArrayList<>();
    public Round(String legId) {
        id = "IDR-" + java.util.UUID.randomUUID().toString();
        this.legId = legId;
    }

    public int getDartsCount() {
        return dartsCount;
    }

    public void setDartsCount(int dartsCount) {
        this.dartsCount = dartsCount;
    }

    public ArrayList<Throw> getThrowsList() {
        return throwsList;
    }

    public void setThrowsList(ArrayList<Throw> throwsList) {
        this.throwsList = throwsList;
    }

    public String getThrowsTotalScore() {
        return throwsTotalScore;
    }

    public void setThrowsTotalScore(String throwsTotalScore) {
        this.throwsTotalScore = throwsTotalScore;
    }
}
