package net.cloudescape.skyblock.miscellaneous.chestordering;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ChestSortConfirmationGui extends MenuFactory {

    public ChestSortConfirmationGui(Player player, Chest chest) {

        super("Are you sure you want to continue?", 3);

        for (int i = 0; i < getInventory().getSize(); i++) {
            addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                    .setDisplayName(" ")
                    .build()));
        }

        addItem(new MenuItem(4, new ItemFactory(Material.CHEST)
            .setDisplayName("&c&lChest Information")
                .setLore(
                        "",
                        "&c&lInfo",
                        "",
                        "&8• &7Size: &c" + chest.getBlockInventory().getSize(),
                        "&8• &7Rows: &c" + (chest.getBlockInventory().getSize() / 9),
                        "&8• &7Total items: &c" + (chest.getBlockInventory().getContents().length)
                )
            .build()));

        addItem(new MenuItem(13, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 5)
                .setDisplayName("&a&lAre you sure?")
                .setLore(
                        "&7Are you sure you'd like to order your chest",
                        "&7By type?",
                        "",
                        "&a• Left click to confirm",
                        "&c• Close the inventory to cancel"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {

                if (chest != null) {

                    orderChest(chest);

                    CustomChatMessage.sendMessage(player, "Chest Sorter", "You have sorted your chest!");
                    player.closeInventory();
                }
            }
        });

        openInventory(player);
    }

    private void orderChest(Chest chest) {
        List<ItemStack> ordered = new ArrayList<>();

        for (ItemStack itemStack : chest.getBlockInventory().getContents()) {
            if (itemStack != null) {
                ordered.add(itemStack);
            }
        }

        ordered.sort(new ChestSortingComparator());

        // Just for extra safety I clear it twice.
        for (int i = 0; i < chest.getBlockInventory().getContents().length; i++) {
            if (chest.getBlockInventory().getItem(i) != null) chest.getBlockInventory().clear(i);
        }

        chest.getBlockInventory().clear();

        for (ItemStack itemStack : ordered) {
            chest.getBlockInventory().addItem(itemStack);
        }
    }
}
