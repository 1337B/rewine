package com.rewine.backend.model.enums;

/**
 * Wine type enumeration.
 */
public enum WineType {
    RED("Red Wine"),
    WHITE("White Wine"),
    ROSE("Rosé Wine"),
    SPARKLING("Sparkling Wine"),
    DESSERT("Dessert Wine"),
    FORTIFIED("Fortified Wine"),
    ORANGE("Orange Wine");

    private final String displayName;

    WineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

