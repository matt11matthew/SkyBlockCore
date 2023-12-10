package net.cloudescape.skyblock.miscellaneous.minions.suit;

import net.cloudescape.skyblock.miscellaneous.minions.Minion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public abstract class Suit {
    private Minion minion;
    private SuitType suitType;

    public Suit(Minion minion, SuitType type){
        this.minion = minion;
        this.suitType = type;
    }

    public void setHealmet(ItemStack helmet) {
        minion.getMinion().setHelmet(helmet);
    }

    public void setHealmet(Color color) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color);
        helmet.setItemMeta(meta);
        minion.getMinion().setHelmet(helmet);
    }

    public void setChestplate(Color color) {
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(color);
        chestplate.setItemMeta(meta);
        minion.getMinion().setChestplate(chestplate);
    }

    public void setLeggings(Color color) {
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(color);
        leggings.setItemMeta(meta);
        minion.getMinion().setLeggings(leggings);
    }

    public void setBoots(Color color) {
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(color);
        boots.setItemMeta(meta);
        minion.getMinion().setBoots(boots);
    }

    public SuitType getSuitType() {
        return suitType;
    }
}
