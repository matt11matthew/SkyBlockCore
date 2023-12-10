package net.cloudescape.skyblock.island.api.events;

import net.cloudescape.skyblock.island.Island;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandPreUnloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Island island;

    public IslandPreUnloadEvent(Island island) {
        this.island = island;
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
