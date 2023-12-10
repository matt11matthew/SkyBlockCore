package net.cloudescape.skyblock.listener;

import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.utils.IslandUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 4/18/2018.
 */
public class BugFixListener implements Listener {

    private List<UUID> temporaryImmunity;

    public BugFixListener() {
        temporaryImmunity = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        temporaryImmunity.add(event.getPlayer().getUniqueId());

        new BukkitRunnable() {

            @Override
            public void run() {
                temporaryImmunity.remove(event.getPlayer().getUniqueId());
            }
        }.runTaskLater(CloudSkyblock.getPlugin(), 20L * 10);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getWorld().getName().contains("world") || temporaryImmunity.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        new BukkitRunnable() {

            @Override
            public void run() {
                if ((player != null && player.isOnline() && player.isDead())) {
                    player.spigot().respawn();
                }
            }
        }.runTaskLater(CloudSkyblock.getPlugin(), 10L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                if ((player != null && player.isOnline())) {
                    IslandUtils.getIslandPlayerIsIn(player, island -> {
                        player.teleport(island.getLocation());

                    });
                }
            }
        }.runTaskLater(CloudSkyblock.getPlugin(), 20L);
    }

    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        event.getBlock().setData((byte) (event.getBlock().getData() + 1));
    }
}
