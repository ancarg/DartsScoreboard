package com.example.dartsscoreboard.model;

import java.util.ArrayList;
import java.util.UUID;

public class Player {

    private String id;
    private String matchId;

    private String displayName;

    private int wonLegsNo;
    private int wonSetsNo;

    private ArrayList<PlayerLeg> playerLegsList;

    public Player(String matchId, String displayName) {
        this.id = "IDP-" + UUID.randomUUID().toString();
        this.matchId = matchId;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
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

    public int getWonSetsNo() {
        return wonSetsNo;
    }

    public void setWonSetsNo(int wonSetsNo) {
        this.wonSetsNo = wonSetsNo;
    }

    public ArrayList<PlayerLeg> getPlayerLegsList() {
        return playerLegsList;
    }

    public void setPlayerLegsList(ArrayList<PlayerLeg> playerLegsList) {
        this.playerLegsList = playerLegsList;
    }
}
