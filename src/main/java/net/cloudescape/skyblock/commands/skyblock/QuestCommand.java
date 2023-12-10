package net.cloudescape.skyblock.commands.skyblock;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import net.cloudescape.skyblock.miscellaneous.quest.gui.QuestViewGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(description = "", usage = "")
public class QuestCommand extends Command {

    public QuestCommand() {
        super("quests");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new QuestViewGui(player);
        }
    }
}
