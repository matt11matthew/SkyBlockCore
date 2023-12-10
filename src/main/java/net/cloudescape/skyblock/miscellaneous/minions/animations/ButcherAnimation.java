package net.cloudescape.skyblock.miscellaneous.minions.animations;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ButcherAnimation extends Animation {
    private Butcher butcher;

    public ButcherAnimation(Butcher butcher){
        this.butcher = butcher;

        this.runTaskTimer(CloudSkyblock.getPlugin(), 0L, 20*15);
    }

    @Override
    public void run() {
        for(Entity entity : butcher.getMinion().getNearbyEntities(butcher.getRadius(), butcher.getRadius(), butcher.getRadius())){
            butcher.getKillableMobs().forEach(entityType -> {
                if(entityType.equals(entity.getType())) {
                    Location start = butcher.getMinion().getLocation();
                    Vector line = entity.getLocation().subtract(start).toVector();

                    for (int position = 0; position < 25; position++) {
                        Vector point = line.clone().multiply((position*0.02));
                        Location display = start.clone().add(point);
                        start.getWorld().spawnParticle(Particle.CLOUD, display.getX(), display.getY(), display.getZ(), 1);
                    }

                    /**
                     * Instead of removing the entity, check if the entity is stacked 1st,
                     * if it is remove one from the stack and get the drops
                     */
                    if (entity.hasMetadata("gcount") && entity instanceof LivingEntity) {
                        int mobStacker = entity.getMetadata("gcount").get(0).asInt() - 1;
                        if (mobStacker >= 1) {
                            LivingEntity livingEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());

                            livingEntity.setCustomName("" + ChatColor.AQUA + mobStacker + "x " + livingEntity.getType().getName());

                            livingEntity.getEquipment().setArmorContents(((LivingEntity) entity).getEquipment().getArmorContents());
                            livingEntity.addPotionEffects(((LivingEntity) entity).getActivePotionEffects());
                            livingEntity.getEquipment().setItemInHand(((LivingEntity) entity).getEquipment().getItemInHand());
                            livingEntity.setMetadata("gcount", new FixedMetadataValue(CloudSkyblock.getPlugin(),mobStacker));
                        }
                    }
                    else {
                        entity.remove();
                    }
                    butcher.addKill();
                    butcher.setXpCollected(butcher.getXpCollected() + (butcher.getBoost().equals(MinionBoostType.DOUBLE_XP) ? 0.04f : 0.02f));
                    return;
                }
            });
        }
    }

    @Override
    public void displayParticles() {}
}
