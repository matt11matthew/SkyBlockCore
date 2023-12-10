package net.cloudescape.skyblock.utils;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class RunnableUtils {
    public static void runSync(Runnable runnable, long delay) {
        new BukkitRunnable() {

            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(CloudSkyblock.getPlugin(), delay);
    }
}
