package net.cloudescape.skyblock.schematics.newsystem.block;

import net.cloudescape.skyblock.schematics.newsystem.Queue;
import net.cloudescape.skyblock.schematics.newsystem.QueueNode;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 4/8/2018.
 */
public abstract class BlockQueue implements Queue<QueueNode<SchematicBlock>> {
    private World world;
    private BlockQueueNode[] nodes;
    private int index;

    public BlockQueue(World world, List<SchematicBlock> schematicBlocks) {
        this.world = world;
        List<BlockQueueNode> queueNodes = new ArrayList<>();
        int tempIndex = 0;
        for (SchematicBlock schematicBlock : schematicBlocks) {
            queueNodes.add(new BlockQueueNode(schematicBlock, tempIndex, this));
            tempIndex++;
        }
        this.nodes = queueNodes.toArray(new BlockQueueNode[0]);
    }


    @Override
    public QueueNode<SchematicBlock>[] getQueueNodes() {
        return nodes;
    }

    @Override
    public long getDelay() {
        return 50;
    }

    @Override
    public int getCurrentIndex() {
        return index;
    }

    @Override
    public int getProcessPerQueue() {
        return 100;
    }


    @Override
    public void setCurrentIndex(int index) {
        this.index = index;
    }


    public World getWorld() {
        return world;
    }

    public abstract void start();
    public abstract Material getTypeToReplace();
    public abstract byte getDataToReplace();
}
