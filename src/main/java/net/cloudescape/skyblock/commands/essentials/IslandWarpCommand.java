package net.cloudescape.skyblock.commands.essentials;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.UUIDUtil;
import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandInfo(description = "", usage = "")
public class IslandWarpCommand extends Command {

    public IslandWarpCommand() {
        super("islandwarp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (args.length == 1) {

            try {

                UUID owner = UUIDUtil.getUUID(args[0]);

                CloudSkyblock.getPlugin().getIslandManager().getIsland(owner, island -> {
                    player.teleport(island.getLocation());
                    CustomChatMessage.sendMessage(player, "Island Warp", "Warped to " + args[0] + "'s island.");
                });
            } catch (Exception e) {
                CustomChatMessage.sendMessage(player, "Island Warp", "That player is invalid or did not have an island.");
            }
        } else {
            CustomChatMessage.sendMessage(player, "Island Warp", "Command help");
            CustomChatMessage.sendMessage(player, " - /islandwarp <username>");
        }
    }
}
