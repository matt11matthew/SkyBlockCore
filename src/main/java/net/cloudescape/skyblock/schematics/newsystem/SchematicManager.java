package net.cloudescape.skyblock.schematics.newsystem;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.Region;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.schematics.newsystem.block.BlockQueue;
import net.cloudescape.skyblock.schematics.newsystem.block.SchematicBlock;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;
import net.cloudescape.skyblock.utils.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by Matthew E on 4/8/2018.
 **/
public class SchematicManager {
    private Map<String, Schematic> fileSchematicMap;

    public Map<String, Schematic> getFileSchematicMap() {
        return fileSchematicMap;
    }

    public SchematicManager() {
        fileSchematicMap = new ConcurrentHashMap<>();
    }

    public static void setBlocksAsync(World world, List<SchematicBlock> toReplace, Material type, byte data, int bps, Consumer<Boolean> onFinish) {
        final String name = new Random().nextInt(2340423) + "";
        BlockQueue blockQueue = new BlockQueue(world, toReplace) {
            @Override
            public void start() {
                CloudSkyblock.getCloudRunnableManager().schedule(this::process, name, CloudRunnableType.SYNC, 500, TimeUnit.MILLISECONDS);
            }
            @Override
            public int getProcessPerQueue() {
                return bps;
            }

            @Override
            public Material getTypeToReplace() {
                return type;
            }

            @Override
            public byte getDataToReplace() {
                return data;
            }

            @Override
            public void finish() {
                Logger.log("Finished.");
                CloudSkyblock.getCloudRunnableManager().cancelTask(name);
                onFinish.accept(true);
            }
        };
        blockQueue.start();
    }

    public static void pasteAsync(Vector location, World world, Schematic schematic, int bps, Consumer<Boolean> onFinish) {
        Region region = schematic.getClipboard().getRegion();
        BlockArrayClipboard blockArrayClipboard = (BlockArrayClipboard) schematic.getClipboard();

        List<SchematicBlock> schematicBlockList = new ArrayList<>();

//        int width = region.getWidth();
//        int length = region.getLength();
//        int height = region.getHeight();

        // Jakes system V
        int topBlockX = (region.getMinimumPoint().getBlockX() < region.getMaximumPoint().getBlockX() ? region.getMaximumPoint().getBlockX() : region.getMinimumPoint().getBlockX());
        int bottomBlockX = (region.getMinimumPoint().getBlockX() > region.getMaximumPoint().getBlockX() ? region.getMaximumPoint().getBlockX() : region.getMinimumPoint().getBlockX());

        int topBlockY = (region.getMinimumPoint().getBlockY() < region.getMaximumPoint().getBlockY() ? region.getMaximumPoint().getBlockY() : region.getMinimumPoint().getBlockY());
        int bottomBlockY = (region.getMinimumPoint().getBlockY() > region.getMaximumPoint().getBlockY() ? region.getMaximumPoint().getBlockY() : region.getMinimumPoint().getBlockY());

        int topBlockZ = (region.getMinimumPoint().getBlockZ() < region.getMaximumPoint().getBlockZ() ? region.getMaximumPoint().getBlockZ() : region.getMinimumPoint().getBlockZ());
        int bottomBlockZ = (region.getMinimumPoint().getBlockZ() > region.getMaximumPoint().getBlockZ() ? region.getMaximumPoint().getBlockZ() : region.getMinimumPoint().getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    BaseBlock clipboardBlock = blockArrayClipboard.getLazyBlock(new Vector(x, y, z));
                    if (clipboardBlock.getId() == 0 || clipboardBlock.isAir())
                        continue;

                    SchematicBlock block = new SchematicBlock(clipboardBlock.getId(), (byte) clipboardBlock.getData(), x, y, z);

                    if (alreadyExists(schematicBlockList, block)) continue;

                    schematicBlockList.add(block);
                }
            }
        }
        // End of Jakes system ^

//        for (int x = 0; x < width; ++x) {
//            for (int y = 0; y < height; ++y) {
//                for (int z = 0; z < length; ++z) {
////                    int index = y * width * length + z * width + x;
//                    Block block = new Location(world, x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
////                    block.setTypeIdAndData(blocks[index], blockData[index], true);
//                    BaseBlock clipboardBlock = blockArrayClipboard.getLazyBlock(new Vector(block.getX(), block.getY(), block.getZ()));
//                    if (clipboardBlock.isAir()) {
//                        continue;
//                    }
//                    schematicBlockList.add(new SchematicBlock(clipboardBlock.getId(), (byte) clipboardBlock.getData(), block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ()));
////                    Bukkit.broadcastMessage("Block - " + Material.getMaterial(clipboardBlock.getId()).name() + " - " + block.getLocation().toVector().toString());
////                    Bukkit.broadcastMessage("X: " + x + "Y: " + y + " Z: " + z);
//                }
//            }
//        }
//        final String name = new Random().nextInt(2340423) + "";
//        BlockQueue blockQueue = new BlockQueue(world, schematicBlockList) {
//            @Override
//            public void start() {
//                CloudSkyblock.getCloudRunnableManager().schedule(this::process, name, CloudRunnableType.SYNC, 500, TimeUnit.MILLISECONDS);
//            }
//
//            @Override
//            public void finish() {
//                Logger.log("Finished.");
//                CloudSkyblock.getCloudRunnableManager().cancelTask(name);
//                onFinish.accept(true);
//            }
//        };
//        blockQueue.start();
    }

    public void copyAsync(Location location, int radius, Consumer<net.cloudescape.skyblock.schematic.Schematic> schematicConsumer) {

    }

    public void loadSchematics(File file) throws IOException {
        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            Schematic schematic = ClipboardFormat.SCHEMATIC.load(file1);
            String name = file1.getName().replaceAll(".schematic", "").trim();
            fileSchematicMap.put(name, schematic);
            Logger.log("[SchematicManager] Loaded schematic " + name + ".schematic");
        }
    }

    private static boolean alreadyExists(List<SchematicBlock> schematicBlocks, SchematicBlock schematicBlock) {
        for (SchematicBlock block : schematicBlocks) {
            if (block.getX() == schematicBlock.getX() && block.getY() == schematicBlock.getY() && block.getZ() == schematicBlock.getZ()) {
                return true;
            }
        }
        return false;
    }
}
