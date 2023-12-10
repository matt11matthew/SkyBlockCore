package net.cloudescape.skyblock.island.temple.customizing;

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
import org.bukkit.inventory.ItemFlag;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class CustomizingGui extends MenuFactory {

    public CustomizingGui(Player player, Island island) {
        super("Customizing Island", 3);

        int slot = 19;
        if (player.getName().equals("matt11matthew")) {
            for (DyeColor dyeColor : DyeColor.values()) {
                if (!island.getUnlockedCarpets().contains(dyeColor.toString())) {
                    island.unlockCarpet(dyeColor.toString());
                }
            }
        }

        addItem(new MenuItem(13, new ItemFactory(Material.CARPET)
                .setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Temple Carpet Color")
                .addItemFlag(ItemFlag.values()).build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                new CarpetCustomizingGui(player, island);
            }
        });
        addItem(new MenuItem(14, new ItemFactory(Material.FENCE)
                .setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Temple Fence")
                .addItemFlag(ItemFlag.values()).build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                new FenceCustomizingGui(player, island);
            }
        });

        if (island == null || island.getBoosters() == null) {
            return;
        }


        openInventory(player);
    }

    private String generateProgressBar(int current, int max, char c, ChatColor progressColor, ChatColor remainingColor, boolean bold) {
        String progressColorString = progressColor.toString();
        String remainingColorString = remainingColor.toString();
        if (bold) {
            progressColorString += ChatColor.BOLD.toString();
            remainingColorString += ChatColor.BOLD.toString();
        }
        return progressColorString + StringUtil.repeat(c, current) + remainingColorString + StringUtil.repeat(c, (max - current));
    }
}
