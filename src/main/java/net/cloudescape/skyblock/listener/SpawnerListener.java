package net.cloudescape.skyblock.listener;

import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.island.spawner.GolemSpawnerInfo;
import net.cloudescape.skyblock.island.spawner.GolemType;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.utils.IslandUtils;
import net.cloudescape.skyblock.utils.ItemUtils;
import net.cloudescape.skyblock.utils.SpawnerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class SpawnerListener implements Listener {

    /**
     * This is the listener for spawner placement
     * The first section checks to see if the block placed is a spawner and if the player is on an island
     * Then we will handle getting spawners around the block to see if we should merge them
     * If we can't find spawners to merge we will create a new spawner
     *
     * @param event The block place event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer(); //The player
        Block blockPlaced = event.getBlockPlaced();// The placed spawner
        Material type = blockPlaced.getType();//The type of placed block
        ItemStack spawnerItemStack = player.getItemInHand(); // Spawner item stack

        /*
        Checking if the block is a mob spawner
        Checking if the player ItemStack is the same as the block placed
        Checking if the itemstack has itemmeta
        Checking if the player is on an island they are allowed to interact with
         */
        if ((type == Material.MOB_SPAWNER) && (spawnerItemStack.hasItemMeta()) && (spawnerItemStack.getType() == type) && IslandUtils.isOnIsland(player, blockPlaced)) {
            Island island = IslandUtils.getIsland(player, blockPlaced); // The island

            /*
            Checking if the island is not null
            this will prevent null pointer exceptions
           */
            if (island == null) {
                CustomChatMessage.sendMessage(player, "Island", "We could not find the island."); // Send the error message to the user
                event.setCancelled(true); // We cancel the event if the island is null
                return; // Returning because the event is cancelled
            }


            /*
            This will check if the spawner type is not invalid
            Will send error message if it is
             */
            EntityType entityType = SpawnerUtils.getEntityTypeFromItemStack(spawnerItemStack); // Getting the entity type
            if (entityType == null) { // Checking if the entity type is null
                CustomChatMessage.sendMessage(player, "Spawner", "This spawner is invalid."); // Send the error message to the user
                event.setCancelled(true); // We cancel the event if the entity type is null
                return; // Returning because the event is cancelled
            }


            GolemSpawnerInfo golemSpawnerInfo = SpawnerUtils.getSpawnerInfoFromSpawnerItemStack(spawnerItemStack); // The spawner
            if (golemSpawnerInfo == null) {
                event.setCancelled(true);
                CustomChatMessage.sendMessage(player, "Spawner", "This spawner is invalid."); // Send the error message to the user
                return;
            }

            GolemSpawner spawnerToMergeWith = IslandUtils.getSpawnerToMergeWith(island, golemSpawnerInfo.getEntityType(), golemSpawnerInfo.getTier(), golemSpawnerInfo.getGolemType(), blockPlaced, 5);
            if (spawnerToMergeWith != null && spawnerToMergeWith.getCount() < 5) {
                event.setCancelled(true); //Cancel
                ItemUtils.removeOneHandItemFromPlayer(player); //Remove hand spawner
                island.upgradeSpawnerCount(spawnerToMergeWith.getLocation(player.getWorld()));//Upgrade the spawner
                CustomChatMessage.sendMessage(player, "Spawner", "You've added 1x spawner to " + spawnerToMergeWith.getDisplayName()); // Send the error message to the user
                return;
            }
            /*
            Checking if the spawner count is above or equal to the cap of 5
            Spawner count is increased when a spawner is placed
            Spawner count is decreased when a spawner is removed
             */
            int spawnerCount = island.getSpawnerCount(); // Island spawner count
            Optional<Booster> boosterByType = island.getBoosterByType(BoosterType.SPAWNER_LIMIT);
            if (boosterByType.isPresent()) {
                Booster booster = boosterByType.get();
                final int level = booster.getLevel();
                int[] counts = {20, 35, 60, 90, 130};

                int allowedCount = counts[level - 1];
                if (spawnerCount >= allowedCount) { // Checking if the count is greator than or equal to 5
                    CustomChatMessage.sendMessage(player, "Spawners", "You cannot place more than " + allowedCount + " spawner(s) on an island at one time!"); // The error message
                    event.setCancelled(true); // We cancel the event because the user is not allowed to place more than 5 spawners.
                    return; // Returning because the event is cancelled
                }
            }
            SpawnerUtils.setEntityTypeOfSpawner(blockPlaced, golemSpawnerInfo.getEntityType());
            GolemSpawner golemSpawner = island.addSpawner(golemSpawnerInfo.toGolemSpawner(blockPlaced));
            golemSpawner.spawnHologram(player.getWorld());
            golemSpawner.spawn(player.getWorld());
            island.updateSpawner(player.getWorld(), golemSpawner);
            island.increaseSpawnerCount();
            island.update();
            CustomChatMessage.sendMessage(player, "Spawner", "You've placed 1x spawner " + player.getItemInHand().getItemMeta().getDisplayName()); // Send the error message to the user

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasMetadata("gtier") && entity.hasMetadata("gcount")) {
            int tier = entity.getMetadata("gtier").get(0).asInt();
            if (entity.hasMetadata("gtype")) {

                GolemType golemType = GolemType.valueOf(entity.getMetadata("gtype").get(0).asString());
                event.getDrops().clear();

                if (new Random().nextInt(100) <= (tier * 100) || tier == 5) {
                    int amount = new Random().nextInt(3) + 1;
                    switch (golemType) {
                        case IRON:
                            event.getDrops().add(new ItemStack(Material.IRON_INGOT, amount));
                            break;
                        case GOLD:
                            event.getDrops().add(new ItemStack(Material.GOLD_INGOT, amount));
                            break;
                        case DIAMOND:
                            event.getDrops().add(new ItemStack(Material.DIAMOND, amount));
                            break;
                        case EMERALD:
                            event.getDrops().add(new ItemStack(Material.EMERALD, amount));
                            break;
                    }
                }
            }
            final List<ItemStack> drops = new ArrayList<>(event.getDrops());
            event.getDrops().clear();
            for (ItemStack itemStack : drops) {
                int amount = itemStack.getAmount();
                ItemUtils.dropItemStack(event.getEntity().getLocation(), itemStack, amount);

            }
        }
    }
}
