package net.cloudescape.skyblock.listener;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Created by Matthew E on 3/4/2018.
 */
public class MobStackerListener implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Arrays.asList(MobStackerUtils.STACK_ENTITIES).contains(event.getEntityType())) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    MobStackerUtils.stackMob(event.getEntity());
                }
            }.runTaskLater(CloudSkyblock.getPlugin(), 2L);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().hasMetadata("gcount")) {
            int mobStacker = event.getEntity().getMetadata("gcount").get(0).asInt() - 1;
            if (mobStacker >= 1) {
                LivingEntity entity = event.getEntity();
                LivingEntity livingEntity = (LivingEntity) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entity.getType());

                livingEntity.setCustomName("" + ChatColor.AQUA + mobStacker + "x " + livingEntity.getType().getName());
                livingEntity.getEquipment().setArmorContents(event.getEntity().getEquipment().getArmorContents());
                livingEntity.addPotionEffects(event.getEntity().getActivePotionEffects());
                livingEntity.getEquipment().setItemInHand(event.getEntity().getEquipment().getItemInHand());
                livingEntity.setMetadata("gcount", new EasyMetadata<>(mobStacker));
                if (entity.hasMetadata("gtype")) {
                    livingEntity.setMetadata("gtype", new EasyMetadata<>(entity.getMetadata("gtype").get(0).asString()));
                }
                if (entity.hasMetadata("gtier")) {
                    int tier = entity.getMetadata("gtier").get(0).asInt();
                    livingEntity.setMetadata("gtier", new EasyMetadata<>(tier));
                }
            }
        }
    }
}
