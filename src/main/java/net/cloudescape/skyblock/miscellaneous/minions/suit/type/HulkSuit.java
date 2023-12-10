package net.cloudescape.skyblock.miscellaneous.minions.suit.type;

import com.cloudescape.utilities.itemstack.SkullFactory;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import org.bukkit.Color;

public class HulkSuit extends Suit {

    public HulkSuit(Minion minion){
        super(minion, SuitType.HULK);

        Color color = Color.fromBGR(0, 153, 76);

        setHealmet(new SkullFactory().setOwner("Incredible_Hulk").build());
        setChestplate(color);
        setLeggings(color);
        setBoots(color);
    }
}
