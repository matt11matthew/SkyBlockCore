package net.cloudescape.skyblock.island.listener;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.utils.callback.Callback;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.Optional;
import java.util.Random;

public class OreGenerationListener implements Listener {

    private final BlockFace[] faces = new BlockFace[]
            {
                    BlockFace.SELF,
                    BlockFace.UP,
                    BlockFace.DOWN,
                    BlockFace.NORTH,
                    BlockFace.EAST,
                    BlockFace.SOUTH,
                    BlockFace.WEST
            };

    @EventHandler
    public void onOreGeneration(BlockFromToEvent event) {
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        Optional<Island> optionalIsland = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(blockTo.getWorld());

        optionalIsland.ifPresent(island -> {

            int id = event.getBlock().getTypeId();
            if(id >= 8 && id <= 11) {
                Block b = event.getToBlock();
                int toid = b.getTypeId();
                if(toid == 0) {
                    if(generatesCobble(id, b)) {
                        getRandomBlockByPercentage(island, blockTo::setType);
                    }
                }
            }
        });
    }

    public boolean generatesCobble(int id, Block b)
    {
        int mirrorID1 = (id == 8 || id == 9 ? 10 : 8);
        int mirrorID2 = (id == 8 || id == 9 ? 11 : 9);
        for(BlockFace face : faces) {
            Block r = b.getRelative(face, 1);
            if(r.getTypeId() == mirrorID1 || r.getTypeId() == mirrorID2) {
                return true;
            }
        }
        return false;
    }

    private void getRandomBlockByPercentage(Island island, Callback<Material> to) {

        Random random = new Random();
        double chance = random.nextInt(100);

        island.getBoosterByType(BoosterType.ORE_GENERATION).ifPresent(booster -> {
            int level = booster.getLevel();

            switch (level) {
                case 0:
                    if (chance <= 5) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
                case 1:
                    if (chance <= 5) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
                case 2:
                    if (chance >= 0 && chance <= 5) {
                        to.call(Material.DIAMOND_ORE);
                    } else if (chance > 5 && chance <= 15) {
                        to.call(Material.GOLD_ORE);
                    } else if (chance > 15 && chance <= 45) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
                case 3:
                    if (chance >= 0 && chance <= 15) {
                        to.call(Material.EMERALD_ORE);
                    } else if (chance > 15 && chance <= 35) {
                        to.call(Material.DIAMOND_ORE);
                    } else if (chance > 35 && chance < 55) {
                        to.call(Material.GOLD_ORE);
                    } else if (chance > 55 && chance <= 75) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
                case 4:
                    if (chance >= 0 && chance <= 25) {
                        to.call(Material.EMERALD_ORE);
                    } else if (chance > 25 && chance <= 50) {
                        to.call(Material.DIAMOND_ORE);
                    } else if (chance > 50 && chance <= 70) {
                        to.call(Material.GOLD_ORE);
                    } else if (chance > 70 && chance <= 90) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
                case 5:
                    if (chance >= 0 && chance <= 25) {
                        to.call(Material.EMERALD_ORE);
                    } else if (chance > 25 && chance <= 50) {
                        to.call(Material.DIAMOND_ORE);
                    } else if (chance > 50 && chance <= 70) {
                        to.call(Material.GOLD_ORE);
                    } else if (chance > 70 && chance <= 90) {
                        to.call(Material.IRON_ORE);
                    } else {
                        to.call(Material.COBBLESTONE);
                    }
                    break;
            }
        });
    }
}
