package net.cloudescape.skyblock.island;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.UUIDUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.shop.Shop;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.island.temple.BoosterTemple;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.schematics.Schematic;
import net.cloudescape.skyblock.schematics.newsystem.SchematicManager;
import net.cloudescape.skyblock.schematics.newsystem.block.SchematicBlock;
import net.cloudescape.skyblock.utils.IslandUtils;
import net.cloudescape.skyblock.utils.Logger;
import net.cloudescape.skyblock.utils.WorldUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Island {

    /**
     * Unique ID of the island.
     */
    private UUID islandUuid;
    private List<Shop> shops;
    private boolean saving = false;
    private long saveTime;

    private com.sk89q.worldedit.Vector bridgeMin;
    private com.sk89q.worldedit.Vector templeMin;
    private com.sk89q.worldedit.Vector templeMax;
    private com.sk89q.worldedit.Vector bridgeMax;

    private int spawnerCount = 0;

    private Map<Location, GolemSpawner> golemSpawners;
    private String server;
    private List<String> unlockedCarpets;
    private String currentCarpet;

    private List<String> unlockedFences;
    private String currentFence;


    public void setBridgeMin(com.sk89q.worldedit.Vector bridgeMin) {
        this.bridgeMin = bridgeMin;
    }

    public com.sk89q.worldedit.Vector getTempleMin() {
        return templeMin;
    }

    public void setTempleMin(com.sk89q.worldedit.Vector templeMin) {
        this.templeMin = templeMin;
    }

    public com.sk89q.worldedit.Vector getTempleMax() {
        return templeMax;
    }

    public void setTempleMax(com.sk89q.worldedit.Vector templeMax) {
        this.templeMax = templeMax;
    }

    public void setBridgeMax(com.sk89q.worldedit.Vector bridgeMax) {
        this.bridgeMax = bridgeMax;
        this.bridgeMax = new com.sk89q.worldedit.Vector(-6, 150, -25);
        this.bridgeMin = new com.sk89q.worldedit.Vector(5, 2, 0 - 1.6);


    }

    /**
     * The UUID of the owner of an island.
     */
    private UUID islandOwner;

    /**
     * List of members (including the island owner)
     */
    private Map<UUID, IslandRank> islandMembers;

    /**
     * The {@link Schematic} they chose to use when creating the island.
     */
    private com.boydti.fawe.object.schematic.Schematic schematic;

    /**
     * Current stage of the islands booster temple.
     */
    private BoosterTemple templeStage;

    public boolean isSaving() {
        return saving;
    }

    public void setSaving(boolean saving) {
        this.saving = saving;
    }

    /**
     * Is the island protected from other players that aren't in the team.
     */
    private boolean isProtected;
    /**
     * Distance an Island will be protected for.
     */
    private int protectionDistance;

    /**
     * World the island has spawned in.
     */
    private World world;

    /**
     * The central X point, central Z point and the height (same for all islands).
     */
    private int centralX;
    private int height;
    private int centralZ;

    /**
     * The minimum and maximum boundary values for the island.
     */
    private int minX, maxX;
    private int minZ, maxZ;

    /**
     * Settings for the island.
     */
    private Map<IslandSettings, Boolean> settings;

    /**
     * Current booster for the island and owned boosters for an island.
     */
    private List<Booster> boosters;

    /**
     * List of all loaded minions
     */
    private List<Minion> loadedMinions;

    /**
     * List of all players invited.
     */
    private List<UUID> invites;

    /**
     * Level of the island based on ores etc.
     */
    private double islandLevel;

    /**
     * Island balance.
     */
    private int balance;

    private List<SuitType> availableSuits;
    private List<MinionBoostType> availableBoostTypes;
    private List<MinionTagType> availableMinionTags;

    public void setUnlockedCarpets(List<String> unlockedCarpets) {
        this.unlockedCarpets = unlockedCarpets;
    }

    public String getCurrentCarpet() {
        return currentCarpet;
    }

    public void setCurrentCarpet(String currentCarpet) {
        this.currentCarpet = currentCarpet;
    }

    /**
     * Create a new instance of an Island.
     *
     * @param islandOwner     - the owner of the island.
     * @param isProtected     - if the island is protected from non-members.
     * @param centralX        - central X value of the island.
     * @param centralZ        - central Z value of the island.
     * @param shops
     * @param golemSpawners
     * @param unlockedCarpets
     * @param currentCarpet
     * @param unlockedFences
     * @param currentFence
     */
    public Island(UUID islandUuid, UUID islandOwner, boolean isProtected, int centralX, int centralZ, com.boydti.fawe.object.schematic.Schematic schematic, List<Shop> shops, boolean generateWorld, Map<Location, GolemSpawner> golemSpawners, String server, List<String> unlockedCarpets, String currentCarpet, List<String> unlockedFences, String currentFence) {
        this.islandUuid = islandUuid;
        this.islandOwner = islandOwner;
        this.isProtected = isProtected;
        this.schematic = schematic;
        this.shops = shops;
        this.unlockedCarpets = unlockedCarpets;
        this.currentCarpet = currentCarpet;
        this.unlockedFences = unlockedFences;
        this.currentFence = currentFence;
        this.saveTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
        this.server = server;
        this.golemSpawners = golemSpawners;
        this.templeStage = BoosterTemple.DEFAULT; // Default temple.
        this.settings = new HashMap<>();
        this.boosters = new ArrayList<>();
        this.loadedMinions = new ArrayList<>();
        this.invites = new ArrayList<>();
        this.availableSuits = new ArrayList<>();
        this.availableBoostTypes = new ArrayList<>();
        this.availableMinionTags = new ArrayList<>();

        if (generateWorld) {
            if (Bukkit.getWorld(islandUuid.toString()) == null) {
                world = WorldUtil.generateWorld(islandUuid.toString(), World.Environment.NORMAL, false, true);
                Logger.log("Empty world generated for (" + UUIDUtil.getUsername(islandOwner) + ")'s island.");
            }
        }

        this.centralX = centralX;
        this.centralZ = centralZ;
        this.height = 115;
        this.protectionDistance = IslandManager.getProtectionDistanceFromIslandRadius(this); // Default protection distance.
        this.islandMembers = new HashMap<>();
        this.islandMembers.put(islandOwner, IslandRank.OWNER);

        this.balance = 0;

        // Minimum and Maximum values (boundaries for island).
        this.minX = centralX - protectionDistance;
        this.maxX = centralX + protectionDistance;
        this.minZ = centralZ - protectionDistance;
        this.maxZ = centralZ + protectionDistance;

        // TODO loading of island settings. (Default settings all revert to false?
        for (IslandSettings setting : IslandSettings.values()) {
            settings.put(setting, false);
        }
        if (world != null) {
            try {
                WorldBorder worldBorder = world.getWorldBorder();
                worldBorder.setSize(protectionDistance * 2);
                worldBorder.setDamageAmount(0);
                worldBorder.setCenter(new Location(world, centralX, height, centralZ));

                IslandUtils.getCountOnIslandAsync(this, Material.MOB_SPAWNER, this::setSpawnerCount);
            } catch (Exception e) {

            }
        }

        // TODO boosters
    }


    public void uploadToDatabaseAsync(Runnable onDone, Consumer<Throwable> onFailure) {
        try {
            CloudSkyblock.getPlugin().getIslandManager().saveIslandToDatabase(this, onDone);
        } catch (Exception e) {
            onFailure.accept(e);
        }
    }

    public void save() {
        CloudSkyblock.getPlugin().getIslandManager().unloadIsland(this, null, () -> {

        }, () -> {

        });
    }

    public void setWorld(World world) {
        this.world = world;
        if (world != null) {
            try {
                WorldBorder worldBorder = world.getWorldBorder();
                worldBorder.setSize(protectionDistance * 2);
                worldBorder.setDamageAmount(0);
                worldBorder.setCenter(new Location(world, centralX, height, centralZ));
                IslandUtils.getCountOnIslandAsync(this, Material.MOB_SPAWNER, this::setSpawnerCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void increaseSpawnerCount() {
        spawnerCount++;
        update();
    }

    public void decreaseSpawnerCount() {
        if (spawnerCount > 0) {
            spawnerCount--;
        }
        update();
    }

    public void addAvailableMinionTag(MinionTagType type) {
        availableMinionTags.add(type);
    }

    public void addAvailableSuit(SuitType suitType) {
        availableSuits.add(suitType);
    }

    public void addAvailableBoostType(MinionBoostType type) {
        availableBoostTypes.add(type);
    }

    public List<MinionTagType> getAvailableMinionTags() {
        return availableMinionTags;
    }

    public List<SuitType> getAvailableSuits() {
        return availableSuits;
    }

    public List<MinionBoostType> getAvailableBoostTypes() {
        return availableBoostTypes;
    }

    public void setAvailableSuits(List<SuitType> availableSuits) {
        this.availableSuits = availableSuits;
    }

    public void setAvailableBoostTypes(List<MinionBoostType> availableBoostTypes) {
        this.availableBoostTypes = availableBoostTypes;
    }

    public void setAvailableMinionTags(List<MinionTagType> availableMinionTags) {
        this.availableMinionTags = availableMinionTags;
    }

    public Minion getMinionByEntity(ArmorStand stand) {

        if (!stand.hasMetadata("minion")) return null;

        Minion check = (Minion) stand.getMetadata("minion").get(0).value();

        for (Minion minion : getLoadedMinions()) {
            if (check.getId() == minion.getId() && stand.getWorld().getName().equalsIgnoreCase(check.getIsland().getWorld().getName())) {
                return minion;
            }
        }
        return null;
    }

    public Optional<Minion> getMinionById(int id) {
        return getLoadedMinions().stream().filter(minion -> minion.getId() == id).findFirst();
    }

    /**
     * Get a uniqueID specific to the island.
     *
     * @return
     */
    public UUID getIslandUuid() {
        return islandUuid;
    }

    /**
     * Get the owner of the island.
     *
     * @return Owners UUID.
     */
    public UUID getIslandOwner() {
        return islandOwner;
    }

    /**
     * Set the current island owner.
     *
     * @param islandOwner - island owner.
     */
    public void setIslandOwner(UUID islandOwner) {
        this.islandOwner = islandOwner;
    }

    /**
     * Add a new island member.
     *
     * @param uuid - uuid.
     * @param rank - rank.
     */
    public void addIslandMember(UUID uuid, IslandRank rank, boolean message) {
        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(uuid, islandUuid);

        islandMembers.put(uuid, rank);
        for (UUID uuid1 : getIslandMembers().keySet()) {
            if (uuid1 != null) {
                Player player = Bukkit.getPlayer(uuid1);
                if (player != null && player.isOnline()) {
                    if (message) {
                        CustomChatMessage.sendMessage(player, "Island", UUIDUtil.getUsername(uuid) + " has joined your island.");
                    }
                }
            }
        }

        Logger.log("Member added to (" + islandOwner + ")'s island. UUID (" + uuid + ").");
    }

    /**
     * Remove an existing island member.
     *
     * @param uuid - uuid.
     */
    public void removeIslandMember(UUID uuid) {
        if (!islandMembers.containsKey(uuid) || uuid == islandOwner) return;
        islandMembers.remove(uuid);
        for (UUID uuid1 : getIslandMembers().keySet()) {
            if (uuid1 != null) {
                Player player = Bukkit.getPlayer(uuid1);
                if (player != null && player.isOnline()) {
                    CustomChatMessage.sendMessage(player, "Island", UUIDUtil.getUsername(uuid) + " has left your island.");
                }
            }
        }
        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().setIslandUuid(uuid, null);
        Logger.log("Member removed from (" + islandOwner + ")'s island. UUID (" + uuid + ").");
    }

    /**
     * Check if a player is the member of an Island.
     *
     * @param uuid - the member's uuid being checked.
     * @return Is member.
     */
    public boolean isIslandMember(UUID uuid) {
        return islandMembers.containsKey(uuid) || islandOwner.equals(uuid);
    }

    /**
     * Check if a member of the island has a specific rank.
     *
     * @param uuid - uuid.
     * @param rank - rank
     * @return Has rank.
     */
    public boolean hasRank(UUID uuid, IslandRank rank) {
        return islandMembers.get(uuid).ordinal() >= rank.ordinal();
    }

    /**
     * Get a list of all members of the island.
     *
     * @return Members.
     */
    public Map<UUID, IslandRank> getIslandMembers() {
        return islandMembers;
    }

    /**
     * Get the schematic the user selected to build their island on.
     *
     * @return Schematic
     */
    public com.boydti.fawe.object.schematic.Schematic getSchematic() {
        return schematic;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    /**
     * Get the settings for an island.
     *
     * @return IslandSettings
     */
    public Map<IslandSettings, Boolean> getSettings() {
        return settings;
    }

    public Map<Location, GolemSpawner> getGolemSpawners() {
        if (golemSpawners == null) {
            golemSpawners = new ConcurrentHashMap<>();
        }
        return golemSpawners;
    }

    public void deleteSpawner(Location location) {
        if (golemSpawners == null) {
            golemSpawners = new ConcurrentHashMap<>();
        }
        if (golemSpawners.containsKey(location)) {

            GolemSpawner remove = golemSpawners.remove(location);
            remove.despawn();
        }
    }

    public GolemSpawner getGolemSpawner(Location location) {
        if (golemSpawners == null) {
            golemSpawners = new ConcurrentHashMap<>();
        }
        return golemSpawners.get(location);
    }

    public GolemSpawner addSpawner(GolemSpawner golemSpawner) {
        golemSpawners.put(new Location(world, golemSpawner.getX(), golemSpawner.getY(), golemSpawner.getZ()), golemSpawner);
        return golemSpawner;
    }

    public void upgradeSpawner(Location location) {
        if (golemSpawners.containsKey(location)) {
            GolemSpawner golemSpawner = golemSpawners.get(location);
            if (golemSpawner.getTier() < 5) {
                golemSpawner.setTier(golemSpawner.getTier() + 1);
            }
        }
    }

    public void upgradeSpawnerCount(Location location) {
        if (golemSpawners.containsKey(location)) {
            GolemSpawner golemSpawner = golemSpawners.get(location);
            if (golemSpawner.getCount() < 5) {
                golemSpawner.setCount(golemSpawner.getCount() + 1);
            }
        }
    }

    public void setGolemSpawners(Map<Location, GolemSpawner> golemSpawners) {
        this.golemSpawners = golemSpawners;
    }

    /**
     * Set a boosters level.
     *
     * @param type  - type
     * @param level - level
     */
    public void setBoosterLevel(BoosterType type, int level) {
        getBoosterByType(type).ifPresent(boost -> boosters.remove(boost));
        boosters.add(new Booster(type, level));
    }

    /**
     * Get a booster by its unique ID.
     *
     * @param type - tyoe
     * @return Booster
     */
    public Optional<Booster> getBoosterByType(BoosterType type) {
        return boosters.stream().filter(booster -> booster.getType() == type).findFirst();
    }

    public void setBoosters(List<Booster> boosters) {
        this.boosters = boosters;
    }

    public List<Booster> getBoosters() {
        return boosters;
    }

    /**
     * Set a setting.
     *
     * @param setting - setting.
     * @param allowed - is allowed.
     */
    public void setSetting(IslandSettings setting, boolean allowed) {
        settings.put(setting, allowed);
    }

    /**
     * Set settings from a map, used on loading large data.
     *
     * @param settings - settings.
     */
    public void setSettings(Map<IslandSettings, Boolean> settings) {
        this.settings = settings;
    }

    /**
     * Set the temples current stage of the Booster temple.
     *
     * @param templeStage - Current temple stage.
     */
    public void setTempleStage(BoosterTemple templeStage) {
        this.templeStage = templeStage;
    }

    /**
     * Get the current temple stage for the island.
     *
     * @return BoosterTemple
     */
    public BoosterTemple getTempleStage() {
        return templeStage;
    }

    /**
     * Is island setting allowed?
     *
     * @param setting - setting
     * @return is allowed.
     */
    public boolean getIslandSettingEnabled(IslandSettings setting) {
        return settings.get(setting);
    }

    /**
     * Get the location of the Island by its central values.
     *
     * @return Location of island.
     */
    private boolean locatedGotten;

    public Location getLocation() {
//        return new Location(getWorld(), centralX, height, centralZ);
        locatedGotten = true;
        Location location = new Location(getWorld(), 2.8, 92, -43, 58.2F, 8.1F);

        return location;
    }

    /**
     * Get the current list of all loaded minions on this island.
     *
     * @return list of all loaded minions on the island.
     */
    public List<Minion> getLoadedMinions() {
        return loadedMinions;
    }

    /**
     * Get the world the island is in.
     *
     * @return World island is in.
     */
    public World getWorld() {
        if (world == null) {
            world = Bukkit.getWorld(getIslandUuid().toString());
        } else {
            if (world.getName().equalsIgnoreCase("world")) {
                Bukkit.getServer().broadcastMessage(ChatColor.RED + UUIDUtil.getUsername(islandOwner) + " FKED UP WORLD");
                return world;
            }
        }
        return world;
    }

    /**
     * Get the central X value of the island.
     *
     * @return X value.
     */
    public int getCentralX() {
        return centralX;
    }

    /**
     * Get the central Z value of the island.
     *
     * @return Z value.
     */
    public int getCentralZ() {
        return centralZ;
    }

    /**
     * Get the minimum X value of the island.
     *
     * @return Minimum X value.
     */
    public int getMinX() {
        return minX;
    }

    /**
     * Get the maximum X value of the island.
     *
     * @return Maximum X value.
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * Get the minimum Z value of the island.
     *
     * @return Minimum Z value.
     */
    public int getMinZ() {
        return minZ;
    }

    /**
     * Get the maximum Z value of the island.
     *
     * @return Maximum Z value.
     */
    public int getMaxZ() {
        return maxZ;
    }

    /**
     * Height of the centre of the island.
     *
     * @return Height value (y).
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Is the island protected from non-members?
     */
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * Get protection distance for the island.
     *
     * @return Protection distance.
     */
    public int getProtectionDistance() {
        return protectionDistance;
    }

    /**
     * Set the protection distance of an island.
     *
     * @param protectionDistance - protection distance.
     */
    public void setProtectionDistance(int protectionDistance) {
        this.protectionDistance = protectionDistance;
        if (world != null) {
            WorldBorder worldBorder = world.getWorldBorder();
            worldBorder.setSize(protectionDistance);
        }
    }

    public void addInvite(Player player) {
        if (invites.contains(player.getUniqueId())) return;
        invites.add(player.getUniqueId());
    }

    public void removeInvite(Player player) {
        if (!invites.contains(player.getUniqueId())) return;
        invites.remove(player.getUniqueId());
    }

    public boolean isInvited(Player player) {
        return invites.contains(player.getUniqueId());
    }

    public List<UUID> getInvites() {
        return invites;
    }

    public void setIslandLevel(double islandLevel) {
        this.islandLevel = islandLevel;
    }

    public double getIslandLevel() {
        return islandLevel;
    }

    public void setLoadedMinions(List<Minion> loadedMinions) {
        this.loadedMinions = loadedMinions;
    }

    /**
     * Check if a player is in the region of an island.
     *
     * @param player - player.
     * @return on island.
     */
    public boolean inIslandRegion(Player player) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        Vector maximum = new Vector(getMaxX(), getWorld().getMaxHeight(), getMaxZ());
        Vector minimum = new Vector(getMinX(), 0, getMinZ());

        return x >= minimum.getBlockX() && x <= maximum.getBlockX() && y >= minimum.getBlockY() && y <= maximum.getBlockY() && z >= minimum.getBlockZ() && z <= maximum.getBlockZ();
    }


    public Shop getShop(Location location) {
        return shops.stream().filter(shop -> shop.getX() == location.getBlockX() && shop.getY() == location.getBlockY() && shop.getZ() == location.getBlockZ()).findFirst().orElse(null);
    }

    public void addShop(Shop shop) {
        shops.add(shop);
    }

    public Shop editShop(Shop shop) {
        if (shops.contains(shop)) {
            shops.remove(shop);
        }
        shops.add(shop);
        return shop;
    }

    public void deleteShop(Shop shop) {
        if (shops.contains(shop)) {
            shops.remove(shop);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Island)) {
            return false;
        }
        Island island = (Island) obj;
        return island != null && island.getIslandUuid() != null && island.getIslandUuid().toString().equals(islandUuid.toString());
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void update() {
        CloudSkyblock.getPlugin().getIslandManager().updateIsland(this);
    }

    public void setLoadedServer(String name) {
        this.server = name;
    }

    public String getServer() {
        return server;
    }

    public int getSpawnerCount() {
        return spawnerCount;
    }

    public void setSpawnerCount(int spawnerCount) {
        this.spawnerCount = spawnerCount;
    }

    public void updateSpawner(World world, GolemSpawner golemSpawner) {
        Location location = golemSpawner.getLocation(world);
        if (golemSpawners.containsKey(location)) {
            golemSpawners.remove(location);
        }
        golemSpawners.put(location, golemSpawner);
        update();
    }

    public boolean isLocatedGotten() {
        return locatedGotten;
    }

    public Island locatedGotten(boolean locatedGotten) {
        this.locatedGotten = locatedGotten;
        return this;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public List<String> getUnlockedCarpets() {
        return unlockedCarpets;
    }

    public List<String> getUnlockedFences() {
        return unlockedFences;
    }

    public void setUnlockedFences(List<String> unlockedFences) {
        this.unlockedFences = unlockedFences;
    }

    public String getCurrentFence() {
        return currentFence;
    }

    public void setCurrentFence(String currentFence) {
        this.currentFence = currentFence;
    }

    private List<Location> getArenaBlocks(Location l, int radius) {
        World w = l.getWorld();
        int xCoord = (int) l.getX();
        int zCoord = (int) l.getZ();
        int YCoord = (int) l.getY();

        List<Location> tempList = new ArrayList<Location>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    tempList.add(new Location(w, xCoord + x, YCoord + y, zCoord + z));
                }
            }
        }
        return tempList;
    }

    private void getSchematicBlocksAsync(Location center, int radius, Material[] search, Consumer<List<SchematicBlock>> listConsumer) {
        List<Location> locations = new ArrayList<>(getArenaBlocks(new Location(world, center.getX(), center.getY(), center.getZ()), radius));
        listConsumer.accept(locations.stream().filter(location -> Arrays.asList(search).contains(location.getBlock().getType())).map(location -> new SchematicBlock(location.getBlock().getTypeId(), location.getBlock().getData(), location.getBlockX(), location.getBlockY(), location.getBlockZ())).collect(Collectors.toList()));
    }

    public void updateCarpet(Runnable onFinish) {
        Region cuboidRegion = new CuboidRegion(templeMin, templeMax);
        com.sk89q.worldedit.Vector center = cuboidRegion.getCenter();
        getSchematicBlocksAsync(new Location(world, center.getBlockX(), center.getBlockY(), center.getBlockZ()), 100, new Material[]{Material.CARPET}, schematicBlocks -> {
            SchematicManager.setBlocksAsync(world, schematicBlocks, Material.CARPET, DyeColor.valueOf(currentCarpet).getWoolData(), 30, aBoolean -> {
                onFinish.run();
            });
        });
    }

    public void updateFence(Runnable onFinish) {
        Region cuboidRegion = new CuboidRegion(templeMin, templeMax);
        com.sk89q.worldedit.Vector center = cuboidRegion.getCenter();
        Material[] fences = Arrays.stream(Material.values()).filter(material -> material.toString().endsWith("_FENCE")).toArray(Material[]::new);
        getSchematicBlocksAsync(new Location(world, center.getBlockX(), center.getBlockY(), center.getBlockZ()), 100, fences, schematicBlocks -> SchematicManager.setBlocksAsync(world, schematicBlocks, Material.getMaterial(currentFence),(byte)0, 30, aBoolean -> onFinish.run()));
    }

    public void unlockCarpet(String s) {
        if (!unlockedCarpets.contains(s)) {
            unlockedCarpets.add(s);
        }
    }
}
