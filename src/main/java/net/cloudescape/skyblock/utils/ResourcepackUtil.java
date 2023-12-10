package net.cloudescape.skyblock.utils;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResourcepackUtil {

    public static void sendResourcepack(Player player, String link) {
        Bukkit.getScheduler().runTaskLater(CloudSkyblock.getPlugin(), () -> {
            player.setResourcePack(link);
        }, 20 * 2);
    }
}
