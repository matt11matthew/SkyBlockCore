package net.cloudescape.skyblock.commands.essentials;

import com.cloudescape.modules.Command;
import com.cloudescape.modules.CommandInfo;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.utils.ParserUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandInfo(usage = "", description = "", permissionValue = 500) // perm tempoary till we decide rank perms.
public class SpeedCommand extends Command {

    public SpeedCommand() {
        super("speed");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                Optional<Float> speedOptional = ParserUtil.parseFloat(args[0]);

                try {
                    if (speedOptional.isPresent()) {
                        float speed = speedOptional.get();
                        if (player.isFlying())
                            player.setFlySpeed((speed / 10));
                        else
                            player.setWalkSpeed((speed / 10));
                        CustomChatMessage.sendMessage(player, "Skyblock Speed", "Your " + (player.isFlying() ? "flying" : "walking") + " speed has been set to " + speed);
                    } else {
                        CustomChatMessage.sendMessage(player, "Skyblock Speed", "You have entered an invalid value! " + args[0]);
                    }
                } catch (Exception e) {
                    CustomChatMessage.sendMessage(player, "Skyblock Speed", "You cannot set your speed to that value.");
                }
            }
        }
    }
}
