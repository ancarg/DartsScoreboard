package com.example.dartsscoreboard.model;

public class Throw {
    private String id;
    private String roundId;
    private int dartHit;
    private int throwScore;
    private ThrowType type;

    public Throw(String roundId, int dartHit, ThrowType type) {
        this.id = "IDT-" + System.currentTimeMillis();
        this.roundId = roundId;
        this.dartHit = dartHit;
        this.type = type;
        this.throwScore =  (dartHit != 0) ? dartHit * type.getCode() : 0 ;
    }

    public int getThrowScore() {
        return throwScore;
    }

    public int getDartHit() {
        return dartHit;
    }

    public void setDartHit(int dartHit) {
        this.dartHit = dartHit;
    }

    public ThrowType getType() {
        return type;
    }

    public void setType(ThrowType type) {
        this.type = type;
    }
}
