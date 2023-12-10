package net.cloudescape.skyblock.miscellaneous.boosters;

public class Booster {

    /** Type of Booster */
    private BoosterType type;
    /** Booster level */
    private int level;

    /**
     * Construct a new booster with a specific type.
     * @param type - type.
     */
    public Booster(BoosterType type, int level) {
        this.type = type;
        this.level = level;
    }

    /**
     * Get the type of booster.
     * @return Type of booster.
     */
    public BoosterType getType() {
        return type;
    }

    /**
     * Get the level of the island, higher level longer it goes on for.
     * @return Level
     */
    public int getLevel() {
        return level;
    }
}
