package net.cloudescape.skyblock.miscellaneous.quest.gui;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.quest.Quest;
import net.cloudescape.skyblock.miscellaneous.quest.QuestPart;
import net.cloudescape.skyblock.miscellaneous.quest.QuestType;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class QuestViewGui extends MenuFactory {

    public QuestViewGui(Player player) {

        super("Available Quests", 6);

        int slot = 0;
        for (QuestType type : QuestType.values()) {
            if (!type.isActiveQuest()) continue;
            Quest quest = type.getQuestInstance(player);
            addItem(new MenuItem(slot, new ItemFactory(type.getDisplayIcon())
                    .setDisplayName("&b" + type.getQuestName())
                    .setLore(
                            StringUtil.getMenuLine(),
                            "           &7Click to start this Quest!",
                            "            &7This quest has " + quest.getQuestParts().size() + " " + (quest.getQuestParts().size() != 1 ? "parts" : "part") + "!",
                            "   &7You have" + (CloudSkyblock.getPlugin().getQuestManager().hasFinishedQuest(player, quest) ? " " : " &cnot &7") + "already finished this quest!",
                            StringUtil.getMenuLine()
                    )
                    .build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    // TODO start quest

                    if (CloudSkyblock.getPlugin().getQuestManager().isActiveQuest(player, type)) {
                        CustomChatMessage.sendMessage(player, "Skyblock Quests", "You have already started this quest!");
                        return;
                    }

                    if (CloudSkyblock.getPlugin().getQuestManager().hasFinishedQuest(player, quest)) {
                        CustomChatMessage.sendMessage(player, "Skyblock Quests", "You have already completed this quest!");
                        return;
                    }

                    for (QuestPart part : quest.getQuestParts()) {
                        part.spawnEntity(player);
                    }

                    CloudSkyblock.getPlugin().getQuestManager().setCurrentQuest(player, quest);
                    CustomChatMessage.sendMessage(player, "Skyblock Quests", "Started a new Quest! " + quest.getQuestName());
                }
            });

            slot += 1;
        }
        openInventory(player);
    }
}
