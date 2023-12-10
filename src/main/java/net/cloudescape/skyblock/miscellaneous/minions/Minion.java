package net.cloudescape.skyblock.miscellaneous.minions;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.minions.animations.Animation;
import net.cloudescape.skyblock.miscellaneous.minions.animations.ButcherAnimation;
import net.cloudescape.skyblock.miscellaneous.minions.animations.MinerAnimation;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.cloudescape.skyblock.miscellaneous.minions.suit.Suit;
import net.cloudescape.skyblock.miscellaneous.minions.suit.type.IronManSuit;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public abstract class Minion {
    /**
     * Id of the minion, this should be per island starting at one & limiting it to the maximum amount of minions allowed on that island
     * witch is determined by the boosters.
     */
    private int id;

    /**
     * Values of the health, hunger, max health, max hunger, these values determin how long the minion will live for
     * but can be edited/added to to allow minions to survive for a longer time.
     */
    private int health, hunger, maxHealth, maxHunger;

    /**
     * This value will determin if the minion has a boost, each minion will react different to boost
     * so it's important to also make sure the boost is usable on the minion before allowing the player
     * to redeem it.
     */
    private MinionBoost boost;

    /**
     * Minion type will determin what type of minion we are spawning into the world, different types of minions will
     * have different abilities/fetures that can be edited / added on to.
     */
    private MinionType type;

    /**
     * Name of the minion is what will be displayed above the minion head, and in chat. this value will be editable
     * and players will be able to add colours witch they have unlocked for there island. once again it's per island
     * not per player.
     */
    private String name;

    /**
     * This is the minion entity that we will be using to animate& interact with.
     */
    private ArmorStand minion;


    private Island island;

    private Animation animation;

    private MinionTagType minionTag;

    private Suit suit;

    private boolean isSpawned;

    public Minion(int id, Island island, Location location, String name, MinionType type, int health, int hunger, MinionBoost boost) {
        this.id = id;
        this.island = island;
        this.name = name;
        this.type = type;
        this.health = health;
        this.hunger = hunger;
        this.boost = boost;

        if (this.minionTag == null) this.minionTag = MinionTagType.DEFAULT;

        location.getChunk().load(); // to ensure that the chunk we are loading the minion on is loaded so we dont get no errors.
//        spawn(location);

        this.suit = new IronManSuit(this);
    }

    public void spawn(Location location) {
        // Adding 0.5, 0, 0.5 to location to ensure that it is in the center of the block, it also looks better for the animations as arms & legs touch the block in front
        // if it is rotated properly
        this.minion = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);

        String tag = this.getMinionTag().getTagText();

        this.minion.setVisible(true);
        this.minion.setBasePlate(false);
        this.minion.setCanPickupItems(false);
        this.minion.setArms(true);
        this.minion.setRemoveWhenFarAway(false);
        this.minion.setCustomNameVisible(true);
        this.minion.setSmall(true);
        this.minion.setCustomName(ChatColor.translateAlternateColorCodes('&', ((getMinionTag() != null || getMinionTag() != MinionTagType.DEFAULT) ? "&8[&5" + tag + "&8] &7" : "") + getName()));
        this.minion.setMetadata("minion", new FixedMetadataValue(CloudSkyblock.getPlugin(), this));

        this.isSpawned = true;
        switch (type) {
            case MINER:
                setAnimation(new MinerAnimation(((Miner) this)));
                break;
            case BUTCHER:
                setAnimation(new ButcherAnimation(((Butcher) this)));
                break;
        }

        this.suit = new IronManSuit(this);
    }

    public Block getFacingBlock() {
        double dir = (this.minion.getLocation().getYaw() * 4.0F / 360.0F) + 0.5D;
        int direction = (int) dir;
        switch (direction) {
            case 0:
                return minion.getLocation().clone().add(0, 0, 1).getBlock();
            case 1:
                return minion.getLocation().clone().add(-1, 0, 0).getBlock();
            case 2:
                return minion.getLocation().clone().add(0, 0, -1).getBlock();
            case 3:
                return minion.getLocation().clone().add(1, 0, 0).getBlock();
            default:
                return null;
        }
    }

    public void removeHunger(int amount) {
        if (hunger - amount <= 0) {
            removeHealth(1);
            return;
        }

        if (getAnimation() != null) {
            getAnimation().displayParticles();
        }

        hunger -= amount;
    }

    public void removeHealth(int amount) {
        if (health - amount <= 0) {
            kill(true, island);
            return;
        }

        if (getAnimation() != null) {
            getAnimation().displayParticles();
        }

        health -= amount;
    }

    /**
     * This method will feed the minion witch will allow it to live longer and will extend the time
     * that the minion will start to decay in health.
     *
     * @param amount that we are feeding the minion
     */
    public void feed(int amount) {
        if ((hunger + amount) > maxHunger) {
            hunger = maxHunger;
            return;
        }

        if (getAnimation() != null) {
            getAnimation().displayParticles();
        }

        this.hunger += amount;
    }

    /**
     * This method is to heal the minion witch will make it live longer.
     *
     * @param amount that we are healthing the minion.
     */
    public void heal(int amount) {
        if ((health + amount) > maxHealth) {
            health = maxHealth;
            return;
        }

        if (getAnimation() != null) {
            getAnimation().displayParticles();
        }

        this.health += amount;
    }

    /**
     * This method will kill the minion & remove it from game & memory
     * <p>
     * This method should only be called when:
     * 1. removing the minion from the world and giving them an item to replace the minion with will require the following
     * 1. Creating and managing item stacks to register and react to minion items.
     * 2. Enable detection of when a player is clicking a item to see if it is a minion item thats wanting to be placed
     * 3. Place the minion and insert the data into memory
     * 2. When the minion health and hunger are down to zero witch there for would mean the minion has reached it's timeline in the game :*(
     */
    public void kill(boolean dead, Island island) {
        if (minion != null) {

            // some explosion effect maybe even add particles :)
            minion.getLocation().getWorld().createExplosion(0, 0, 0, 2);

            if (dead) {
                if (island != null) {
                    island.getLoadedMinions().remove(island.getMinionByEntity(minion));
                } else {
                    CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(minion.getWorld()).ifPresent(island1 -> island1.getLoadedMinions().remove(island1.getMinionByEntity(minion)));
                }
            }

            // remove the minion from the world.
            minion.remove();
            isSpawned = false;
        }

        if (getAnimation() != null) {
            getAnimation().displayParticles(); // display particles before killing :)
            getAnimation().cancel(); // stop the task
        }

        isSpawned = false;
    }

    public void setAnimation(Animation animation) {
        if (this.animation != null) {
            animation.cancel();
        }
        this.animation = animation;
    }

    public void setSuit(Suit newSuit) {
        if (this.suit != null) {
            switch (this.suit.getSuitType()) {
                case DISCO:
                    CloudSkyblock.getCloudRunnableManager().cancelTask(getIsland().getIslandOwner() + "" + getId() + "_discoArmor");
                    break;
            }
        }
        this.suit = newSuit;
    }

    public MinionType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public Island getIsland() {
        return island;
    }

    public ArmorStand getMinion() {
        return minion;
    }

    public String getName() {
        return name;
    }

    public MinionBoost getBoost() {
        return boost;
    }

    public void setBoost(MinionBoost boost) {
        this.boost = boost;
    }

    public int getMaxHunger() {
        return maxHunger;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHealth() {
        return health;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public MinionTagType getMinionTag() {
        return minionTag;
    }

    public void setMinionTag(MinionTagType minionTag) {
        this.minionTag = minionTag;
    }

    public boolean isSpawned() {
        return isSpawned;
    }
}