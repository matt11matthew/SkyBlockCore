package net.cloudescape.skyblock.miscellaneous.quest.listener;

import com.cloudescape.utilities.CloudUtils;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.database.skyblockplayer.SkyBlockPlayer;
import net.cloudescape.skyblock.miscellaneous.quest.Quest;
import net.cloudescape.skyblock.miscellaneous.quest.QuestPart;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class QuestCentralListener implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SkyBlockPlayer skyBlockPlayer = CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().loadPlayer(player);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
       final Player player = event.getPlayer();
        CloudSkyblock.getPlugin().getSkyblockPlayerWrapper().savePlayer(player);
        CloudSkyblock.getPlugin().getQuestManager().wipeQuests(player);

        CloudSkyblock.getPlugin().getQuestManager().getCurrentQuests().remove(player.getUniqueId());
        CloudSkyblock.getPlugin().getQuestManager().getCompletedQuests().remove(player.getUniqueId());
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.hasMetadata("questPartBase")) {

            Quest entityQuest = (Quest) entity.getMetadata("questPartBase").get(0).value();
            event.setCancelled(true);

            if (!CloudSkyblock.getPlugin().getQuestManager().isActiveQuest(player, entityQuest.getType())) {
                CustomChatMessage.sendMessage(player, entity.getCustomName(), "How bout no.. :'D");
                return;
            }

            Quest quest = CloudSkyblock.getPlugin().getQuestManager().getCurrentQuestByType(player, entityQuest.getType());
            int partId = entity.getMetadata("questPartId").get(0).asInt() - 1;

            // TODO work on id stuff when I wake up to only allow you to click the required id.
            if (quest.getNextQuestPartIndex() >= (quest.getQuestParts().size() - 1)) {
                // Finished quest! Save to database etc that is completed.
                quest.getQuestParts().forEach(QuestPart::removeEntity);
                CloudSkyblock.getPlugin().getQuestManager().removeActiveQuest(player, quest.getType());
                CloudSkyblock.getPlugin().getQuestManager().addCompletedQuest(player, quest);
                CustomChatMessage.sendMessage(player, "Skyblock Quests", "You have successfully completed " + quest.getQuestName());
                quest.getFinishedExecutor().run();
                return;
            }

            if (partId == entityQuest.getNextQuestPartIndex()) {

                if (entityQuest.getQuestPart(partId).getRequiredToContinue() != null && !hasItem(player, entityQuest.getQuestPart(partId).getRequiredToContinue())) {
                    ItemStack required = entityQuest.getQuestPart(partId).getRequiredToContinue();
                    CustomChatMessage.sendMessage(player, "Skyblock Quests", "You do not have the required item needed to continue! You require " + CloudUtils.setUppercaseEachStart(required.getType().name().replace("_", " ")) + " x" + required.getAmount());
                    return;
                }

                if (entityQuest.getQuestPart(partId).getRequiredToContinue() != null) {
                    player.getInventory().getItemInMainHand().setType(Material.AIR);
                }

                quest.setCurrentQuestPart(quest.getNextQuestPartIndex());

                for (String dialogue : quest.getQuestPart(partId).getDescription()) {
                    CustomChatMessage.sendMessage(player, "&d" + quest.getQuestPart(partId).getPartName(), dialogue);
                }
                if (quest.getQuestPart(partId).getExecutor() != null) quest.getQuestPart(partId).getExecutor().run();
            } else {
                // If they shouldn't be interacting with this stage yet!
                CustomChatMessage.sendMessage(player, "&d" + quest.getQuestPart(partId).getPartName(), "Hey there! How are you? :)");
            }
        }
    }

    @EventHandler
    public void onVillagerOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            boolean disabled = false;
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals("&c&lDO NOT OPEN")) {
                    disabled = true;
                    break;
                }
            }

            if (disabled) {
                event.setCancelled(true);
            }
        }
    }

    private boolean hasItem(Player player, ItemStack stack) {
        if (player.getInventory().getItemInMainHand().isSimilar(stack)) {
            return true;
        }
        return false;
    }
}
