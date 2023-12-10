package net.cloudescape.skyblock.island;

public enum IslandSettings {

    PUBLIC_BUILD(false),
    PUBLIC_INTERACT(false),
    VISIT_ISLAND(true),
    ;

    private boolean defaultValue;

    IslandSettings(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean isDefault() {
        return defaultValue;
    }
}
