package net.cloudescape.skyblock.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Matthew E on 4/9/2018.
 */
public class ItemListener implements Listener {
    @EventHandler
    public void onEntityPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("no_pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (event.getEntity().hasMetadata("no_merge") || event.getTarget().hasMetadata("no_merge")) {
            event.setCancelled(true);
        }
    }
}
