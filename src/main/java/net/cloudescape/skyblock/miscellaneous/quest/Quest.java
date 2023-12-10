package net.cloudescape.skyblock.miscellaneous.quest;

import com.cloudescape.utilities.countdown.executors.Executor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quest {

    private Player player;

    private QuestType type;
    private String questName;
    private List<QuestPart> questParts;

    private Executor finishedExecutor;

    private int currentQuestPart;

    public Quest(QuestType type, String questName, Player player) {
        this.type = type;
        this.questName = questName;
        this.player = player;
        this.questParts = new ArrayList<>();
        this.currentQuestPart = -1;
        loadQuestParts();
    }

    public QuestType getType() {
        return type;
    }

    public String getQuestName() {
        return questName;
    }

    public Player getPlayer() {
        return player;
    }

    public void addQuestPart(QuestPart part) { questParts.add(part); }

    public QuestPart getQuestPart(int index) { return questParts.get((index)); }

    public List<QuestPart> getQuestParts() {
        return questParts;
    }

    public void setCurrentQuestPart(int currentQuestPart) {
        this.currentQuestPart = currentQuestPart;
    }

    public int getNextQuestPartIndex() {
        return getCurrentQuestPart() + 1;
    }

    public int getCurrentQuestPart() {
        return currentQuestPart;
    }

    public void setFinishedExecutor(Executor finishedExecutor) {
        this.finishedExecutor = finishedExecutor;
    }

    public Executor getFinishedExecutor() {
        return finishedExecutor;
    }

    public void loadQuestParts() {}
}
