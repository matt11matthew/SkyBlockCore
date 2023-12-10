package net.cloudescape.skyblock.miscellaneous.minions.gui;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.utils.LocationUtil;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class DirectionInterface extends MenuFactory {

    private final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    public DirectionInterface(Player player, Minion minion){
        super("Select a Direction", 4);

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

        int position = 10;

        for (int i = 0; i < 4; i++) {

            String displayName = "";

            float yaw = 0;

            switch (i) {
                case 0:
                    displayName = "North";
                    yaw = 180;
                    break;
                case 1:
                    displayName = "East";
                    yaw = -90;
                    break;
                case 2:
                    displayName = "South";
                    yaw = 0;
                    break;
                case 3:
                    displayName = "West";
                    yaw = 90;
                    break;
            }

            final String finalDisplayName = displayName;
            final float finalYaw = yaw;

            addItem(new MenuItem(position, new ItemFactory(Material.BOOK)
                    .setDisplayName("&c&l" + displayName)
                    .setLore(
                            "",
                            "&8• &7Set your minion to face &c" + StringUtil.capitalizeFirstLetter(displayName)
                    )
                    .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    Location location = minion.getMinion().getLocation();
                    location.setYaw(finalYaw);
                    minion.getMinion().teleport(location);
                    CustomChatMessage.sendMessage(player, "Minion Editor", "Direction changed to face &c" + finalDisplayName);
                }
            });

            position += 2;
        }


//        for (MinionTagType available : island.getAvailableMinionTags()) {
//            addItem(new MenuItem(slot, new ItemFactory(Material.PAPER)
//            .setDisplayName("&8[&5" + StringUtil.capitalizeFirstLetter(available.name()) + "&8] &bTag")
//                    .setLore(
//                            "&8• &7Click to equip this tag!"
//                    )
//            .build()) {
//
//                @Override
//                public void click(Player player, ClickType clickType) {
//                    if (minion != null) {
//                        minion.setMinionTag(available);
//                        minion.getMinion().setCustomName(ChatColor.translateAlternateColorCodes('&', ("&8[&5" + minion.getMinionTag().getTagText() + "&8] &7") + minion.getName()));
//                        CustomChatMessage.sendMessage(player, "Minion", "Tag updated to " + StringUtil.capitalizeFirstLetter(available.name()) + ".");
//                        player.closeInventory();
//                    }
//                }
//            });
//            slot += 1;
//        }

        openInventory(player);
    }

    private float getNearestYaw(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();

        switch (face) {
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            case NORTH:
                return -180;
            case EAST:
                return -90;
        }

        return 0;
    }

}
