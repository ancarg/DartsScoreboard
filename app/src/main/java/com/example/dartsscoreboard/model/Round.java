package com.example.dartsscoreboard.model;

import java.util.ArrayList;

public class Round {
    private String id;
    private String legId;

    private ArrayList<Throw> throwsList;
    public Round(String legId) {
        id = "IDR-" + System.currentTimeMillis();
        this.legId = legId;
    }

    public ArrayList<Throw> getThrowsList() {
        return throwsList;
    }

    public void setThrowsList(ArrayList<Throw> throwsList) {
        this.throwsList = throwsList;
    }
}
