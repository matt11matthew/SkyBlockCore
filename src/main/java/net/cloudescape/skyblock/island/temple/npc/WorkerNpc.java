package net.cloudescape.skyblock.island.temple.npc;

import com.cloudescape.utilities.CustomChatMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

import java.util.Random;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class WorkerNpc extends TempleNpc {

    public WorkerNpc() {
        super(-10.923, 104, -44.943, 0, 0, ChatColor.GREEN + "Temple Worker", TempleNpcStatus.RELEASED, player -> {
            String[] messages = new String[]{
                    "Keep the temple clean!",
                    "Purchasing upgrades allows you to unlock new parts of the temple",
                    "Maxing out boosters may lead to something ???",
                    "Maybe check out the ruins in the temple"
            };
            String message = messages[new Random().nextInt(messages.length - 1)];
            CustomChatMessage.sendMessage(player, "Worker", message);
        });
    }

    @Override
    public void spawn(World world) {
        Location location = getLocation(world);
        IronGolem ironGolem = (IronGolem) world.spawnEntity(location, EntityType.IRON_GOLEM);
        ironGolem.setInvulnerable(true);
        ironGolem.setGravity(false);
        ironGolem.setCustomNameVisible(true);
        ironGolem.setCustomName(this.displayName);
        ironGolem.setPlayerCreated(false);
    }
}
