package net.cloudescape.skyblock.miscellaneous.minions.minions;

import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.animations.ButcherAnimation;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.suit.type.IronManSuit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.*;

public class Butcher extends Minion implements Listener {
    private int mobsKilled;
    private int radius;
    private List<EntityType> killableMobs;
    private float xpCollected;
    private float maxXpValue;

    public Butcher(int id, Island island, int mobsKilled, int radius, String killableMobs, Location location, String name, MinionType type, int health, int hunger, MinionBoost boost){
        super(id, island, location, name, type, health, hunger, boost);

        this.mobsKilled = mobsKilled;
        this.radius = radius;
        this.killableMobs = new ArrayList<>();

        this.maxXpValue = 90.0f;

        for(String mob : killableMobs.split(":")){
            this.killableMobs.add(EntityType.valueOf(mob));
        }

        if (location==null||getMinion()==null){
    return;
        }
        setSuit(new IronManSuit(this));
        setAnimation(new ButcherAnimation(this));
    }

    public void addKill(){
        this.mobsKilled++;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getRadius() {
        return radius;
    }

    public List<EntityType> getKillableMobs() {
        return killableMobs;
    }

    public float getXpCollected() {
        return xpCollected;
    }

    public float getMaxXpValue() {
        return maxXpValue;
    }

    public void setXpCollected(float xpCollected) {
        if(xpCollected > maxXpValue){
            this.xpCollected = maxXpValue;
            return;
        }

        this.xpCollected = xpCollected;
    }
}
