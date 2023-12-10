package net.cloudescape.skyblock.miscellaneous.worldedit.commands;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.utilities.Callback;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.itemstack.ItemFactory;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.RegionSelection;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.worldedit.PlayerWorldeditManager;
import net.cloudescape.skyblock.miscellaneous.worldedit.Position;
import net.cloudescape.skyblock.miscellaneous.worldedit.listener.PWEListener;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(usage = "/pwe", description = "Player worldedit commands", permissionValue=50) // Legend + for now.
public class PlayerWorldeditCommand extends Command {

    public PlayerWorldeditCommand() {
        super("pwe");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerWorldeditManager pweManager = CloudSkyblock.getPlugin().getPlayerWorldeditManager();
            // pwe 0set 1block
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("wand")) {
                    player.getInventory().addItem(PWEListener.getWandStack());
                }
            } else if (args.length == 2) {
                CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
                switch (args[0]) {
                    case "set":
                        try {
                            Material block = Material.getMaterial(args[1].toUpperCase());
                            Position position = pweManager.getPlayerPosition(player);

                            if (position.getPosition1() == null || position.getPosition2() == null) {
                                CustomChatMessage.sendMessage(player, "PWE", "You must make a selection!");
                                CustomChatMessage.sendMessage(player, "PWE", "Type /pwe wand then click select your 2 points!");
                                return;
                            }

                            RegionSelection selection = new CuboidSelection(player.getWorld(), position.getPosition1(), position.getPosition2());
                            EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(player.getWorld()), (cloudEscapePlayer.getHeroClass().equals("MAGE") ? 250 : 100)); // Default 100, mage = 250

                            getBlocksBetweenPositions(position.getPosition1(), position.getPosition2(), callback -> {

                              try {

                                  CustomChatMessage.sendMessage(player, "PWE", "Beginning changing of blocks to " + StringUtil.capitalizeFirstLetter(block.name()) + ". Blocks changing: " + callback.size());

                                  if (CloudSkyblock.getPlugin().getPlayerWorldeditManager().getAmountInInventory(player, new ItemFactory(block).build()) < callback.size()) {
                                      CustomChatMessage.sendMessage(player, "PWE", "You do not have enough " + StringUtil.capitalizeFirstLetter(block.name()) + " to set this region! You require " + callback.size());
                                      return;
                                  }

                                  session.setBlocks(selection.getRegionSelector().getRegion(), new BaseBlock(block.getId()));
                                  CloudSkyblock.getPlugin().getPlayerWorldeditManager().removeAmountFromInventory(player, new ItemFactory(block).build(), callback.size());
                                  session.addNotifyTask(() -> {
                                      CustomChatMessage.sendMessage(player, "PWE", "Area set to " + StringUtil.capitalizeFirstLetter(block.name()) + ". Total blocks changed: " + session.getBlockChangeCount());

                                      try {
                                          selection.getRegionSelector().getRegion().getChunks().forEach(chunk -> {
                                              player.getWorld().refreshChunk(chunk.getBlockX(), chunk.getBlockZ());
                                          });

                                          CustomChatMessage.sendMessage(player, "PWE", "Chunks have been refreshed where your edits took place.");
                                      } catch (IncompleteRegionException e) {
                                          e.printStackTrace();
                                          CustomChatMessage.sendMessage(player, "PWE", "An error was thrown, &c" + e.getMessage());
                                      }
                                  });
                              } catch (IncompleteRegionException e) {
                                  CustomChatMessage.sendMessage(player, "PWE", "There was an error setting region!");
                              }
                          });
                        } catch (Exception e) {
                             CustomChatMessage.sendMessage(player, "Player ", "That is not a valid block type.");
                            CustomChatMessage.sendMessage(player, "Player ", "You can find a list of them at: &ahttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
                        }
                        break;
                }
            } else {
                sendHelp(player);
            }
        }
    }

    private void sendHelp(Player player) {
        CustomChatMessage.sendMessage(player, "PWE", "Command help");
        CustomChatMessage.sendMessage(player, "/pwe set <block>");
    }

    private void getBlocksBetweenPositions(Location position1, Location position2, Callback<List<Block>> callback) {

        List<Block> blocks = new ArrayList<>();

        Bukkit.getScheduler().runTaskAsynchronously(CloudSkyblock.getPlugin(), () -> {

            int topBlockX = (position1.getBlockX() < position2.getBlockX() ? position2.getBlockX() : position1.getBlockX());
            int bottomBlockX = (position1.getBlockX() > position2.getBlockX() ? position2.getBlockX() : position1.getBlockX());

            int topBlockY = (position1.getBlockY() < position2.getBlockY() ? position2.getBlockY() : position1.getBlockY());
            int bottomBlockY = (position1.getBlockY() > position2.getBlockY() ? position2.getBlockY() : position1.getBlockY());

            int topBlockZ = (position1.getBlockZ() < position2.getBlockZ() ? position2.getBlockZ() : position1.getBlockZ());
            int bottomBlockZ = (position1.getBlockZ() > position2.getBlockZ() ? position2.getBlockZ() : position1.getBlockZ());

            for(int x = bottomBlockX; x <= topBlockX; x++) {
                for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                    for(int y = bottomBlockY; y <= topBlockY; y++) {
                        Block block = position1.getWorld().getBlockAt(x, y, z);
                        blocks.add(block);
                    }
                }
            }

            callback.call(blocks);
        });
    }

    private void getSpecificBlocksBetweenPositions(Location position1, Location position2, Material blockType, Callback<List<Block>> callback) {

        List<Block> blocks = new ArrayList<>();

        Bukkit.getScheduler().runTaskAsynchronously(CloudSkyblock.getPlugin(), () -> {

            int topBlockX = (position1.getBlockX() < position2.getBlockX() ? position2.getBlockX() : position1.getBlockX());
            int bottomBlockX = (position1.getBlockX() > position2.getBlockX() ? position2.getBlockX() : position1.getBlockX());

            int topBlockY = (position1.getBlockY() < position2.getBlockY() ? position2.getBlockY() : position1.getBlockY());
            int bottomBlockY = (position1.getBlockY() > position2.getBlockY() ? position2.getBlockY() : position1.getBlockY());

            int topBlockZ = (position1.getBlockZ() < position2.getBlockZ() ? position2.getBlockZ() : position1.getBlockZ());
            int bottomBlockZ = (position1.getBlockZ() > position2.getBlockZ() ? position2.getBlockZ() : position1.getBlockZ());

            for(int x = bottomBlockX; x <= topBlockX; x++) {
                for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                    for(int y = bottomBlockY; y <= topBlockY; y++) {
                        Block block = position1.getWorld().getBlockAt(x, y, z);
                        if (block.getType() == blockType) {
                            blocks.add(block);
                        }
                    }
                }
            }

            callback.call(blocks);
        });
    }
}
