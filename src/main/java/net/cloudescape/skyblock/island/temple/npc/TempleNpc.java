package net.cloudescape.skyblock.island.temple.npc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Created by Matthew E on 5/12/2018.
 */
public abstract class TempleNpc {
    protected double x;
    protected double y;
    protected double z;
    protected float yaw;
    protected float pitch;
    protected String displayName;
    protected TempleNpcStatus status;
    protected Consumer<Player> rightClick;

    public TempleNpc(double x, double y, double z, float yaw, float pitch, String displayName, TempleNpcStatus status, Consumer<Player> rightClick) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.displayName = displayName;
        switch (status) {
            case COMING_SOON:
                this.displayName = displayName + " " + ChatColor.GRAY + "(" + ChatColor.RED + ChatColor.BOLD.toString() + "Coming Soon" + ChatColor.GRAY + ")";
                break;
            case RELEASED:
                break;
        }
        this.status = status;
        this.rightClick = rightClick;
    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public abstract void spawn(World world);
}
