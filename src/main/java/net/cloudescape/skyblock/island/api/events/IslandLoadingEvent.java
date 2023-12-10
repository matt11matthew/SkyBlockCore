package net.cloudescape.skyblock.island.api.events;

import net.cloudescape.skyblock.database.island.IslandContainer;
import net.cloudescape.skyblock.island.Island;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandLoadingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Island island;
    private IslandContainer islandContainer;

    public IslandLoadingEvent(Island island, IslandContainer islandContainer) {
        this.island = island;
        this.islandContainer = islandContainer;
    }

    public IslandContainer getIslandContainer() {
        return islandContainer;
    }

    public Island getIsland() {
        return island;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
