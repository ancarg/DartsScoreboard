package com.example.dartsscoreboard.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Match {
    private String id;
    private LocalDateTime startDate;
    private int legsNo;
    private int legSize;

    private ArrayList<Player> playersList = new ArrayList<>();

    private ArrayList<Leg> legsList = new ArrayList<>();

    public Match(int legsNo, int legSize) {
        this.id = "IDM-" + System.currentTimeMillis();
        this.startDate = LocalDateTime.now();
        this.legsNo = legsNo;
        this.legSize = legSize;
    }

    public int getLegsNo() {
        return legsNo;
    }

    public int getLegSize() {
        return legSize;
    }

    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        this.playersList = playersList;
    }

    public ArrayList<Leg> getLegsList() {
        return legsList;
    }

    public void setLegsList(ArrayList<Leg> legsList) {
        this.legsList = legsList;
    }

    public String getId() {
        return id;
    }
}