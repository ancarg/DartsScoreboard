package com.example.dartsscoreboard.model;

import java.util.ArrayList;

public class Player {

    private String id;
    private String matchId;

    private String displayName;

    private int wonLegsNo;

    private ArrayList<PlayerLeg> playerLegsList;

    public Player(String matchId, String displayName) {
        this.id = "IDP-" + System.currentTimeMillis();
        this.matchId = matchId;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getWonLegsNo() {
        return wonLegsNo;
    }

    public void setWonLegsNo(int wonLegsNo) {
        this.wonLegsNo = wonLegsNo;
    }

    public ArrayList<PlayerLeg> getPlayerLegsList() {
        return playerLegsList;
    }

    public void setPlayerLegsList(ArrayList<PlayerLeg> playerLegsList) {
        this.playerLegsList = playerLegsList;
    }
}
