package net.cloudescape.skyblock.commands.essentials;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.modules.modules.server.listeners.ServerPlayerListener;
import com.cloudescape.utilities.CustomChatMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(usage = "/spawn", description = "Teleport to hub.")
public class SpawnCommand extends Command {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        ServerPlayerListener.switchServers(player, "Hub", (success) -> {

            if (success) {
                CustomChatMessage.sendMessage(player, "Skyblock", "You have been returned to spawn.");
            } else {
                CustomChatMessage.sendMessage(player, "Skyblock", "We have failed to send you to spawn.");
            }
        });
    }
}
