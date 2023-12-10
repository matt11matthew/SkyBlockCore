package net.cloudescape.skyblock.miscellaneous.minions;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.gui.BankerInterface;
import net.cloudescape.skyblock.miscellaneous.minions.gui.ButcherInterface;
import net.cloudescape.skyblock.miscellaneous.minions.gui.DirectionInterface;
import net.cloudescape.skyblock.miscellaneous.minions.gui.MinerInterface;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Banker;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.cloudescape.skyblock.utils.ParserUtil;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public class MinionManager implements Listener {

    public MinionManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                CloudSkyblock.getPlugin().getIslandManager().getLoadedIslands().stream()
                        .forEach(island1 -> {
                            island1.getLoadedMinions().stream()
                                    .forEach(minion -> {
                                        minion.removeHunger(1);
                                    });
                        });
            }
        }.runTaskTimer(CloudSkyblock.getPlugin(), 0L, 20 * 80);
    }

    public boolean isMinion(Entity entity) {
        if (!(entity instanceof ArmorStand)) {
            return false;
        }

        return CloudSkyblock.getPlugin().getIslandManager()
                .getLoadedIslands().stream().filter(island ->
                        island.getLoadedMinions()
                                .stream()
                                .filter(minion -> minion.getMinion().getUniqueId().equals(entity.getUniqueId()))
                                .findAny()
                                .orElse(null) != null
                ).findAny()
                .orElse(null) == null ? false : true;
    }

    public Minion getMinion(Entity entity) {
        if (!(entity instanceof ArmorStand))
            return null;

        Island is = CloudSkyblock.getPlugin().getIslandManager().getLoadedIslands()
                .stream()
                .filter(island -> island.getLoadedMinions()
                        .stream()
                        .filter(minion -> minion.getMinion().getUniqueId().equals(entity.getUniqueId()))
                        .findAny()
                        .orElse(null)
                        != null).findAny().orElse(null);

        if (is != null) {
            return is.getLoadedMinions().stream().filter(minion -> minion.getMinion().getUniqueId().equals(entity.getUniqueId())).findFirst().orElse(null);
        }

        return null;
    }

    public void pickupMinion(Minion minion, Player player) {
        if (minion.getIsland().getIslandMembers().entrySet().stream().filter(member -> member.getKey().equals(player.getUniqueId())).findAny().orElse(null) == null) {
            CustomChatMessage.sendMessage(player, "&6[&eMinions&6]", "&cYou must be a member of the island to edit minions!");
            return;
        }

        if (player.getInventory().firstEmpty() < 0) {
            CustomChatMessage.sendMessage(player, "&6[&eMinions&6]", "&cPlease clear room in your inventory.");
            return;
        }

        Island island = minion.getIsland();

        // TODO: check if member has the right permission to remove a minion.

        ArrayList<String> lore = new ArrayList<>();

        lore.add("");
        lore.add("&c[&eType&c]       &7-> " + StringUtil.capitalizeFirstLetter(minion.getType().name()));
        lore.add("&c[&Name&c]        &7-> " + minion.getName());
        lore.add("&c[&eHealth&c]     &7-> " + minion.getHealth());
        lore.add("&c[&eHunger&c]     &7-> " + minion.getHunger());
        lore.add("&c[&eBoost&c]      &7-> " + minion.getBoost());

        switch (minion.getType()) {
            case MINER:
                Miner miner = (Miner) minion;
                lore.add("&c[&eBlocks Mined] &7-> " + miner.getBlocksMined());
                break;
        }

        lore.add("");

        ItemStack item = new ItemFactory(Material.ARMOR_STAND)
                .setDisplayName("    " + minion.getName() + "    ")
                .setLore("lore")
                .build();

        minion.kill(false, island);
        player.getInventory().addItem(item);
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.ARMOR_STAND) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().size() < 3)
                return;

            MinionType type = MinionType.valueOf(itemStack.getItemMeta().getLore().get(0));
            Optional<Integer> id = ParserUtil.parseInt(itemStack.getItemMeta().getLore().get(1));
            UUID islandUUID = UUID.fromString(itemStack.getItemMeta().getLore().get(2));

            if (id.isPresent()) {
                Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

                if (islandOptional.isPresent()) {

                    Island island = islandOptional.get();

                    if (!islandUUID.equals(island.getIslandUuid())) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "This minion does not belong to this island.");
                        return;
                    }

                    if (!island.isIslandMember(player.getUniqueId())) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You are not a member of this island!");
                        return;
                    }

                    Booster minionBooster = island.getBoosterByType(BoosterType.MINION_LIMIT).get();
                    int maxLimit = 2;

                    switch (minionBooster.getLevel()) {
                        case 1:
                            maxLimit = 2;
                            break;
                        case 2:
                            maxLimit = 5;
                            break;
                        case 3:
                            maxLimit = 8;
                            break;
                        case 4:
                            maxLimit = 10;
                            break;
                    }

                    long currentCount = island.getWorld().getEntitiesByClass(ArmorStand.class).stream().filter(stand -> stand.hasMetadata("minion")).count();

                    if (currentCount >= maxLimit) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You have reached your maximum limit of minions allowed at this time.");
                        event.setCancelled(true);
                        return;
                    }

                    Optional<Minion> minionOptional = island.getMinionById(id.get());

                    if (minionOptional.isPresent()) {

                        Location spawn = event.getClickedBlock().getLocation().add(0, 2, 0);
                        switch (type){
                            case MINER:
                                Miner miner = (Miner) minionOptional.get();
                                miner.spawn(spawn);
                                new DirectionInterface(player, miner);
                                break;
                            case BUTCHER:
                                Butcher butcher = (Butcher) minionOptional.get();
                                butcher.spawn(spawn);
                                new DirectionInterface(player, butcher);
                                break;
                            case BANKER:
                                Banker banker = (Banker) minionOptional.get();
                                banker.spawn(spawn);
                                new DirectionInterface(player, banker);
                                break;
                        }

                        player.getInventory().remove(player.getInventory().getItemInMainHand());
                        CustomChatMessage.sendMessage(player, "Skyblock Minions", "Spawned your minion!");
                    } else {
                        CustomChatMessage.sendMessage(player, "Skyblock Minions", "There was an issue placing your minion! Contact an Admin!");
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        if (event.getEntityType() != EntityType.ARMOR_STAND) {
            return;
        }

        ArmorStand stand = (ArmorStand) event.getEntity();
        Optional<Island> island = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (!island.isPresent()) {
            return;
        }

        Island islandPresent = island.get();

        if (islandPresent.getMinionByEntity(stand) != null) {
            Minion minion = islandPresent.getMinionByEntity(stand);

            if (player.isSneaking()) {
                player.getInventory().addItem(new ItemFactory(Material.ARMOR_STAND)
                        .setDisplayName("&bMinion Spawner")
                        .setLore(minion.getType().name(), "" + minion.getId(), minion.getIsland().getIslandUuid().toString())
                        .build());
                CustomChatMessage.sendMessage(player, "Skyblock Minions", "Place the stand on the floor to spawn your minion!");
                minion.kill(false, islandPresent);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        ArmorStand stand = event.getRightClicked();
        Optional<Island> island = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (!island.isPresent()) {
            return;
        }

        Island islandPresent = island.get();

        if (islandPresent.getMinionByEntity(stand) != null) {
            Minion minion = islandPresent.getMinionByEntity(stand);

            // TODO: OPEN MINION GUI
            switch (minion.getType()) {
                case MINER:
                    new MinerInterface(player, minion);
                    break;
                case BUTCHER:
                    new ButcherInterface(player, minion);
                    break;
                case BANKER:
                    new BankerInterface(player, minion);
                    break;
            }

            event.setCancelled(true);
        }
    }
}