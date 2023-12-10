package net.cloudescape.skyblock.commands.essentials.blocklevels;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import net.cloudescape.skyblock.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(description = "", usage = "", aliases = { "level" })
public class BlockLevelCommand extends Command {

    public BlockLevelCommand() {
        super("levels");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            Logger.log("&cYou are required to be a player to view island block level values.");
            return;
        }

        Player player = (Player) sender;
        new BlockLevelMenu(player);
    }
}
