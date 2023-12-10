package net.cloudescape.skyblock.miscellaneous.minions.boost;

import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public enum  MinionBoostType {
    SPEED(MinionBoost.class, Miner.class),
    DOUBLE_XP(MinionBoost.class, Miner.class);

    // MinionBoostType type, long duration
    private Class<? extends MinionBoost> minionClass;
    private Class<? extends Minion>[] allowedMinions;

    MinionBoostType(Class<? extends MinionBoost> minionClass, Class<? extends Minion>... allowedMinions) {
        this.minionClass = minionClass;
        this.allowedMinions = allowedMinions;
    }

    public MinionBoost getMinionBoost(MinionBoostType type, long duration) {
        try {
            return this.minionClass.getConstructor(MinionBoostType.class, Long.class).newInstance(type, duration);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAllowedMinion(Class<? extends Minion> clazz) {
        for (Class<? extends Minion> allowedMinion : allowedMinions) {
            if (allowedMinion == clazz) return true;
        }
        return false;
    }

    public Class<? extends MinionBoost> getMinionClass() {
        return minionClass;
    }

    public Class<? extends Minion>[] getAllowedMinions() {
        return allowedMinions;
    }
}
