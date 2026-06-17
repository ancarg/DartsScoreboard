package com.example.dartsscoreboard.model;

public class Leg {
    private String id;
    private String matchId;
    private int displayNo;


    public Leg(String matchId, int displayNo) {
        this.id = "IDL-" + System.currentTimeMillis();
        this.matchId = matchId;
        this.displayNo = displayNo;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getDisplayNo() {
        return displayNo;
    }

    public void setDisplayNo(int displayNo) {
        this.displayNo = displayNo;
    }
}
