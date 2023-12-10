package net.cloudescape.skyblock.commands;

import com.cloudescape.CloudCore;
import net.cloudescape.skyblock.commands.essentials.IslandWarpCommand;
import net.cloudescape.skyblock.commands.essentials.SpawnCommand;
import net.cloudescape.skyblock.commands.essentials.blocklevels.BlockLevelCommand;
import net.cloudescape.skyblock.commands.essentials.istop.IslandTopCommand;
import net.cloudescape.skyblock.commands.skyblock.ISAdminCommand;
import net.cloudescape.skyblock.commands.skyblock.IslandCommand;
import net.cloudescape.skyblock.island.spawner.SpawnerGiveCommand;
import net.cloudescape.skyblock.miscellaneous.worldedit.commands.PlayerWorldeditCommand;

public class CommandManager {

    public void loadCommands() {

        com.cloudescape.modules.CommandManager commandManager = CloudCore.getInstance().getCommandManager();

        // TODO load commands
        commandManager.addCommand(new IslandCommand());
        commandManager.addCommand(new ISAdminCommand());
        commandManager.addCommand(new BlockLevelCommand());
//        addCommand(new QuestCommand());

        // Essentials
        commandManager.addCommand(new SpawnCommand());
        commandManager.addCommand(new IslandTopCommand());

        commandManager.addCommand(new IslandWarpCommand());

        // PWE
        commandManager.addCommand(new PlayerWorldeditCommand());


        /**
         * Spawner commands
         */
        commandManager.addCommand(new SpawnerGiveCommand());

//        commandManager.addCommand(new HelpCommand());
    }
}
