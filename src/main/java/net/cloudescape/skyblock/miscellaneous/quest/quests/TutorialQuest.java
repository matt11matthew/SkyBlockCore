package net.cloudescape.skyblock.miscellaneous.quest.quests;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.miscellaneous.quest.Quest;
import net.cloudescape.skyblock.miscellaneous.quest.QuestPart;
import net.cloudescape.skyblock.miscellaneous.quest.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TutorialQuest extends Quest {

    /*
     * Set questnames in QuestType..
     */
    public TutorialQuest(QuestType type, String questName, Player player) {
        super(type, questName, player);

        setFinishedExecutor(() -> {
            ItemStack[] rewards = new ItemStack[] {
                    new ItemFactory(Material.GRASS).setAmount(120).build(),
                    new ItemFactory(Material.STONE).setAmount(120).build(),
                    new ItemFactory(Material.WOOD).setAmount(120).build(),
                    new ItemFactory(Material.LOG, 64, (byte) 2).build(),
                    new ItemFactory(Material.LOG, 64, (byte) 3).build()
            };

            for (ItemStack reward : rewards) {
                player.getInventory().addItem(reward);
            }

            CustomChatMessage.sendMessage(player, "Skyblock Quests", "Congratulations! You finished the tutorial quest.");
        });
    }

    @Override
    public void loadQuestParts() {
        Player player = getPlayer();
        addQuestPart(new QuestPart(this, 1, "&dBobby", new String[] {
                "Hey there dude! Welcome to the server.",
                "To begin with, at the end of this tutorial you will",
                "Be given a welcome reward for joining the server. :)",
                "There are a number of support agents along this tutorial",
                "That will help you get to know our Skyblock plugin!",
                "Turn around and you'll see your first guide."
        }, new Location(Bukkit.getWorld("world"), 8, 148, -5, 0, 1), EntityType.VILLAGER, () -> {
            player.teleport(new Location(Bukkit.getWorld("world"), 43, 145, 26, -88, -6));
        }));

        addQuestPart(new QuestPart(this, 2, "&aSarah the island genie", new String[]{
                "Oh hey! You must be " + player.getName() + ".",
                "Welcome from the whole team here at Skyheroes.",
                "To create a new island you can do /is. This will open a gui",
                "Where you will select your island template and begin your adventure!"
        }, new Location(Bukkit.getWorld("world"), 46, 146, 26, 90, 3), EntityType.VILLAGER, () -> {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }));

        addQuestPart(new QuestPart(this, 3, "&bJames the salesman", new String[]{
                "On your island you have access to a number of tools to enhance your experience,",
                "This sign you see will allow you to create saleable items on your island, if you place it onfront of a chest.",
                "This allows for the best player experience on your island by letting players buy and sell items on your island.",
                "You can also replace [Sell] with [Buy] and it will allow players to buy your items for the amount you specify!"
        }, new Location(Bukkit.getWorld("world"), 14, 148, -5, 28, 1), EntityType.VILLAGER, () -> {
            player.teleport(new Location(Bukkit.getWorld("world"), 18, 144, 22, 134, 27));
        }));

        addQuestPart(new QuestPart(this, 4, "&7ChronicNinjaz", new String[]{
                "We allow for multiple server connections here at Skyheroes for the best player",
                "Experience with minimal lag. Servers are all connected via chat and tab so you will",
                "Never have issues with playing with friends!"
        }, new Location(Bukkit.getWorld("world"), 16, 143, 20, -44, -3), EntityType.VILLAGER, () -> {
            player.teleport(new Location(Bukkit.getWorld("world"), 8, 147, -33, 180, 0));
        }));

        addQuestPart(new QuestPart(this, 5, "&7MatthewE", new String[]{
                "Now go and have fun! As Sarah said, you can start an island by doing /is or /island",
                "Anymore queries? Contact a moderator+. :)"
        },  new Location(Bukkit.getWorld("world"), 8, 147, -42, 0, 1), EntityType.VILLAGER, () -> {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }));
    }
}
