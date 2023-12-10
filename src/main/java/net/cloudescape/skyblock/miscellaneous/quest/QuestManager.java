package net.cloudescape.skyblock.miscellaneous.quest;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.quest.listener.QuestCentralListener;
import net.cloudescape.skyblock.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestManager {

    private Map<UUID, List<Quest>> completedQuests;
    private Map<UUID, List<Quest>> currentQuests;

    public QuestManager() {
        completedQuests = new HashMap<>();
        currentQuests = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new QuestCentralListener(), CloudSkyblock.getPlugin());
    }

    public void addCompletedQuest(Player player, Quest quest) {
        List<Quest> completed = (completedQuests.get(player.getUniqueId()) == null ? new ArrayList<>() : completedQuests.get(player.getUniqueId()));
        completed.add(quest);
        completedQuests.put(player.getUniqueId(), completed);
        Logger.log("New completed quest added to " + player.getName() + ": " + quest.getQuestName());
    }

    public void setCurrentQuest(Player player, Quest quest) {
        List<Quest> quests = (currentQuests.get(player.getUniqueId()) == null ? new ArrayList<>() : currentQuests.get(player.getUniqueId()));
        quests.add(quest);
        currentQuests.put(player.getUniqueId(), quests);
    }

    public List<Quest> getCurrentQuest(Player player) { return currentQuests.get(player.getUniqueId()); }

    public Quest getCurrentQuestByType(Player player, QuestType type) {
        for (Quest quest : (currentQuests.get(player.getUniqueId()) == null ? new ArrayList<Quest>() : currentQuests.get(player.getUniqueId()))) {
            if (quest.getType() == type) {
                return quest;
            }
        }
        return null;
    }

    public boolean isActiveQuest(Player player, QuestType type) {
        for (Quest quest : (currentQuests.get(player.getUniqueId()) == null ? new ArrayList<Quest>() : currentQuests.get(player.getUniqueId()))) {
            if (quest.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public void removeActiveQuest(Player player, QuestType type) {
        Quest remove = null;
        for (Quest quest : (currentQuests.get(player.getUniqueId()) == null ? new ArrayList<Quest>() : currentQuests.get(player.getUniqueId()))) {
            if (quest.getType() == type) {
                remove = quest;
                break;
            }
        }

        if (remove != null) {
            currentQuests.get(player.getUniqueId()).remove(remove);
        }
    }

    public boolean hasFinishedQuest(Player player, Quest quest) {
        if (getCompletedQuests().get(player.getUniqueId()) == null) {
            return false;
        }

        for (Quest finished : getCompletedQuests().get(player.getUniqueId())) {
            if (finished.getQuestName().equals(quest.getQuestName())) {
                return true;
            }
        }
        return false;
    }

    public void wipeQuests(Player player) {
        if (!currentQuests.containsKey(player.getUniqueId())) return;
        currentQuests.get(player.getUniqueId()).forEach(quest -> {
            quest.getQuestParts().forEach(QuestPart::removeEntity);
        });
        currentQuests.get(player.getUniqueId()).clear();
        currentQuests.remove(player.getUniqueId());
    }

    public Map<UUID, List<Quest>> getCompletedQuests() {
        return completedQuests;
    }

    public Map<UUID, List<Quest>> getCurrentQuests() {
        return currentQuests;
    }
}
