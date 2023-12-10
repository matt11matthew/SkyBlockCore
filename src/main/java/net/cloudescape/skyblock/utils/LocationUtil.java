package net.cloudescape.skyblock.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class LocationUtil {

    private static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    public static Location getLocationFromString(String location) {
        String[] locationParts = location.split(";");
        return new Location(Bukkit.getWorld(locationParts[0]), ParserUtil.parseInt(locationParts[1]).get(), ParserUtil.parseInt(locationParts[2]).get(), ParserUtil.parseInt(locationParts[3]).get(), ParserUtil.parseFloat(locationParts[4]).get(), ParserUtil.parseFloat(locationParts[5]).get());
    }

    public static String getStringFromLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static float getNearestYaw(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();

        switch (face) {
            case SOUTH:
                return 0;
            case WEST:
                return 90;
            case NORTH:
                return -180;
            case EAST:
                return -90;
        }

        return 0;
    }

}
