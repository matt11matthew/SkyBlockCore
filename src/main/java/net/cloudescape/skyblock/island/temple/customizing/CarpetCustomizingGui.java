package net.cloudescape.skyblock.island.temple.customizing;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class CarpetCustomizingGui extends MenuFactory {
    public CarpetCustomizingGui(Player player, Island island) {
        super("Carpet Customizer", 3);

        addItem(new MenuItem(18, new ItemFactory(Material.ARROW).setDisplayName(ChatColor.AQUA + "Go Back").build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                new CustomizingGui(player, island);
            }
        });

        int slot = 0;
        for (DyeColor dyeColor : DyeColor.values()) {
            if (island.getUnlockedCarpets().contains(dyeColor.toString())) {
                addItem(new MenuItem(slot, new ItemFactory(Material.CARPET, 1, dyeColor.getWoolData()).setDisplayName(ChatColor.GREEN + StringUtil.capitalizeFirstLetter(dyeColor.toString()) + " Carpet").appendLore(ChatColor.GRAY + "Click to select").build()) {
                    @Override
                    public void click(Player player, ClickType clickType) {
                        player.closeInventory();
                        island.setCurrentCarpet(dyeColor.toString());
                        CustomChatMessage.sendMessage(player, "Carpet", "Selected carpet " + StringUtil.capitalizeFirstLetter(dyeColor.toString()) + ".");
                        island.updateCarpet(() -> {

                        });
                    }
                });
                slot++;
            } else {
                addItem(new MenuItem(slot, new ItemFactory(Material.CARPET, 1, dyeColor.getWoolData()).setDisplayName(ChatColor.RED + StringUtil.capitalizeFirstLetter(dyeColor.toString()) + " Carpet").appendLore(ChatColor.RED + ChatColor.UNDERLINE.toString() + "LOCKED").build()) {
                    @Override
                    public void click(Player player, ClickType clickType) {
                        player.closeInventory();
                        new CustomizingGui(player, island);
                    }
                });
                slot++;
            }
        }
        openInventory(player);
    }
}
