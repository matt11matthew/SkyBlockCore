package net.cloudescape.skyblock.island.gui.island;

import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class IslandCPSettingsGui extends MenuFactory {

    /**
     * Builds the Island based control panel menu for a player to edit their island settings.
     * @param player - the player the gui will open to.
     */
    public IslandCPSettingsGui(Player player, Island island) {

        super("Control Panel - Settings", 6);

        // TODO implement pages.
        int slot = 0;
        for (IslandSettings setting : island.getSettings().keySet()) {
            addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE)
                    .setDisplayName("&e&l" + CloudUtils.setUppercaseEachStart(setting.name().replace("_", " ")))
                    .setLore(
                            StringUtil.getMenuLine(),
                            "    &7Click to enable or disable your island settings!",
                            StringUtil.getMenuLine(),
                            "                   &7This setting is",
                            "                      " + (island.getSettings().get(setting) ? " &a(Enabled)" : "&c(Disabled)"),
                            StringUtil.getMenuLine()
                    )
                    .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    boolean opposite = !island.getSettings().get(setting);
                    island.setSetting(setting, opposite);
                    player.closeInventory();
                    new IslandCPSettingsGui(player, island);
                }
            });

            slot += 1;
        }

        openInventory(player);
    }
}
