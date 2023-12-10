package net.cloudescape.skyblock.miscellaneous.minions.animations;

import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Healer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;


public class HealerAnimation extends Animation {
    private Healer healer;
    private int effectedMobs = 0;

    public HealerAnimation(Healer healer) {
        this.healer = healer;

        runTaskTimer(CloudSkyblock.getPlugin(), 0L, ((20 * 60) * 5));
    }

    @Override
    public void displayParticles() {

    }

    @Override
    public void run() {

        for (Entity entity : healer.getMinion().getNearbyEntities(healer.getRadius(), healer.getRadius(), healer.getRadius())) {
            if (CloudSkyblock.getPlugin().getMinionManager().isMinion(entity)) {
                Minion minion = CloudSkyblock.getPlugin().getMinionManager().getMinion(entity);

                if(!healer.getUnlockedTypes().contains(minion.getType()))
                    continue;

                if(minion.getMaxHealth() >= minion.getHealth())
                    continue;

                minion.heal(1);
                effectedMobs++;
            }

            if (effectedMobs >= healer.getMaximumEffected())
                break;
        }

        healer.getIsland().getIslandMembers().forEach((uuid, islandRank) -> {
            if(Bukkit.getPlayer(uuid).isOnline()){
                CustomChatMessage.sendMessage(Bukkit.getPlayer(uuid),"&c[&eHealer&c]", "&eI have healed &c" + effectedMobs + " &eminions on our island!");
            }
        });

        effectedMobs = 0;
    }
}
