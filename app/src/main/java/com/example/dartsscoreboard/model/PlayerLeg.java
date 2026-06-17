package com.example.dartsscoreboard.model;

import java.util.ArrayList;

public class PlayerLeg {
    private String id;
    private String matchId;

    private String playerId;

    private String legId;

    private int currentScore;

    private ArrayList<Round> roundsList;

    public PlayerLeg(String matchId, String playerId, String legId, int currentScore) {
        this.id = "IDPL-" + System.currentTimeMillis();
        this.matchId = matchId;
        this.playerId = playerId;
        this.legId = legId;
        this.currentScore = currentScore;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public ArrayList<Round> getRoundsList() {
        return roundsList;
    }

    public void setRoundsList(ArrayList<Round> roundsList) {
        this.roundsList = roundsList;
    }
}
