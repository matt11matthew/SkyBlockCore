package net.cloudescape.skyblock.schematics;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SchematicRegion {

    /**
     * Point 1 (typically bottom, left point)
     */
    private Location pointOne;
    /**
     * Point 2 (typically top right point)
     */
    private Location pointTwo;

    private World world;

    public SchematicRegion(Location p1, Location p2) {
        this.pointOne = p1;
        this.pointTwo = p2;
        this.world = p1.getWorld();

        this.world = p1.getWorld();
    }

    /**
     * Get maximum position of the schematic.
     *
     * @return Vector
     */
    public Vector getMaximumPosition() {
        return new Vector(Math.max(pointOne.getX(), pointTwo.getX()), Math.max(pointOne.getY(), pointTwo.getY()), Math.max(pointOne.getZ(), pointTwo.getZ()));
    }

    /**
     * Get minimum position of the schematic.
     *
     * @return Vector
     */
    public Vector getMinimumPosition() {
        return new Vector(Math.min(pointOne.getX(), pointTwo.getX()), Math.min(pointOne.getY(), pointTwo.getY()), Math.min(pointOne.getZ(), pointTwo.getZ()));
    }

    public World getWorld() {
        return world;
    }

    /**
     * Is the vector (block) within the region.
     *
     * @param block - block vector.
     * @return Is in region.
     */
    public boolean inRegion(Vector block) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();

        Vector maximum = getMaximumPosition();
        Vector minimum = getMinimumPosition();

        return x >= minimum.getBlockX() && x <= maximum.getBlockX() && y >= minimum.getBlockY() && y <= maximum.getBlockY() && z >= minimum.getBlockZ() && z <= maximum.getBlockZ();
    }

    /**
     * Get centre of the region.
     *
     * @return Vector position.
     */
    public Vector getCenter() {
        Vector maximum = getMaximumPosition();
        Vector minimum = getMinimumPosition();

        return minimum.add(minimum.subtract(maximum).multiply(0.5));
    }

}
