package net.cloudescape.skyblock.miscellaneous.minions.animations;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public abstract class Animation extends BukkitRunnable {

    protected long patricaleCooldown;

    public abstract void displayParticles();

    public void stop()
    {
        cancel();
    }
}
