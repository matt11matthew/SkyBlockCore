package net.cloudescape.skyblock.listener;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Matthew E on 3/4/2018.
 */
public class MobStackerUtils {
    public static EntityType[] STACK_ENTITIES = new EntityType[]{
            EntityType.ZOMBIE,
            EntityType.COW,
            EntityType.PIG,
            EntityType.CAVE_SPIDER,
            EntityType.SPIDER,
            EntityType.SKELETON,
            EntityType.BLAZE,
            EntityType.ENDERMAN,
            EntityType.MAGMA_CUBE,
            EntityType.WITCH,
            EntityType.SLIME,
            EntityType.SQUID,
            EntityType.PIG_ZOMBIE,
            EntityType.MUSHROOM_COW,
            EntityType.SILVERFISH,
            EntityType.IRON_GOLEM,
            EntityType.CREEPER,
            EntityType.CHICKEN,
    };

    public static void stackMob(LivingEntity livingEntity) {
        if (livingEntity.hasMetadata("gcount")) {
            return;
        }
        List<Entity> entityList = livingEntity.getNearbyEntities(3, 3, 3).stream().filter(entity -> entity.getType() == livingEntity.getType()).filter(entity -> !entity.isDead()).collect(Collectors.toList());
        int amount = 1;
        for (Entity entity : entityList) {
            if (entity.getUniqueId().equals(livingEntity.getUniqueId())) {
                continue;
            }
            if (entity.hasMetadata("gcount")) {
                amount += entity.getMetadata("gcount").get(0).asInt();
                entity.remove();
            } else {
                amount++;
                entity.remove();
            }
        }
        if (amount > 1) {
            livingEntity.setCustomNameVisible(true);
            livingEntity.setCustomName("" + ChatColor.AQUA + amount + "x " + livingEntity.getType().getName());
        }

        livingEntity.setMetadata("gcount", new FixedMetadataValue(CloudSkyblock.getPlugin(), amount));
    }


}
