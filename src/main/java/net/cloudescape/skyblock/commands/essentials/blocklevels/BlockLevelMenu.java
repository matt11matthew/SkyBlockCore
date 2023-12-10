package net.cloudescape.skyblock.commands.essentials.blocklevels;

import com.cloudescape.utilities.gui.MenuFactory;
import com.cloudescape.utilities.gui.MenuItem;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.island.IslandManager;
import net.cloudescape.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class BlockLevelMenu extends MenuFactory {

    private final List<Material> LEVELLED_BLOCKS = Arrays.asList(
            Material.EMERALD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK,
            Material.IRON_BLOCK,
            Material.COAL_BLOCK,
            Material.LAPIS_BLOCK,
            Material.REDSTONE_BLOCK,
            Material.BEACON,
            Material.OBSIDIAN,
            Material.ENDER_CHEST,
            Material.ENCHANTMENT_TABLE,
            Material.HOPPER
    );

    public BlockLevelMenu(Player player) {

        super("Island level block values", 6);

        int slot = 9;

        for (int i = 0; i < getInventory().getSize(); i++) {
            addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, 1, (byte) 7)
                    .setDisplayName(" ")
                    .build()));
        }

        addItem(new MenuItem(4, new ItemFactory(Material.PAPER)
            .setDisplayName("&c&lBlock values")
            .build()));

        for (Material material : LEVELLED_BLOCKS) {
            addItem(new MenuItem(slot, new ItemFactory(material)
                    .setDisplayName("&b&l" + StringUtil.capitalizeFirstLetter(material.name().replace("_", "")))
                    .setLore(
                            "&7The value that a block of &b" + StringUtil.capitalizeFirstLetter(material.name().replace("_", "")) + " &7increases",
                            "&7Your islands level by is",
                            "",
                            "&8• &b" + IslandManager.getIslandBlockLevelValues(material) + " &7per block."
                            ).build()));
            slot += 1;
        }

        openInventory(player);
    }
}
