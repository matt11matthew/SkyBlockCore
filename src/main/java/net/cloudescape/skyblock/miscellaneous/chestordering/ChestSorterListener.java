package net.cloudescape.skyblock.miscellaneous.chestordering;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.CustomChatMessage;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.backend.commons.rank.GlobalRank;
import net.cloudescape.backend.commons.rank.GlobalRankManager;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChestSorterListener implements Listener {

    private final List<Material> UNBLOCKED_ITEMS = Arrays.asList(Material.HOPPER);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null && block.getType() == Material.CHEST && (block.getState() instanceof Chest) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Chest chest = ((Chest) block.getState());

            if (player.isSneaking()) {

                if (UNBLOCKED_ITEMS.contains(player.getInventory().getItemInMainHand().getType())) return;

                BackendModule backendModule = CloudCore.getModuleManager().getModule(BackendModule.class);
                CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
                GlobalRankManager globalRankManager = backendModule.getGlobalRankManager();
                GlobalRank rank = globalRankManager.getGlobalRank(cloudEscapePlayer.getGlobalRank());

                if (rank.getValue() < 70) {
                    CustomChatMessage.sendMessage(player, "Chest Sorter", "Please purchase a rank at &ahttps://store.skyheroes.org/ &7to unlock chest sorting.");
                    return;
                }

                Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

                if (islandOptional.isPresent()) {

                    Island island = islandOptional.get();

                    if (!island.isIslandMember(player.getUniqueId())) {
                        CustomChatMessage.sendMessage(player, "Chest Sorter", "You need to be a member of an island to sort their chest.");
                        return;
                    }

                    new ChestSortConfirmationGui(player, chest);
                    event.setCancelled(true);
                }
            }
        }
    }
}
