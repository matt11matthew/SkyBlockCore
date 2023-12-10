package net.cloudescape.skyblock.miscellaneous.minions.boost;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public class MinionBoost {
    private MinionBoostType type;

    private long duration;

    public MinionBoost(MinionBoostType type, long duration) {
        this.type = type;
        this.duration = duration;
    }

    public MinionBoostType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public boolean hasExpiered(){
        return System.currentTimeMillis() <= duration;
    }
}
