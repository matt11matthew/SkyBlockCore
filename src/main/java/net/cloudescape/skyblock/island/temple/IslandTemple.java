package net.cloudescape.skyblock.island.temple;

import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;

import java.util.UUID;

/**
 * Created by Matthew E on 4/12/2018.
 */
public class IslandTemple {
    private UUID islandUuid;
    private Vector min;
    private Vector max;
    private World world;
    private Location npcLocation;
    private BoosterTemple boosterTemple;
    /**
     * Villager Object
     */
    private Villager villager;

    /**
     * @param islandUuid
     * @param min
     * @param max
     * @param world
     * @param npcLocation
     * @param boosterTemple
     */
    public IslandTemple(UUID islandUuid, Vector min, Vector max, World world, Location npcLocation, BoosterTemple boosterTemple) {
        this.islandUuid = islandUuid;
        this.min = min;
        this.max = max;
        this.world = world;
        this.npcLocation = npcLocation;
        this.boosterTemple = boosterTemple;
    }

    /**
     * @return
     */
    public UUID getIslandUuid() {
        return islandUuid;
    }

    /**
     * @return
     */
    public Vector getMin() {
        return min;
    }

    /**
     * @return
     */
    public Vector getMax() {
        return max;
    }

    /**
     * @return
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return
     */
    public Location getNpcLocation() {
        return npcLocation;
    }

    /**
     * @return
     */
    public BoosterTemple getBoosterTemple() {
        return boosterTemple;
    }

    /**
     * @return
     */
    public Villager getVillager() {
        return villager;
    }

    /**
     * ;     *
     *
     * @throws MaxChangedBlocksException
     */
    public void delete() throws MaxChangedBlocksException {
        if (this.villager != null) {
            this.villager.remove();
            this.villager = null;
        }
        EditSession editSession = new EditSessionBuilder(new BukkitWorld(world)).fastmode(true).build();
        editSession.setBlock(min, new BaseBlock(0, 0));
        editSession.flushQueue();
    }

    /**
     *
     */
    public void paste() {

        Schematic schematic = boosterTemple.loadSchematic();
        EditSession paste = schematic.paste(new BukkitWorld(world), min, false, false, null);
        paste.addNotifyTask(() -> {

        });
    }
}
