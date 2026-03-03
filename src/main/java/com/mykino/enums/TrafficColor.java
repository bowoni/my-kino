package com.mykino.enums;

public enum TrafficColor {
    GREEN("추천", "#4CAF50"),
    YELLOW("보통", "#FFC107"),
    RED("비추천", "#F44336");

    private final String displayName;
    private final String colorCode;

    TrafficColor(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
