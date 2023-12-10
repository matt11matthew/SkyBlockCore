package net.cloudescape.skyblock.commands.skyblock;

import com.boydti.fawe.object.schematic.Schematic;
import com.cloudescape.CloudCore;
import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerContainer;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.UUIDUtil;
import com.cloudescape.utilities.itemstack.ItemFactory;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.database.island.IslandContainer;
import net.cloudescape.skyblock.database.skyblockplayer.SkyBlockPlayer;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandManager;
import net.cloudescape.skyblock.island.IslandRank;
import net.cloudescape.skyblock.island.gui.island.ConfirmGui;
import net.cloudescape.skyblock.island.gui.island.IslandControlPanelGui;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.utils.IslandUtils;
import net.cloudescape.skyblock.utils.ParserUtil;
import net.cloudescape.skyblock.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CommandInfo(usage = "/island help", description = "Island command", aliases = {"is"})
public class IslandCommand extends Command {


    public IslandCommand() {
        super("island");
    }

    @Override
    public List<String> getAliases() {
        return java.util.Arrays.asList("is");
    }

    private void sendHelp(Player player) {
        CustomChatMessage.sendMessage(player, "Skyblock", "Command help");
        CustomChatMessage.sendMessage(player, "- /is help - Displays this dialogue.");
        CustomChatMessage.sendMessage(player, "- /is - Crates a new island or tps you to island home.");
        CustomChatMessage.sendMessage(player, "- /is delete - Deletes your island");
        CustomChatMessage.sendMessage(player, "- /is leave - Leave your island");
        CustomChatMessage.sendMessage(player, "- /is join <owner> - Join island");
        CustomChatMessage.sendMessage(player, "- /istop - View top islands");
        CustomChatMessage.sendMessage(player, "- /is invite <name> - invites a player to your island.");
        CustomChatMessage.sendMessage(player, "- /is bank deposit <amount> - deposits money into the global island bank.");
        CustomChatMessage.sendMessage(player, "- /is bank withdraw <amount> - withdraws money from the global island bank.");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            IslandManager islandManager = CloudSkyblock.getPlugin().getIslandManager();

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sendHelp(player);
                    return;
                }

                if (args[0].equalsIgnoreCase("leave")) {

                    SkyBlockPlayer skyBlockPlayer = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getSkyblockPlayer(player.getUniqueId());
                    IslandContainer island = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(skyBlockPlayer.getIslandUuid());

                    if (island == null) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You are not connected to an island to leave!");
                        return;
                    }

