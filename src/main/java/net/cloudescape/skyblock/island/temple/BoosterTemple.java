package net.cloudescape.skyblock.island.temple;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import net.cloudescape.skyblock.CloudSkyblock;

import java.io.File;
import java.io.IOException;

public enum BoosterTemple {

    DEFAULT(1, "defaulttemple"),
    DEFAULT_TIER_1(2, "t1"),;

    /**
     * ID for the temple. 1 - 10 etc.
     */
    private int templeId;
    /**
     * Schematic name.
     */
    private String templeSchematicName;

    BoosterTemple(int templeId, String templeSchematicName) {
        this.templeId = templeId;
        this.templeSchematicName = templeSchematicName;
    }

    public com.boydti.fawe.object.schematic.Schematic loadSchematic() {
        TempleManager templeManager = CloudSkyblock.getTempleManager();
        if (templeManager.getTempleSchematicMap().containsKey(this)) {
            return templeManager.getTempleSchematicMap().get(this);
        }
        if (CloudSkyblock.getSchematicManager().getFileSchematicMap().containsKey("defaulttemplate")) {
            return CloudSkyblock.getSchematicManager().getFileSchematicMap().get("defaulttemplate");
        }
        File file = new File(CloudSkyblock.getPlugin().getDataFolder() + "/temples/");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(CloudSkyblock.getPlugin().getDataFolder() + "/temples/", templeSchematicName + ".schematic");
        if (file1.exists()) {
            try {
                return ClipboardFormat.SCHEMATIC.load(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Get the temples unique ID.
     *
     * @return Temple ID.
     */
    public int getTempleId() {
        return templeId;
    }

    /**
     * Get the temples schematic name.
     *
     * @return Schematic name.
     */
    public String getTempleSchematicName() {
        return templeSchematicName;
    }
}
