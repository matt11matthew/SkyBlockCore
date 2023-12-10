package net.cloudescape.skyblock.miscellaneous.minions.minions;

import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.animations.MinerAnimation;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import org.bukkit.Location;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public class Miner extends Minion {
    private int blocksMined;
    private Location linkedChest;

    public Miner(int id, Island island, Location location, String name, MinionType type, int health, int hunger, MinionBoost boost){
        super(id, island, location, name, type, health, hunger, boost);
        super.setAnimation(new MinerAnimation(this));
    }

    public int getBlocksMined(){
        return blocksMined;
    }

    public void setBlocksMined(int blocksMined) {
        this.blocksMined = blocksMined;
    }
}