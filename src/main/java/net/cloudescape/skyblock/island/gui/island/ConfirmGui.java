package net.cloudescape.skyblock.island.gui.island;

import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Matthew E on 4/13/2018.
 */
public class ConfirmGui extends MenuFactory {
    public ConfirmGui(Player player, String name, Runnable onConfirm, Runnable onCancel) {

        super("Confirm " + name, 3);

//        int[] whiteGlass = new int[] {0, 8, 9, 17, 18, 26, 27 };

//        for (int slot = 0; slot < getInventory().getSize(); slot++) {
//            addItem(new MenuItem(slot, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 1)
//                    .setDisplayName(" ")
//                    .build()));
//        }

        setBorder(new MenuItem(5, new ItemFactory(Material.STAINED_GLASS_PANE)
                .setDisplayName(" ")
                .build()));

        addItem(new MenuItem(12, new ItemFactory(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getWoolData()).setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Confirm " + ChatColor.WHITE + name).build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                onConfirm.run();
            }
        });

        addItem(new MenuItem(14, new ItemFactory(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData()).setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel " + ChatColor.WHITE + name).build()) {
            @Override
            public void click(Player player, ClickType clickType) {
                player.closeInventory();
                onCancel.run();
            }
        });

        openInventory(player);
    }
}
