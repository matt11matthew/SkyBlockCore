package net.cloudescape.skyblock.miscellaneous.minions.suit;

import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import org.bukkit.Bukkit;
import org.bukkit.Color;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DiscoSuit extends Suit {

    public DiscoSuit(Minion minion) {
        super(minion, SuitType.DISCO);

        new SkullFactory().setOwner(Bukkit.getPlayer(minion.getIsland().getIslandOwner()).getName()).build();
        String name = minion.getName();


        String taskName = minion.getIsland().getIslandOwner() + name + minion.getId() + "_discoArmor";
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
            if (minion == null || minion.getMinion() == null || !minion.getMinion().isValid() || minion.getMinion().isDead()) {
                CloudSkyblock.getCloudRunnableManager().cancelTask(taskName);
                return;
            }
            /**
             * Random RGB color within the light zone, dark colours dont go to well with armour :/ looks ass.
             */
            Color color = Color.fromBGR(20 + new Random().nextInt(225), 20 + new Random().nextInt(225), 20 + new Random().nextInt(225));

            setChestplate(color);
            setLeggings(color);
            setBoots(color);
        }, taskName, CloudRunnableType.SYNC, 500, TimeUnit.MILLISECONDS);
    }
}
