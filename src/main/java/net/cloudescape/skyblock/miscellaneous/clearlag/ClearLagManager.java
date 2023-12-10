package net.cloudescape.skyblock.miscellaneous.clearlag;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.countdown.Countdown;
import mkremins.fanciful.FancyMessage;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ClearLagManager {

    private Countdown clearlagCountdown;

    public ClearLagManager() {
        resetClearlagCountdown();
    }

    public void resetClearlagCountdown() {

        if (clearlagCountdown != null) {
            clearlagCountdown.endCountdown();
            clearlagCountdown = null;
        }

        System.out.println("Resetting clearlag countdown.");

        clearlagCountdown = new Countdown((60 * 30), new int[] { 60, (60 * 10), (60 * 15), (60 * 30) }, () -> {

            Bukkit.getWorlds().stream().filter(world -> !world.getName().equalsIgnoreCase("world") && !world.getName().equalsIgnoreCase("world_nether") && !world.getName().equalsIgnoreCase("world_the_end")).forEach(world -> {

                getClearableEntities().forEach(entity -> world.getEntitiesByClass(entity.getEntityClass()).forEach(Entity::remove));

                world.getPlayers().forEach(worldPlayer -> {

                    CustomChatMessage.sendMessage(worldPlayer, "Clear Lag", "Removed entities from your island..");

                    StringBuilder entities = new StringBuilder();
                    getClearableEntities().forEach(entity -> entities.append(StringUtil.capitalizeFirstLetter(entity.name().replace("_", " "))).append(", "));
                    String finalEntities = entities.toString().substring(0, entities.length() - 2);
                    new FancyMessage("Hover for a list of entities removed on your island.").tooltip(finalEntities).color(ChatColor.WHITE).style(ChatColor.BOLD).send(worldPlayer);
                });
            });

            resetClearlagCountdown();
        }, (value) -> Bukkit.getWorlds().stream().filter(world -> !world.getName().equalsIgnoreCase("world") && !world.getName().equalsIgnoreCase("world_nether") && !world.getName().equalsIgnoreCase("world_the_end")).forEach(world -> {
            world.getPlayers().forEach(worldPlayer -> CustomChatMessage.sendMessage(worldPlayer, "Clear Lag", "&7Clearing your island of mobs in &c" + value + " &7seconds."));
        }));

        clearlagCountdown.beginCountdown();
    }

    /**
     * List of entities that get cleared by clear lag..
     * @return List<EntityType>
     */
    private List<EntityType> getClearableEntities() {
        List<EntityType> entities = new ArrayList<>();
        entities.add(EntityType.IRON_GOLEM);
        entities.add(EntityType.CHICKEN);
        entities.add(EntityType.COW);
        entities.add(EntityType.PIG);
        entities.add(EntityType.DROPPED_ITEM);
        return entities;
    }
}
