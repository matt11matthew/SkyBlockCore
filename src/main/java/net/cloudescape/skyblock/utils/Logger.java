package net.cloudescape.skyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    /**
     * Prints coloured messages to console.
     * @param message
     */
    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[Cloud Skyblock] &7" + message));
    }
}
