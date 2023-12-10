package net.cloudescape.skyblock.schematics;

public class SchematicBlock {
    private short id;
    private short data;

    /**
     * Build a new instance of {@link SchematicBlock}
     * @param id - block id.
     * @param data - block data.
     */
    public SchematicBlock(short id, short data){
        this.id = id;
        this.data = data;
    }

    /**
     * Get the blocks ID.
     * @return Block ID.
     */
    public short getId() {
        return id;
    }

    /**
     * Get the blocks data.
     * @return Block data.
     */
    public short getData() {
        return data;
    }
}
