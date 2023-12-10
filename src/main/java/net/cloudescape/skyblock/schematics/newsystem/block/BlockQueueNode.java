package net.cloudescape.skyblock.schematics.newsystem.block;

import net.cloudescape.skyblock.schematics.newsystem.Queue;
import net.cloudescape.skyblock.schematics.newsystem.QueueNode;
import net.cloudescape.skyblock.utils.BlockUtils;
import org.bukkit.block.Block;

/**
 * Created by Matthew E on 4/8/2018.
 */
public class BlockQueueNode implements QueueNode<SchematicBlock> {

    private SchematicBlock schematicBlock;
    private int index;
    private BlockQueue queue;

    public BlockQueueNode(SchematicBlock schematicBlock, int index, BlockQueue queue) {
        this.schematicBlock = schematicBlock;
        this.index = index;
        this.queue = queue;
    }

    @Override
    public Queue<QueueNode<SchematicBlock>> getQueue() {
        return queue;
    }


    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void process() {
        Block block = queue.getWorld().getBlockAt(schematicBlock.getX(), schematicBlock.getY(), schematicBlock.getZ());
        BlockQueue blockQueue = (BlockQueue) getQueue();
        BlockUtils.setBlockSuperFast(block, blockQueue.getTypeToReplace().getId(), blockQueue.getDataToReplace(), false);
//        block.setTypeId(schematicBlock.getId());
//        block.setData(schematicBlock.getData());
    }
}
