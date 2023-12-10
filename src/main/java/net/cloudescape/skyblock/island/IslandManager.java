package net.cloudescape.skyblock.island;

import com.cloudescape.utilities.UUIDUtil;
import com.sk89q.worldedit.Vector;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.IslandLogType;
import net.cloudescape.skyblock.database.island.IslandContainer;
import net.cloudescape.skyblock.island.api.events.IslandLoadingEvent;
import net.cloudescape.skyblock.island.api.events.IslandPreUnloadEvent;
import net.cloudescape.skyblock.island.shop.Shop;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.MinionManager;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import net.cloudescape.skyblock.utils.WorldUtil;
import net.cloudescape.skyblock.utils.callback.Callback;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Stairs;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IslandManager {

    /**
     * Total array of every island in the world.
     */
    private List<Island> loadedIslands;

    /**
     * Minion manager manages all the minions on every island.
     */
    private MinionManager minionManager;

    public static List<UUID> getLoadingIslandList() {
        return loadingIslandList;
    }

    /**
     * Constructs a new instance of {@link IslandManager}.
     */
    public IslandManager() {
        this.loadedIslands = new ArrayList<>();

        this.minionManager = new MinionManager();
        this.startTasks();
    }

    private void startTasks() {
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
            toTeleportList.forEach(player -> player.teleport(Bukkit.getWorld("world").getSpawnLocation()));
            toTeleportList.clear();
        }, "teleportPlayers", CloudRunnableType.SYNC, 1, TimeUnit.SECONDS);
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
            toDeleteMap.keySet().forEach(file -> {
                if (file.isDirectory()) {
                    Runnable runnable = toDeleteMap.get(file);
                    try {
                        FileUtils.deleteDirectory(file);
                        runnable.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            toDeleteMap.clear();
        }, "deleteWorlds", CloudRunnableType.SYNC, 3, TimeUnit.SECONDS);
//        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
//            for (UUID uuid : islandUnloadingPlayerList) {
//                Player player = Bukkit.getPlayer(uuid);
//                if (player != null && player.isOnline()) {
//                    player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "...", ChatColor.GRAY + "Saving island...");
//                }
//            }
//        }, "unloadingIslandMessage", CloudRunnableType.ASYNC, 1, TimeUnit.SECONDS);
//        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
//            for (UUID uuid : loadingIslandList) {
//                Player player = Bukkit.getPlayer(uuid);
//                if (player != null && player.isOnline()) {
////                    player.sendTitle(ChatColor.AQUA + ChatColor.BOLD.toString() + "...", ChatColor.GRAY + "Loading island...");
//                }
//            }
//        }, "loadingIslandMessage", CloudRunnableType.ASYNC, 500, TimeUnit.MILLISECONDS);
        CloudSkyblock.getCloudRunnableManager().schedule(() -> this.loadedIslands.forEach(island -> island.getShops().stream().filter(Shop::isItemNotSpawned).forEach(shop -> {
            if (!island.isSaving()) {

                shop.spawnItem(island.getWorld());
            }
        })), "shopRenderItems", CloudRunnableType.SYNC, 1, TimeUnit.SECONDS);


        CloudSkyblock.getCloudRunnableManager().schedule(() -> this.loadedIslands.forEach(island -> {
            if (!island.isSaving() && System.currentTimeMillis() > island.getSaveTime()) {
                island.setSaveTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                island.save();
            }
        }), "autoSaveIslandz", CloudRunnableType.SYNC, 1, TimeUnit.SECONDS);
        CloudSkyblock.getCloudRunnableManager().schedule(() -> this.loadedIslands
                        .stream()
                        .filter(island -> island.getGolemSpawners() != null)
                        .forEach(island -> island.getGolemSpawners().values().forEach(GolemSpawner::updateDisplayName)),
                "updateGolemNames", CloudRunnableType.SYNC, 1, TimeUnit.SECONDS);

        CloudSkyblock.getCloudRunnableManager().schedule(() -> this.loadedIslands
                        .stream()
                        .filter(island -> island.getGolemSpawners() != null)
                        .forEach(island -> island.getGolemSpawners().values().forEach(golemSpawner -> golemSpawner.spawn(island.getWorld()))),
                "spawnGolems", CloudRunnableType.SYNC, 1, TimeUnit.SECONDS);
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {

            getLoadedIslands().forEach(island -> {

                if (island.getWorld() != null && island.getWorld().getPlayers().size() > 0) {
                    // Island level
                    getIslandLevel(island, (level) -> {
                        IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(island.getIslandOwner());
                        island.setIslandLevel(level);
                        if (container != null) {
                            container.setCurrentIslandLevel(level);
                            container.update();
                        }
                    });
                }
            });

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&cAll island levels have been refreshed!"));
        }, "updateIslandLevels", CloudRunnableType.SYNC, 15, TimeUnit.MINUTES);
    }

    public MinionManager getMinionManager() {
        return minionManager;
    }

    /**
     * Add a new island to the loadedIslands cache. Could be used if an island already exists!
     *
     * @param island - island.
     * @return Successfully added.
     */
    public boolean addIsland(Island island) {
        if (loadedIslands.contains(island)) return false;
        loadedIslands.add(island);
        return true;
    }

    /**
     * Create a new island in the islands world.
     *
     * @param island - island.
     * @return Successfully created.
     */
    public boolean createNewIsland(Island island) {
        // Checks if island exists at location, if doesn't then add centralX/Z + (protection distance * 2) to see if next spot is free?
        // Spawns schematic at centre location of successfully found island.
        // Adds island to cache after being created.
        CloudSkyblock.getPlugin().getIslandWrapper().createIsland(island);
        addIsland(island);
        return true;
    }

    public static List<Player> toTeleportList = new ArrayList<>();
    public static List<UUID> loadingIslandList = new ArrayList<>();
    public static List<UUID> unLoadingIslandList = new ArrayList<>();
    public static Map<File, Runnable> toDeleteMap = new ConcurrentHashMap<>();
    public static List<UUID> islandUnloadingPlayerList = new ArrayList<>();


    public void unloadIsland(Island island, Player unloader, Runnable onDone, Runnable onFail) {
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
        UUID unloaderUuid = null;
        if (unloader != null) {
            unloaderUuid = unloader.getUniqueId();
        } else {
            unloaderUuid = island.getIslandOwner();
        }
        if (unLoadingIslandList.contains(island.getIslandUuid())) {
            return;
        }
        if (loadedIslands.contains(island)) {
            island.setSaving(true);
            unLoadingIslandList.add(island.getIslandUuid());
            if (unloaderUuid != null) {
                islandUnloadingPlayerList.add(unloaderUuid);
            }
//            World islandWorld = Bukkit.getWorld(island.getIslandUuid().toString());
//            try {
//                toTeleportList.addAll(islandWorld.getPlayers());
//            } catch (Exception e) {
//
//            }
            for (Minion minion : island.getLoadedMinions()) {
                minion.kill(false, island);
            }

            island.getWorld().getEntities().stream().filter(entity -> (!(entity instanceof Player))).forEach(Entity::remove);

            for (Player player : island.getWorld().getPlayers()) {
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                player.kickPlayer("");
            }

            Vector bot = new Vector(island.getMinX(), 0, island.getMinZ()); //MUST be a whole number eg integer
            Vector top = new Vector(island.getMaxX(), 255, island.getMaxZ()); //MUST be a whole number eg integer

            UUID finalUnloaderUuid = unloaderUuid;
            WorldUtil.uploadWorld(island.getWorld(), true, () -> {

                IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(island.getIslandUuid());

                if (container == null) {
                    IslandLogType.ISLAND_FAIL_TO_UNLOAD.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
                    onFail.run();
                    if (finalUnloaderUuid != null) {
                        islandUnloadingPlayerList.remove(finalUnloaderUuid);
                    }
                    loadedIslands.remove(island);
                    return;
                }
                container.setCentralX(island.getCentralX());
                container.setCentralZ(island.getCentralZ());
                container.setHeight(island.getHeight());
                container.setProtectionDistance(getProtectionDistanceFromIslandRadius(island));
                container.setIslandMembers(island.getIslandMembers());
                container.setLoadedServer("null");
                island.getSettings().forEach(container::setSetting);
                container.setIslandMinions(island.getLoadedMinions());
                container.setBoosters(island.getBoosters());

                island.setLoadedServer("null");

                CloudSkyblock.getPlugin().getIslandWrapper().saveIsland(island, false);
                IslandPreUnloadEvent islandPreUnloadEvent = new IslandPreUnloadEvent(island);
                Bukkit.getPluginManager().callEvent(islandPreUnloadEvent);


                final Island island1 = islandPreUnloadEvent.getIsland();


                island1.setSaving(false);
                loadedIslands.remove(island1);
                if (finalUnloaderUuid != null) {
                    islandUnloadingPlayerList.remove(finalUnloaderUuid);
                }
                IslandLogType.ISLAND_UNLOAD.send(UUIDUtil.getUsername(island1.getIslandOwner()), island1.getIslandUuid().toString());
                onDone.run();
                if (unLoadingIslandList.contains(island1.getIslandUuid())) {
                    unLoadingIslandList.remove(island1.getIslandUuid());
                }
            }, () -> {
                IslandLogType.ISLAND_FAIL_TO_UNLOAD.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
                onFail.run();
            });


        } else {
            IslandLogType.ISLAND_FAIL_TO_UNLOAD.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
            onFail.run();
        }
    }

    public void loadIslandByOwner(UUID owner, Consumer<Island> islandConsumer, Runnable onFailure) {
        IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByOwner(owner);

        if (loadingIslandList.contains(owner)) {
            return;
        }
        loadingIslandList.add(owner);

        if (container == null) {
            loadingIslandList.remove(owner);
            onFailure.run();
            return;

        }

        int centralX = container.getCentralX();
        int centralZ = container.getCentralZ();
        List<String> fences = new ArrayList<>();
        fences.add(Material.DARK_OAK_FENCE.toString());
        Island island = new Island(
                container.getUniqueID(),
                container.getOwner(),
                container.isProtected(),
                centralX,
                centralZ,
                null,
                container.getShops(), false, new ConcurrentHashMap<>(), CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getName(), new ArrayList<>(), DyeColor.BROWN.toString(), fences, Material.DARK_OAK_FENCE.toString());
        container.setLoadedServer(island.getServer());
        container.update();
        for (Map.Entry<UUID, IslandRank> member : container.getIslandMembers().entrySet()) {
            CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(member.getKey(), island.getIslandUuid());
            island.addIslandMember(member.getKey(), member.getValue(), false);
        }

        WorldUtil.downloadWorld(island.getIslandUuid().toString(), world -> {

            Island island1 = handleDownload(island, container, owner, world);
            islandConsumer.accept(island1);
            IslandLogType.ISLAND_LOAD.send(UUIDUtil.getUsername(island1.getIslandOwner()), island1.getIslandUuid().toString());
        }, () -> {
            IslandLogType.ISLAND_FAIL_TO_LOAD.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
            onFailure.run();

        });


    }

    public void spawnTempleNpcs(Island island) {
        spawnTempleMasterNpc(island);
        spawnCustomizerNpc(island);
        spawnPlayerVaultNpc(island);
    }

    private void spawnTempleMasterNpc(Island island) {
        Location villagerLocation = new Location(island.getWorld(), -0.5, 87, -34.5, 30, 4);
        Villager villager = (Villager) island.getWorld().spawnEntity(villagerLocation, EntityType.VILLAGER);
        spawnNpcPad(new Location(island.getWorld(), -0.5, 86, -34.5, 30, 4));
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Temple Master " + ChatColor.GRAY + ChatColor.BOLD.toString() + "(RIGHT-CLICK)");
        villager.setAI(false);
        villager.setSilent(true);
        villager.setMetadata("templeMaster", new FixedMetadataValue(CloudSkyblock.getPlugin(), island.getIslandUuid().toString()));
        villager.setInvulnerable(true);

        villager.setProfession(Villager.Profession.PRIEST);
        villager.setRiches(0);
    }

    private void spawnNpcPad(Location location) {
        Block block = location.getBlock();
        block.setType(Material.GOLD_BLOCK);
        block.getRelative(BlockFace.NORTH_EAST).setType(Material.SMOOTH_BRICK);
        block.getRelative(BlockFace.NORTH_WEST).setType(Material.SMOOTH_BRICK);
        block.getRelative(BlockFace.SOUTH_EAST).setType(Material.SMOOTH_BRICK);
        block.getRelative(BlockFace.SOUTH_WEST).setType(Material.SMOOTH_BRICK);
        block.getRelative(BlockFace.WEST).setType(Material.SMOOTH_STAIRS);
        block.getRelative(BlockFace.EAST).setType(Material.SMOOTH_STAIRS);
        block.getRelative(BlockFace.SOUTH).setType(Material.SMOOTH_STAIRS);
        block.getRelative(BlockFace.NORTH).setType(Material.SMOOTH_STAIRS);

        faceStairs(block.getRelative(BlockFace.NORTH), BlockFace.NORTH);
        faceStairs(block.getRelative(BlockFace.SOUTH), BlockFace.SOUTH);
        faceStairs(block.getRelative(BlockFace.EAST), BlockFace.EAST);
        faceStairs(block.getRelative(BlockFace.WEST), BlockFace.WEST);

        Location location1 = new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getZ());
        Block locationBlock = location1.getBlock();
        locationBlock.setType(Material.AIR);
        locationBlock.getRelative(BlockFace.NORTH_EAST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.NORTH_WEST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.SOUTH_EAST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.SOUTH_WEST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.WEST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.EAST).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.SOUTH).setType(Material.AIR);
        locationBlock.getRelative(BlockFace.NORTH).setType(Material.AIR);
    }

    private void faceStairs(Block block, BlockFace blockFace) {
        if (block.getType().toString().contains("STAIRS")) {

            BlockState state1 = block.getState();
            Stairs state = (Stairs) state1.getData();
            state.setFacingDirection(blockFace.getOppositeFace());
            state1.setData(state);

            state1.update(true);


        }
    }


    private void spawnCustomizerNpc(Island island) {
        Location villagerLocation = new Location(island.getWorld(), 6.550D, 87D, -34.5D, 90F, -2.5F);
        Villager villager = (Villager) island.getWorld().spawnEntity(villagerLocation, EntityType.VILLAGER);
        villager.getLocation().getBlock().setType(Material.AIR);
        spawnNpcPad(new Location(island.getWorld(), 6.550D, 86D, -34.5D, 90F, -2.5F));
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Customizing Master " + ChatColor.RED + ChatColor.BOLD.toString() + "(Coming Soon)");
        villager.setAI(false);
        villager.setSilent(true);
        villager.setMetadata("customizeMaster", new FixedMetadataValue(CloudSkyblock.getPlugin(), island.getIslandUuid().toString()));
        villager.setInvulnerable(true);

        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setRiches(0);
    }

    private void spawnPlayerVaultNpc(Island island) {
        Location villagerLocation = new Location(island.getWorld(), 7.353D, 92D, -40.496D, 89.3F, 5.4F);
        Villager villager = (Villager) island.getWorld().spawnEntity(villagerLocation, EntityType.VILLAGER);

        villager.getLocation().getBlock().setType(Material.AIR);
        villager.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST).setType(Material.ENDER_CHEST);
        villager.setCustomNameVisible(true);
        villager.setCustomName(ChatColor.AQUA+ChatColor.BOLD.toString() + "Vault Master " + ChatColor.RED + ChatColor.BOLD.toString() + "(Coming Soon)");
        villager.setAI(false);
        villager.setSilent(true);
        villager.setMetadata("vaultMaster", new FixedMetadataValue(CloudSkyblock.getPlugin(), island.getIslandUuid().toString()));
        villager.setInvulnerable(true);

        villager.setProfession(Villager.Profession.BUTCHER);
        villager.setRiches(0);
    }

    public void loadIslandByUuid(UUID islandUuid, Player member, Consumer<Island> islandConsumer, Runnable onFailure) {
        IslandContainer container = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(islandUuid);

        if (loadingIslandList.contains(member.getUniqueId())) {
            onFailure.run();
            return;
        }
        loadingIslandList.add(member.getUniqueId());

        if (container == null) {
            loadingIslandList.remove(member.getUniqueId());
            onFailure.run();
            return;

        }

        int centralX = container.getCentralX();
        int centralZ = container.getCentralZ();

        List<String> fences = new ArrayList<>();
        fences.add(Material.DARK_OAK_FENCE.toString());
        Island island = new Island(
                container.getUniqueID(),
                container.getOwner(),
                container.isProtected(),
                centralX,
                centralZ,
                null,
                container.getShops(), false, new ConcurrentHashMap<>(), CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getName(), new ArrayList<>(), DyeColor.BROWN.toString(), fences, Material.DARK_OAK_FENCE.toString());
        container.setLoadedServer(island.getServer());
        container.update();

        WorldUtil.downloadWorld(island.getIslandUuid().toString(), world -> {
            Island island1 = handleDownload(island, container, member.getUniqueId(), world);
            islandConsumer.accept(island1);
            IslandLogType.ISLAND_LOAD.send(UUIDUtil.getUsername(island1.getIslandOwner()), island1.getIslandUuid().toString());


        }, onFailure);


    }

    private Island handleDownload(Island island, IslandContainer container, UUID loader, World world) {

        island.setTempleStage(container.getBoosterTemple());
        island.setSettings(container.getSettings());
        island.setBoosters(container.getBoosters());
        island.setProtectionDistance(container.getProtectionDistance());
        island.setBalance(container.getBalance());
        island.setCurrentCarpet(container.getCurrentCarpet());
        island.setUnlockedCarpets(container.getUnlockedCarpets());

        island.setCurrentFence(container.getCurrentFence());
        island.setUnlockedFences(container.getUnlockedFences());


        for (Map.Entry<UUID, IslandRank> member : container.getIslandMembers().entrySet()) {
            CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(member.getKey(), island.getIslandUuid());
            island.addIslandMember(member.getKey(), member.getValue(), false);
        }

        island.setWorld(Bukkit.getWorld(island.getIslandUuid().toString()));
        addIsland(island);
        loadingIslandList.remove(loader);


        island.setLoadedServer(CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getName());
        try {
            island.setAvailableSuits(container.getSuitTypes());
        } catch (Exception e) {
            e.printStackTrace();
            IslandLogType.FAILED_TO_LOAD_SUITS.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
        }
        try {

            island.setLoadedMinions(container.getIslandMinions());
        } catch (Exception e) {
            e.printStackTrace();
            IslandLogType.FAILED_TO_LOAD_MINIONS.send(UUIDUtil.getUsername(island.getIslandOwner()), island.getIslandUuid().toString());
        }

        // Island level
        getIslandLevel(island, (level) -> {
            island.setIslandLevel(level);
            container.setCurrentIslandLevel(level);
            container.update();
        });

        island.setBridgeMin(new Vector(-3.5D, 0, -1.6D));
        island.setBridgeMax(new Vector(-2.5D, 250, -21.5D));


        island.setTempleMin(new Vector(13, 0, -49));
        island.setTempleMax(new Vector(-15, world.getMaxHeight(), -22));

        island.setAvailableBoostTypes(container.getBoostTypes());
        island.setAvailableMinionTags(container.getMinionTagTypes());

        Map<Location, GolemSpawner> locationGolemSpawnerMap = new ConcurrentHashMap<>();
        for (GolemSpawner golemSpawner : container.getSpawners()) {
            locationGolemSpawnerMap.put(new Location(island.getWorld(), golemSpawner.getX(), golemSpawner.getY(), golemSpawner.getZ()), golemSpawner);
        }
        island.setGolemSpawners(locationGolemSpawnerMap);
        world.getEntities().forEach(Entity::remove);
        if (island.getGolemSpawners() != null) {
            island.getGolemSpawners().values().forEach(golemSpawner -> golemSpawner.spawnHologram(world));
        }

        island.setProtectionDistance(getProtectionDistanceFromIslandRadius(island));

        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(island.getIslandOwner(), island.getIslandUuid());
        if (loader != null && !island.isIslandMember(loader) && (CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(loader).equals(island.getIslandUuid()))) {
            island.addIslandMember(loader, IslandRank.MEMBER, false);
        }
        for (UUID uuid : island.getIslandMembers().keySet()) {
            CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(uuid, island.getIslandUuid());

        }

        /**
         * Isload event
         */
        IslandLoadingEvent islandLoadingEvent = new IslandLoadingEvent(island, container);
        Bukkit.getPluginManager().callEvent(islandLoadingEvent);
        return islandLoadingEvent.getIsland();
    }

    public void getIslandByUUID(Player member, UUID islandUuid, Consumer<Island> islandConsumer) {
        List<Island> collect;
        if (loadedIslands == null) {
            loadedIslands = new ArrayList<>();
        }
        if (loadedIslands.isEmpty()) {
            collect = new ArrayList<>();
        } else {
            collect = loadedIslands.stream().filter(island -> island.getIslandUuid().toString().equalsIgnoreCase(islandUuid.toString())).collect(Collectors.toList());
        }
        if (!collect.isEmpty()) {
            islandConsumer.accept(collect.get(0));
            return;
        }
        loadIslandByUuid(islandUuid, member, island1 -> {
            spawnTempleNpcs(island1);
            islandConsumer.accept(island1);
        }, () -> {
            islandConsumer.accept(null);
        });

    }

    /**
     * Get a specific island by its owner.
     *
     * @param uuid - owners uuid.
     * @return Island.
     */
    public void getIslandOwnedBy(UUID uuid, Consumer<Island> islandConsumer) {
        List<Island> collect;
        if (loadedIslands == null) {
            loadedIslands = new ArrayList<>();
        }
        if (loadedIslands.isEmpty()) {
            collect = new ArrayList<>();
        } else {
            collect = loadedIslands.stream().filter(island -> island.getIslandOwner().toString().equalsIgnoreCase(uuid.toString())).collect(Collectors.toList());
        }
        if (!collect.isEmpty()) {
            islandConsumer.accept(collect.get(0));
            return;
        }
        loadIslandByOwner(uuid, island1 -> {
            spawnTempleNpcs(island1);
            islandConsumer.accept(island1);
        }, () -> {
            islandConsumer.accept(null);
        });

    }

    public void getIsland(UUID uuid, Consumer<Island> islandConsumer) {

        IslandContainer foundIsland = CloudSkyblock.getPlugin().getIslandWrapper().getIslandByUUID(CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getIslandUuid(uuid));

        List<Island> collect;
        if (loadedIslands == null) {
            loadedIslands = new ArrayList<>();
        }
        if (loadedIslands.isEmpty()) {
            collect = new ArrayList<>();
        } else {
            collect = loadedIslands.stream().filter(island -> island.getIslandOwner().toString().equalsIgnoreCase(uuid.toString()) || island.getIslandMembers().containsKey(uuid.toString())).collect(Collectors.toList());
        }
        if (!collect.isEmpty()) {
            islandConsumer.accept(collect.get(0));
            return;
        }

        if (foundIsland == null || foundIsland.getOwner() == null) {
            System.out.println("Island or island owner was null! Not loading island.");
            return;
        }

        loadIslandByOwner(foundIsland.getOwner(), island1 -> {
            spawnTempleNpcs(island1);
            islandConsumer.accept(island1);
        }, () -> {
            islandConsumer.accept(null);
        });
    }

    /**
     * Get a specific island by world.
     *
     * @param world - current world.
     * @return Island.
     */
    public Optional<Island> getIslandByWorld(World world) {
        return loadedIslands.stream().filter(island -> island.getWorld().getName().equalsIgnoreCase(world.getName())).findFirst();
    }

    public Optional<Island> getLoadedIslandByMember(UUID uuid) {
        return loadedIslands.stream().filter(island -> island.getIslandMembers().containsKey(uuid)).findFirst();
    }

    /**
     * Get a list of all loaded islands.
     *
     * @return List of all islands.
     */
    public List<Island> getLoadedIslands() {
        return loadedIslands;
    }

    public void updateIsland(Island island) {
        if (loadedIslands.contains(island)) {
            loadedIslands.remove(island);
        }
        loadedIslands.add(island);
    }

    public void fillDefaultChest(Island island, ItemStack... items) {

        int radius = island.getProtectionDistance();

        for (double x = island.getCentralX() - radius; x <= island.getCentralX() + radius; x++) {
            for (double y = 0; y <= island.getWorld().getMaxHeight(); y++) {
                for (double z = island.getCentralZ() - radius; z <= island.getCentralZ() + radius; z++) {
                    Location location = new Location(island.getWorld(), x, y, z);
                    if (location.getBlock().getType() == Material.AIR) {
                        continue;
                    }

                    Block block = location.getBlock();
                    Material material = block.getType();

                    if (material == Material.CHEST) {

                        if (block.getState() instanceof Chest) {

                            Chest chest = (Chest) location.getBlock().getState();

                            for (ItemStack item : items) {
                                chest.getBlockInventory().addItem(item);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void getIslandLevel(Island island, Callback<Double> level) {

        new BukkitRunnable() {
            @Override
            public void run() {

                double islandLevel = 0;
                int radius = island.getProtectionDistance();

                for (double x = island.getCentralX() - radius; x <= island.getCentralX() + radius; x++) {
                    for (double y = 0; y <= island.getWorld().getMaxHeight(); y++) {
                        for (double z = island.getCentralZ() - radius; z <= island.getCentralZ() + radius; z++) {
                            Location location = new Location(island.getWorld(), x, y, z);
                            if (location.getBlock().getType() == Material.AIR) {
                                continue;
                            }

                            Material block = location.getBlock().getType();
                            islandLevel += getIslandBlockLevelValues(block);
                        }
                    }
                }

                level.call(islandLevel);
            }
        }.runTaskAsynchronously(CloudSkyblock.getPlugin());
    }

    public static double getIslandBlockLevelValues(Material block) {

        switch (block) {
            case EMERALD_BLOCK:
                return 4;
            case DIAMOND_BLOCK:
                return 3.5;
            case GOLD_BLOCK:
                return 2;
            case IRON_BLOCK:
                return 1.5;
            case COAL_BLOCK:
                return 1;
            case LAPIS_BLOCK:
                return 0.25;
            case REDSTONE_BLOCK:
                return 0.25;
            case BEACON:
                return 20;
            case OBSIDIAN:
                return 3;
            case ENDER_CHEST:
                return 1;
            case ENCHANTMENT_TABLE:
                return 1;
            case HOPPER:
                return 0.25;
            default:
                return 0;
        }
    }

    public static int getProtectionDistanceFromIslandRadius(Island island) {

        Optional<Booster> islandRadius = island.getBoosterByType(BoosterType.ISLAND_RADIUS);

        if (islandRadius.isPresent()) {
            Booster booster = islandRadius.get();

            switch (booster.getLevel()) {
                case 1:
                    return 120;
                case 2:
                    return 130;
                case 3:
                    return 140;
                case 4:
                    return 150;
                case 5:
                    return 160;
                case 6:
                    return 170;
                case 7:
                    return 180;
                case 8:
                    return 190;
                case 9:
                    return 200;
                case 10:
                    return 210;
                default:
                    return 220;
            }
        }
        return 120;
    }

    public void saveIslandToDatabase(Island island, Runnable onDone) {
        new Thread(() -> {
            CloudSkyblock.getPlugin().getIslandWrapper().saveIsland(island, false);
            onDone.run();
        }).start();
    }
}
