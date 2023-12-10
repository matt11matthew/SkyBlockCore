package net.cloudescape.skyblock.database.island;

import com.cloudescape.database.container.Container;
import com.cloudescape.database.wrappers.Wrapper;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandRank;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.island.shop.Shop;
import net.cloudescape.skyblock.island.shop.ShopStage;
import net.cloudescape.skyblock.island.shop.ShopType;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.island.spawner.GolemType;
import net.cloudescape.skyblock.island.temple.BoosterTemple;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoost;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.miscellaneous.minions.enums.InvestmentType;
import net.cloudescape.skyblock.miscellaneous.minions.enums.MinionType;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Banker;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.utils.LocationUtil;
import net.cloudescape.skyblock.utils.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class IslandContainer extends Container {

    /**
     * Construct a new instance of {@link IslandContainer}.
     *
     * @param wrapper  - {@link IslandWrapper}
     * @param document - Document.
     */
    public IslandContainer(Wrapper wrapper, Document document) {
        super(wrapper, document);
    }

    public void setLoadedServer(String serverName) {
        this.setValue("loadedServer", serverName);
    }

    public String getLoadedServer() {
        return (String) this.getValue("loadedServer", null);
    }

    public int getBalance() {
        return (int) this.getValue("balance", 500);
    }

    public boolean isSettingEnabled(IslandSettings settings) {
        return (boolean) this.getValue("settings." + settings.name(), settings.isDefault());
    }

    /**
     * @return Get the Islands UUID.
     */
    public UUID getUniqueID() {
        return ((UUID) this.getValue("uniqueID", null));
    }

    /**
     * @return Get the owner of the islands UUID.
     */
    public UUID getOwner() {
        return ((UUID) this.getValue("owner", null));
    }

    /**
     * @return Get the central X point of the island (typically 0).
     */
    public int getCentralX() {
        return ((int) this.getValue("centralX", 0));
    }

    /**
     * @return Get the central Z point of the island (typically 0).
     */
    public int getCentralZ() {
        return ((int) this.getValue("centralZ", 0));
    }

    /**
     * @return Get the height of the island.
     */
    public int getHeight() {
        return ((int) this.getValue("height", 115));
    }

    /**
     * @return Get the distance around the island players are protected in (can build in etc).
     */
    public int getProtectionDistance() {
        return ((int) this.getValue("protectionDistance", 100));
    }

    /**
     * @return Is their island protected.
     */
    public boolean isProtected() {
        return ((boolean) this.getValue("protected", true));
    }

    public double getIslandLevel() {
        return (double) this.getValue("islandLevel", 0.0);
    }

    public void setCurrentIslandLevel(double level) {
        this.setValue("islandLevel", level);
    }

    /**
     * @return Get their current temple stage.
     */
    public BoosterTemple getBoosterTemple() {
        return BoosterTemple.valueOf(((String) this.getValue("boosterTemple", BoosterTemple.DEFAULT.name())));
    }

    public void setIslandMembers(Map<UUID, IslandRank> islandMembers) {
        int id = 1;
        for (Map.Entry<UUID, IslandRank> uuidIslandRankEntry : islandMembers.entrySet()) {
            setValue("members." + id + ".uuid", uuidIslandRankEntry.getKey().toString());
            setValue("members." + id + ".rank", uuidIslandRankEntry.getValue().toString());
            id++;
        }
        update();
    }

    public void setIslandMinions(List<Minion> minions) {
        int id = 1;
        for (Minion minionEntry : minions) {
            if (minionEntry.getMinion() != null && minionEntry.getMinion().getLocation() != null)
                setValue("minions." + id + ".location", LocationUtil.getStringFromLocation(minionEntry.getMinion().getLocation()));
            setValue("minions." + id + ".hunger", minionEntry.getHunger());
            setValue("minions." + id + ".health", minionEntry.getHealth());
            setValue("minions." + id + ".type", minionEntry.getType().name());
            setValue("minions." + id + ".name", minionEntry.getName());
            setValue("minions." + id + ".booster.type", minionEntry.getBoost() == null ? null : minionEntry.getBoost().getType().name());
            setValue("minions." + id + ".booster.duration", minionEntry.getBoost() == null ? -1 : minionEntry.getBoost().getDuration() - System.currentTimeMillis());

            setValue("minions." + id + ".minionTag", minionEntry.getMinionTag().name());

            switch (minionEntry.getType()) {
                case MINER:
                    // Save miner data etc
                    setValue("minions." + id + ".blocksMined", ((Miner) minionEntry).getBlocksMined());
                    break;
                case BUTCHER:
                    // Save Butcher data etc
                        /*
                        // id, mobsKilled, radius, killableMobs, island, location, name, type, health, hunger, tag, boost
                        int mobsKilled = (int) getValue("minions." + id + ".mobsKilled", 0);
                        int radius = (int) getValue("minions." + id + ".radius", 5);
                        String possibleEntities = (String) getValue("minions." + id + ".killableMobs", Collections.singleton(EntityType.ZOMBIE.name() + ":"));
                         */

                    String killableMobs = "";

                    for (EntityType type : ((Butcher) minionEntry).getKillableMobs())
                        killableMobs += type.name() + ":";

                    setValue("minions." + id + ".radius", ((Butcher) minionEntry).getRadius());
                    setValue("minions." + id + ".mobsKilled", ((Butcher) minionEntry).getMobsKilled());
                    setValue("minions." + id + ".killableMobs", killableMobs);
                    break;
                case BANKER:
                    setValue("minions." + id + ".canWithdraw", ((Banker) minionEntry).getCanWithdraw());
                    setValue("minions." + id + ".investment", ((Banker) minionEntry).getInvestment());
                    setValue("minions." + id + ".maxInvestment", ((Banker) minionEntry).getMaximumInvestment());
                    setValue("minions." + id + ".minInvestment", ((Banker) minionEntry).getMinimumInvestment());
                    setValue("minions." + id + ".maxInvestment", ((Banker) minionEntry).getInvestmentType().name());
                    break;
            }

            id += 1;
        }
    }

    /**
     * @return Get an islands members.
     */
    public Map<UUID, IslandRank> getIslandMembers() {

        int id = 1;
        boolean isFinished = false;
        Map<UUID, IslandRank> members = new HashMap<>();
        while (getValue("members." + id + ".uuid", getWrapper().getValue(getDocument(), "members." + id + ".uuid")) != null) {

            UUID uuid = null;
            try {
                uuid = UUID.fromString((String) getValue("members." + id + ".uuid", null));
            } catch (Exception e) {
                continue;
            }
            IslandRank islandRank = IslandRank.getRankByName((String) getValue("members." + id + ".rank", IslandRank.MEMBER.name()));
            members.put(uuid, islandRank);
            id++;
        }

        return members;
    }

    public List<UUID> getIslandInvites() {
        return (List<UUID>) getValue("invites", null);
    }

    public List<SuitType> getSuitTypes() {
        List<SuitType> type = new ArrayList<>();

        for (String str : (List<String>) getValue("suits", new ArrayList<>())) {
            type.add(SuitType.valueOf(str));
            Logger.log("&b&lAdded new Suit " + str);
        }

        return type;
    }


    public List<MinionTagType> getMinionTagTypes() {
        List<MinionTagType> type = new ArrayList<>();

        for (String str : (List<String>) getValue("minionTags", new ArrayList<>())) {
            type.add(MinionTagType.valueOf(str));
        }

        return type;
    }

    public List<MinionBoostType> getBoostTypes() {
        List<MinionBoostType> type = new ArrayList<>();

        for (String str : (List<String>) getValue("minionBoosts", new ArrayList<>())) {
            type.add(MinionBoostType.valueOf(str));
        }

        return type;
    }


    /**
     * @return Get an islands members.
     */
    public List<Minion> getIslandMinions() {

        int id = 1;
        boolean isFinished = false;
        List<Minion> minions = new ArrayList<>();
        while (!isFinished) {

            try {

                String minionType = (String) getValue("minions." + id + ".type", null);

                if (minionType == null || minionType.equalsIgnoreCase("null")) {
                    break;
                }

                Location location = LocationUtil.getLocationFromString((String) getValue("minions." + id + ".location", LocationUtil.getStringFromLocation(new Location(Bukkit.getWorld(getUniqueID().toString()), 0, 0, 0, 0F, 0F))));
                int hunger = (int) getValue("minions." + id + ".hunger", 100);
                int health = (int) getValue("minions." + id + ".health", 100);
                String name = (String) getValue("minions." + id + ".name", "NONE");
                MinionType type = MinionType.valueOf(minionType);

                Minion minion = null;
                MinionBoost boost = null;
                Island island = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(location.getWorld()).get();
                if (getValue("minions." + id + ".booster.type", null) != null) {
                    boost = new MinionBoost(MinionBoostType.valueOf((String) getValue("minions." + id + ".booster.type", null)), (long) getValue("minions." + id + ".booster.duration", 0));
                }
                switch (type) {
                    case MINER:
                        // id, island, location, name, type, health, hunger, tag, boost
                        minion = new Miner(id, island, location, name, type, health, hunger, boost);
                        ((Miner) minion).setBlocksMined((int) getValue("minions." + id + ".blocksMined", 0));
                        break;
                    case BUTCHER:
                        // id, mobsKilled, radius, killableMobs, island, location, name, type, health, hunger, tag, boost
                        int mobsKilled = (int) getValue("minions." + id + ".mobsKilled", 0);
                        int radius = (int) getValue("minions." + id + ".radius", 5);
                        String possibleEntities = (String) getValue("minions." + id + ".killableMobs", Collections.singleton(EntityType.ZOMBIE.name() + ":"));
                        minion = new Butcher(id, island, mobsKilled, radius, possibleEntities, location, name, type, health, hunger, boost);
                        break;
                    case BANKER:
                        minion = new Banker(id, island, location, name, type, health, hunger, boost, null, InvestmentType.DAILY);
                        Banker banker = ((Banker) minion);

                        banker.setInvestment((double) getValue("minions." + id + ".investment", 0));
                        banker.setMaximumInvestment((double) getValue("minions." + id + ".maxInvestment", 1000));
                        banker.setMinimumInvestment((double) getValue("minions." + id + ".minInvestment", 20));
                        banker.setInvestmentType(InvestmentType.valueOf((String) getValue("minions." + id + ".investment", InvestmentType.DAILY.name())));
                        banker.setCanWithdraw((long) getValue("minions." + id + ".canWithdraw", 0));
                        break;
                }

                minion.setMinionTag(MinionTagType.valueOf((String) getValue("minions." + id + ".minionTag", MinionTagType.DEFAULT.name())));

                // this is the get method, just add the data you need to get lmao then saving is in wrapper
                minions.add(minion);
                id += 1;
            } catch (Exception e) {
                e.printStackTrace();
                isFinished = true;
            }
        }
        return minions;
    }

    /**
     * @return Get settings.
     */
    public Map<IslandSettings, Boolean> getSettings() {
        Map<IslandSettings, Boolean> settings = new HashMap<>();
        for (IslandSettings islandSettings : IslandSettings.values()) {

            settings.put(islandSettings, (Boolean) getValue("settings." + islandSettings.toString(), islandSettings.isDefault()));

        }
        return settings;
    }

    /**
     * @return Get owned boosters.
     */
    public List<Booster> getBoosters() {

        List<Booster> boosters = new ArrayList<>();

        for (BoosterType type : BoosterType.values()) {
            boosters.add(new Booster(type, (int) getValue("boosters." + type.name() + ".level", 1)));
        }

        return boosters;
    }

    public void setBoosters(List<Booster> boosters) {
        for (Booster booster : boosters) {
            this.setValue("boosters." + booster.getType().name() + ".level", booster.getLevel());
        }
    }

    /**
     * Set the Owner of the island.
     *
     * @param uniqueID - owners uuid.
     */
    public void setOwner(UUID uniqueID) {
        this.setValue("owner", uniqueID);
    }

    /**
     * Set the CentralX point for an island.
     *
     * @param x - x coordinate.
     */
    public void setCentralX(int x) {
        this.setValue("centralX", x);
    }

    /**
     * Set the CentralZ point for an island.
     *
     * @param z - z coordinate.
     */
    public void setCentralZ(int z) {
        this.setValue("centralZ", z);
    }

    /**
     * Set the height value of an island.
     *
     * @param height - height
     */
    public void setHeight(int height) {
        this.setValue("height", height);
    }

    /**
     * Set the protection distance of the island.
     *
     * @param distance - distance. (Start = 150, average = 500).
     */
    public void setProtectionDistance(int distance) {
        this.setValue("protectionDistance", distance);
    }

    public void setSetting(IslandSettings setting, boolean value) {
        this.setValue("settings." + setting.toString(), value);
        this.update();
    }

    /**
     * Set if the island is protected.
     *
     * @param isProtected - is protected.
     */
    public void setIsProtected(boolean isProtected) {
        this.setValue("protected", isProtected);
    }

    /**
     * Set the temple stage for the island.
     *
     * @param temple - island temple.
     */
    public void setTempleStage(BoosterTemple temple) {
        this.setValue("boosterTemple", temple.name());
    }

    public void setSchematic(String s) {
        this.setValue("schematic", s);
    }

    public String getSchematic() {
        return (String) getValue("schematic", null);
    }

    public List<Shop> getShops() {
        List<Shop> shops = new ArrayList<>();
        int id = 1;
        boolean finished = false;


        while (!finished) {
            try {
                Object value = this.getWrapper().getValue(this.getDocument(), "shops." + id + ".x");
                if (value == null) {
                    finished = true;
                    break;
                }
                int x = (int) getValue("shops." + id + ".x", -1);
                int y = (int) getValue("shops." + id + ".y", -1);
                int z = (int) getValue("shops." + id + ".z", -1);
                if (x == -1 && y == -1 && z == -1) {
                    finished = true;
                    break;
                }
                int itemId = (int) getValue("shops." + id + ".itemType", -1);
                int itemData = (int) getValue("shops." + id + ".itemData", -1);
                int amount = (int) getValue("shops." + id + ".amount", -1);
                UUID uuid = (UUID) getValue("shops." + id + ".uuid", null);
                UUID playerUuid = (UUID) getValue("shops." + id + ".playerUuid", null);
                double price = (double) getValue("shops." + id + ".price", -1);
                ShopType shopType = ShopType.valueOf(((String) getValue("shops." + id + ".type", "buy")).trim().toUpperCase());
                ShopStage shopStage = ShopStage.valueOf(((String) getValue("shops." + id + ".stage", "created")).trim().toUpperCase());
                Shop shop = new Shop(uuid, shopStage, shopType, price, amount, playerUuid, x, y, z, itemId, itemData);
                shops.add(shop);
                id += 1;
            } catch (Exception e) {
                finished = true;
            }
        }

        return shops;
    }

    public List<GolemSpawner> getSpawners() {
        List<GolemSpawner> golemSpawners = new ArrayList<>();
        int id = 1;
        boolean finished = false;


        while (!finished) {
            try {
                Object value = this.getWrapper().getValue(this.getDocument(), "spawners." + id + ".x");
                if (value == null) {
                    finished = true;
                    break;
                }
                int x = (int) getValue("spawners." + id + ".x", -1);
                int y = (int) getValue("spawners." + id + ".y", -1);
                int z = (int) getValue("spawners." + id + ".z", -1);
                if (x == -1 && y == -1 && z == -1) {
                    finished = true;
                    break;
                }
                GolemType golemType = GolemType.valueOf((String) getValue("spawners." + id + ".type", GolemType.IRON.toString()));
                UUID owner = (UUID) getValue("spawners." + id + ".owner", null);
                int tier = (int) getValue("spawners." + id + ".tier", 1);
                int count = (int) getValue("spawners." + id + ".count", 1);
                double dropMultiplier = (double) getValue("spawners." + id + ".dropMultiplier", 1.0D);
                EntityType entityType = EntityType.valueOf((String) getValue("spawners." + id + ".entityType", EntityType.IRON_GOLEM.toString()));
                GolemSpawner golemSpawner = new GolemSpawner(x, y, z, golemType, tier, owner, entityType, count, dropMultiplier);
                golemSpawners.add(golemSpawner);

                id += 1;
            } catch (Exception e) {
                finished = true;
            }
        }
        return golemSpawners;
    }

    public List<String> getUnlockedCarpets() {
        List<String> defaultCarpets = new ArrayList<>();
        defaultCarpets.add(DyeColor.BROWN.toString());

        List<String> carpets = (List<String>) getValue("carpets.unlocked", defaultCarpets);
        return carpets;
    }

    public String getCurrentCarpet() {
        return (String) getValue("carpets.current", DyeColor.BROWN.toString());
    }


    public List<String> getUnlockedFences() {
        List<String> defaultFences = new ArrayList<>();
        defaultFences.add(Material.DARK_OAK_FENCE.toString());

        List<String> fences = (List<String>) getValue("fences.unlocked", defaultFences);
        return fences;
    }

    public String getCurrentFence() {
        return (String) getValue("fences.current",Material.DARK_OAK_FENCE.toString());
    }
}