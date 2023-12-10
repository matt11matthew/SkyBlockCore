package net.cloudescape.skyblock.miscellaneous.minions.suit.type;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import org.bukkit.Color;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DiscoSuit extends Suit {

    public DiscoSuit(Minion minion) {
        super(minion, SuitType.DISCO);

        String taskName = minion.getIsland().getIslandOwner() + "" + minion.getId() + "_discoArmor";
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
            if (minion == null || minion.getMinion() == null || !minion.getMinion().isValid() || minion.getMinion().isDead()) {
                CloudSkyblock.getCloudRunnableManager().cancelTask(taskName);
                return;
            }
            /**
             * Random RGB color within the light zone, dark colours dont go to well with armour :/ looks ass.
             */
            Color color = Color.fromBGR(20 + new Random().nextInt(225), 20 + new Random().nextInt(225), 20 + new Random().nextInt(225));

            setHealmet(color);
            setChestplate(color);
            setLeggings(color);
            setBoots(color);
        }, taskName, CloudRunnableType.SYNC, 500, TimeUnit.MILLISECONDS);
    }
}
