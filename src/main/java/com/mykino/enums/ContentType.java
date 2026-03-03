package com.mykino.enums;

public enum ContentType {
    MOVIE("영화"),
    DRAMA("드라마"),
    VARIETY("예능"),
    DOCUMENTARY("다큐멘터리"),
    ANIMATION("애니메이션");

    private final String displayName;

    ContentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
