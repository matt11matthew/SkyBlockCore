package net.cloudescape.skyblock.island.spawner;

import org.bukkit.ChatColor;

/**
 * Created by Matthew E on 4/15/2018.
 */
public enum GolemType {
    IRON(ChatColor.GRAY, "Iron"), GOLD(ChatColor.YELLOW, "Gold"), DIAMOND(ChatColor.AQUA, "Diamond"), EMERALD(ChatColor.GREEN, "Emerald");

    private ChatColor color;
    private String name;

    GolemType(ChatColor color, String name) {
        this.color = color;
        this.name = name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public GolemType getNextType() {
        switch (this) {
            case IRON:
                return GOLD;
            case GOLD:
                return DIAMOND;
            case DIAMOND:
                return EMERALD;
            case EMERALD:
                return null;
        }
        return null;
    }
}
