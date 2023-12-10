package net.cloudescape.skyblock;

import com.cloudescape.CloudCore;
import com.cloudescape.database.Mongo;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.modules.modules.moderation.ModerationModule;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.client.events.CloudServerRebootEvent;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.skyblock.commands.CommandManager;
import net.cloudescape.skyblock.database.island.IslandWrapper;
import net.cloudescape.skyblock.database.schematics.SchematicWrapper;
import net.cloudescape.skyblock.database.skyblockplayer.SkyblockPlayerWrapper;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandManager;
import net.cloudescape.skyblock.island.listener.OreGenerationListener;
import net.cloudescape.skyblock.island.temple.TempleListener;
import net.cloudescape.skyblock.island.temple.TempleManager;
import net.cloudescape.skyblock.listener.*;
import net.cloudescape.skyblock.miscellaneous.chestordering.ChestSorterListener;
import net.cloudescape.skyblock.miscellaneous.clearlag.ClearLagManager;
import net.cloudescape.skyblock.miscellaneous.minions.MinionManager;
import net.cloudescape.skyblock.miscellaneous.quest.QuestManager;
import net.cloudescape.skyblock.miscellaneous.worldedit.PlayerWorldeditManager;
import net.cloudescape.skyblock.miscellaneous.worldedit.listener.PWEListener;
import net.cloudescape.skyblock.schematics.newsystem.SchematicManager;
import net.cloudescape.skyblock.schematics.newsystem.Test;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableManager;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import net.cloudescape.skyblock.utils.BungeeChannelApi;
import net.cloudescape.skyblock.utils.Logger;
import net.cloudescape.skyblock.utils.WorldUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CloudSkyblock extends JavaPlugin implements Listener {

    /**
     * Used to instance the main class.
     */
    private static CloudSkyblock plugin;
    private static SchematicManager schematicManager;
    private static TempleManager templeManager;

    private static CloudRunnableManager cloudRunnableManager;

    private PlayerWorldeditManager playerWorldeditManager;

    /**
     * {@link CommandManager} instance.
     */
    private CommandManager commandManager;

    /**
     * {@link IslandManager} instance.
     */
    private IslandManager islandManager;

    private static BungeeChannelApi bungeeChannelApi;
    /**
     * {@link QuestManager} instance.
     */
    private QuestManager questManager;

    /**
     * {@link Mongo} instance.
     */
    private Mongo mongo;
    private IslandWrapper islandWrapper;
    private SchematicWrapper schematicWrapper;
    private SkyblockPlayerWrapper skyblockPlayerWrapper;
    private MinionManager minionManager;

    @Override
    public void onEnable() {
        plugin = this;

        Logger.log("==========================================");
        Logger.log("Starting SkyBlock 8s...");
        Logger.log("==========================================");

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {


            getConfig().options().copyDefaults(true);
            saveConfig();
            Logger.log("Configuration has been loaded!");

            mongo = CloudCore.getInstance().getMongo();

            cloudRunnableManager = new CloudRunnableManager();
            cloudRunnableManager.startTimers();
            schematicManager = new SchematicManager();
            File file = new File(getDataFolder() + "/schematics/");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                schematicManager.loadSchematics(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mongo.registerWrapper(IslandWrapper.COLLECTION_NAME, islandWrapper = new IslandWrapper());
            mongo.registerWrapper(SchematicWrapper.COLLECTION_NAME, schematicWrapper = new SchematicWrapper());
            mongo.registerWrapper(SkyblockPlayerWrapper.COLLECTION_NAME, skyblockPlayerWrapper = new SkyblockPlayerWrapper());
            Logger.log("MongoDB instance setup on Skyblock!");

            islandManager = new IslandManager();

            Logger.log("registered all accessible skyblock.");

            questManager = new QuestManager();
            minionManager = new MinionManager();
            Logger.log("Instances initialised.");
            templeManager = new TempleManager();
            Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
            Bukkit.getPluginManager().registerEvents(this, this);
            Bukkit.getPluginManager().registerEvents(new IslandProtectionListener(), this);
            Bukkit.getPluginManager().registerEvents(new IslandLogListener(), this);
            Bukkit.getPluginManager().registerEvents(new IslandLoadingListener(), this);
            Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
            Bukkit.getPluginManager().registerEvents(new PWEListener(), this);
            Bukkit.getPluginManager().registerEvents(new IslandChunkListener(), this);
            Bukkit.getPluginManager().registerEvents(new TempleListener(), this);
            Bukkit.getPluginManager().registerEvents(new BugFixListener(), this);
            Bukkit.getPluginManager().registerEvents(new SpawnerListener(), this);
            Bukkit.getPluginManager().registerEvents(new MobStackerListener(), this);
            Bukkit.getPluginManager().registerEvents(minionManager, this);
            Bukkit.getPluginManager().registerEvents(new OreGenerationListener(), this);
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
            Bukkit.getPluginManager().registerEvents(new ChestSorterListener(), this);

            Logger.log("Listeners registered.");
            bungeeChannelApi = BungeeChannelApi.of(this);
            Bukkit.getWorlds().forEach(world -> {
                world.getEntities().forEach(entity -> {
                    if (entity.hasMetadata("questPartBase") || entity.hasMetadata("crateBase")) {
                        entity.remove();
                    }
                });

                if (world.getName().equals("world") || world.getName().equals("world_nether") || world.getName().equals("world_the_end")) {
                    return;
                }
                world.getPlayers().forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));

                WorldUtil.unloadWorld(world, false);
                File file1 = new File(Bukkit.getWorldContainer(), world.getName());
                if (file1.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(file1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Logger.log("Cloud Skyblock has been enabled.");
            Test.test();
            commandManager = new CommandManager();
            commandManager.loadCommands();
            playerWorldeditManager = new PlayerWorldeditManager();
//            updateTab();

            getCloudRunnableManager().schedule(() -> Bukkit.getServer().getOnlinePlayers().forEach(this::setTabListNameForPlayer), "updateDisplayName", CloudRunnableType.ASYNC, 1, TimeUnit.SECONDS);
//            getCloudRunnableManager().schedule(this::updateTab, "updateTabList", CloudRunnableType.ASYNC, 1, TimeUnit.SECONDS);

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().loadPlayer(player);
            }

            new ClearLagManager();
        } catch (Exception e) {
            Logger.log("Could not enable due to error");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static BungeeChannelApi getBungeeChannelApi() {
        return bungeeChannelApi;
    }

    //    static ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
//            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//
//    static {
//        root.setLevel(Level.INFO);
//    }
    public static TempleManager getTempleManager() {
        return templeManager;
    }


    private void setTabListNameForPlayer(Player player) {

        CloudEscapePlayer escapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
        String prefix = "";
        try {
            if (CloudCore.getInstance().isGlobalRanks()) {
                prefix = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager().getGlobalRank(escapePlayer.getGlobalRank()).getPrefix();
            } else {
                prefix = CloudCore.getModuleManager().getModule(ModerationModule.class).getPlayerManager().getRankPlayer(player).getRankObject().getPrefix();
            }
        } catch (Exception e) {
            prefix = ChatColor.GRAY.toString();
        }
        if (prefix.contains("[")) {

            String nameColor = ChatColor.GRAY.toString();
            if (prefix.contains(" ")) {
                nameColor = prefix.split(" ")[1].trim();
            }
            prefix = prefix.replace("&8[", "").replace("&8]", "");
            String color = prefix.substring(1, 2);
            String s = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix.replaceAll("&" + color, "")));
            prefix = "&" + color + ChatColor.BOLD.toString() + s + nameColor;
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            if (prefix.length() > 16) {
                prefix = prefix.substring(0, 16);
            }
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', prefix) + player.getName());
            player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', prefix) + player.getName());
        } else {
            player.setDisplayName(ChatColor.GRAY + player.getName());
            player.setPlayerListName(ChatColor.GRAY + player.getName());
        }
    }

    public static CloudRunnableManager getCloudRunnableManager() {
        return cloudRunnableManager;
    }

    @Override
    public void onDisable() {
        System.out.println("[CloudSkyBlock] onDisable()");

    }


    @EventHandler
    public void onCloudServerReboot(CloudServerRebootEvent event) {
        if (event.getAddonName().equalsIgnoreCase(getDescription().getName())) {
            this.reboot();
            event.setSleep(3000L);
        } else {
            event.setSleep(300L);
        }
        Logger.log("=====================================");
        Logger.log("[" + event.getAddonName() + "] has been disabled.");
        Logger.log("=====================================");
    }

    private void reboot() {


//        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));

        List<Island> toUnload = new ArrayList<>();


        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.performCommand("is unload");
        }
        islandManager.getLoadedIslands().forEach(island -> {
            toUnload.add(island);
        });
//
        for (Island island : toUnload) {
            islandManager.unloadIsland(island, null, () -> {
                Logger.log("Successfully unloaded island " + island.getIslandUuid().toString());
                Logger.log("Successfully unloaded island " + island.getIslandUuid().toString());
                Logger.log("Successfully unloaded island " + island.getIslandUuid().toString());
                Logger.log("Successfully unloaded island " + island.getIslandUuid().toString());
                Logger.log("Successfully unloaded island " + island.getIslandUuid().toString());
                WorldUtil.deleteWorld(island.getWorld().getWorldFolder());
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, () -> {
                Logger.log("Failed to unload island " + island.getIslandUuid().toString());
                Logger.log("Failed to unload island " + island.getIslandUuid().toString());
                Logger.log("Failed to unload island " + island.getIslandUuid().toString());
                WorldUtil.deleteWorld(island.getWorld().getWorldFolder());
                try {

                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
        File tempFile = new File(getDataFolder() + "/temp/");
        tempFile.delete();

        cloudRunnableManager.cancelAllTasks();

        Logger.log("Cloud Skyblock has been disabled.");
    }

    /**
     * Used to access the instance of the main class.
     *
     * @return CloudSkyblock
     */
    public static CloudSkyblock getPlugin() {
        return plugin;
    }

    /**
     * Used to access the instance of {@link IslandManager}.
     *
     * @return IslandManager.
     */
    public IslandManager getIslandManager() {
        return islandManager;
    }


    /**
     * Used to access the instance of {@link Mongo}.
     *
     * @return Mongo.
     */
    public Mongo getMongo() {
        return mongo;
    }

    /**
     * Get the MongoDB IslandWrapper.
     *
     * @return IslandWrapper.
     */
    public IslandWrapper getIslandWrapper() {
        return islandWrapper;
    }

    /**
     * Get the {@link QuestManager} instance.
     *
     * @return QuestManager.
     */
    public QuestManager getQuestManager() {
        return questManager;
    }

    /**
     * Get the MongoDB {@link SchematicWrapper}.
     *
     * @return SchematicWrapper.
     */
    public SchematicWrapper getSchematicWrapper() {
        return schematicWrapper;
    }

    public static SchematicManager getSchematicManager() {
        return schematicManager;
    }

    public SkyblockPlayerWrapper getSkyblockPlayerWrapper() {
        return skyblockPlayerWrapper;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PlayerWorldeditManager getPlayerWorldeditManager() {
        return playerWorldeditManager;
    }

    public MinionManager getMinionManager() {
        return minionManager;
    }
}
