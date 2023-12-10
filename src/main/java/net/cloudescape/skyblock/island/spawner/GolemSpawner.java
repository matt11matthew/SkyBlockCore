package net.cloudescape.skyblock.island.spawner;

import net.cloudescape.skyblock.listener.EasyMetadata;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 4/15/2018.
 */
public class GolemSpawner {
    private int x;
    private int y;
    private int z;
    private GolemType golemType;
    private int tier;
    private long delay;
    private UUID owner;
    private ArmorStand hologram;
    private String displayName;
    private EntityType entityType;
    private int count;
    private double dropMultiplier;

    public EntityType getEntityType() {
        return entityType;
    }

    public GolemSpawner(int x, int y, int z, GolemType golemType, int tier, UUID owner, EntityType entityType, int count, double dropMultiplier) {
        this.x = x;
        this.y = y;
        this.dropMultiplier = dropMultiplier;
        this.z = z;
        this.golemType = golemType;
        this.tier = tier;
        this.owner = owner;
        this.entityType = entityType;
        this.count = count;
        this.displayName = "Spawner";
        this.delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
    }

    public int getCount() {

        return count;
    }

    private long customMessageDisplayTime;
    private String customMessage;

    public long getCustomMessageDisplayTime() {
        return customMessageDisplayTime;
    }


    public void setCustomMessageDisplayTime(long customMessageDisplayTime) {
        this.customMessageDisplayTime = customMessageDisplayTime;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getCustomMessage() {
        return customMessage;
    }


    public void setCount(int count) {
        this.count = count;
        if (entityType == EntityType.IRON_GOLEM) {
            if (tier < 5) {
                tier++;
            }
            if (tier >= 5 && count == 5 && golemType != GolemType.EMERALD) {
                this.count = 1;
                this.tier = 1;
                final GolemType oldGolemType = this.golemType;
                this.golemType = golemType.getNextType();
                updateDisplayName();
                sendHologramText(ChatColor.WHITE + ChatColor.BOLD.toString() + "UPGRADED " + oldGolemType.getColor() + oldGolemType.getName() + " " + ChatColor.GRAY + ChatColor.BOLD.toString() + " -> " + golemType.getColor() + golemType.getName() + " ", 3, TimeUnit.SECONDS);
                return;
            }
//            if (count == 5 && tier == 5) {
//                this.count = 1;
//                this.tier = 1;
//                this.dropMultiplier += 1.0D;
//                updateDisplayName();
//                sendHologramText(ChatColor.WHITE + ChatColor.BOLD.toString() + "UPGRADED " + ChatColor.AQUA + ((dropMultiplier - 1)) + "x " + ChatColor.GRAY + ChatColor.BOLD.toString() + " -> " + ChatColor.AQUA + ((dropMultiplier)) + "x ", 3, TimeUnit.SECONDS);
//            } else {
//                if (count == 5 && tier < 5) {
//                    this.count = 1;
//                    this.tier++;
//                    updateDisplayName();
//                    sendHologramText(this.displayName + ChatColor.GRAY + " +" + ((tier * 20) + "%"), 3, TimeUnit.SECONDS);
//                }
//            }
        } else {
            if (count == 5 && tier < 5) {
                this.count = 1;
                this.tier++;
                updateDisplayName();
                sendHologramText(this.displayName + ChatColor.GRAY + " +" + ((tier * 20) + "%"), 3, TimeUnit.SECONDS);
            }
        }

    }

    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }

