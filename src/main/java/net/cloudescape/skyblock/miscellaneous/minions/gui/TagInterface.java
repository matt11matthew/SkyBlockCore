package net.cloudescape.skyblock.miscellaneous.minions.gui;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TagInterface extends MenuFactory {

    public TagInterface(Player player, Island island, Minion minion){
        super("Available Tags for your island.", 4);

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

        if (island.getAvailableMinionTags().size() < 1) {
            addItem(new MenuItem(13, new ItemFactory(Material.PAPER).setDisplayName("&c&lYou have no available tags!").build()));
            openInventory(player);
            return;
        }

        int slot = 10;

        for (MinionTagType available : island.getAvailableMinionTags()) {
            addItem(new MenuItem(slot, new ItemFactory(Material.PAPER)
            .setDisplayName("&8[&5" + StringUtil.capitalizeFirstLetter(available.name()) + "&8] &bTag")
                    .setLore(
                            "&8• &7Click to equip this tag!"
                    )
            .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    if (minion != null) {
                        minion.setMinionTag(available);
                        minion.getMinion().setCustomName(ChatColor.translateAlternateColorCodes('&', ("&8[&5" + minion.getMinionTag().getTagText() + "&8] &7") + minion.getName()));
                        CustomChatMessage.sendMessage(player, "Minion", "Tag updated to " + StringUtil.capitalizeFirstLetter(available.name()) + ".");
                        player.closeInventory();
                    }
                }
            });
            slot += 1;
        }

        openInventory(player);
    }
}
