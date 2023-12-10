package net.cloudescape.skyblock.commands.essentials;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.rank.GlobalRank;
import net.cloudescape.skyblock.utils.ParserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            final int perPage = 10;
            final int totalPages = getPages(player, perPage);

            if (args.length == 1) {
                Optional<Integer> pageOptional = ParserUtil.parseInt(args[0]);

                if (pageOptional.isPresent()) {
                    int page = pageOptional.get();

                    if (page < 1 || page > totalPages) {
                        CustomChatMessage.sendMessage(player, "Skyblock Commands", "That is not a valid page, maximum page: " + totalPages + "!");
                        return false;
                    }

                    StringBuilder available = new StringBuilder();
                    for (BukkitCommand command : getCommands(player, page, perPage)) {
                        available.append("&7" + command.getName()).append("&f, ");
                    }
                    if (available.length() < 1) {
                        CustomChatMessage.sendMessage(player, "Skyblock Commands", "&cYou have no available commands!");
                        return false;
                    }
                    String commands = available.toString().trim();
                    commands = commands.substring(0, commands.length() - 1);
                    CustomChatMessage.sendMessage(player, "Skyblock Commands", "&aPage [1/" + totalPages + "].");
                    CustomChatMessage.sendMessage(player, "&bYou have permissions to use: ");
                    CustomChatMessage.sendMessage(player, commands);
                } else {
                    CustomChatMessage.sendMessage(player, "Skyblock Help", "Please enter a valid page number!");
                }
            } else {
                StringBuilder available = new StringBuilder();
                for (BukkitCommand command : getCommands(player, 1, perPage)) {
                    available.append("&7" + command.getName()).append("&f, ");
                }
                if (available.length() < 1) {
                    CustomChatMessage.sendMessage(player, "Skyblock Commands", "&cYou have no available commands!");
                    return false;
                }
                String commands = available.toString().trim();
                commands = commands.substring(0, commands.length() - 1);
                CustomChatMessage.sendMessage(player, "Skyblock Commands", "&aPage [1/" + totalPages + "].");
                CustomChatMessage.sendMessage(player, "&bYou have permissions to use: ");
                CustomChatMessage.sendMessage(player, commands);
            }
        }

        return false;
    }

    private int getPages(Player player, int perPage) {

        int availableCommand = 0;

        GlobalRank rank = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager().getGlobalRank(CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId()).getGlobalRank());

        for (com.cloudescape.modules.Command command : CloudCore.getInstance().getCommandManager().getCommands()) {
            if (command.getClass().isAnnotationPresent(CommandInfo.class)) {
                CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
                if (info.permissionValue() == -1 || rank.getValue() >= info.permissionValue()) {
                    availableCommand += 1;
                }
            }
        }

        int totalItems = availableCommand;
        int pages = (totalItems / perPage);

        if (pages < 1)
            pages = 1;

        return ((totalItems % perPage == 0 || totalItems <= perPage) ? pages : pages + 1);
    }

    private List<BukkitCommand> getCommands(Player player, int page, int perPage) {

        List<BukkitCommand> commands = new ArrayList<>();

        GlobalRank rank = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager().getGlobalRank(CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId()).getGlobalRank());

        try {
            for (int i = ((page - 1) * perPage); i < (((page - 1) * perPage) + perPage); i++) {
                com.cloudescape.modules.Command command = CloudCore.getInstance().getCommandManager().getCommands().get(i);
                if (command != null) {
                   if (command.getClass().isAnnotationPresent(CommandInfo.class)) {
                       CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
                       if (info.permissionValue() == -1 || rank.getValue() >= info.permissionValue()) {
                           commands.add(command);
                       }
                   }
                }
            }
        } catch (IndexOutOfBoundsException e) { /* Ignored */ }

        return commands;
    }
}
