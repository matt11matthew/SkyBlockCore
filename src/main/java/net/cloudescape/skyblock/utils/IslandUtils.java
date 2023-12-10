package net.cloudescape.skyblock.utils;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.island.spawner.GolemType;
import net.cloudescape.skyblock.utils.callback.Callback;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class IslandUtils {
    public static Island getIsland(Player player, Block block) {
        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return null;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (islandOptional.isPresent()) {

            Island island = islandOptional.get();

            if (!island.isIslandMember(player.getUniqueId()) && !island.getIslandSettingEnabled(IslandSettings.PUBLIC_BUILD)) {
                return null;
            }

//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()))) {
//                return null;
//            }

            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()))) {
                return null;
            }
            return island;
        }
        return null;
    }

    public static void getCountOnIslandAsync(Island island, Material type, Callback<Integer> count) {

        new BukkitRunnable() {
            @Override
            public void run() {

                int blockCount = 0;
                int radius = island.getProtectionDistance();

                for (double x = island.getCentralX() - radius; x <= island.getCentralX() + radius; x++) {
                    for (double y = 0; y <= island.getWorld().getMaxHeight(); y++) {
                        for (double z = island.getCentralZ() - radius; z <= island.getCentralZ() + radius; z++) {
                            Location location = new Location(island.getWorld(), x, y, z);
                            if (location.getBlock().getType() == Material.AIR) {
                                continue;
                            }

                            Material block = location.getBlock().getType();

                            if (block == type) {
                                blockCount += 1;
                            }
                        }
                    }
                }

                count.call(blockCount);
            }
        }.runTaskAsynchronously(CloudSkyblock.getPlugin());
    }

    public static boolean isOnIsland(Player player, Block block) {
        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return false;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (islandOptional.isPresent()) {

            Island island = islandOptional.get();

            if (!island.isIslandMember(player.getUniqueId()) && !island.getIslandSettingEnabled(IslandSettings.PUBLIC_BUILD)) {
                return false;
            }
//
//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()))) {
//                return false;
//            }

            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()))) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static GolemSpawner getSpawnerToMergeWith(Island island, EntityType entityType, int tier, GolemType golemType, Block block, int radius) {
        GolemSpawner returnGolemSpawner = null;
        for (int i = 1; i <= radius; i++) {
            for (BlockFace blockFace : Arrays.asList(BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST)) {
                Block relative = block.getRelative(blockFace, i);
                if (relative.getType() == Material.MOB_SPAWNER && relative.getData() == block.getData() && island.getGolemSpawners().containsKey(relative.getLocation()) && island.getGolemSpawner(relative.getLocation()) != null && island.getGolemSpawner(relative.getLocation()).getEntityType() == entityType ) {
                    GolemSpawner golemSpawner = island.getGolemSpawner(relative.getLocation());
                    if (relative.getLocation().equals(block.getLocation())) {
                        continue;
                    }
                    if (golemSpawner.getEntityType() == EntityType.IRON_GOLEM && golemType != null && golemSpawner.getGolemType() == golemType) {
                        returnGolemSpawner = golemSpawner;
                    } else if (golemSpawner.getEntityType() != EntityType.IRON_GOLEM) {
                        returnGolemSpawner = golemSpawner;
                    }
                }
            }
        }
        return returnGolemSpawner;
    }

    public static void getIslandPlayerIsIn(Player player, Consumer<Island> islandConsumer) {
        UUID islandUuid = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getIslandUuid(player.getUniqueId());
        if (islandUuid == null) {
            islandConsumer.accept(null);
            return;
        }
        CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), playerIsland -> {
            if (playerIsland != null) {
                islandConsumer.accept(playerIsland);
            } else {
                CloudSkyblock.getPlugin().getIslandManager().getIslandByUUID(player, islandUuid, island -> {
                    if (island == null) {
                        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(player.getUniqueId(), null);
                    }
                    islandConsumer.accept(island);
                });
            }
        });
    }

    public static Island getLoadedIsland(Player player) {
        UUID islandUuid = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getIslandUuid(player.getUniqueId());
        if (islandUuid == null) {
            return null;
        }
        for (Island island : CloudSkyblock.getPlugin().getIslandManager().getLoadedIslands()) {
            if (island.getIslandUuid().equals(islandUuid)) {
                return island;
            }
        }
        return null;

    }
}