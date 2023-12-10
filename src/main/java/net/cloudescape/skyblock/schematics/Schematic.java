package net.cloudescape.skyblock.schematics;

import org.bukkit.util.Vector;

public class Schematic {
    /**
     * Schematic name
     */
    private String name;
    /**
     * Data block for each block (thats not air / null) within the region in a (x, y, z) format
     */
    private SchematicBlock[][][] data;
    /**
     * Region of the schematic (storing min & max values)
     */
    private SchematicRegion region;
    /** List of all the entities within the region when copying / loading */
    /**
     * Vectors for the minimum and maximum points of the map. (Should form a cube).
     */
    private Vector maximum;

    private Vector minimum;

    /**
     * Build a new schematic.
     *
     * @param name
     * @param region - region boundaries.
     */
    public Schematic(String name, SchematicRegion region) {
        this.name = name;
        this.region = region;

        this.maximum = this.region.getMaximumPosition();
        this.minimum = this.region.getMinimumPosition();

//        this.world = this.region.getCenter().getWo

        this.data = new SchematicBlock[][][]{};
    }

    /**
     * Copy all block data within the schematic boundaries.
     */
    public void copy() {
        for (int x = minimum.getBlockX(); x <= maximum.getBlockX(); x++) {
            for (int y = minimum.getBlockY(); y <= maximum.getBlockY(); y++) {
                for (int z = minimum.getBlockZ(); z <= maximum.getBlockZ(); z++) {
                    data[x][y][z] = getBlock(new Vector(x, y, z));
                }
            }
        }
    }


    /**
     * Get the name of the schematic (unique).
     *
     * @return Schematic name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a {@link SchematicBlock} at a specific vector.
     *
     * @param vector - vector.
     * @return SchematicBlock
     */
    public SchematicBlock getBlock(Vector vector) {
        for (SchematicBlock[][] datum : data) {
            for (SchematicBlock[] schematicBlocks : datum) {
                for (SchematicBlock schematicBlock : schematicBlocks) {

                }
            }
        }
        return null;
    }

}
