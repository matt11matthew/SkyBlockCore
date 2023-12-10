package net.cloudescape.skyblock.database.skyblockplayer;

import com.cloudescape.database.wrappers.Wrapper;
import net.cloudescape.skyblock.utils.Logger;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkyblockPlayerWrapper extends Wrapper {

    public static final String COLLECTION_NAME = "skyblock_users";
    public static Map<UUID, SkyBlockPlayer> playerMap;

    public SkyblockPlayerWrapper() {
        playerMap = new ConcurrentHashMap<>();
    }

    public Document getPlayerByUUID(UUID uuid) {
        Document document = search("uniqueId", uuid);

        if (document == null) return null;

        return document;
    }

    public SkyBlockPlayer loadPlayer(Player player) {
        if (playerMap.containsKey(player.getUniqueId())) {
            return playerMap.get(player.getUniqueId());
        }

        Document document = getPlayerByUUID(player.getUniqueId());

        if (document == null) {
            Logger.log("Container was null for " + player.getName());
            savePlayer(player);

            return loadPlayer(player);
        }


//        for (Map.Entry<String, Object> data : document.entrySet()) {
//            switch (data.getKey()) {
//                case "quests":
//                    for (Map.Entry<String, Object> questData : ((Document) data.getValue()).entrySet()) {
//                        int questOrdinal = Integer.parseInt(questData.getKey());
//                        Quest quest = QuestType.getTypeByOridinal(questOrdinal).getQuestInstance(player);
//                        if (quest == null) continue;
//                        int stage = ((int) getValue(document, "quests." + questOrdinal + ".currentStage"));
//                        quest.setCurrentQuestPart(stage);
//                        quest.getQuestParts().forEach(part -> part.spawnEntity(player));
//                        CloudSkyblock.getPlugin().getQuestManager().setCurrentQuest(player, quest);
//                        Logger.log("Added new active quest: " + quest.getQuestName());
//                    }
//                    break;
//                case "finishedQuests":
//                    for (Map.Entry<String, Object> questData : ((Document) data.getValue()).entrySet()) {
//                        int questOrdinal = Integer.parseInt(questData.getKey());
//                        Quest quest = QuestType.getTypeByOridinal(questOrdinal).getQuestInstance(player);
//                        if (quest == null) continue;
//                        CloudSkyblock.getPlugin().getQuestManager().addCompletedQuest(player, quest);
//                        Logger.log("Added new finished quest: " + quest.getQuestName());
//                    }
//                    break;
//            }
//        }

        Boolean fly = (Boolean) getValue(document, "fly.active");
        Long time = (Long) getValue(document, "fly.time");
        SkyBlockPlayer skyBlockPlayer = new SkyBlockPlayer(player.getUniqueId(), (UUID) getValue(document, "island"), fly, time);
        if (playerMap == null) {
            playerMap = new ConcurrentHashMap<>();
        }
        playerMap.put(player.getUniqueId(), skyBlockPlayer);
        return skyBlockPlayer;

        // TODO load rank

//        for (Quest quest : container.getActiveQuests()) {
//            quest.getQuestParts().forEach(questPart -> questPart.spawnEntity(player));
//            CloudSkyblock.getPlugin().getQuestManager().setCurrentQuest(player, quest);
//            Logger.log("Added a new active quest to " + player.getName() + " - " + quest.getQuestName());
//        }
//
//        for (Quest quest : container.getCompletedQuests()) {
//            CloudSkyblock.getPlugin().getQuestManager().getCompletedQuests().get(player.getUniqueId()).add(quest);
//            Logger.log("Added a new completed quest to " + player.getName() + " - " + quest.getQuestName());
//        }
    }

    /**
     * Save a players quest stats.
     *
     * @param player
     */
    public void savePlayer(Player player) {

        if (playerMap.containsKey(player.getUniqueId())) {
            Document playerDocument = search("uniqueId", player.getUniqueId());

            if (playerDocument != null) {

                // TODO rank

//                for (Quest activeQuest : (CloudSkyblock.getPlugin().getQuestManager().getCurrentQuests().get(player.getUniqueId()) != null ? CloudSkyblock.getPlugin().getQuestManager().getCurrentQuests().get(player.getUniqueId()) : new ArrayList<Quest>())) {
//                    setValue(playerDocument, "quests." + activeQuest.getType().ordinal() + ".currentStage", activeQuest.getCurrentQuestPart());
//                    Logger.log("Set a new active quest: " + activeQuest.getQuestName());
//                }
//
//                for (Quest completedQuests : (CloudSkyblock.getPlugin().getQuestManager().getCompletedQuests().get(player.getUniqueId()) != null ? CloudSkyblock.getPlugin().getQuestManager().getCompletedQuests().get(player.getUniqueId()) : new ArrayList<Quest>())) {
//                    setValue(playerDocument, "finishedQuests." + completedQuests.getType().ordinal(), true);
//                    Logger.log("Set a new finished quest: " + completedQuests.getQuestName());
//                }

                SkyBlockPlayer skyBlockPlayer = playerMap.get(player.getUniqueId());
//                String[] strings = InventorySerialization.playerInventoryToBase64(player.getInventory());
//                setValue(playerDocument, "inventory.content", strings[0]);
//                setValue(playerDocument, "inventory.armor", strings[1]);
                setValue(playerDocument, "island", skyBlockPlayer.getIslandUuid());
                setValue(playerDocument, "fly.active", skyBlockPlayer.isActive());
                setValue(playerDocument, "fly.time", skyBlockPlayer.getFlyTime());
                updateDocument(playerDocument);

                Logger.log("Saved Skyblock player data.");
                playerMap.remove(player.getUniqueId());
            }
        } else {
            Logger.log("DEBUG`3");
            createDocument("uniqueId", player.getUniqueId());

            SkyBlockPlayer skyBlockPlayer = new SkyBlockPlayer(player.getUniqueId(), null, false, 0L);

            setBalance(player.getUniqueId(),500.0D);
            playerMap.put(player.getUniqueId(), skyBlockPlayer);
            savePlayer(player);
        }
    }

    public UUID getIslandUuid(UUID uuid) {
        if (playerMap.containsKey(uuid)) {
            return playerMap.get(uuid).getIslandUuid();
        }
        Document playerDocument = search("uniqueId", uuid);
        if (playerDocument != null) {
            return (UUID) getValue(playerDocument, "island");
        }
        return null;

    }

    public boolean hasIsland(UUID uuid) {
        return getIslandUuid(uuid) != null;
    }


    public  void setBalance(UUID uuid, double balance) {
        Document uniqueId = search("uniqueId", uuid);
        if (uniqueId != null) {
            setValue(uniqueId, "balance", balance);
           updateDocument(uniqueId);
        }
    }
    //
//    public double getBalance() { return (double) this.getValue("balance", 0.0); }

    public  double getBalance(UUID uuid) {
        Document uniqueId = search("uniqueId", uuid);
        if (uniqueId != null) {
            Object balance =getValue(uniqueId, "balance");
            if (balance != null) {
                return (double) balance;
            }
        }
        return 0.0D;
    }

    public void setIslandUuid(UUID uuid, UUID islandUuid) {

        if (playerMap.containsKey(uuid)) {
            playerMap.get(uuid).setIslandUuid(islandUuid);
        } else {
            Document playerDocument = search("uniqueId", uuid);

            if (playerDocument != null) {
                setValue(playerDocument, "island", islandUuid);
                updateDocument(playerDocument);
            }
        }
    }

    public SkyBlockPlayer getSkyblockPlayer(UUID uuid) {
        return playerMap.get(uuid);
    }
}