    public ArmorStand spawnHologram(World world) {
        if (this.hologram != null) {
            this.hologram.remove();

        }
        Location location = getLocation(world);
        location.setY(location.getBlockY() + 1);
        this.hologram = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        this.hologram.setMarker(false);
        this.hologram.setVisible(false);
        this.hologram.setCanPickupItems(false);
        this.hologram.setCustomNameVisible(true);
        this.hologram.setCustomName(displayName);
        this.hologram.setAI(false);
        this.hologram.setGravity(false);
        this.hologram.setInvulnerable(true);
        this.hologram.setSilent(true);
        this.hologram.setSmall(true);
        this.hologram.setCollidable(false);

        return hologram;

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setGolemType(GolemType golemType) {
        this.golemType = golemType;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public GolemType getGolemType() {
        return golemType;
    }

    public int getTier() {
        return tier;
    }

    public UUID getOwner() {
        return owner;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void sendHologramText(String text, long time, TimeUnit timeUnit) {
        this.customMessage = text;
        this.customMessageDisplayTime = System.currentTimeMillis() + timeUnit.toMillis(time);
        updateDisplayName();
    }

    public void updateDisplayName() {
        if (System.currentTimeMillis() < customMessageDisplayTime) {
            this.displayName = this.customMessage;
            if (hologram != null) {
                hologram.setCustomName(this.displayName);
            }
        } else {
            customMessageDisplayTime = 0L;

            if (entityType == EntityType.IRON_GOLEM) {
                ChatColor color = null;
                switch (getGolemType()) {
                    case IRON:
                        color = ChatColor.GRAY;
                        break;
                    case GOLD:
                        color = ChatColor.YELLOW;
                        break;
                    case DIAMOND:
                        color = ChatColor.AQUA;
                        break;
                    case EMERALD:
                        color = ChatColor.GREEN;
                        break;
                }
                String fixedName = golemType.name().substring(0, 1).toUpperCase() + golemType.name().substring(1, golemType.name().length()).toLowerCase();
                this.displayName = color + fixedName + " " + ChatColor.AQUA.toString() + count + "x " + ChatColor.WHITE + (tier * 20) + "%";
            } else {
                this.displayName = ChatColor.AQUA.toString() + count + "x " + ChatColor.WHITE + (tier * 20) + "%";

            }
            if (hologram != null) {
                hologram.setCustomName(this.displayName);
            }
        }

    }

    public void setHologram(ArmorStand armorStand) {
        this.hologram = armorStand;
    }

    public void despawn() {

        if (hologram != null) {
            hologram.remove();
        }
    }

    public void spawn(World world) {
        if (System.currentTimeMillis() > delay) {

            if (entityType == EntityType.IRON_GOLEM) {
                IronGolem ironGolem = (IronGolem) world.spawnEntity(getLocation(world).add(0, 1, 0), EntityType.IRON_GOLEM);
                ironGolem.setCustomName(ChatColor.AQUA.toString() + count + "x " + getGolemType().toString().toLowerCase());
                ironGolem.setCustomNameVisible(true);
                ironGolem.setMetadata("gtier", new EasyMetadata<>(getTier()));
                ironGolem.setMetadata("gtype", new EasyMetadata<>(getGolemType().toString()));
                ironGolem.setMetadata("gcount", new EasyMetadata<>(count));
//                Color color = null;
//                switch (getGolemType()) {
//                    case IRON:
//                        color = Color.GRAY;
//                        break;
//                    case GOLD:
//                        color = Color.YELLOW;
//                        break;
//                    case DIAMOND:
//                        color = Color.AQUA;
//                        break;
//                    case EMERALD:
//                        color = Color.GREEN;
//                        break;
//                }
//                if (color != null) {
//                    ironGolem.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 200, true, true, color));
//                }
                setDelay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
                return;
            }
            if (entityType != null) {
                Entity entity = world.spawnEntity(getLocation(world).add(0, 1, 0), entityType);
                entity.setCustomName(ChatColor.AQUA.toString() + count + "x " + StringUtil.capitalizeWords(entityType.toString(), "_"));
                entity.setCustomNameVisible(true);
                entity.setMetadata("gtier", new EasyMetadata<>(getTier()));
                if (entityType == EntityType.IRON_GOLEM) {
                    entity.setMetadata("gtype", new EasyMetadata<>(getGolemType().toString()));
                }
                entity.setMetadata("gcount", new EasyMetadata<>(count));
                setDelay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));

            }


        }

    }

    public double getDropMultiplier() {
        return dropMultiplier;
    }

    public void setDropMultiplier(double dropMultiplier) {
        this.dropMultiplier = dropMultiplier;
    }
}
