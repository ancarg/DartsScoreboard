package com.example.dartsscoreboard.model;

public class Leg {
    private String id;
    private String matchId;
    private String setId;
    private int displayNo;
    private String winnerId;


    public Leg(String matchId, String setId, int displayNo) {
        this.id = "IDL-" + System.currentTimeMillis();
        this.matchId = matchId;
        this.setId = setId;
        this.displayNo = displayNo;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getDisplayNo() {
        return displayNo;
    }

    public String getId() {
        return id;
    }

    public void setDisplayNo(int displayNo) {
        this.displayNo = displayNo;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}
