package net.cloudescape.skyblock.miscellaneous.minions.suit.type;

import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import org.bukkit.Color;

public class BatmanSuit extends Suit {

    public BatmanSuit(Minion minion){
        super(minion, SuitType.BATMAN);

        Color color = Color.fromBGR(0, 0, 0);

        setHealmet(new SkullFactory().setOwner("neillyken").build());
        setChestplate(color);
        setLeggings(color);
        setBoots(color);
    }
}
