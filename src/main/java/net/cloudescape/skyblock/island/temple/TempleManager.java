package net.cloudescape.skyblock.island.temple;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import net.cloudescape.skyblock.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew E on 4/9/2018.
 */
public class TempleManager {
    private Map<BoosterTemple, Schematic> templeSchematicMap;

    public TempleManager() {
        this.templeSchematicMap = new ConcurrentHashMap<>();
        Logger.log("====================================================");
        Logger.log("Loading schematics");
        for (BoosterTemple boosterTemple : BoosterTemple.values()) {
            try {
                Schematic schematic = boosterTemple.loadSchematic();
                if (schematic == null) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            this.templeSchematicMap.put(boosterTemple, boosterTemple.loadSchematic());
            Logger.log("Loaded schematic " + boosterTemple.getTempleSchematicName() + ".schematic");
        }
        Logger.log("====================================================");

    }

    public Map<BoosterTemple, Schematic> getTempleSchematicMap() {
        return templeSchematicMap;
    }

    public static void test(Player player) throws IncompleteRegionException {
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        LocalSession session = worldEditPlugin.getSession(player);
        Region selection = session.getSelection(new BukkitWorld(player.getWorld()));
        Location location = player.getLocation();
        IslandTemple islandTemple = new IslandTemple(UUID.randomUUID(), selection.getMinimumPoint(), selection.getMaximumPoint(), player.getWorld(), location, BoosterTemple.DEFAULT);
        islandTemple.paste();

    }
}
