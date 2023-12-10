package net.cloudescape.skyblock.miscellaneous.minions.gui;

import com.cloudescape.CloudCore;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerContainer;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper;
import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.text.DecimalFormat;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public class ButcherInterface extends MenuFactory {


    public ButcherInterface(Player player, Minion minion) {
        super(minion.getType().name() + "'s Interface", 4);

        if (!(minion instanceof Butcher)) {
            return;
        }

        int[] whiteGlass = new int[]{0, 8, 9, 17, 18, 26, 27, 35};

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

        Butcher butcher = (Butcher) minion;

        addItem(new MenuItem(4,
                new ItemFactory(Material.ARMOR_STAND, 1)
                        .setDisplayName("&c&lMinion: &7" + minion.getId())
                        .setLore(

                                "&8• &7Minion Name -  &c" + StringUtil.capitalizeFirstLetter(minion.getName()),
                                "&8• &7Minion Type - &c" + CloudUtils.setUppercaseEachStart(minion.getType().name()),
                                "&8• &7Minion Health - &c" + minion.getHealth() + " &c❤",
                                "&8• &7Minion Hunger - &c" + minion.getHunger() + " &c\uD83C\uDF55"
//                                "&8• &7Minion Tag - &c" + (minion.getMinionTag() == null || minion.getMinionTag().getTag() == null ? "None" : minion.getMinionTag().getTag())
                        ).build()));

        addItem(new MenuItem(11, new ItemFactory(Material.EXP_BOTTLE)
                .setDisplayName("&b&lFeed")
                .setLore(
                        "&7Click to feed your minion, their current",
                        "&7hunger is currently at &c" + minion.getHunger(),
                        "",
                        "&8• &7Left Click - $50",
                        "&8• &7Right Click - 5 experience"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {

                //EXP
                if (clickType.isRightClick()) {

                    if ((player.getExp() - 5) < 0) {
                        CustomChatMessage.sendMessage(player, "Skyblock Minions", "You do not have enough experience to feed your minion!");
                        return;
                    }

                    minion.setHunger(butcher.getHunger() + 2);
                    CustomChatMessage.sendMessage(player, "Skyblock Minions", "Feeding your minion! &a+2 hunger&7.");
                    CustomChatMessage.sendMessage(player, "Skyblock Minions", "&c-5 experience");
                    player.closeInventory();
                    new MinerInterface(player, minion);
                } else if (clickType.isLeftClick()) { // MONEY

                    SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());

                    if (skyBlockPlayer == null) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "There was an issue loading your data..");
                        return;
                    }

                    if ((SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID()) - 50 < 0)) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You need $50 to feed your Minion! You have " + new DecimalFormat("#.##").format(SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())));
                        return;
                    }

                    minion.setHunger(butcher.getHunger() + 2);
                    SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())-50);
                    CustomChatMessage.sendMessage(player, "Skyblock Minions", "Feeding your minion! &a+2 hunger&7.");
                    CustomChatMessage.sendMessage(player, "Skyblock Minions", "&c-$50");
                    player.closeInventory();
                    new MinerInterface(player, minion);
                }
            }
        });

        addItem(new MenuItem(13, new ItemFactory(Material.SKULL_ITEM)
                .setDisplayName("&aMobs killed")
                .setLore(
                        StringUtil.getMenuLine(),
                        " &7This minion has killed &b" + butcher.getMobsKilled() + " &7mobs!",
                        StringUtil.getMenuLine()
                )
                .build()));

        addItem(new MenuItem(15, new ItemFactory(Material.NAME_TAG)
                .setDisplayName("&d&lCurrent Tag")
                .setLore(
                        "&8• &7This minions current tag is &8[&5" + minion.getMinionTag().getTagText() + "&8]",
                        "",
                        "&c• Left-click to change tag"
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new TagInterface(player, minion.getIsland(), minion);
            }
        });

        addItem(new MenuItem(20, new ItemFactory(Material.LEATHER_CHESTPLATE)
                .setDisplayName("&4&lSuit Selector")
                .setLore(
                        "&8• &7Set the current suit of your minion."
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new SuitInterface(player, minion.getIsland(), minion);
            }
        });

        addItem(new MenuItem(24, new ItemFactory(Material.REDSTONE)
                .setDisplayName("&3&lBoost Selector")
                .setLore(
                        "&8• &7Set the current boost of your minion."
                )
                .build()) {

            @Override
            public void click(Player player, ClickType clickType) {
                new BoostInterface(player, minion.getIsland(), minion);
            }
        });

        openInventory(player);
    }

}
