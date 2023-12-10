package net.cloudescape.skyblock.island.gui.island.controls.members;

import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandRank;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

public class IslandCPRankGui extends MenuFactory {

    public IslandCPRankGui(Player player, Player target, Island island) {

        super("Island Ranks", 3);

        int slot = 10;
        IslandRank currentRank = island.getIslandMembers().get(target.getUniqueId());
        for (IslandRank rank : IslandRank.values()) {
            ItemFactory itemFactory = new ItemFactory(Material.STAINED_GLASS, 1, (byte) 4).setDisplayName("&a")
                    .setLore(
                            StringUtil.getMenuLine(),
                            "&7Click to set " + target.getName(),
                            "&7to " + StringUtil.capitalizeFirstLetter(rank.name()),
                            StringUtil.getMenuLine()
                    );

            if (currentRank.ordinal() == rank.ordinal()) {
                itemFactory.enchant(Enchantment.DURABILITY);
                itemFactory.addItemFlag(ItemFlag.HIDE_ENCHANTS);
            } else if (rank.ordinal() >= island.getIslandMembers().get(player.getUniqueId()).ordinal()) {
                itemFactory = new ItemFactory(Material.STAINED_GLASS, 1, (byte) 14).setDisplayName("&cUNAVAILABLE")
                .setLore(
                        StringUtil.getMenuLine(),
                        "&7You are not a high enough rank to",
                        "&7Access these ranks! :(",
                        StringUtil.getMenuLine()
                );
            }

            addItem(new MenuItem(slot, itemFactory.build()) {

                @Override
                public void click(Player player, ClickType clickType) {
                    // TODO set their rank
                    if (rank.ordinal() >= island.getIslandMembers().get(player.getUniqueId()).ordinal()) {
                        CustomChatMessage.sendMessage(player, "Skyblock", "You do not have permission to access these ranks.");
                        return;
                    }
                    island.addIslandMember(target.getUniqueId(), rank,false);
                    CustomChatMessage.sendMessage(player, "Skyblock", target.getName() + "'s rank was updated.");
                }
            });
            slot += 2;
        }

        openInventory(player);
    }
}
