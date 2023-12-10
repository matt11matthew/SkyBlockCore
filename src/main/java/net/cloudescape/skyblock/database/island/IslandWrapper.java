package net.cloudescape.skyblock.database.island;

import com.cloudescape.database.wrappers.Wrapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandRank;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.island.shop.Shop;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.miscellaneous.boosters.Booster;
import net.cloudescape.skyblock.miscellaneous.boosters.BoosterType;
import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import net.cloudescape.skyblock.miscellaneous.minions.boost.MinionBoostType;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Butcher;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.cloudescape.skyblock.miscellaneous.minions.suit.SuitType;
import net.cloudescape.skyblock.miscellaneous.minions.tag.MinionTagType;
import net.cloudescape.skyblock.utils.LocationUtil;
import net.cloudescape.skyblock.utils.Logger;
import org.bson.Document;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IslandWrapper extends Wrapper {

    public static final String COLLECTION_NAME = "islands";

    public IslandWrapper() {
    }

    /**
     * Add a new island to the MongoDB database.
     *
     * @param island - island.
     * @return Document
     */
    public Document createIsland(Island island) {
        Document doc = createDocument("uniqueID", island.getIslandUuid());
        doc = this.setValue(doc, "owner", island.getIslandOwner());
        doc = this.setValue(doc, "centralX", island.getCentralX());
        doc = this.setValue(doc, "centralZ", island.getCentralZ());
        doc = this.setValue(doc, "height", island.getHeight());

        int id = 1;
        doc = this.setValue(doc, "members", null);
        for (Map.Entry<UUID, IslandRank> uuidIslandRankEntry : island.getIslandMembers().entrySet()) {
            doc = setValue(doc, "members." + id + ".uuid", uuidIslandRankEntry.getKey().toString());
            doc = setValue(doc, "members." + id + ".rank", uuidIslandRankEntry.getValue().name());
            id++;
        }

        doc = this.setValue(doc, "invites", island.getInvites());

        doc = this.setValue(doc, "protectionDistance", island.getProtectionDistance());
        doc = this.setValue(doc, "protected", island.isProtected());
        doc = this.setValue(doc, "boosterTemple", island.getTempleStage().name());
        doc = this.setValue(doc, "shops", null);
        doc = this.setValue(doc, "spawners", null);
        doc = this.setValue(doc, "islandLevel", 0.0);

        List<String> defaultCarpets = new ArrayList<>();
        defaultCarpets.add(DyeColor.BROWN.toString());

        List<String> defaultFences = new ArrayList<>();
        defaultFences.add(Material.DARK_OAK_FENCE.toString());

        doc = this.setValue(doc, "carpets.current", DyeColor.BROWN.toString());
        doc = this.setValue(doc, "carpets.unlocked", defaultCarpets);

         doc = this.setValue(doc, "fences.current", Material.DARK_OAK_FENCE.toString());
        doc = this.setValue(doc, "fences.unlocked", defaultFences);

        for (Map.Entry<IslandSettings, Boolean> settings : island.getSettings().entrySet()) {
            doc = this.setValue(doc, "settings." + settings.getKey().name(), settings.getValue());
        }

        id = 1;
        for (BoosterType type : BoosterType.values()) {
            doc = this.setValue(doc, "boosters." + type.name() + ".level", 1);
        }

        doc = this.setValue(doc, "balance", island.getBalance());


        List<String> suits = new ArrayList<>();
        for (SuitType suit : island.getAvailableSuits()) {
            suits.add(suit.name());
        }
        doc = this.setValue(doc, "suits", suits);

        for (Minion minionEntry : island.getLoadedMinions()) {
            doc = this.setValue(doc, "minions." + id + ".location", LocationUtil.getStringFromLocation(minionEntry.getMinion().getLocation()));
            doc = this.setValue(doc, "minions." + id + ".hunger", minionEntry.getHunger());
            doc = this.setValue(doc, "minions." + id + ".health", minionEntry.getHealth());
            doc = this.setValue(doc, "minions." + id + ".type", minionEntry.getType().name());
            doc = this.setValue(doc, "minions." + id + ".name", minionEntry.getName());
            doc = this.setValue(doc, "minions." + id + ".booster.type", minionEntry.getBoost() == null ? null : minionEntry.getBoost().getType().name());
            doc = this.setValue(doc, "minions." + id + ".booster.duration", minionEntry.getBoost() == null ? -1 : minionEntry.getBoost().getDuration() - System.currentTimeMillis());
            doc = this.setValue(doc, "minions." + id + ".tag", minionEntry.getMinionTag().getTagText());
            // TODO tags

            switch (minionEntry.getType()) {
                case MINER:
                    // Save miner data etc
                    doc = this.setValue(doc, "minions." + id + ".blocksMined", ((Miner) minionEntry).getBlocksMined());
                    break;
                case BUTCHER:
                    // Save Butcher data etc

                    String killableMobs = "";

                    for (EntityType type : ((Butcher) minionEntry).getKillableMobs())
                        killableMobs += type.name() + ":";

                    doc = this.setValue(doc, "minions." + id + ".radius", ((Butcher) minionEntry).getRadius());
                    doc = this.setValue(doc, "minions." + id + ".mobsKilled", ((Butcher) minionEntry).getMobsKilled());
                    doc = this.setValue(doc, "minions." + id + ".killableMobs", killableMobs);
                    break;
            }

            id += 1;
        }

        this.updateDocument(doc);
        return doc;
    }

    /**
     * Remove an island from the database.
     */
    public void removeIsland(UUID uuid) {
        this.getCollection().deleteOne(Filters.eq("uniqueID", uuid));
    }

    /**
     * Get an Island from MongoDB by its owner.
     * <p>
     * //     * @param owner - owner.
     *
     * @return IslandContainer
     */
//    public IslandContainer getIslandByOwner(UUID owner) {
//        Document document = search("owner", owner);
//
//        if (document == null) return null;
//
//        return new IslandContainer(this, document);
//    }
    @Override
    public MongoCollection<Document> getCollection() {
        return super.getCollection();
    }

    /**
     * Get an Island from MongoDB by its uniqueID.
     *
     * @param uuid - islands UUID.
     * @return IslandContainer
     */
    public IslandContainer getIslandByUUID(UUID uuid) {
        Document document = search("uniqueID", uuid);

        if (document == null) return null;

        return new IslandContainer(this, document);
    }

    public IslandContainer getIslandByMember(UUID member) {
        UUID islandUuid = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().getIslandUuid(member);
        if (islandUuid == null) {
            return null;
        }
        return getIslandByUUID(islandUuid);
    }

    public IslandContainer getIslandByOwner(UUID owner) {
        Document document = search("owner", owner);

        if (document == null) return null;

        return new IslandContainer(this, document);
    }

    public IslandContainer getIslandByOwnerOrMember(UUID uuid) {
        if (getIslandByUUID(uuid) != null) {
            return getIslandByUUID(uuid);
        }

        return getIslandByMember(uuid);
    }

    /**
     * Save an islands stats.
     *
     * @param island
     */
    public void saveIsland(Island island, boolean create) {

        Document islandDocument = search("uniqueID", island.getIslandUuid());

        if (islandDocument != null) {
            this.setValue(islandDocument, "owner", island.getIslandOwner());
            this.setValue(islandDocument, "loadedServer", island.getServer());
            this.setValue(islandDocument, "centralX", island.getCentralX());
            this.setValue(islandDocument, "centralZ", island.getCentralZ());
            this.setValue(islandDocument, "height", island.getHeight());
            this.setValue(islandDocument, "balance", island.getBalance());
            this.setValue(islandDocument, "carpets.current", island.getCurrentCarpet());
            this.setValue(islandDocument, "carpets.unlocked", island.getUnlockedCarpets());


            this.setValue(islandDocument, "fences.current", island.getCurrentFence());
            this.setValue(islandDocument, "fences.unlocked", island.getUnlockedFences());

            int id = 1;

            this.setValue(islandDocument, "members", null);
            for (Map.Entry<UUID, IslandRank> uuidIslandRankEntry : island.getIslandMembers().entrySet()) {
                if (uuidIslandRankEntry.getKey() == null) {
                    continue;
                }
                setValue(islandDocument, "members." + id + ".uuid", uuidIslandRankEntry.getKey().toString());
                setValue(islandDocument, "members." + id + ".rank", uuidIslandRankEntry.getValue().name());
                id++;
            }
            List<MinionBoostType> minionBoostTypes = island.getAvailableBoostTypes();
            if (minionBoostTypes != null) {
                List<String> typeList = new ArrayList<>();
                for (MinionBoostType type : minionBoostTypes) typeList.add(type.name());
                this.setValue(islandDocument, "minionBoosts", typeList);
            } else {
                this.setValue(islandDocument, "minionBoosts", new ArrayList<String>());

            }

            List<MinionTagType> availableMinionTags = island.getAvailableMinionTags();
            if (availableMinionTags != null) {

                List<String> minionTagTypes = new ArrayList<>();
                for (MinionTagType type : availableMinionTags) minionTagTypes.add(type.name());
                this.setValue(islandDocument, "minionTags", minionTagTypes);
            } else {
                this.setValue(islandDocument, "minionTags", new ArrayList<String>());

            }


            List<SuitType> availableSuits = island.getAvailableSuits();
            if (availableSuits != null) {
                List<String> suitTypes = new ArrayList<>();
                for (SuitType type : availableSuits) {
                    suitTypes.add(type.name());
                }
                this.setValue(islandDocument, "suits", suitTypes);
            } else {
                this.setValue(islandDocument, "suits", new ArrayList<String>());

            }
            this.setValue(islandDocument, "invites", island.getInvites());
            this.setValue(islandDocument, "protectionDistance", island.getProtectionDistance());
            this.setValue(islandDocument, "protected", island.isProtected());
            this.setValue(islandDocument, "boosterTemple", island.getTempleStage().name());
            id = 1;
            if (island.getShops().isEmpty()) {
                islandDocument = this.setValue(islandDocument, "shops", null);
            } else {
                for (Shop shop : island.getShops()) {

                    islandDocument = this.setValue(islandDocument, "shops." + id + ".x", shop.getX());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".y", shop.getY());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".uuid", shop.getUuid());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".z", shop.getZ());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".type", shop.getType().toString().toLowerCase());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".stage", shop.getStage().toString().toLowerCase());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".itemType", shop.getItemId());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".itemData", shop.getItemData());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".amount", shop.getAmount());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".price", shop.getPrice());
                    islandDocument = this.setValue(islandDocument, "shops." + id + ".playerUuid", shop.getPlayerUuid());

                    id++;
                }
            }

            this.setValue(islandDocument, "balance", island.getBalance());

            id = 1;
            islandDocument = this.setValue(islandDocument, "spawners", null);

            for (GolemSpawner golemSpawner : island.getGolemSpawners().values()) {
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".x", golemSpawner.getX());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".y", golemSpawner.getY());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".z", golemSpawner.getZ());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".owner", golemSpawner.getOwner());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".tier", golemSpawner.getTier());
                if (golemSpawner.getGolemType() != null) {
                    islandDocument = this.setValue(islandDocument, "spawners." + id + ".type", golemSpawner.getGolemType().toString());
                }
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".count", golemSpawner.getCount());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".dropMultiplier", golemSpawner.getDropMultiplier());
                islandDocument = this.setValue(islandDocument, "spawners." + id + ".entityType", golemSpawner.getEntityType().toString());
                id++;
            }

            List<String> suits = new ArrayList<>();
            for (SuitType suit : availableSuits) {
                suits.add(suit.name());
            }
            this.setValue(islandDocument, "suits", suits);

            for (Map.Entry<IslandSettings, Boolean> settings : island.getSettings().entrySet()) {
                this.setValue(islandDocument, "settings." + settings.getKey().name(), settings.getValue());
            }
