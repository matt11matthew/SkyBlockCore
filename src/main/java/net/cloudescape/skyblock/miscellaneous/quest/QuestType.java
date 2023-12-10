package net.cloudescape.skyblock.miscellaneous.quest;

import net.cloudescape.skyblock.miscellaneous.quest.quests.TutorialQuest;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public enum QuestType {

    TUTORIAL("Tutorial Quest", Material.EMERALD, false, TutorialQuest.class);

    private String questName;
    private Material displayIcon;
    private boolean isActiveQuest;
    private Class<? extends Quest> questClass;

    QuestType(String questName, Material displayIcon, boolean isActiveQuest, Class<? extends Quest> questClass) {
        this.questName = questName;
        this.displayIcon = displayIcon;
        this.isActiveQuest = isActiveQuest;
        this.questClass = questClass;
    }

    public String getQuestName() {
        return questName;
    }

    public Material getDisplayIcon() {
        return displayIcon;
    }

    public boolean isActiveQuest() {
        return isActiveQuest;
    }

    public Class<? extends Quest> getQuestClass() {
        return questClass;
    }

    public static QuestType getTypeByOridinal(int oridinal) {
        for (QuestType values : QuestType.values()) {
            if (values.ordinal() == oridinal) {
                return values;
            }
        }
        return null;
    }

    public Quest getQuestInstance(Player player) {
        try {
            return questClass.getConstructor(QuestType.class, String.class, Player.class).newInstance(this, questName, player);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
