package net.cloudescape.skyblock.island.spawner;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.utils.SpawnerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew E on 4/22/2018.
 */
@CommandInfo(usage = "", description = "", permissionValue = 900)
public class SpawnerGiveCommand extends Command {
    public SpawnerGiveCommand() {
        super("spawnergive");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String playerName = args[0];
            EntityType entityType = null;
            GolemType golemType = null;
            int tier = -1;

            try {
                tier = Integer.parseInt(args[2].trim());
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Please enter proper tier in the range[1, 5].");
                return;
            }

            if (tier > 5) {
                sender.sendMessage(ChatColor.RED + "Please enter proper tier in the range[1, 5].");
                return;
            }
            if (tier < 1) {
                sender.sendMessage(ChatColor.RED + "Please enter proper tier in the range[1, 5].");
                return;
            }
            try {
                entityType = EntityType.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Please enter proper entity type.");
                return;
            }

            Player playerExact = Bukkit.getPlayerExact(playerName);
            if (playerExact == null || !playerExact.isOnline()) {
                sender.sendMessage(ChatColor.RED + playerName + " is not online.");
                return;
            }


            if (entityType == EntityType.IRON_GOLEM) {
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "You must specify a golem type: " + ChatColor.GRAY + "[IRON, GOLD, DIAMOND, EMERALD]");
                    return;
                }

                try {
                    golemType = GolemType.valueOf(args[3].toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "You must specify a golem type: " + ChatColor.GRAY + "[IRON, GOLD, DIAMOND, EMERALD]");
                    return;
                }
            }

            GolemSpawnerInfo golemSpawnerInfo = new GolemSpawnerInfo(tier, entityType, golemType);

            ItemStack spawnerItemStack = SpawnerUtils.createSpawnerItemStack(golemSpawnerInfo);
            playerExact.getInventory().addItem(spawnerItemStack);
            if (golemSpawnerInfo.isIronGolem()) {
                CustomChatMessage.sendMessage(playerExact,"Spawner", "You were given " + golemSpawnerInfo.getEntityType().getName() + " "  +golemSpawnerInfo.getGolemType().getName()+" spawner.");
            } else {
                CustomChatMessage.sendMessage(playerExact,"Spawner", "You were given " + golemSpawnerInfo.getEntityType().getName() + " spawner.");
            }
            sender.sendMessage(ChatColor.AQUA+"Gave spawner to " + playerExact.getName());
        } else {
            sendUsage(sender);
        }

    }
}
