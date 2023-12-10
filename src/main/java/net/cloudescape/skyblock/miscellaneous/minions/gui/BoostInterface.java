package net.cloudescape.skyblock.miscellaneous.minions.gui;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BoostInterface extends MenuFactory {

    public BoostInterface(Player player, Island island, Minion minion){
        super("Available Boosts for your island minions.", 4);

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

        if (island.getAvailableBoostTypes().size() < 1) {
            addItem(new MenuItem(13, new ItemFactory(Material.PAPER).setDisplayName("&c&lYou have no available boosts!").build()));
            openInventory(player);
            return;
        }

        int slot = 10;

        for (MinionBoostType available : island.getAvailableBoostTypes()) {

            if (!available.isAllowedMinion(minion.getClass())) {
                continue;
            }

            addItem(new MenuItem(slot, new ItemFactory(Material.PAPER)
            .setDisplayName("&8[&b" + StringUtil.capitalizeFirstLetter(available.name()) + "&8] &bBoost")
                    .setLore(
                            StringUtil.getMenuLine(),
                            "&8• Click to equip this suit!",
                            StringUtil.getMenuLine()
                    )
            .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    if (minion != null) {
                        // do the duration, should work now.
                        minion.setBoost(available.getMinionBoost(available, (1000 * 60) * 5));
                        CustomChatMessage.sendMessage(player, "Minion", "Suit updated to " + StringUtil.capitalizeFirstLetter(available.name()) + ".");
                        player.closeInventory();
                    }
                }
            });
            slot += 1;
        }

        openInventory(player);
    }
}
