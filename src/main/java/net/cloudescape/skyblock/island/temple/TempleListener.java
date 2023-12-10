package net.cloudescape.skyblock.island.temple;

import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.temple.customizing.CustomizingGui;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Optional;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class TempleListener implements Listener {
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager && event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().equals(ChatColor.AQUA + ChatColor.BOLD.toString() + "Temple Master " + ChatColor.GRAY + ChatColor.BOLD.toString() + "(RIGHT-CLICK)")) {
            event.setCancelled(true);
            Optional<Island> islandByWorld = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getPlayer().getWorld());
            if (islandByWorld.isPresent()) {
                Island island = islandByWorld.get();
                if (island.isIslandMember(event.getPlayer().getUniqueId())) {
                    new TempleGui(event.getPlayer(), island);
                } else {
                    CustomChatMessage.sendMessage(event.getPlayer(), "Temple", "You cannot interact with the Temple master on this island.");
                    event.setCancelled(true);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }
        } else if (event.getRightClicked() instanceof Villager && event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().startsWith(ChatColor.AQUA + ChatColor.BOLD.toString() + "Customizing Master")) {
            event.setCancelled(true);
            Optional<Island> islandByWorld = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getPlayer().getWorld());
            if (islandByWorld.isPresent()) {
                Island island = islandByWorld.get();
                if (island.isIslandMember(event.getPlayer().getUniqueId())) {
                    if (event.getPlayer().isOp()) {

                        new CustomizingGui(event.getPlayer(), island);
                    } else {
                        CustomChatMessage.sendMessage(event.getPlayer(), "Customizing Master", "Coming soon");
                        event.setCancelled(true);
                    }
                } else {
                    CustomChatMessage.sendMessage(event.getPlayer(), "Temple", "You cannot interact with the customizing master on this island.");
                    event.setCancelled(true);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }
        }else if (event.getRightClicked() instanceof Villager && event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().startsWith(ChatColor.AQUA+ChatColor.BOLD.toString() + "Vault Master ")) {
            event.setCancelled(true);
            Optional<Island> islandByWorld = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getPlayer().getWorld());
            if (islandByWorld.isPresent()) {
                Island island = islandByWorld.get();
                if (island.isIslandMember(event.getPlayer().getUniqueId())) {
                    if (event.getPlayer().isOp()){

                        new PlayerVaultGui(event.getPlayer(), island);
                    } else {
                        CustomChatMessage.sendMessage(event.getPlayer(), "Vault Master", "Coming soon");
                        event.setCancelled(true);
                    }
                } else {
                    CustomChatMessage.sendMessage(event.getPlayer(), "Temple", "You cannot interact with the player vault master on this island.");
                    event.setCancelled(true);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ChatColor.AQUA + ChatColor.BOLD.toString() + "Temple Master " + ChatColor.GRAY + ChatColor.BOLD.toString() + "(RIGHT-CLICK)")) {
            event.setCancelled(true);
        } else if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null && event.getEntity().getCustomName().startsWith(ChatColor.AQUA + ChatColor.BOLD.toString() + "Customizing Master ")) {
            event.setCancelled(true);
        }else if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null && event.getEntity().getCustomName().startsWith(ChatColor.AQUA+ChatColor.BOLD.toString() + "Vault Master ")) {
            event.setCancelled(true);
        }
    }

    /**
     * @param event player chat event
     */
    @EventHandler
    public void onPlayerChat(final PlayerChatEvent event) {
        Player player = event.getPlayer();
    }
}
