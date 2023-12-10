package net.cloudescape.skyblock.island.gui.island.controls;

import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.gui.island.IslandControlPanelGui;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IslandCPMinionsGui extends MenuFactory {

    public IslandCPMinionsGui(Player player, Island island, int page) {

        super("Control Panel -  Minions", 4);

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

        List<Integer> blockedSlots = Arrays.asList(9, 17, 18, 26, 27, 35, 36, 44);

        int itemsPerPage = 28;
        int totalPages = getPages(island, itemsPerPage);
        int position = 10;

        // List of items for the current page the user is on.
        List<Minion> items = getMinions(island, page, itemsPerPage);

        if (items.size() < 1) {
            addItem(new MenuItem(13, new ItemFactory(Material.GOLD_INGOT)
                    .setDisplayName("&eThis island does not have any minions available!")
                    .setLore(
                            StringUtil.getMenuLine(),
                            "&7Purchase Minions on our store,",
                            "&ahttps://skyheroes.org/store",
                            StringUtil.getMenuLine()
                    )
                    .build()));

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
            return;
        }

        // Loading items into the GUI.
        for (Minion item : items) {

            addItem(new MenuItem(position,
                    new ItemFactory(Material.ARMOR_STAND, 1)
                            .setDisplayName("&c&lMinion: &7" + item.getId())
                            .setLore(
                                    "&8• &7Minion Name -  &c" + StringUtil.capitalizeFirstLetter(item.getName()),
                                    "&8• &7Minion Type - &c" + CloudUtils.setUppercaseEachStart(item.getType().name()),
                                    "&8• &7Minion Health - &c" + item.getHealth() + " &c❤",
                                    "&8• &7Minion Hunger - &c" + item.getHunger() + " &c\uD83C\uDF55",
                                    "&8• &7Minion Tag - &c" + (item.getMinionTag().getTagText())
                            ).build()) {
                // What data will be specific to Miner? Just blocks broke etc?
                @Override
                public void click(Player player, ClickType clickType) {

                    if (item.isSpawned()) {
                        item.kill(false, island);
                        CustomChatMessage.sendMessage(player, "Skyblock Minions", "Removed old minion.");
                    }

                    ItemStack itemStack = new ItemFactory(Material.ARMOR_STAND)
                            .setDisplayName("&bMinion Spawner")
                            .setLore(item.getType().name(), "" + item.getId(), item.getIsland().getIslandUuid().toString())
                            .build();

                    if (!doesItemExist(player, itemStack)) {
                        player.getInventory().addItem(itemStack);
                    }

                    CustomChatMessage.sendMessage(player, "Skyblock Minions", "Place the stand on the floor to spawn your minion!");
                }
            });

            if (blockedSlots.contains(position + 1)) {
                position += 3;
            } else {
                position += 1;
            }
        }

        // Information item used to check the current page the user is on.
        addItem(new MenuItem(31, new ItemFactory(Material.STICK)
                .setDisplayName("&c&lCurrent Page:")
                .setLore(
                        "&7The current page is:",
                        "",
                        "&8• &c" + page + "&7/&c" + getPages(island, itemsPerPage)
                )
                .build()));

        // Adds an item if a next page is available for the user.
        addItem(new MenuItem(28, new ItemFactory(Material.ARROW)
                .setDisplayName("&c&lBack Page")
                .setLore(
                        (page - 1 == 0) ? "&7Return to your Island control panel." : "&7Go back a page.",
                        "",
                        "&8• &cLeft click to go back"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                if (page - 1 == 0) {
                    new IslandControlPanelGui(player, island);
                } else {
                    new IslandCPMinionsGui(player, island, page - 1);
                }
            }
        });

        // Adds an item if the user can go back to a previous page.
        addItem(new MenuItem(34, new ItemFactory(Material.ARROW)
                .setDisplayName("&c&lNext Page")
                .setLore(
                        ((page + 1) <= totalPages ? "&7Go forward a page." : "&7There are no more pages."),
                        "",
                        "&8• &cLeft click to go forward"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                if (((page + 1) <= totalPages)) {
                    new IslandCPMinionsGui(player, island, page + 1);
                }
            }
        });

        openInventory(player);
    }

    private int getPages(Island island, int perPage) {

        int totalItems = island.getLoadedMinions().size();
        int pages = (totalItems / perPage);

        if (pages < 1)
            pages = 1;

        return ((totalItems % perPage == 0 || totalItems <= perPage) ? pages : pages + 1);
    }

    private List<Minion> getMinions(Island island, int page, int perPage) {

        List<Minion> minions = new ArrayList<>();

        try {
            for (int i = ((page - 1) * perPage); i < (((page - 1) * perPage) + perPage); i++) {
                Minion item = island.getLoadedMinions().get(i);
                minions.add(item);
            }
        } catch (IndexOutOfBoundsException e) { /* Ignored */ }

        return minions;
    }

    public boolean doesItemExist(Player player, ItemStack item) {
        for (int i = 0; i <  player.getInventory().getSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);

            if (itemStack != null && itemStack.isSimilar(item)) {
                return true;
            }
        }

        return false;
    }
}