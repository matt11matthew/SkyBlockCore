package net.cloudescape.skyblock.utils;

import org.bukkit.entity.LivingEntity;

public class EntityUtil {

    /**
     * Remove movement and AI from an entity.
     * @param entity - entity
     * @param hasAI - if it has AI.
     */
    public static void removeAI(LivingEntity entity, boolean hasAI) {
        entity.setAI(hasAI);
    }
}
