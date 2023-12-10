package net.cloudescape.skyblock.miscellaneous.worldedit.listener;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.CustomChatMessage;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.backend.client.CloudEscapeClientPlugin;
import net.cloudescape.backend.commons.player.CloudEscapePlayer;
import net.cloudescape.backend.commons.rank.GlobalRankManager;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.miscellaneous.worldedit.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PWEListener implements Listener {

    @EventHandler
    public void onIntract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (stack == null) return;

        if (!stack.isSimilar(getWandStack())) return;

        Block block = event.getClickedBlock();

        if (CloudSkyblock.getPlugin().getPlayerWorldeditManager().getPlayerPosition(player) == null) {
            CloudSkyblock.getPlugin().getPlayerWorldeditManager().addPlayerPosition(player);
        }

        CloudEscapePlayer cloudEscapePlayer = CloudEscapeClientPlugin.getInstance().getCloudEscapeClient().getCloudEscapePlayer(player.getUniqueId());
        GlobalRankManager manager = CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager();

        if (manager.getGlobalRank(cloudEscapePlayer.getGlobalRank()).getValue() < 70) {
            CustomChatMessage.sendMessage(player, "PWE", "You need to be a donor to use PWE!");
            return;
        }

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (!islandOptional.isPresent()) {
            CustomChatMessage.sendMessage(player, "PWE", "You can only worldedit on islands!");
            return;
        }

        Island island = islandOptional.get();

        if (!island.isIslandMember(player.getUniqueId())) {
            CustomChatMessage.sendMessage(player, "PWE", "You can only worldedit on islands you're a member of!");
            return;
        }

        Position position = CloudSkyblock.getPlugin().getPlayerWorldeditManager().getPlayerPosition(player);
        int x = block.getLocation().getBlockX(), y = block.getLocation().getBlockY(), z = block.getLocation().getBlockZ();

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                // Position 1
                position.setPosition1(new Location(player.getWorld(), x, y, z));
                CustomChatMessage.sendMessage(player, "PWE", "Position 1 set @ " + x + ", " + y + ", " + z);
                break;
            case RIGHT_CLICK_BLOCK:
                // Position 2
                position.setPosition2(new Location(player.getWorld(), x, y, z));
                CustomChatMessage.sendMessage(player, "PWE", "Position 2 set @ " + x + ", " + y + ", " + z);
                break;
        }
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        CloudSkyblock.getPlugin().getPlayerWorldeditManager().addPlayerPosition(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CloudSkyblock.getPlugin().getPlayerWorldeditManager().getPositionCache().remove(player.getUniqueId());
    }

    public static ItemStack getWandStack() {
        ItemFactory factory = new ItemFactory(Material.WOOD_AXE);
        factory.setDisplayName("&b&lPWE Wand");
        factory.setUnbreakable(true);
        factory.addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        return factory.build();
    }
}
