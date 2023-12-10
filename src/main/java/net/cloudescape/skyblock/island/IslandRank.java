package net.cloudescape.skyblock.island;

public enum IslandRank {
    MEMBER,
    TRUSTED,
    MANAGER,
    OWNER;

    public static IslandRank getRankByName(String name) {
        for (IslandRank rank : values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }
}
