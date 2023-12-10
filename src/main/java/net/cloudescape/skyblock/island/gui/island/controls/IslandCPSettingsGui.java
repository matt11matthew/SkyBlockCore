package net.cloudescape.skyblock.island.gui.island.controls;

import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.island.gui.island.IslandControlPanelGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class IslandCPSettingsGui extends MenuFactory {

    /**
     * Builds the Island based control panel menu for a player to edit their island settings.
     * @param player - the player the gui will open to.
     */
    public IslandCPSettingsGui(Player player, Island island) {

        super("Control Panel - Settings", 4);

        List<Integer> blockedSlots = Arrays.asList(9, 17, 18, 26, 27, 35, 36, 44);

        int[] whiteGlass = new int[] {0, 8, 9, 17, 18, 26, 27, 35 };

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

        int slot = 1;
        for (IslandSettings setting : island.getSettings().keySet()) {
            addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS, 1, island.getSettings().get(setting) ? (byte) 5 : (byte) 14)
                    .setDisplayName("&e&l" + CloudUtils.setUppercaseEachStart(setting.name().replace("_", " ")))
                    .setLore(
                            "&7This setting is currently",
                            "",
                            "&8• &cLeft click to " + (island.getSettings().get(setting) ? " &a(Enabled)" : "&c(Disabled)")
                    )
                    .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    if (island.getSettings().get(setting)) {
                        // Disable
                        island.getSettings().put(setting, false);
                        CustomChatMessage.sendMessage(player, "Skyblock Settings", "You have &cdisabled &7" + CloudUtils.setUppercaseEachStart(setting.name().replace("_", " ")));
                    } else {
                        // Enable
                        island.getSettings().put(setting, true);
                        CustomChatMessage.sendMessage(player, "Skyblock Settings", "You have enabled &7" + CloudUtils.setUppercaseEachStart(setting.name().replace("_", " ")));

                    }

                    new IslandCPSettingsGui(player, island);
                }
            });

            if (blockedSlots.contains(slot + 1)) {
                slot += 3;
            } else {
                slot += 1;
            }
        }

        // Adds an item if a next page is available for the user.
        addItem(new MenuItem(28, new ItemFactory(Material.ARROW)
                .setDisplayName("&c&lBack Page")
                .setLore(
                        "&7Return to your Island control panel.",
                        "",
                        "&8• &cLeft click to go back"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new IslandControlPanelGui(player, island);
            }
        });

        openInventory(player);
    }
}
