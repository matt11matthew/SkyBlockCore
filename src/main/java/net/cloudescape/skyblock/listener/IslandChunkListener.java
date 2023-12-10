package net.cloudescape.skyblock.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class IslandChunkListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        Location location = new Location(world, event.getChunk().getX(), 20, event.getChunk().getZ());
        if (world.getName().equals("world") || world.getName().equals("world_nether") || world.getName().equals("world_the_end")) {
            return;
        }
        if (worldBorder != null && !worldBorder.isInside(location)) {
            event.getChunk().unload(true);
        }
    }

}
