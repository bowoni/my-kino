package com.mykino.enums;

public enum NotificationType {
    OTT_ADDED("OTT 추가"),
    OTT_REMOVING("OTT 종료 예정"),
    NEW_CONTENT("신작 알림"),
    REVIEW_LIKED("리뷰 공감"),
    RECOMMENDATION("맞춤 추천");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
