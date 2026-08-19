package com.example.dartsscoreboard.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Match {
    private String id;
    private LocalDateTime startDate;
    private int legsNo;

    private int setsNo;

    private int legSize;

    private ArrayList<Player> playersList = new ArrayList<>();

    private ArrayList<Set> setsList = new ArrayList<>();

    public Match(int setsNo, int legsNo, int legSize) {
        this.id = "IDM-" + System.currentTimeMillis();
        this.startDate = LocalDateTime.now();
        this.legsNo = legsNo;
        this.legSize = legSize;
        this.setsNo = setsNo;
    }

    public int getLegsNo() {
        return legsNo;
    }

    public int getLegSize() {
        return legSize;
    }

    public int getSetsNo() {
        return setsNo;
    }

    public void setSetsNo(int setsNo) {
        this.setsNo = setsNo;
    }

    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        this.playersList = playersList;
    }

    public ArrayList<Set> getSetsList() {
        return setsList;
    }

    public void setSetsList(ArrayList<Set> setsList) {
        this.setsList = setsList;
    }

    public String getId() {
        return id;
    }
}