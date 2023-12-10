package net.cloudescape.skyblock.utils;

import com.cloudescape.utilities.Callback;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.generator.EmptyWorldGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WorldUtil {
    public static boolean deleteWorld(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Generate a Minecraft world. This can be used on worlds already on the server to load them!
     *
     * @param worldName   - World name
     * @param environment - World environment.
     * @param autoSave    - Should autosave be enabled?
     * @param emptyWorld  - Should the world be generated with no chunks (empty world).
     * @return
     */
    public static World generateWorld(String worldName, World.Environment environment, boolean autoSave, boolean emptyWorld) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            WorldCreator creator = new WorldCreator(worldName);
            if (emptyWorld) creator.generator(new EmptyWorldGenerator());
            creator.environment(environment);
            creator.generateStructures(false);
            world = creator.createWorld();
            world.setAutoSave(autoSave);
            world.setTime(0);

            world.setGameRuleValue("doDaylightCycle", "false");
            return world;
        }
        return null;
    }

    private static void uploadZipFile(File zipFile, String name, Callback<Boolean> finished) {
        InputStream inputStream = null;

        boolean completed = false;
        try {
            Logger.log("Attempting to upload file to database!");
            inputStream = new FileInputStream(zipFile);
            GridFS gridfs = new GridFS(CloudSkyblock.getPlugin().getMongo().getClient().getDB("cloudescape_db"), "island_schematics");
            GridFSInputFile gfsFile = gridfs.createFile(inputStream);
            Logger.log("Hash: " + gfsFile.getMD5());
            gfsFile.setFilename(name);
            gfsFile.save();
            Logger.log("Uploaded zip file " + name);
            completed = true;
            finished.call(true);
        } catch (Exception e) {
            e.printStackTrace();
            if (!completed) {
                finished.call(false);
            }
            completed = true;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    if (!completed) {
                        finished.call(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (!completed) {
                        finished.call(false);
                    }
                }
            }
        }
    }

    public static void uploadWorld(World world, boolean delete, Runnable onDone, Runnable onFailure) {
        File worldFile = new File(Bukkit.getWorldContainer(), world.getName());

        if (worldFile.exists()) {
            world.save();
            WorldUtil.unloadWorld(world, true);
            Logger.log("Unloaded world " + world.getName() + ".");
        }
        for (Player player : world.getPlayers()) {
            player.kickPlayer(ChatColor.RED + "Failed to send you to hub");
        }
        boolean error = false;
        try {
            for (File file : worldFile.listFiles()) {

            }
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
        }
        if (!error && worldFile.list() != null && worldFile.listFiles() != null) {
            for (File files : worldFile.listFiles()) {
                if (files.getName().equals("session.lock") || files.getName().equals("uid.dat")) {
                    try {
                        FileUtils.forceDelete(files);
                        Logger.log("Successfully removed " + files.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        File zipFile = new File(CloudSkyblock.getPlugin().getDataFolder(), "/temp/" + world.getName() + ".zip");

        try {

            FileUtil.zip(worldFile, zipFile);
            Logger.log("Zipping directory " + worldFile.getName() + " into " + zipFile.getAbsolutePath());

            if (zipFile.exists()) {
                FileUtils.deleteDirectory(worldFile);
                if (zipFile.exists()) {
                    Logger.log("Zip file found.");
                    uploadZipFile(zipFile, world.getName(), finished -> {
                        if (finished) {
                            Logger.log("Uploading Zip file complete.");

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (zipFile.exists()) FileUtils.forceDelete(zipFile);
                                        onDone.run();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.runTaskLater(CloudSkyblock.getPlugin(), 20);
                        } else {
                            Logger.log("Failed to upload zip!");
                            onFailure.run();

                        }
                    });
                }
            } else {
                Logger.log("Failed to zip " + world.getName());
                onFailure.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void downloadWorld(String name, Consumer<World> worldConsumer, Runnable onFailure) {
        Logger.log("Downloading began.");
        FileOutputStream fos = null;
        File tempLocation = new File(CloudSkyblock.getPlugin().getDataFolder(), "/temp/downloading/" + name + ".zip");
        File tempLocationDirectory = new File(CloudSkyblock.getPlugin().getDataFolder(), "/temp/downloading/");
        Logger.log("Zip file exists: " + (tempLocation.exists()));
        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        if (worldFolder.exists()) {
            try {
                FileUtils.deleteDirectory(worldFolder);
                Logger.log("Deleted world folder, wasn't removed already! " + worldFolder.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GridFS gridFSBucket = new GridFS(CloudSkyblock.getPlugin().getMongo().getClient().getDB("cloudescape_db"), "island_schematics");
        boolean successful = false;
        try {
            Logger.log("Downloading world " + name + ".");

            List<GridFSDBFile> gridFSDBFiles = gridFSBucket.find(name);
            if (gridFSDBFiles.size() == 0) {
                onFailure.run();
                return;
            }
            InputStream inputStream = gridFSDBFiles.get((gridFSDBFiles.size() == 0 ? 0 : gridFSDBFiles.size() - 1)).getInputStream();

            if (!tempLocationDirectory.exists()) tempLocationDirectory.mkdirs();
            if (!tempLocation.exists()) tempLocation.createNewFile();

            fos = new FileOutputStream(tempLocation);
            IOUtils.copy(inputStream, fos);

            FileUtil.unzip(tempLocation, worldFolder);
            Logger.log("Downloaded and extracted world " + name + ".");
            successful = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            if (tempLocation.exists()) {
                try {
                    FileUtils.forceDelete(tempLocation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
             */

            if (successful) {

                World world = WorldUtil.generateWorld(name, World.Environment.NORMAL, false, true);

                if (world == null) {
                    Logger.log("World is null!");
                    return;
                }

                world.setSpawnLocation(0, 0, 0);
                world.setStorm(false);

                worldConsumer.accept(world);
                Logger.log("Finished loading world " + name);

                if (tempLocation.exists()) {
                    try {
                        FileUtils.forceDelete(tempLocation);
                        Logger.log("Removed temporary files.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get all the blocks within a radius.
     *
     * @param centre - centre point.
     * @param radius - radius outwards.
     * @return List of blocks.
     */
    public static void getBlocks(Location centre, int radius, Callback<List<Block>> blockCallback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> blocks = new ArrayList<>();
                for (double x = centre.getX() - radius; x <= centre.getX() + radius; x++) {
                    for (double y = centre.getY() - radius; y <= centre.getY() + radius; y++) {
                        for (double z = centre.getZ() - radius; z <= centre.getZ() + radius; z++) {
                            Location location = new Location(centre.getWorld(), x, y, z);
                            if (location.getBlock().getType() == Material.AIR) continue;
                            blocks.add(location.getBlock());
                        }
                    }
                }

                blockCallback.call(blocks);
            }
        }.runTaskAsynchronously(CloudSkyblock.getPlugin());
    }

    /**
     * Unload a Minecraft world.
     *
     * @param world - the world.
     * @param save  - should save?
     */
    public static void unloadWorld(World world, boolean save) {
        if (world == null) return;
        Bukkit.unloadWorld(world, save);
    }
}
