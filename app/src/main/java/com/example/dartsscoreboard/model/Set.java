package com.example.dartsscoreboard.model;

import java.util.ArrayList;

public class Set {

    private String id;
    private String matchId;
    private int displayNo;

    private ArrayList<Leg> legsList = new ArrayList<>();

    public Set(String matchId, int displayNo) {
        this.id = "IDS-" + System.currentTimeMillis();
        this.matchId = matchId;
        this.displayNo = displayNo;
    }

    public ArrayList<Leg> getLegsList() {
        return legsList;
    }

    public void setLegsList(ArrayList<Leg> legsList) {
        this.legsList = legsList;
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

    public String getId() {
        return id;
    }

    public void setDisplayNo(int displayNo) {
        this.displayNo = displayNo;
    }
}
