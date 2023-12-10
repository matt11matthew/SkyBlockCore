package net.cloudescape.skyblock.listener;

import com.cloudescape.CloudCore;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class IslandLoadingListener implements Listener {

    private Map<World, Long> removalMap;

    public IslandLoadingListener() {
        this.removalMap = new HashMap<>();

        List<World> tempWorlds = new ArrayList<>();

//        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
//            for (Map.Entry<World, Long> world : removalMap.entrySet()) {
//
//                if (System.currentTimeMillis() >= world.getValue()) {
//                    // TODO unload world.
//
//                    Optional<Island> island = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(world.getKey());
//                    if (island.isPresent()) {
//
//                        CloudSkyblock.getPlugin().getIslandManager().unloadIsland(island.get(), () -> {
//                            Logger.log(world.getKey().getName() + " was successfully unload!");
//                        }, () -> {
//                            Logger.log(world.getKey().getName() + " could not be successfully unloaded.");
//                        });
//
//                        tempWorlds.add(world.getKey());
//                    }
//                }
//            }
//
//            // So I don't run into concurrency issues with ^
//            for (int i = 0; i < tempWorlds.size(); i++) {
//                removalMap.remove(tempWorlds.get(i));
//                tempWorlds.remove(i);
//            }
//        }, "islandremoval", CloudRunnableType.SYNC, 1L, TimeUnit.SECONDS);
    }

    /*
     * Just incase switching world event isn't called
     * when a player leaves the server.
     */
     @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (world.getName().equals("world") || world.getName().equals("world_nether") || world.getName().equals("world_the_end")) {
            return;
        }

        if (removalMap.containsKey(world)) {
            return;
        }

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(world);

        if (islandOptional.isPresent() && (world.getPlayers().size()) < 1) {
            // 5 minutes in ticks.
            if (CloudCore.getInstance().getCloudServer().isDev_mode()) {
                removalMap.put(world, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
            } else {
                removalMap.put(world, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getFrom();
        World worldTo = event.getPlayer().getWorld();

        if (world.getName().equals("world") || world.getName().equals("world_nether") || world.getName().equals("world_the_end")) {
            return;
        }

        // Will remove the world the player is travelling to from the 5 minute removal time.
        if (removalMap.containsKey(worldTo)) {
            removalMap.remove(worldTo);
            return;
        }

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(world);

        if (islandOptional.isPresent() && world.getPlayers().size() < 1) {
            // 5 minutes in ticks.
            if (CloudCore.getInstance().getCloudServer().isDev_mode()) {
                removalMap.put(world, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
            } else {
                removalMap.put(world, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
            }
        }
    }
}
