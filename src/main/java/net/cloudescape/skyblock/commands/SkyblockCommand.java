package net.cloudescape.skyblock.commands;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.backend.commons.rank.GlobalRank;
import net.cloudescape.backend.commons.rank.GlobalRankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkyblockCommand extends BukkitCommand {

    private String command;
    private int permissionValue;

    public SkyblockCommand(String command) {
        super(command);
        this.command = command;
        register();
    }

    public String getCommand() {
        return command;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public void register() {

        try {

            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());

            if (this.getClass().isAnnotationPresent(SkyblockCommandInfo.class)) {
                SkyblockCommandInfo info = this.getClass().getAnnotation(SkyblockCommandInfo.class);
                List<String> aliases = new ArrayList<>(Arrays.asList(info.aliases()));
                setAliases(aliases);
                setUsage(info.usage());
                setDescription(info.description());
                this.permissionValue = info.permissionValue();
            }

            commandMap.register(command, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!(sender instanceof Player)) {
            execute(sender, args);
            return false;
        }

        Player player = (Player) sender;

        BackendModule backendModule = CloudCore.getModuleManager().getModule(BackendModule.class);
        CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
        GlobalRankManager globalRankManager = backendModule.getGlobalRankManager();

        GlobalRank rank = globalRankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank());

        if (rank == null) return false;

        if (permissionValue == -1) {
            execute(sender, args);
            return false;
        }

        if (rank.getValue() < permissionValue) {
            CustomChatMessage.sendMessage(player, "Permissions", "You do not have permission to use this command!");
            return false;
        }

        execute(sender, args);
        return false;
    }

    public void sendUsage(CommandSender sender) {
        if (usageMessage != null && !usageMessage.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Correct Usage: " + ChatColor.GRAY + usageMessage);
        }
    }
}
