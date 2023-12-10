package net.cloudescape.skyblock.island.temple;

import com.cloudescape.utilities.gui.MenuFactory;
import net.cloudescape.skyblock.island.Island;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class PlayerVaultGui extends MenuFactory {
    public PlayerVaultGui(Player player, Island island) {
        super("Player Vault Master", 3);

        openInventory(player);
    }
}