//
//            id = 1;
//            for (Booster owned : island.getBoosters()) {
//                this.setValue(islandDocument, "ownedBoosters." + id + ".type", owned.getType().name());
//                this.setValue(islandDocument, "ownedBoosters." + id + ".level", owned.getLevel());
//                id += 1;
//            }
//
//            id = 1;
//            for (Booster owned : island.getBoosters()) {
//                this.setValue(islandDocument, "boosters." + id + ".type", owned.getType().name());
//                this.setValue(islandDocument, "boosters." + id + ".level", owned.getLevel());
//                id += 1;
//            }

            for (Booster type : island.getBoosters()) {
                islandDocument = this.setValue(islandDocument, "boosters." + type.getType().name() + ".level", type.getLevel());
            }

            id = 1;
            this.setValue(islandDocument, "minions", null);
            for (Minion minionEntry : island.getLoadedMinions()) {
                if (minionEntry.getMinion() != null && minionEntry.getMinion().isValid() && !minionEntry.getMinion().isDead()) {
                    setValue(islandDocument, "minions." + id + ".location", LocationUtil.getStringFromLocation(minionEntry.getMinion().getLocation()));
                } else {
                    setValue(islandDocument, "minions." + id + ".location", LocationUtil.getStringFromLocation(island.getLocation()));

                }
                setValue(islandDocument, "minions." + id + ".hunger", minionEntry.getHunger());
                setValue(islandDocument, "minions." + id + ".health", minionEntry.getHealth());
                setValue(islandDocument, "minions." + id + ".type", minionEntry.getType().name());
                setValue(islandDocument, "minions." + id + ".name", minionEntry.getName());
                setValue(islandDocument, "minions." + id + ".booster.type", minionEntry.getBoost() == null ? null : minionEntry.getBoost().getType().name());
                setValue(islandDocument, "minions." + id + ".booster.duration", minionEntry.getBoost() == null ? -1 : minionEntry.getBoost().getDuration() - System.currentTimeMillis());
                setValue(islandDocument, "minions." + id + ".tag", minionEntry.getType() == null ? "[Default]" : minionEntry.getMinionTag().getTagText());
                // TODO tags

                switch (minionEntry.getType()) {
                    case MINER:
                        // Save miner data etc
                        setValue(islandDocument, "minions." + id + ".blocksMined", ((Miner) minionEntry).getBlocksMined());
                        break;
                    case BUTCHER:
                        String killableMobs = "";

                        for (EntityType type : ((Butcher) minionEntry).getKillableMobs())
                            killableMobs += type.name() + ":";

                        setValue(islandDocument, "minions." + id + ".radius", ((Butcher) minionEntry).getRadius());
                        setValue(islandDocument, "minions." + id + ".mobsKilled", ((Butcher) minionEntry).getMobsKilled());
                        setValue(islandDocument, "minions." + id + ".killableMobs", killableMobs);
                        break;
                }

                id += 1;
            }

            updateDocument(islandDocument);
            Logger.log("Saved island data.");
        } else {
            if (create) {
                createIsland(island);
            }
        }
    }

    /**
     * Set a specific value in the database to a new value (used for offline updating).
     *
     * @param uuid  - uuid of owner of island.
     * @param key   - key I want to set.
     * @param value - value I want to set.
     */
    public void setKeyToNewValue(UUID uuid, String key, Object value) {
        Document document = search("owner", uuid);
        setValue(document, key, value);
        updateDocument(document);
    }

    /**
     * Get a list of values by key.
     *
     * @param uuid - uuid of island.
     * @param key  - key.
     * @return Object
     */
    public List<Object> getListFromKey(UUID uuid, String key) {
        List<Object> objects = new ArrayList<>();
        Document document = search("owner", uuid);
        List<String> strings = (List<String>) getValue(document, key);
        objects.addAll(strings);
        return objects;
    }
}
