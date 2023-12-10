package net.cloudescape.skyblock.miscellaneous.minions.suit.type;

import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import org.bukkit.Color;

public class IronManSuit extends Suit {

    public IronManSuit(Minion minion){
        super(minion, SuitType.IRONMAN);
        if (minion.isSpawned()){

            setHealmet(new SkullFactory().setOwner("DavidPrime14").build());
            setChestplate(Color.fromBGR(0, 0, 193));
            setLeggings(Color.fromBGR(0, 0, 193));
            setBoots(Color.fromBGR(0, 0, 193));
        }

    }
}
