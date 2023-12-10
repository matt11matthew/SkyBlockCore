package net.cloudescape.skyblock.commands.skyblock;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.UUIDUtil;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.IslandManager;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandInfo(usage = "", description = "", permissionValue = 900)
public class ISAdminCommand extends Command {

    public ISAdminCommand() {
        super("isadmin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cloudskyblock.admin")) {
            sendMessage(sender, "Skyblock", "Failed to execute admin only command. Your attempt has been logged.");
            Logger.log("&4" + sender.getName() + " failed to execute admin commands due to a lack of permissions.");
            return;
        }

        // isadmin info <username>
        // isadmin world <world>
        // isadmin booster give <name> <boostertype>
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String username = args[1];
                Player player = (Player) sender;
                try {
                    // Change over to UUIDUtil.getUUID("");
                    UUID uuid = Bukkit.getOfflinePlayer(username).getUniqueId();

                    if (uuid == null) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "UUID could not be found!");
                        return;
                    }


                    IslandManager manager = CloudSkyblock.getPlugin().getIslandManager();
                    manager.getIslandOwnedBy(uuid, island -> {


                        if (island == null) {
                            CustomChatMessage.sendMessage(player, "Skyblock", username + " does not own an island!");
                            return;
                        }

                        CustomChatMessage.sendMessage(player, "Skyblock", "Island Information [&e" + username + "&7]");
                        CustomChatMessage.sendMessage(player, "Owner: " + UUIDUtil.getUsername(island.getIslandOwner()));
                        CustomChatMessage.sendMessage(player, "Island Level: " + island.getIslandLevel());
                        CustomChatMessage.sendMessage(player, "Invited members: " + island.getInvites().size());
                        CustomChatMessage.sendMessage(player, "Owned minions: " + island.getLoadedMinions().size());
                        CustomChatMessage.sendMessage(player, "&eMembers:");
                        for (UUID member : island.getIslandMembers().keySet())
                            CustomChatMessage.sendMessage(player, "&7- &e" + UUIDUtil.getUsername(member) + " " + (island.getIslandOwner() == member ? "&a(Owner)" : "&e(Member)"));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (args[0].equalsIgnoreCase("world") && sender instanceof Player) {

                String world = args[1];
                Player player = (Player) sender;

                if (Bukkit.getWorld(world) == null) {
                    CustomChatMessage.sendMessage(player, "Skyblock", world + " does not exist.");
                    Logger.log("&4" + player.getName() + " failed to execute /isadmin world " + world + " due to non-existent world.");
                    int id = 1;
                    for (World loadedWorld : Bukkit.getWorlds()) {
                        if (loadedWorld.getName().equals("world") || loadedWorld.getName().equals("world_nether") || loadedWorld.getName().equals("world_the_end"))
                            continue;

                        CustomChatMessage.sendMessage(player, "World " + id + " = " + loadedWorld.getName());
                        id += 1;
                    }
                    return;
                }

                player.teleport(Bukkit.getWorld(world).getSpawnLocation());
                CustomChatMessage.sendMessage(player, "Skyblock", "&eYou have travelled to " + world + "!");
            }
        } else if (args.length == 4) {
            // isadmin minion give <name> <type>
            // isadmin suit give <name> <type>
            // isadmin carpet give <name> <type>
            if (args[0].equalsIgnoreCase("carpet") && args[1].equalsIgnoreCase("give")) {
                String username = args[2];
                Player target = Bukkit.getPlayer(username);

                if (target == null) {
                    sendMessage(sender, "Skyblock Customization", username + " is not online!");
                    return;
                }

                DyeColor dyeColor = null;
                try{
                    dyeColor = DyeColor.valueOf(args[3].toUpperCase());
                } catch (Exception e) {
                    sendMessage(sender, "Skyblock Customization", "Invalid color");
                    return;
                }

                DyeColor finalDyeColor = dyeColor;
                CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(target.getUniqueId(), foundIsland -> {
                    foundIsland.unlockCarpet(args[3]);
                    sendMessage(sender, "Skyblock Customization", "You have given " + username + " a carpet color " + finalDyeColor.name() + ".");
                    sendMessage(target, "Skyblock Customization", "You have received " + username + " the carpet color " + finalDyeColor.name() + ".");
                });
            }
            if (args[0].equalsIgnoreCase("minion") && args[1].equalsIgnoreCase("give")) {

                try {
                    String username = args[2];
                    MinionType type = MinionType.valueOf(args[3].toUpperCase());
                    Player target = Bukkit.getPlayer(username);

                    if (target == null) {
                        sendMessage(sender, "Skyblock Minions", username + " is not online!");
                        return;
                    }

                    CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(target.getUniqueId(), foundIsland -> {

                        if (foundIsland != null) {
                            switch (type) {
                                case MINER:
                                    foundIsland.getLoadedMinions().add(new Miner(foundIsland.getLoadedMinions().size() + 1, foundIsland, foundIsland.getLocation(), "Miner", type, 100, 100, null));
                                    break;
                                case BUTCHER:
                                    foundIsland.getLoadedMinions().add(new Butcher(foundIsland.getLoadedMinions().size() + 1, foundIsland, 0, 5, "ZOMBIE", foundIsland.getLocation(), "Butcher", type, 10, 10, null));
                                    break;
                                case BANKER:
                                    foundIsland.getLoadedMinions().add(new Butcher(foundIsland.getLoadedMinions().size() + 1, foundIsland, 0, 5, "ZOMBIE", foundIsland.getLocation(), "Butcher", type, 10, 10, null));
                                    break;
                            }

                            sendMessage(sender, "Skyblock Minions", "You have given " + username + " a new minion! " + type.name() + ".");
                            sendMessage(target, "Skyblock Minions", sender.getName() + " has given you a new Minion! You can access it in your island settings. Type: " + type.name());
                        } else {
                            sendMessage(sender, "Skyblock Minions", "There was an issue! " + username + " doesn't have an island.");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(sender, "Skyblock Minions", "That is not a valid Minion!");
                    for (MinionType minionType : MinionType.values()) {
                        sendMessage(sender, " - " + minionType.name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("suit") && args[1].equalsIgnoreCase("give")) {

                try {

                    String username = args[2];
                    SuitType type = SuitType.valueOf(args[3].toUpperCase());
                    Player target = Bukkit.getPlayer(username);

                    if (target == null) {
                        sendMessage(sender, "Skyblock Minions", username + " is not online!");
                        return;
                    }

                    CloudSkyblock.getPlugin().getIslandManager().getIsland(target.getUniqueId(), foundIsland -> {

                        if (foundIsland != null) {
                            foundIsland.addAvailableSuit(type);
                            sendMessage(sender, "Skyblock Minions", "You have given " + username + " a new suit! " + type.name() + ".");
                            CustomChatMessage.sendMessage(target, "Skyblock Minions", sender.getName() + " has given you a new Suit! You can access it in your island minion settings. Type: " + type.name());
                        } else {
                            sendMessage(sender, "Skyblock Minions", "There was an issue! " + username + " doesn't have an island.");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(sender, "Skyblock Minions", "That is not a valid Suit!");
                    for (SuitType suitType : SuitType.values()) {
                        sendMessage(sender, " - " + suitType.name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("booster") && args[1].equalsIgnoreCase("give")) {

                try {

                    String username = args[2];
                    MinionBoostType type = MinionBoostType.valueOf(args[3].toUpperCase());
                    Player target = Bukkit.getPlayer(username);

                    if (target == null) {
                        sendMessage(sender, "Skyblock Minions", username + " is not online!");
                        return;
                    }

                    CloudSkyblock.getPlugin().getIslandManager().getIsland(target.getUniqueId(), foundIsland -> {

                        if (foundIsland != null) {
                            // TODO
                            foundIsland.addAvailableBoostType(type);
                            sendMessage(sender, "Skyblock Minions", "You have given " + username + " a new minion boost! " + type.name() + ".");
                            sendMessage(target, "Skyblock Minions", sender.getName() + " has given you a new Minion boost! You can access it in your island minion settings. Type: " + type.name());
                        } else {
                            sendMessage(sender, "Skyblock Minions", "There was an issue! " + username + " doesn't have an island.");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(sender, "Skyblock Minions", "That is not a valid boost!");
                    for (MinionBoostType boostType : MinionBoostType.values()) {
                        sendMessage(sender, " - " + boostType.name());
                    }
                }
            } else if (args[0].equalsIgnoreCase("tags") && args[1].equalsIgnoreCase("give")) {

                try {

                    String username = args[2];
                    MinionTagType type = MinionTagType.valueOf(args[3].toUpperCase());
                    Player target = Bukkit.getPlayer(username);

                    if (target == null) {
                        sendMessage(sender, "Skyblock Minions", username + " is not online!");
                        return;
                    }

                    CloudSkyblock.getPlugin().getIslandManager().getIsland(target.getUniqueId(), foundIsland -> {

                        if (foundIsland != null) {
                            // TODO
                            foundIsland.addAvailableMinionTag(type);
                            sendMessage(sender, "Skyblock Minions", "You have given " + username + " a new minion tag! " + type.name() + ".");
                            CustomChatMessage.sendMessage(target, "Skyblock Minions", sender.getName() + " has given you a new Minion tag! You can access it in your island minion settings. Type: " + type.name());
                        } else {
                            sendMessage(sender, "Skyblock Minions", "There was an issue! " + username + " doesn't have an island.");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(sender, "Skyblock Minions", "That is not a valid tag!");
                    for (MinionTagType tagType : MinionTagType.values()) {
                        sendMessage(sender, " - " + tagType.name());
                    }
                }
            }
        } else if (args.length == 5) {
//                // isadmin booster give <name> <boostertype> <level>
//                if (args[0].equalsIgnoreCase("booster")) {
//
//                    String username = args[2];
//                    UUID uuid = null;
//                    Player foundPlayer = Bukkit.getPlayer(username);
//                    boolean isOnline = false;
//                    Booster booster = null;
//                    int boosterLevel = -1;
//
//                    try {
//                        BoosterType type = BoosterType.valueOf(args[3].toUpperCase());
//                        boosterLevel = Integer.parseInt(args[4]);
//                        booster = new Booster(type, boosterLevel);
//                        uuid = UUIDUtil.getUUID(username);
//                    } catch (Exception e) {
//                    }
//
//                    if (foundPlayer.isOnline()) {
//                        isOnline = true;
//                    }
//
//                    switch (args[1].toLowerCase()) {
//
//                        case "give":
//
//                            if (booster == null) {
//                                CustomChatMessage.sendMessage(player, "Skyblock", "That booster does not exist! Type: " + args[3]);
//                                return;
//                            }
//
//                            if (boosterLevel == -1) {
//                                CustomChatMessage.sendMessage(player, "Skyblock", "That booster does not allow for that level! Highest level for " + args[3] + " is " + (booster.getType().getDuration().length));
//                                return;
//                            }
//
//                            if (isOnline) {
//                                // TODO apply booster directly to account.
//                                Booster finalBooster = booster;
//                                int finalBoosterLevel = boosterLevel;
//                                CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(foundPlayer.getUniqueId(), island -> {
//
//                                   if (island == null) {
//                                       CustomChatMessage.sendMessage(foundPlayer, "Skyblock", "You do not own an island, we failed to credit your booster.");
//                                       return;
//                                   }
//
//                                   island.addOwnedBooster(finalBooster);
//                                   CustomChatMessage.sendMessage(foundPlayer, "Skyblock", "A new booster has been credited to your account!");
//                                   CustomChatMessage.sendMessage(foundPlayer, "Skyblock", "Type: " + finalBooster.getType().name() + ", Level: " + finalBoosterLevel + ".");
//                               });
//
//                            } else {
//                                // TODO apply booster via database.
//                                if (uuid == null) {
//                                    // Failed to get UUID correctly..
//                                    CustomChatMessage.sendMessage(player, "Skyblock", "There was an issue with the UUID of this player!");
//                                    break;
//                                }
//
//                                // TODO set..
//                                IslandWrapper wrapper = CloudSkyblock.getPlugin().getIslandWrapper();
//                                List<Object> ownedBoosters = wrapper.getListFromKey(uuid, "ownedBoosters");
//                                ownedBoosters.add(booster);
//                                wrapper.setKeyToNewValue(uuid, "ownedBoosters", ownedBoosters);
//                            }
//
//                            CustomChatMessage.sendMessage(player, "Skyblock", "You gave " + username + " 1x " + booster.getType().name() + " - level " + booster.getLevel());
//                            break;
//                    }
//                }
        } else {
            sendHelp(sender);
        }
    }

    public static void sendMessage(CommandSender player, String prefix, String message) {
        if (prefix.contains("(")) {
            sendMessage(player, prefix + " &7" + message);
        } else {
            sendMessage(player, "&e(" + prefix + "&e) &7" + message);
        }

    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + message));
    }

    private void sendHelp(CommandSender player) {
        sendMessage(player, "Skyblock", "Command help");
        sendMessage(player, "- /isadmin info <name> - View information regarding a players island.");
        sendMessage(player, "- /isadmin world <name> - Teleport to a world.");
        sendMessage(player, "- /isadmin minion give <username> <minion>");
        sendMessage(player, "- /isadmin booster give <boost_type>");
        sendMessage(player, "- /isadmin suit give <username> <suit>");
        sendMessage(player, "- /isadmin tags give <username> <tag>");


    }
}
