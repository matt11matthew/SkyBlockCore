package net.cloudescape.skyblock.miscellaneous.worldedit;

import com.cloudescape.utilities.itemstack.ItemFactory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerWorldeditManager {

    private Map<UUID, Position> positionCache;

    public PlayerWorldeditManager() {
        this.positionCache = new HashMap<>();
    }

    public void addPlayerPosition(Player  player){
        positionCache.put(player.getUniqueId(), new Position());
    }

    public Position getPlayerPosition(Player player) { return positionCache.get(player.getUniqueId()); }

    public Map<UUID, Position> getPositionCache() {
        return positionCache;
    }

    public ItemStack getWorldEditWand() {
        ItemFactory itemFactory = new ItemFactory(Material.WOOD_AXE);
        itemFactory.setDisplayName("&b&lPWE Wand");
        itemFactory.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        ItemStack stack = itemFactory.build();
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        stack.setItemMeta(meta);
        return stack;
    }

    public int getAmountInInventory(Player player, ItemStack item) {

        int quantity = 0;

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack checkItem = player.getInventory().getItem(slot);

            if (checkItem == null)
                continue;

            if (checkItem.getType() != item.getType())
                continue;

            if (checkItem.getData().getData() != item.getData().getData())
                continue;

            quantity += checkItem.getAmount();
        }

        return quantity;
    }

    public void removeAmountFromInventory(Player player, ItemStack item, int quantity) {

        int quantityLeft = quantity;

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack checkItem = player.getInventory().getItem(slot);

            if (quantityLeft <= 0) {
                break;
            }

            if (checkItem == null)
                continue;
            if (checkItem.getType() != item.getType())
                continue;

            if (checkItem.getData().getData() != item.getData().getData())
                continue;

            int amount = checkItem.getAmount() - quantityLeft;

            if (amount > 0) {
                checkItem.setAmount(amount);
            } else {
                player.getInventory().setItem(slot, null);
            }

            quantityLeft -= (amount + quantityLeft);
            player.updateInventory();
        }
    }
}