                    if (island.getOwner().equals(player.getUniqueId())) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You cannot leave your own island! Disband it with /is delete.");
                        return;
                    }

                    CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(island.getOwner(), found -> {
                        found.removeIslandMember(player.getUniqueId());
                        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(player.getUniqueId(), null);
                        CustomChatMessage.sendMessage(player, "Skyblock", "You have left the island!");
                    });
                } else {
                    CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), islandOwnedBy -> {

                        if (islandOwnedBy != null) {
                            new IslandControlPanelGui(player, islandOwnedBy);
                        }

                        //new IslandSchematicSelectionGui(player);
                    });
                }

                if (args[0].equalsIgnoreCase("cp")) {
                    CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), islandOwnedBy -> {

                        if (islandOwnedBy != null) {
                            new IslandControlPanelGui(player, islandOwnedBy);
                        }

                        //new IslandSchematicSelectionGui(player);
                    });
                    return;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    islandManager.getIslandOwnedBy(player.getUniqueId(), islandOwnedBy -> {

                        if (islandOwnedBy != null && !islandOwnedBy.isSaving()) {
                            new ConfirmGui(player, "Delete Island", () -> {
                                CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(islandOwnedBy.getIslandOwner(), null);
                                CustomChatMessage.sendMessage(player, "SkyBlock", "Deleting your island.");
                                for (UUID uuid : islandOwnedBy.getIslandMembers().keySet()) {
                                    CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(uuid, null);

                                }
                                islandManager.unloadIsland(islandOwnedBy, player, () -> {
                                    WorldUtil.deleteWorld(islandOwnedBy.getWorld().getWorldFolder());
                                    CloudSkyblock.getPlugin().getIslandWrapper().removeIsland(islandOwnedBy.getIslandUuid());
                                    CustomChatMessage.sendMessage(player, "SkyBlock", "Deleted your island.");
                                }, () -> {
                                });
                            }, () -> {
                            });
                        }
                    });
                    return;
                }

                if (args[0].equalsIgnoreCase("unload")) {
                    islandManager.getIsland(player.getUniqueId(), islandOwnedBy -> {

                        if (islandOwnedBy != null) {
                            if (!islandOwnedBy.getIslandOwner().equals(player.getUniqueId())) {
                                CustomChatMessage.sendMessage(player, "Island", "You must be owner to unload the island");
                                return;
                            }
                            islandManager.unloadIsland(islandOwnedBy, player, () -> {
//                                player.sendMessage(ChatColor.AQUA + "Unloaded your island.");
//                                IslandManager.toDeleteMap.put(islandOwnedBy.getWorld().getWorldFolder(), () -> {
//                                    loadedIslands.remove(island);
//                                    islandUnloadingPlayerList.remove(island.getIslandOwner());
//                                    onDone.run();
                                WorldUtil.deleteWorld(islandOwnedBy.getWorld().getWorldFolder());
//                                });
                            }, () -> player.sendMessage(ChatColor.RED + "Failed to unload island."));
                        }
                    });
                } else {
                    CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), islandOwnedBy -> {

                        if (islandOwnedBy != null) {
                            new IslandControlPanelGui(player, islandOwnedBy);
                        }

                        //new IslandSchematicSelectionGui(player);
                    });
                }
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "invite":
                        Player invited = Bukkit.getPlayer(args[1]);

                        if (invited == null) {
                            CustomChatMessage.sendMessage(player, "Skyblock", args[1] + " is not online to invite.");
                            return;
                        }

                        CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), invitedIsland -> {

                            if (invitedIsland == null) {
                                CustomChatMessage.sendMessage(player, "Skyblock", "You must have an island to invite " + invited.getName() + ".");
                                return;
                            }

                            if (invitedIsland.getIslandMembers().containsKey(invited.getUniqueId())) {
                                CustomChatMessage.sendMessage(player, "Skyblock", invited.getName() + " is already a member of your island.");
                                return;
                            }

                            if (invitedIsland.getInvites().contains(invited.getUniqueId())) {
                                CustomChatMessage.sendMessage(player, "Skyblock", invited.getName() + " has already been invited!");
                                return;
                            }

                            invitedIsland.getInvites().add(invited.getUniqueId());
                            CustomChatMessage.sendMessage(player, "Skyblock", invited.getName() + " has been invited to your island! &a/island join " + player.getName());
                            CustomChatMessage.sendMessage(invited, "Skyblock", player.getName() + " invited you to their island!");
                        });

                        break;
                    case "join":
                        Player joinPlayer = Bukkit.getPlayer(args[1]);
                        if (joinPlayer == null || !joinPlayer.isOnline()) {
                            CustomChatMessage.sendMessage(player, "Skyblock", "Invalid player");
                            return;
                        }

                        CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(joinPlayer.getUniqueId(), island -> {

                            if (island == null) {
                                CustomChatMessage.sendMessage(player, "Skyblock", joinPlayer.getName() + " does not have an island to have invited you to.");
                                return;
                            }

                            if (island.getIslandMembers().containsKey(player.getUniqueId())) {
                                CustomChatMessage.sendMessage(player, "Skyblock", "You're already a member of the island.");
                                return;
                            }

                            if (!island.getInvites().contains(player.getUniqueId())) {
                                CustomChatMessage.sendMessage(player, "Skyblock", "You need to be invited by an owner first!");
                                return;
                            }

                            IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByOwner(player.getUniqueId());

                            if (container == null) {
                                island.addIslandMember(player.getUniqueId(), IslandRank.MEMBER, true);
                                CustomChatMessage.sendMessage(player, "Skyblock", "You did not have an island!");
                                CustomChatMessage.sendMessage(player, "Skyblock", "You have joined the island!");
                                return;
                            }


                            new ConfirmGui(player, "Removal of your island.", () -> {
                                CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), playerIsland -> {
                                    if (playerIsland != null) { // The player owns an island this means we delete it
                                        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(player.getUniqueId(), null);
                                        CustomChatMessage.sendMessage(player, "SkyBlock", "Deleting your island.");
                                        for (UUID uuid : playerIsland.getIslandMembers().keySet()) {
                                            CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(uuid, null);

                                        }

                                        islandManager.unloadIsland(playerIsland, player, () -> {
                                            WorldUtil.deleteWorld(playerIsland.getWorld().getWorldFolder());
                                            CloudSkyblock.getPlugin().getIslandWrapper().removeIsland(playerIsland.getIslandUuid());
                                            CustomChatMessage.sendMessage(player, "SkyBlock", "Deleted your island.");
                                            island.addIslandMember(player.getUniqueId(), IslandRank.MEMBER, true);
                                            CustomChatMessage.sendMessage(player, "Skyblock", "You have joined the island owned by " + UUIDUtil.getUsername(island.getIslandOwner()));
                                        }, () -> {
                                            //Failure
                                        });
                                    }
                                });
                            }, () -> {
                            });
                        });
                        break;

                    default:
