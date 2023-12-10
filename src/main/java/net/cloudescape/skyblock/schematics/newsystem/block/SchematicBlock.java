package net.cloudescape.skyblock.schematics.newsystem.block;

/**
 * Created by Matthew E on 4/8/2018.
 */
public class SchematicBlock {
    private int id;
    private byte data;
    private int x;
    private int y;
    private int z;

    public SchematicBlock(int id, byte data, int x, int y, int z) {
        this.id = id;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}