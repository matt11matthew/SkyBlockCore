package net.cloudescape.skyblock.island.spawner;

import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Matthew E on 4/16/2018.
 */
public class SpawnerMenu extends MenuFactory {
    public SpawnerMenu(Player player, Island island, GolemSpawner golemSpawner) {
        super("Spawner", 3);

        for (int i = 0; i < 27; i++) {
//            if (i == 11) {
//                if (golemSpawner.getTier() < 5) {
//
//                    int multiplier = 1;
//                    if (golemSpawner.getEntityType() == EntityType.IRON_GOLEM) {
//                        switch (golemSpawner.getGolemType()) {
//                            case IRON:
//                                multiplier = 1;
//                                break;
//                            case GOLD:
//                                multiplier = 2;
//                                break;
//                            case DIAMOND:
//                                multiplier = 3;
//                                break;
//                            case EMERALD:
//                                multiplier = 4;
//                                break;
//                        }
//                    }
//                    final int cost = ((golemSpawner.getTier() * 10) * golemSpawner.getCount()) * multiplier;
//                    addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData()).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + (golemSpawner.getTier() * 20) + "%").appendLore(ChatColor.GRAY + "Click to level up for " + cost + " XP Levels").build()) {
//                        @Override
//                        public void click(Player player, ClickType clickType) {
//                            player.closeInventory();
//                            if (golemSpawner.getTier() == 5) {
//                                return;
//                            }
//                            if (player.getLevel() < cost) {
//                                int remainingLevels = cost - player.getLevel();
//                                player.sendMessage(ChatColor.RED + "You need " + ChatColor.UNDERLINE + remainingLevels + ChatColor.RED + " more level(s).");
//                                return;
//                            }
//                            golemSpawner.setTier(golemSpawner.getTier() + 1);
//                            player.sendMessage(ChatColor.AQUA + "Upgraded percent " + ChatColor.GRAY + ((golemSpawner.getTier() - 1) * 20) + "% -> " + (golemSpawner.getTier() * 20) + "%");
//                            new SpawnerMenu(player, island, golemSpawner);
//                        }
//                    });
//                } else {
//                    addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData()).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + (golemSpawner.getTier() * 20) + "%").appendLore(ChatColor.RED + "Max Percent").build()) {
//                        @Override
//                        public void click(Player player, ClickType clickType) {
//
//                        }
//                    });
//                }
            if (i == 14) {
                String typeString = "";
                if (golemSpawner.getEntityType() == EntityType.IRON_GOLEM) {
                    typeString = ChatColor.AQUA + "Type: " + ChatColor.GRAY + golemSpawner.getGolemType().toString().toLowerCase();
                } else {

                    typeString = ChatColor.AQUA + "Type: " + ChatColor.GRAY + golemSpawner.getEntityType().getName();
                }
                addItem(new MenuItem(i, new ItemFactory(Material.BOOK, 1).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "SPAWNER INFO")
                        .appendLore(typeString).appendLore(ChatColor.AQUA + "Percent: " + ChatColor.GRAY + (golemSpawner.getTier() * 20) + "%").appendLore(ChatColor.AQUA + "Count: " + ChatColor.GRAY + golemSpawner.getCount()).build()) {
                    @Override
                    public void click(Player player, ClickType clickType) {

                    }
                });

            } else {
                addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE).setDisplayName(" ").build()));
            }
        }
        openInventory(player);
    }
}
