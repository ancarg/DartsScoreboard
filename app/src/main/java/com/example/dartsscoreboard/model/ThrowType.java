package com.example.dartsscoreboard.model;

public enum ThrowType {
    STRIKE(0),
    SIMPLE(1),
    DOUBLE(2),
    TRIPLE(3);

    private final int code;

    ThrowType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
