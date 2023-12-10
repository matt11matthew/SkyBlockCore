package net.cloudescape.skyblock.utils;

import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

public class StringUtil {

    /**
     * Get the long lines in GUI menus (formatting purposes).
     *
     * @return Formatted line.
     */
    public static String getMenuLine() {
        return ChatColor.DARK_GRAY + ChatColor.STRIKETHROUGH.toString() + StringUtil.repeat('-', 35);
    }


    public static String repeat(char c, int amount) {
        StringBuilder returnString = new StringBuilder();
        if (amount < 1) {
            return "";
        }
        for (int i = 0; i < amount; i++) {
            returnString.append(c);
        }
        return returnString.toString();
    }

    public static String capitalizeWords(String input, String split) {
        if (!input.contains(split)) {
            return capitalizeFirstLetter(input);
        }
        String returnString = "";
        for (String s : input.split(split)) {
            returnString += capitalizeFirstLetter(s) + " ";
        }
        if (returnString.endsWith(" ")) {
            returnString = returnString.trim();
        }
        return returnString;
    }

    public static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(" days, ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" hours, ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(" minutes, ");
        }

        if (seconds > 0) {
            sb.append(seconds);
            sb.append(" seconds");
        }

        return (sb.toString());
    }
}
