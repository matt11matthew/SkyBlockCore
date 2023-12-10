package net.cloudescape.skyblock.listener;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.backend.commons.rank.GlobalRankManager;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    public PlayerListener() {

        // For Jake (testing features for possible island cosmetics).
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {

            for (Player all : Bukkit.getOnlinePlayers()) {
                CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(all.getUniqueId());
                GlobalRankManager globalRankManager = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager();
                if (cloudEscapePlayer==null){
                    continue;
                }

                if (all.getName().equalsIgnoreCase("ThatAbstractWolf") || globalRankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank()).getValue() >= 950) {
                    if (all.isGliding()) {
                        all.setVelocity(all.getLocation().getDirection().normalize());
                    }
                }
            }
        }, "abstractelytra", CloudRunnableType.SYNC, 1L, TimeUnit.MILLISECONDS);
    }

    // For Jake (testing features for possible island cosmetics).
    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
        GlobalRankManager globalRankManager = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager();

        if (player.getName().equalsIgnoreCase("ThatAbstractWolf") || globalRankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank()).getValue() >= 950) {
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
        }
    }

    // For Jake (testing features for possible island cosmetics).
    @EventHandler
    public void onElytraFly(EntityToggleGlideEvent event) {
        Entity possiblePlayer = event.getEntity();

        if (possiblePlayer instanceof Player) {
            Player player = (Player) possiblePlayer;

            CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
            GlobalRankManager globalRankManager = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager();

            if (player.getName().equalsIgnoreCase("ThatAbstractWolf") || globalRankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank()).getValue() >= 950) {
                boolean toggle = player.isGliding();
                player.setGliding(toggle);
                CustomChatMessage.sendMessage(player, "Elytra", "Elytra toggled: " + (!toggle ? "&aenabled" : "&cdisabled") + "&7!");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();

        switch (cause) {
            case VOID:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7fell off their island."));
                break;
            case LAVA:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7fell into &clava&7."));
                break;
            case DROWNING:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &bdrowned &7to death!"));
                break;
            case CUSTOM:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7died from unknown causes."));
                break;
            case FIRE:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &cburned &7to death."));
                break;
            case FALL:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7fell to their death."));
                break;
            case STARVATION:
                event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + " &7died of food deprivation."));
                break;
        }
    }
}
