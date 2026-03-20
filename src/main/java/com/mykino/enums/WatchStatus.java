package com.mykino.enums;

public enum WatchStatus {
    WANT_TO_WATCH("찜"),
    WATCHING("보는중"),
    WATCHED("봤어요");

    private final String displayName;

    WatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
