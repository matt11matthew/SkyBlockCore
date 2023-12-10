package net.cloudescape.skyblock.miscellaneous.minions.suit;

import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.suit.type.*;

public enum SuitType {

    DISCO(DiscoSuit.class),
    IRONMAN(IronManSuit.class),
    HULK(HulkSuit.class),
    BATMAN(BatmanSuit.class),
    CRAZY(CrazySuit.class);

    private Class<? extends Suit> suitClass;

    SuitType(Class<? extends Suit> suitClass) {
        this.suitClass = suitClass;
    }

    public Suit newSuit(Minion minion) {
        try {
            return this.suitClass.getConstructor(Minion.class).newInstance(minion);
        } catch (Exception e) {
            return null;
        }
    }

    public Class<? extends Suit> getSuitClass() {
        return suitClass;
    }
}
