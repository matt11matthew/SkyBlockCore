package net.cloudescape.skyblock.island.gui.island;

import com.cloudescape.modules.modules.skyblock.islandtop.IslandTopGui;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.gui.island.controls.IslandCPMembersGui;
import net.cloudescape.skyblock.island.gui.island.controls.IslandCPMinionsGui;
import net.cloudescape.skyblock.island.gui.island.controls.IslandCPSettingsGui;
import net.cloudescape.skyblock.utils.IslandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.UUID;

public class IslandControlPanelGui extends MenuFactory {

    /**
     * Builds the Island based control panel menu for a player to edit their island.
     *
     * @param player - the player the gui will open to.
     */
    public IslandControlPanelGui(Player player, Island island) {

        super("Control Panel", 4);

        if (player.getOpenInventory()!=null&&player.getOpenInventory().getTopInventory()!=null&&player.getOpenInventory().getTopInventory().getHolder() instanceof  IslandControlPanelGui) {
            return;
        }
        int[] whiteGlass = new int[]{0, 8, 9, 17, 18, 26, 27, 35};

        for (int slot = 0; slot < getInventory().getSize(); slot++) {
            addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 1)
                    .setDisplayName(" ")
                    .build()));
        }

        for (int slot : whiteGlass) {
            addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE)
                    .setDisplayName(" ")
                    .build()));
        }

        addItem(new MenuItem(10, new ItemFactory(Material.GRASS)
                .setDisplayName("&c&lTeleport to your island")
                .setLore(
                        "&7Go to your current island.",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                // Teleport to the players island

                IslandUtils.getIslandPlayerIsIn(player, island -> {
                    if (island == null) {
                        CustomChatMessage.sendMessage(player, "SkyBlock", "Could not find your island.");
                        return;
                    }
                    player.teleport(island.getLocation());
                    CustomChatMessage.sendMessage(player, "Skyblock", "You are being returned to your island!");
                });


            }
        });

        addItem(new MenuItem(12, new ItemFactory(Material.ENCHANTMENT_TABLE)
                .setDisplayName("&c&lQuests")
                .setLore(
                        "&7View all your active or unlocked quests.",
                        "",
                        "&c&lCOMING SOON!",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                CustomChatMessage.sendMessage(player, "Quests", "&cComing soon!");
            }
        });

        addItem(new MenuItem(14, new ItemFactory(Material.EMERALD_BLOCK)
                .setDisplayName("&c&lTop 10 Islands")
                .setLore(
                        "&7See all of the top Islands.",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                try {
                    new IslandTopGui(player, (document) -> {
                        UUID uuid = UUID.fromString(document.get("owner").toString());
                        CloudSkyblock.getPlugin().getIslandManager().getIsland(uuid, island -> {
                            player.teleport(island.getLocation());
                        });
                    });
                }catch (Exception e){
                    player.sendMessage(ChatColor.RED+"Could not load top 10 islands");
                }
            }

            ;
        });

        addItem(new MenuItem(16, new ItemFactory(Material.NETHER_STAR)
                .setDisplayName("&c&lIsland Warps")
                .setLore(
                        "&7Teleport to other people's warps.",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                CustomChatMessage.sendMessage(player, "Skyblock", "Coming soon!");
            }
        });

        addItem(new MenuItem(19, new SkullFactory()
                .setCustomData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3NWZlZDM2NDlmODdhNDhlNTExNjliNTdkY2NlNmQ0YThkYzk3ZGI0MThmMjJkNjBkOTZjMmE2MmIzZSJ9fX0=")
                .setDisplayName("&c&lList Team Members")
                .setLore(
                        "&7View the members of your team.",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new IslandCPMembersGui(player, island, 1);
            }
        });

        addItem(new MenuItem(21, new ItemFactory(Material.EXP_BOTTLE)
                .setDisplayName("&c&lIsland Level")
                .setLore(
                        "&7Your current Island level is:",
                        "&7" + island.getIslandLevel()
                ).build()));

        addItem(new MenuItem(23, new ItemFactory(Material.COMPASS)
                .setDisplayName("&c&lIsland Settings")
                .setLore(
                        "&7Access your island specific setting.",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new IslandCPSettingsGui(player, island);
            }
        });

        addItem(new MenuItem(25, new ItemFactory(Material.ARMOR_STAND)
                .setDisplayName("&c&lIsland Minions")
                .setLore(
                        "&7Access your island specific minions.",
                        "",
                        "&c&lCOMING SOON!",
                        "",
                        "&8• &cLeft click to open"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
//                new IslandCPMinionsGui(player, island, 1);
            }
        });

        addItem(new MenuItem(31, new ItemFactory(Material.PAPER)
                .setDisplayName("&c&lIsland Balance")
                .setLore(
                        "&7Your current island balance is: &c" + island.getBalance(),
                        "",
                        "&8• &cLeft click to view commands"
                ).build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                CustomChatMessage.sendMessage(player, "Island Bank", "Commands");
                CustomChatMessage.sendMessage(player, "- /is bank deposit <amount>");
                CustomChatMessage.sendMessage(player, "- /is bank withdraw <amount>");
            }
        });

        openInventory(player);
    }
}
