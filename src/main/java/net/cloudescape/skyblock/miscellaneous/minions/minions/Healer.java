package net.cloudescape.skyblock.miscellaneous.minions.minions;

import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.animations.HealerAnimation;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import org.bukkit.Location;

import java.util.ArrayList;

public class Healer extends Minion{
    private int radius;
    private int maximumEffected;
    private ArrayList<MinionType> unlockedTypes;

    public Healer(int id, Island island, Location location, String name, MinionType type, int health, int hunger, MinionBoost boost, int radius, int maximumEffected, ArrayList<MinionType> unlockedTypes){
        super(id, island, location, name, type, health, hunger, boost);

        this.maximumEffected    = maximumEffected;
        this.radius             = radius;
        this.unlockedTypes      = unlockedTypes;

        setAnimation(new HealerAnimation(this));
    }

    public int getRadius() {
        return radius;
    }

    public int getMaximumEffected() {
        return maximumEffected;
    }

    public ArrayList<MinionType> getUnlockedTypes() {
        return unlockedTypes;
    }
}
