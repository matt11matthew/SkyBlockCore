package net.cloudescape.skyblock.island.temple.customizing;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class FenceCustomizingGui extends MenuFactory {
    public FenceCustomizingGui(Player player, Island island) {
        super("Fence Customizer", 3);

        addItem(new MenuItem(18, new ItemFactory(Material.ARROW).setDisplayName(ChatColor.AQUA + "Go Back").build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                new CustomizingGui(player, island);
            }
        });

        int slot = 0;
        for (Material material : Arrays.stream(Material.values()).filter(material -> material.toString().endsWith("_FENCE")).collect(Collectors.toList())) {

            if (island.getUnlockedFences().contains(material.toString())) {
                addItem(new MenuItem(slot, new ItemFactory(material, 1).setDisplayName(ChatColor.GREEN + StringUtil.capitalizeWords(material.toString(),"_")).appendLore(ChatColor.GRAY + "Click to select").build()) {
                    @Override
                    public void click(Player player, ClickType clickType) {
                        player.closeInventory();
                        island.setCurrentFence(material.toString());
                        CustomChatMessage.sendMessage(player, "Fence", "Selected fence " + StringUtil.capitalizeWords(material.toString(),"_") + ".");
                        island.updateFence(() -> {

                        });
                    }
                });
                slot++;
            } else {
                addItem(new MenuItem(slot, new ItemFactory(material, 1).setDisplayName(ChatColor.RED + StringUtil.capitalizeWords(material.toString(),"_")).appendLore(ChatColor.RED + ChatColor.UNDERLINE.toString() + "LOCKED").build()) {
                    @Override
                    public void click(Player player, ClickType clickType) {
                        player.sendMessage(ChatColor.RED+"Not unlocked!");
                    }
                });
                slot++;
            }
        }

        openInventory(player);
    }
}
