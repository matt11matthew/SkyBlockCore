package net.cloudescape.skyblock.listener;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.modules.modules.backend.events.SkyBlockChatEvent;
import com.cloudescape.server.ServerType;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.game.SkyFallStatistic;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.backend.commons.rank.GlobalRank;
import net.cloudescape.backend.commons.rank.GlobalRankManager;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.database.island.IslandContainer;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.utils.IslandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectionListener implements Listener {

    /**
     * Handles players connecting to the server.
     */
    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        new BukkitRunnable() {

            @Override
            public void run() {
                CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
                GlobalRankManager rankManager = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager();
                if (cloudEscapePlayer != null && cloudEscapePlayer.getGlobalRank() != null) {
                    int permissionValue = rankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank()).getValue();

                    if (permissionValue >= 70 && permissionValue <= 100) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e(Join) &b" + player.getName() + " &7joined the game!"));
                    }
                }
            }
        }.runTaskLater(CloudSkyblock.getPlugin(), 50L);
//        SkyBlockPlayer skyBlockPlayer = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getSkyblockPlayer(player.getUniqueId());

        Island loadedIsland = IslandUtils.getLoadedIsland(player);
        if (loadedIsland != null) {
            player.teleport(loadedIsland.getLocation());
            CustomChatMessage.sendMessage(player, "Skyblock", "You were returned to your island!");
            return;
        }

        player.performCommand("is");
//        if (skyBlockPlayer == null || skyBlockPlayer.getIslandUuid() == null) {
//            return;
//        } else {
//
//        }

//        IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(skyBlockPlayer.getIslandUuid());
//
//        if (container != null) {
//            try {
//                IslandUtils.getIslandPlayerIsIn(player, found -> {
//                    if (found!=null){
//                        player.teleport(found.getLocation());
//                        CustomChatMessage.sendMessage(player, "Skyblock", "You were returned to your island!");
//                    } else {
//                        CustomChatMessage.sendMessage(player, "Skyblock", "Failed to load your island..");
//                    }
//                } );
//
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        } else {
//            player.performCommand("is");
//            return;
//        }

        // Skyblock resourcepack.
//        ResourcepackUtil.sendResourcepack(player, "https://www.dropbox.com/s/5szrjc7t68owap0/Skyheroes%20Skyblock%20Resourcepack.zip?dl=1");
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    /**
     * Handles the resourcepack loading.
     */
    @EventHandler
    public void onResourcepackChange(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED || event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            return;
        }

        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&cHey! Sorry. You have to enable our resourcepack to play.\nRe-enable the resourcepack by your server settings."));
    }

    @EventHandler
    public void onSkyblockChat(SkyBlockChatEvent event) {
        Player player = event.getPlayer();
        IslandContainer island = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByMember(player.getUniqueId());

        if (island == null) {
            return;
        }
        BackendModule module = CloudCore.getModuleManager().getModule(BackendModule.class);
        CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
        if (cloudEscapePlayer != null && cloudEscapePlayer.getGlobalRank() != null && module.getGlobalRankManager().getGlobalRankMap().containsKey(cloudEscapePlayer.getGlobalRank())) {
            if (cloudEscapePlayer.getSkyFallStatistics().isEmpty()) {
                for (SkyFallStatistic skyFallStatistic : SkyFallStatistic.values()) {
                    cloudEscapePlayer.setSkyFallStatistic(skyFallStatistic, 0);
                }
            }

            String globalRankName = cloudEscapePlayer.getGlobalRank();
            GlobalRank globalRank = module.getGlobalRankManager().getGlobalRank(globalRankName);

            if (CloudCore.getInstance().getCloudServer().getServerType() == ServerType.SKYBLOCK && globalRank != null) {
                String islandPrefix = ChatColor.translateAlternateColorCodes('&', "&8[" + getColourOfLevel(island.getIslandLevel()) + "&8] ");
                if (cloudEscapePlayer.getCurrentTag() != null && CloudCore.getChatTagManager().getChatTagMap().containsKey(cloudEscapePlayer.getCurrentTag())) {
                    event.setFormat(islandPrefix+ChatColor.translateAlternateColorCodes('&', globalRank.getPrefix()) + CloudCore.getChatTagManager().getSpigotChatTag(cloudEscapePlayer.getCurrentTag()).getFormat() + " " + ChatColor.translateAlternateColorCodes('&', "&" + globalRank.getPrefix().substring(globalRank.getPrefix().length() - 1, globalRank.getPrefix().length())) + player.getName() + ChatColor.GRAY + " \u00bb " + ChatColor.GREEN + event.getMessage());
                } else {
                    event.setFormat(islandPrefix+ChatColor.translateAlternateColorCodes('&', globalRank.getPrefix()) + player.getName() + ChatColor.GRAY + " \u00bb " + ChatColor.GREEN + event.getMessage());
                }
            }
        }
    }

    private String getColourOfLevel(double level) {
        if (level >= 0 && level <= 999)
            return "&7" + level;
        else if (level > 999 && level <= 9999)
            return "&a" + level;
        else if (level > 9999 && level <= 99999)
            return "&6" + level;
        else
            return "&5" + level;

    }
}
