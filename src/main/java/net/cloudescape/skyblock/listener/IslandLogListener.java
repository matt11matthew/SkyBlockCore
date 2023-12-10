package net.cloudescape.skyblock.listener;

import net.cloudescape.skyblock.IslandLogType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class IslandLogListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        IslandLogType.PLAYER_EXECUTED_COMMAND.send(event.getPlayer().getName(),event.getMessage());
    }
}
