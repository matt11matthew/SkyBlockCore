package net.cloudescape.skyblock.miscellaneous.chestordering;

import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class ChestSortingComparator implements Comparator<ItemStack> {

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        System.out.println(o1.getType().name() + " compared to " + o2.getType().name() + " = " + o1.getType().name().compareTo(o2.getType().name()));
        return o1.getType().name().compareTo(o2.getType().name());
    }
}
