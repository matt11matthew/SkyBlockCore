package net.cloudescape.skyblock.commands.essentials.istop;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.modules.modules.skyblock.islandtop.IslandTopGui;
import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandInfo(usage = "", description = "")
public class IslandTopCommand extends Command {

    public IslandTopCommand() {
        super("istop");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

          try {
              new IslandTopGui(player, (document) -> {
                  UUID uuid = UUID.fromString(document.get("owner").toString());
                  CloudSkyblock.getPlugin().getIslandManager().getIsland(uuid, island -> {
                      player.teleport(island.getLocation());
                  });
              });
          } catch (Exception e){
              player.sendMessage(ChatColor.RED+"Could not load top 10 islands");
              e.printStackTrace();
          }
        }
    }
}
