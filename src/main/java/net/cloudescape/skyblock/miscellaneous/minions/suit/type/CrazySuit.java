package net.cloudescape.skyblock.miscellaneous.minions.suit.type;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;

import java.util.concurrent.TimeUnit;

public class CrazySuit extends Suit {

    public CrazySuit(Minion minion){
       super(minion, SuitType.CRAZY);

       String taskName = minion.getIsland().getIslandUuid() + "" + minion.getId() + "_crazysuit";
       CloudSkyblock.getCloudRunnableManager().schedule(() -> {

           }, taskName, CloudRunnableType.ASYNC, 500, TimeUnit.MILLISECONDS);
    }


}
