package com.mykino.enums;

public enum CastRoleType {
    DIRECTOR("감독"),
    ACTOR("배우"),
    WRITER("작가");

    private final String displayName;

    CastRoleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
