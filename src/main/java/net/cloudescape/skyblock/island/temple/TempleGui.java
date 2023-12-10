package net.cloudescape.skyblock.island.temple;

import com.cloudescape.CloudCore;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerContainer;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper;
import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

import java.text.DecimalFormat;
import java.util.Optional;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class TempleGui extends MenuFactory {

    public TempleGui(Player player, Island island) {
        super("Island Temple", 4);

        int slot = 19;

        addItem(new MenuItem(13, new ItemFactory(Material.BOOK)
                .setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Island Temple Information")
                .addItemFlag(ItemFlag.values()).build()));

        if (island == null || island.getBoosters() == null) {
            CustomChatMessage.sendMessage(player, "Temple", "Found no boosters");
            return;
        }

        for (BoosterType boosterType : BoosterType.values()) {
            Optional<Booster> boosterByType = island.getBoosterByType(boosterType);
            if (boosterByType.isPresent()) {
                Booster boosters = boosterByType.get();
                try {
                    addItem(new MenuItem(slot, new ItemFactory(boosters.getType().getDisplayIcon())
                            .setDisplayName("&b&l" + CloudUtils.setUppercaseEachStart(boosters.getType().name().replace("_", " ")))
                            .setLore(
                                    StringUtil.getMenuLine(),
                                    " &bCurrent Progress: ",
                                    " " + generateProgressBar(boosters.getLevel(), boosters.getType().getLevelCosts().length, '▎', ChatColor.GREEN, ChatColor.GRAY, true) + " &7[&b" + boosters.getLevel() + "&7/&b" + boosters.getType().getLevelCosts().length + "&7]",
                                    " ",
                                    " &7Next level cost: &b$" + new DecimalFormat("#.##").format(boosters.getType().getLevelCosts()[boosters.getLevel() + 1]),
                                    StringUtil.getMenuLine()
                            )
                            .build()) {

                        @Override
                        public void click(Player player, ClickType clickType) {
                            double cost = boosters.getType().getLevelCosts()[boosters.getLevel() + 1]; // Should work as its calling out of bounds due to ad

                            SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());

                            if (SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())>= cost) {
                                SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID()) - cost);
                                CustomChatMessage.sendMessage(player, "Skyblock Boosters", "You have upgraded your islands '" + CloudUtils.setUppercaseEachStart(boosters.getType().name().replace("_", " ")) + "' booster to level " + (boosters.getLevel() + 1));
                                island.setBoosterLevel(boosters.getType(), boosters.getLevel() + 1);
                                player.closeInventory();
                                new TempleGui(player, island);
                                updateBooster(boosterType, island);
                            } else {
                                CustomChatMessage.sendMessage(player, "Skyblock Boosters", "You do not have enough to upgrade this booster!");
                            }
                        }
                    });
                } catch (ArrayIndexOutOfBoundsException e) {

                    if (boosters.getLevel() == boosters.getType().getLevelCosts().length) {
                        addItem(new MenuItem(slot, new ItemFactory(boosters.getType().getDisplayIcon())
                                .setDisplayName("&e&l" + CloudUtils.setUppercaseEachStart(boosters.getType().name().replace("_", " ")))
                                .setLore(
                                        StringUtil.getMenuLine(),
                                        " &7You have already gotten to the maximum level,",
                                        " &7Begin upgrading your other boosters! :D",
                                        StringUtil.getMenuLine()
                                )
                                .build()));
                    } else {
                        addItem(new MenuItem(slot, new ItemFactory(boosters.getType().getDisplayIcon())
                                .setDisplayName("&b&l" + CloudUtils.setUppercaseEachStart(boosters.getType().name().replace("_", " ")))
                                .setLore(
                                        StringUtil.getMenuLine(),
                                        " &bCurrent Progress: ",
                                        " " + generateProgressBar(boosters.getLevel(), boosters.getType().getLevelCosts().length, '▎', ChatColor.GREEN, ChatColor.GRAY, true) + " &7[&b" + boosters.getLevel() + "&7/&b" + boosters.getType().getLevelCosts().length + "&7]",
                                        " ",
                                        " &7Next level cost: &b$" + new DecimalFormat("#.##").format(boosters.getType().getLevelCosts()[boosters.getType().getLevelCosts().length - 1]),
                                        StringUtil.getMenuLine()
                                )
                                .build()) {

                            @Override
                            public void click(Player player, ClickType clickType) {

                                double cost = boosters.getType().getLevelCosts()[boosters.getType().getLevelCosts().length - 1]; // Should work as its calling out of bounds due to ad
                                SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());

                                if (SkyblockPlayerWrapper.getBalance(player.getUniqueId()) >= cost) {
                                    SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())-cost);
                                    CustomChatMessage.sendMessage(player, "Skyblock Boosters", "You have upgraded your islands '" + CloudUtils.setUppercaseEachStart(boosters.getType().name().replace("_", " ")) + "' booster to level " + (boosters.getLevel() + 1));
                                    island.setBoosterLevel(boosters.getType(), boosters.getLevel() + 1);
                                    player.closeInventory();
                                    new TempleGui(player, island);
                                    updateBooster(boosterType, island);
                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        CustomChatMessage.sendMessage(all, "Skyblock Boosters", "&c" + player.getName() + " &7maxed out their &c`" + StringUtil.capitalizeFirstLetter(boosters.getType().name().replace("_", " ")) + "` &7booster.");
                                    }
                                } else {
                                    CustomChatMessage.sendMessage(player, "Skyblock Boosters", "You do not have enough to upgrade this booster!");
                                }
                            }
                        });
                    }
                }
                slot++;
            }
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

    private void updateBooster(BoosterType type, Island island) {

        switch (type) {
            case ISLAND_RADIUS:
                Optional<Booster> boosterOptional = island.getBoosterByType(type);

                if (boosterOptional.isPresent()) {
                    Booster booster = boosterOptional.get();

                    switch (booster.getLevel()) {
                        case 1:
                            island.setProtectionDistance(75);
                            break;
                        case 2:
                            island.setProtectionDistance(100);
                            break;
                        case 3:
                            island.setProtectionDistance(130);
                            break;
                        case 4:
                            island.setProtectionDistance(140);
                            break;
                        case 5:
                            island.setProtectionDistance(150);
                            break;
                        case 6:
                            island.setProtectionDistance(160);
                            break;
                        case 7:
                            island.setProtectionDistance(170);
                            break;
                        case 8:
                            island.setProtectionDistance(180);
                            break;
                        case 9:
                            island.setProtectionDistance(190);
                            break;
                        case 10:
                            island.setProtectionDistance(200);
                            break;
                    }
                }
                break;
        }
    }
}
