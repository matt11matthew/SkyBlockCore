package net.cloudescape.skyblock.miscellaneous.minions.suit;

import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import org.bukkit.Color;

public class IronManSuit extends Suit {

    public IronManSuit(Minion minion){
        super(minion,SuitType.IRONMAN);

        setHealmet(new SkullFactory().setOwner("DavidPrime14").build()); // is this right?
        setChestplate(Color.fromBGR(0, 0, 193));
        setLeggings(Color.fromBGR(0, 0, 193));
        setBoots(Color.fromBGR(0, 0, 193));
    }
}