//                        CloudSkyblock.getPlugin().getIslandManager().getIslandOwnedBy(player.getUniqueId(), islandOwnedBy -> {
//
//                            if (islandOwnedBy != null) {
//                                new IslandControlPanelGui(player, islandOwnedBy);
//                            }
//
//                            //new IslandSchematicSelectionGui(player);
//                        });
                        break;
                }
            } else if (args.length == 3) {

                if (args[0].equalsIgnoreCase("bank")) {

                    Optional<Integer> amountOptional = ParserUtil.parseInt(args[2]);

                    if (!amountOptional.isPresent()) {
                        CustomChatMessage.sendMessage(player, "Island Bank", "Please enter a valid amount");
                        return;
                    }

                    SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());

                    if (skyBlockPlayer == null) {
                        CustomChatMessage.sendMessage(player, "Island Bank", "Please relog.");
                        return;
                    }

                    int amount = amountOptional.get();
                    double currentBalance = com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID());

                    switch (args[1]) {
                        case "deposit":
                            // add to
                            islandManager.getIsland(player.getUniqueId(), island -> {

                                if (island == null) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "You do not have an island.");
                                    return;
                                }

                                if (amount <= 0) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "Please enter an amount above 0!");
                                    return;
                                }

                                if ((island.getBalance() + amount) > getMaximumAllowedBalance(island)) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "You have exceeded your maximum allowance of " + getMaximumAllowedBalance(island) + ". Upgrade your island balance booster.");
                                    return;
                                }

                                if (currentBalance - amount < 0) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "You do not have enough in your bank to deposit.");
                                    return;
                                }

                                island.setBalance(island.getBalance() + amount);
                                com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(), com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID()) - amount);
                                CustomChatMessage.sendMessage(player, "Island Bank", "Deposited " + amount + " into your island bank.");
                            });
                            break;
                        case "withdraw":
                            // take from
                            islandManager.getIsland(player.getUniqueId(), island -> {

                                if (island == null) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "You do not have an island.");
                                    return;
                                }

                                if (amount <= 0) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "Please enter an amount above 0!");
                                    return;
                                }

                                if (island.getBalance() < amount || (island.getBalance() - amount) < 0) {
                                    CustomChatMessage.sendMessage(player, "Island Bank", "There is not enough in the island bank to take.");
                                    return;
                                }

                                island.setBalance(island.getBalance() - amount);

                                SkyblockPlayerWrapper.addBalance(skyBlockPlayer.getUniqueID(), amount);
                                CustomChatMessage.sendMessage(player, "Island Bank", "Withdrew " + amount + " from your island bank.");
                            });
                            break;
                    }
                } else {
                    sendHelp(player);
                    return;
                }
            } else {


                UUID islandUuid = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getIslandUuid(player.getUniqueId());
                if (islandUuid == null || CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(islandUuid) == null) {
                    Schematic schematic = CloudSkyblock.getSchematicManager().getFileSchematicMap().get("_island");
                    CustomChatMessage.sendMessage(player, "Skyblock", "Building your new island with the map &edefault&7!");
                    List<String> unlockedCarpets = new ArrayList<>();
                    unlockedCarpets.add("BROWN");

                    List<String> fences = new ArrayList<>();
                    fences.add(Material.DARK_OAK_FENCE.toString());
                    Island island = new Island(UUID.randomUUID(), player.getUniqueId(), true, 0, 0, schematic, new ArrayList<>(), true, new ConcurrentHashMap<>(), CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getName(), unlockedCarpets, "BROWN", fences, Material.DARK_OAK_FENCE.toString());

                    // set the (I added this because the owner was not being set as an having an island on there player instance)
                    CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(player.getUniqueId(), island.getIslandUuid());

                    if (CloudSkyblock.getPlugin().getIslandManager().createNewIsland(island) && schematic != null && schematic.getClipboard() != null) {

                        Vector multiply = schematic.getClipboard().getMinimumPoint().multiply(new Vector(0, 115, 0));
                        Vector divide = multiply.divide(schematic.getClipboard().getRegion().getCenter());
                        EditSession paste = schematic.paste(new BukkitWorld(island.getWorld()), divide, false, false, null);

                        island.setBridgeMin(new Vector(-3.5D, 0, -1.6D));
                        island.setBridgeMax(new Vector(-2.5D, 250, -21.5D));


                        island.setTempleMin(new Vector(14, 60, -18.5D));
                        island.setTempleMax(new Vector(-15, 150, -60D));

                        CloudSkyblock.getPlugin().getIslandManager().spawnTempleNpcs(island);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                CustomChatMessage.sendMessage(player, "Skyblock", "Teleporting");
                                player.teleport(island.getLocation());
                            }
                        }.runTaskLater(CloudSkyblock.getPlugin(), 20);
                    }

                    ItemStack[] defaultItems = new ItemStack[]{
                            new ItemFactory(Material.WATER_BUCKET, 1).build(),
                            new ItemFactory(Material.LAVA_BUCKET, 1).build(),
                            new ItemFactory(Material.TORCH, 3).build(),
                            new ItemFactory(Material.SUGAR_CANE, 1).build(),
                            new ItemFactory(Material.PUMPKIN_SEEDS, 1).build(),
                            new ItemFactory(Material.SEEDS, 1).build(),
                            new ItemFactory(Material.BROWN_MUSHROOM, 1).build(),
                            new ItemFactory(Material.RED_MUSHROOM, 1).build(),
                            new ItemFactory(Material.BONE, 6).build(),
                            new ItemFactory(Material.CACTUS, 1).build(),
                            new ItemFactory(Material.SAPLING, 1).build(),
                            new ItemFactory(Material.WHEAT, 1).build(),
                            new ItemFactory(Material.ICE, 2).build()
                    };

                    for (BoosterType booster : BoosterType.values()) {
                        island.setBoosterLevel(booster, 1);
                    }

                    CloudSkyblock.getPlugin().getIslandManager().fillDefaultChest(island, defaultItems);
                } else {
                    IslandUtils.getIslandPlayerIsIn(player, island -> {
                        if (island == null) {
                            CustomChatMessage.sendMessage(player, "SkyBlock", "Could not find your island.");
                            CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(player.getUniqueId(), null);
                            return;
                        }

                        if (!island.isLocatedGotten()) {
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    CustomChatMessage.sendMessage(player, "Skyblock", "Teleporting");
                                    player.teleport(island.getLocation());
                                }
                            }.runTaskLater(CloudSkyblock.getPlugin(), 20);
                            return;
                        }
                        new IslandControlPanelGui(player, island);
                    });

                }
            }
        }
    }

    private int getMaximumAllowedBalance(Island island) {
        Optional<Booster> type = island.getBoosterByType(BoosterType.BALANCE_LIMIT);

        if (!type.isPresent()) {
            return 2500;
        }

        Booster booster = type.get();

        switch (booster.getLevel()) {
            case 1:
                return 2500;
            case 2:
                return 5000;
            case 3:
                return 10000;
            case 4:
                return 15000;
            case 5:
                return 30000;
            case 6:
                return 50000;
            default:
                return 2500;
        }
    }
}