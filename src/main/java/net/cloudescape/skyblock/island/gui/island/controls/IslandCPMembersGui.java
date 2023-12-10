package net.cloudescape.skyblock.island.gui.island.controls;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.UUIDUtil;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandRank;
import net.cloudescape.skyblock.island.gui.island.IslandControlPanelGui;
import net.cloudescape.skyblock.island.gui.island.controls.members.IslandCPRankGui;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class IslandCPMembersGui extends MenuFactory {

    public IslandCPMembersGui(Player player, Island island, int page) {

        super("Control Panel -  Members", 4);

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

        int itemsPerPage = 14;
        int totalPages = getPages(island, itemsPerPage);
        int position = 10;

        // List of items for the current page the user is on.
        List<UUID> items = getMembers(island, page, itemsPerPage);

        // Loading items into the GUI.
        for (UUID item : items) {

            addItem(new MenuItem(position,
                            new SkullFactory()
                                    .setDisplayName("&b" + UUIDUtil.getUsername(item))
                                    .setOwner(UUIDUtil.getUsername(item))
                                    .setLore(
                                            "&8• &7Unique ID: &c" + item.toString(),
                                            "&8• &7Rank: &7" + getRankColour(island.getIslandMembers().get(item)) + StringUtil.capitalizeFirstLetter(island.getIslandMembers().get(item).name())
                    ).build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    // TODO Open set rank shit
                    if (island.getIslandMembers().get(item).ordinal() == IslandRank.values()[IslandRank.values().length - 1].ordinal()) {
                        CustomChatMessage.sendMessage(player, "Skyblock Islands", "You cannot alter the owner!");
                        player.closeInventory();
                        return;
                    }

                    if (island.getIslandMembers().get(item).ordinal() >= island.getIslandMembers().get(player.getUniqueId()).ordinal()) {
                        CustomChatMessage.sendMessage(player, "Skyblock Islands", "You are not a high enough rank to set them!");
                        player.closeInventory();
                        return;
                    }

                    new IslandCPRankGui(player, Bukkit.getPlayer(item), island);
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
                    new IslandCPMembersGui(player, island, page - 1);
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
                    new IslandCPMembersGui(player, island, page + 1);
                }
            }
        });

        openInventory(player);
    }

    private int getPages(Island island, int perPage) {

        int totalItems = island.getIslandMembers().size();
        int pages = (totalItems / perPage);

        if (pages < 1)
            pages = 1;

        return ((totalItems % perPage == 0 || totalItems <= perPage) ? pages : pages + 1);
    }

    private List<UUID> getMembers(Island island, int page, int perPage) {

        List<UUID> members = new ArrayList<>();
        List<UUID> tempMembers = new ArrayList<>(island.getIslandMembers().keySet());

        try {
            for (int i = ((page - 1) * perPage); i < (((page - 1) * perPage) + perPage); i++) {
                UUID item = tempMembers.get(i);
                members.add(item);
            }
        } catch (IndexOutOfBoundsException e) { /* Ignored */ }

        return members;
    }

    private ChatColor getRankColour(IslandRank rank) {
        switch (rank) {
            case MEMBER:
                return ChatColor.GREEN;
            case TRUSTED:
                return ChatColor.DARK_PURPLE;
            case MANAGER:
                return ChatColor.RED;
            case OWNER:
                return ChatColor.DARK_RED;
            default:
                return ChatColor.GRAY;
        }
    }
}