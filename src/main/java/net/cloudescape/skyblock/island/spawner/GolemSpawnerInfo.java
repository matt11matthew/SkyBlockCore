package net.cloudescape.skyblock.island.spawner;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class GolemSpawnerInfo {
    private int tier; // The tier of the spawner
    private EntityType entityType; // The entity type
    private GolemType golemType; // The golem type will be null if its not an iron golem

    /**
     * @param tier       The spawner tier
     * @param entityType The spawner entity type
     * @param golemType  The golem spawner type @see()
     * @see GolemType
     * @see EntityType
     */
    public GolemSpawnerInfo(int tier, EntityType entityType, GolemType golemType) {
        this.tier = tier;
        this.entityType = entityType;
        this.golemType = golemType;
    }

    /**
     * @param tier       The spawner tier
     * @param entityType The spawner entity type
     * @see EntityType
     */
    public GolemSpawnerInfo(int tier, EntityType entityType) {
        this.tier = tier;
        this.entityType = entityType;
        this.golemType = null;
    }

    /**
     * @param entityType The spawner entity type
     * @see EntityType
     */
    public GolemSpawnerInfo(EntityType entityType) {
        this.entityType = entityType;
        this.tier = 1;
        this.golemType = null;
    }

    /**
     * @param golemType The golem spawner type @see()
     * @see GolemType
     */
    public GolemSpawnerInfo(GolemType golemType) {
        this.golemType = golemType;
        this.entityType = EntityType.IRON_GOLEM;
        this.tier = 1;
    }


    /**
     * @return Returns the spawner tier
     */
    public int getTier() {
        return tier;
    }

    /**
     * @return Returns spawner entity type
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * @return Returns golem type
     */
    public GolemType getGolemType() {
        return golemType;
    }

    public boolean isIronGolem() {
        return (entityType != null) && (entityType == EntityType.IRON_GOLEM) && (golemType != null);
    }

    public GolemSpawner toGolemSpawner(Block block) {
        return new GolemSpawner(block.getX(),block.getY(),block.getZ(),golemType,tier, UUID.randomUUID(),entityType, 1, 1.0D);
    }
}
