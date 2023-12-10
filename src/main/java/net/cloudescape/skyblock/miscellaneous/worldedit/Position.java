package net.cloudescape.skyblock.miscellaneous.worldedit;

import org.bukkit.Location;

public class Position {

    private Location position1;
    private Location position2;

    public Position() {
        position1 = null;
        position2 = null;
    }

    public Location getPosition1() {
        return position1;
    }

    public Location getPosition2() {
        return position2;
    }

    public void setPosition1(Location position1) {
        this.position1 = position1;
    }

    public void setPosition2(Location position2) {
        this.position2 = position2;
    }
}
