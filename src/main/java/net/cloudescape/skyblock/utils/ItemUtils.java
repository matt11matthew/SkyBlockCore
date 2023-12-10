package net.cloudescape.skyblock.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class ItemUtils {
    public static void dropItemStack(Location location, ItemStack itemStack, int amount) {
        List<ItemStack> drops = new ArrayList<>();
        if (amount <= 64) {
            ItemStack itemStack1 = new ItemStack(itemStack);
            itemStack1.setAmount(amount);
            drops.add(itemStack1);
        } else {
            int stacks = amount / 64;
            int remaining = amount % 64;
            for (int i = 0; i < stacks; i++) {
                ItemStack itemStack1 = new ItemStack(itemStack);
                itemStack1.setAmount(64);
                drops.add(itemStack1);
            }
            if (remaining > 0) {
                ItemStack itemStack1 = new ItemStack(itemStack);
                itemStack1.setAmount(remaining);
                drops.add(itemStack1);
            }
        }
        for (ItemStack drop : drops) {
            location.getWorld().dropItemNaturally(location, drop);
        }
    }

    public static void removeOneHandItemFromPlayer(Player player) {
        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
    }
}
