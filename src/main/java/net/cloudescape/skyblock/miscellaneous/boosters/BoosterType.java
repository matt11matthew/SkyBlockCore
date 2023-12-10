package net.cloudescape.skyblock.miscellaneous.boosters;

import org.bukkit.Material;

public enum BoosterType {

    /** BoosterTypes.. */
    ISLAND_RADIUS(Material.EMERALD, new double[] { 0, 80000, 400000, 2000000, 5000000, 15000000, 40000000, 100000000, 500000000, 800000000}),
    ORE_GENERATION(Material.DIAMOND_ORE, new double[] { 0, 125000, 300000, 700000, 4000000 }),
    MINION_LIMIT(Material.ARMOR_STAND, new double[] { 0, 100000, 250000, 600000, 1000000}),
    SPAWNER_LIMIT(Material.MOB_SPAWNER, new double[] { 0, 125000, 400000, 1000000, 6000000}),
    TEAM_MEMBER_LIMIT(Material.SKULL_ITEM, new double[] { 0, 12000, 50000, 90000, 300000 }),
    BALANCE_LIMIT(Material.PAPER, new double[] { 0, 5000, 15000, 25000, 40000, 65000, 80000 });

    private Material displayIcon;
    private double[] levelCosts;

    BoosterType(Material displayIcon, double[] levelCosts) {
        this.displayIcon = displayIcon;
        this.levelCosts = levelCosts;
    }

    public Material getDisplayIcon() {
        return displayIcon;
    }

    public double[] getLevelCosts() {
        return levelCosts;
    }
}
