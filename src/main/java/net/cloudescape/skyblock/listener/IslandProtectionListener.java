package net.cloudescape.skyblock.listener;

import com.cloudescape.utilities.CustomChatMessage;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.island.Island;
import net.cloudescape.skyblock.island.IslandSettings;
import net.cloudescape.skyblock.island.shop.Shop;
import net.cloudescape.skyblock.island.shop.ShopStage;
import net.cloudescape.skyblock.island.shop.ShopType;
import net.cloudescape.skyblock.island.spawner.GolemSpawner;
import net.cloudescape.skyblock.island.spawner.GolemSpawnerInfo;
import net.cloudescape.skyblock.island.spawner.GolemType;
import net.cloudescape.skyblock.island.spawner.SpawnerMenu;
import net.cloudescape.skyblock.utils.SpawnerUtils;
import net.cloudescape.skyblock.utils.callback.Callback;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class IslandProtectionListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEgg(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().hasItemMeta() && event.getItem().getType() == Material.MONSTER_EGG) {
            ItemStack item = event.getItem();
            net.minecraft.server.v1_12_R1.ItemStack itemStack1 = CraftItemStack.asNMSCopy(item);
            if ((itemStack1.hasTag() && itemStack1.getTag() != null) && itemStack1.getTag().hasKey("bossEggType")) {
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must use this at a boss arena"));
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(EntityDamageEvent event) {
        if (CloudSkyblock.getPlugin().getIslandManager().getLoadingIslandList().contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            event.getEntity().teleport(event.getEntity().getLocation().add(0, 100, 0));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            if (event.getCause().name().contains("ENTITY") && CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getEntity().getWorld()) != null) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof ArmorStand) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageIsland(EntityExplodeEvent event) {

        if (event.getEntityType() == EntityType.PRIMED_TNT || event.getEntityType() == EntityType.MINECART_TNT) {
            System.out.println("TNT is not protected against explosion.");
            return;
        }

        event.setCancelled(true);
        System.out.println("An explosion from " + event.getEntityType().name() + " was protected..");
    }


    /**
     * Disables any block placing on an island.
     */
    //@EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (islandOptional.isPresent()) {

            Island island = islandOptional.get();
            Block block = event.getBlock();

            if (!island.isIslandMember(player.getUniqueId()) && !island.getIslandSettingEnabled(IslandSettings.PUBLIC_BUILD)) {
                CustomChatMessage.sendMessage(player, "Skyblock Protection", "You are not a member of this island!");
                event.setCancelled(true);
                return;
            }

//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
//                event.setCancelled(true);
//                player.sendMessage(ChatColor.RED + "You can't do that here");
//                return;
//            }

            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
                CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot build in the island temple.");
                event.setCancelled(true);
                return;
            }

            // Limits world to 5 spawners per world.
            if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
                ItemStack item = event.getItemInHand();
                ItemStack newStack = item;
                newStack.setAmount(1);

                getTotalBlocks(island, Material.MOB_SPAWNER, blocks -> {
                    if (blocks > 5) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (event.getBlockPlaced() != null && event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
                                    if (player.getGameMode() != GameMode.CREATIVE && newStack.getType() != Material.AIR) {
                                        Location location = event.getBlockPlaced().getLocation();
                                        location.getWorld().dropItem(location.add(0, 1, 0), newStack);
                                    }
                                    event.getBlockPlaced().breakNaturally();
                                }
                            }
                        }.runTaskLater(CloudSkyblock.getPlugin(), 1L);
                        CustomChatMessage.sendMessage(player, "Spawners", "You cannot place more than 5 spawners on an island at one time!");
                    }
                });
            }


            if (island.inIslandRegion(player) && island.isProtected() && island.isIslandMember(player.getUniqueId()) && block.getType() == Material.MOB_SPAWNER) {
                ItemStack itemStack = event.getPlayer().getItemInHand();
                CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlockPlaced().getState();
                net.minecraft.server.v1_12_R1.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);

                if (stack != null && stack.hasTag() && stack.getTag() != null && stack.getTag().hasKey("tier") && stack.getTag().hasKey("type")) {
                    NBTTagCompound tag = stack.getTag();
                    int tier = tag.getInt("tier");
                    int mobId = tag.getInt("mobId");

                    if (creatureSpawner.getSpawnedType() == EntityType.IRON_GOLEM) {
                        GolemType golemType = GolemType.valueOf(tag.getString("type"));
                        int combineRadius = 5;
                        Location combineSpawnerLocation = null;

                        try {
                            for (int i = 1; i <= combineRadius; i++) {
                                for (BlockFace blockFace : Arrays.asList(BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST)) {
                                    Block relative = block.getRelative(blockFace, i);
                                    if (relative.getType() == Material.MOB_SPAWNER && relative.getData() == block.getData() && island.getGolemSpawners().containsKey(relative.getLocation()) && island.getGolemSpawner(relative.getLocation()) != null && island.getGolemSpawner(relative.getLocation()).getTier() == tier && island.getGolemSpawner(relative.getLocation()).getGolemType() == golemType) {
                                        if (relative.getLocation().equals(block.getLocation())) {
                                            continue;
                                        }
                                        combineSpawnerLocation = relative.getLocation();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            combineSpawnerLocation = null;
                        }
                        if (combineSpawnerLocation != null && island.getGolemSpawner(combineSpawnerLocation) != null) {
                            GolemSpawner golemSpawner = island.getGolemSpawner(combineSpawnerLocation);
                            //combine
                            golemSpawner.setCount(golemSpawner.getCount() + 1);
                            golemSpawner.updateDisplayName();
                            event.setCancelled(true);
                            if (player.getItemInHand().getAmount() > 1) {
                                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                            } else {
                                player.setItemInHand(new ItemStack(Material.AIR));
                            }
                            player.updateInventory();

                            player.sendMessage(ChatColor.AQUA + "Added golem spawner");
                            return;

                        } else {
                            //add spawner
                            Location location = event.getBlock().getLocation();
                            GolemSpawner golemSpawner1 = new GolemSpawner(location.getBlockX(), location.getBlockY(), location.getBlockZ(), golemType, tier, player.getUniqueId(), EntityType.IRON_GOLEM, 1, 1.0D);
                            ArmorStand armorStand = golemSpawner1.spawnHologram(location.getWorld());
                            golemSpawner1.setHologram(armorStand);
                            golemSpawner1.updateDisplayName();
                            island.addSpawner(golemSpawner1);
                            player.sendMessage(ChatColor.AQUA + "Placed golem spawner");
                            return;
                        }
                    } else {
                        /**
                         * test
                         */
                        int combineRadius = 5;
                        Location combineSpawnerLocation = null;

                        try {
                            for (int i = 1; i <= combineRadius; i++) {
                                for (BlockFace blockFace : Arrays.asList(BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST)) {
                                    Block relative = block.getRelative(blockFace, i);
                                    if (relative.getType() == Material.MOB_SPAWNER && relative.getData() == block.getData() && island.getGolemSpawners().containsKey(relative.getLocation()) && island.getGolemSpawner(relative.getLocation()) != null && island.getGolemSpawner(relative.getLocation()).getTier() == tier) {
                                        if (relative.getLocation().equals(block.getLocation())) {
                                            continue;
                                        }
                                        combineSpawnerLocation = relative.getLocation();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            combineSpawnerLocation = null;
                        }
                        if (combineSpawnerLocation != null && island.getGolemSpawner(combineSpawnerLocation) != null) {
                            GolemSpawner golemSpawner = island.getGolemSpawner(combineSpawnerLocation);
                            //combine
                            golemSpawner.setCount(golemSpawner.getCount() + 1);
                            golemSpawner.updateDisplayName();
                            event.setCancelled(true);
                            if (player.getItemInHand().getAmount() > 1) {
                                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                            } else {
                                player.setItemInHand(new ItemStack(Material.AIR));
                            }
                            player.updateInventory();

                            player.sendMessage(ChatColor.AQUA + "Added spawner");
                            return;

                        } else {
                            //add spawner
                            Location location = event.getBlock().getLocation();
                            GolemSpawner golemSpawner1 = new GolemSpawner(location.getBlockX(), location.getBlockY(), location.getBlockZ(), GolemType.IRON, tier, player.getUniqueId(), creatureSpawner.getSpawnedType(), 1, 1.0D);
                            ArmorStand armorStand = golemSpawner1.spawnHologram(location.getWorld());
                            golemSpawner1.setHologram(armorStand);
                            golemSpawner1.updateDisplayName();
                            island.addSpawner(golemSpawner1);
                            player.sendMessage(ChatColor.AQUA + "Placed spawner");
                            return;
                        }
                    }

                } else {
                    /**
                     * TEST1
                     */
                    CreatureSpawner blockStateMeta = (CreatureSpawner) event.getBlock().getState();
                    if (blockStateMeta != null) {

                        EntityType entityType = blockStateMeta.getSpawnedType();
                        if (entityType == EntityType.IRON_GOLEM) {
                            blockStateMeta.setSpawnedType(EntityType.IRON_GOLEM);
                            blockStateMeta.update(true);

                            int combineRadius = 5;
                            Location combineSpawnerLocation = null;
                            try {
                                for (int i = 1; i <= combineRadius; i++) {
                                    for (BlockFace blockFace : Arrays.asList(BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST)) {
                                        Block relative = block.getRelative(blockFace, i);
                                        if (relative.getType() == Material.MOB_SPAWNER && island.getGolemSpawners().containsKey(relative.getLocation()) && island.getGolemSpawner(relative.getLocation()) != null && island.getGolemSpawner(relative.getLocation()).getTier() == 1 && island.getGolemSpawner(relative.getLocation()).getGolemType() == GolemType.IRON) {
                                            if (relative.getLocation().equals(block.getLocation())) {
                                                continue;
                                            }
                                            combineSpawnerLocation = relative.getLocation();
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                combineSpawnerLocation = null;
                            }
                            if (combineSpawnerLocation != null && island.getGolemSpawner(combineSpawnerLocation) != null) {
                                GolemSpawner golemSpawner = island.getGolemSpawner(combineSpawnerLocation);
                                //combine
                                golemSpawner.setCount(golemSpawner.getCount() + 1);
                                golemSpawner.updateDisplayName();
                                event.setCancelled(true);
                                if (player.getItemInHand().getAmount() > 1) {
                                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                                } else {
                                    player.setItemInHand(new ItemStack(Material.AIR));
                                }
                                player.updateInventory();

                                player.sendMessage(ChatColor.AQUA + "Added golem spawner");
                                return;

                            } else {
                                //add spawner
                                Location location = event.getBlock().getLocation();
                                GolemSpawner golemSpawner1 = new GolemSpawner(location.getBlockX(), location.getBlockY(), location.getBlockZ(), GolemType.IRON, 1, player.getUniqueId(), EntityType.IRON_GOLEM, 11
                                        , 1.0D);
                                ArmorStand armorStand = golemSpawner1.spawnHologram(location.getWorld());
                                golemSpawner1.setHologram(armorStand);
                                golemSpawner1.updateDisplayName();
                                island.addSpawner(golemSpawner1);
                                player.sendMessage(ChatColor.AQUA + "Created golem spawner");
                                return;
                            }
                        } else {


                        /*
                        sdfgjlk
                         */
                            int combineRadius = 5;
                            Location combineSpawnerLocation = null;
                            try {
                                for (int i = 1; i <= combineRadius; i++) {
                                    for (BlockFace blockFace : Arrays.asList(BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST)) {
                                        Block relative = block.getRelative(blockFace, i);
                                        if (relative.getType() == Material.MOB_SPAWNER && island.getGolemSpawners().containsKey(relative.getLocation()) && island.getGolemSpawner(relative.getLocation()) != null && island.getGolemSpawner(relative.getLocation()).getTier() == 1) {
                                            if (relative.getLocation().equals(block.getLocation())) {
                                                continue;
                                            }
                                            combineSpawnerLocation = relative.getLocation();
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                combineSpawnerLocation = null;
                            }
                            if (combineSpawnerLocation != null && island.getGolemSpawner(combineSpawnerLocation) != null) {
                                GolemSpawner golemSpawner = island.getGolemSpawner(combineSpawnerLocation);
                                //combine
                                golemSpawner.setCount(golemSpawner.getCount() + 1);
                                golemSpawner.updateDisplayName();
                                event.setCancelled(true);
                                if (player.getItemInHand().getAmount() > 1) {
                                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                                } else {
                                    player.setItemInHand(new ItemStack(Material.AIR));
                                }
                                player.updateInventory();

                                player.sendMessage(ChatColor.AQUA + "Added spawner");
                                return;

                            } else {
                                //add spawner
                                Location location = event.getBlock().getLocation();
                                GolemSpawner golemSpawner1 = new GolemSpawner(location.getBlockX(), location.getBlockY(), location.getBlockZ(), GolemType.IRON, 1, player.getUniqueId(), EntityType.fromId(event.getBlock().getState().getData().toItemStack(1).getDurability()), 1, 1.0D);
                                ArmorStand armorStand = golemSpawner1.spawnHologram(location.getWorld());
                                golemSpawner1.setHologram(armorStand);
                                golemSpawner1.updateDisplayName();
                                island.addSpawner(golemSpawner1);
                                player.sendMessage(ChatColor.AQUA + "Created spawner");
                                return;
                            }

                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (event.getEntityType() == EntityType.VILLAGER || event.getEntity().getType() == EntityType.ARMOR_STAND) {
            return;
        }
        if (event.getEntity().getWorld().getName().equals("world") || event.getEntity().getWorld().getName().equals("world_nether") || event.getEntity().getWorld().getName().equals("world_the_end")) {
            return;
        }
        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld((World) event.getEntity().getWorld());


        if (islandOptional.isPresent()) {
            Island island = islandOptional.get();
//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getEntity().getLocation().getBlock().getLocation().getBlockX(), event.getEntity().getLocation().getBlock().getLocation().getBlockY(), event.getEntity().getLocation().getBlock().getLocation().getBlockZ()))) {
//
//                event.setCancelled(true);
//
//                return;
//            }
            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getEntity().getLocation().getBlock().getLocation().getBlockX(), event.getEntity().getLocation().getBlock().getLocation().getBlockY(), event.getEntity().getLocation().getBlock().getLocation().getBlockZ()))) {

                event.setCancelled(true);

                return;
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end")) {
            return;
        }

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getPlayer().getWorld());

        if (islandOptional.isPresent() && ((event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.CHEST))) {
            Island island = islandOptional.get();

            if (island.inIslandRegion(player) && island.isProtected() && island.isIslandMember(player.getUniqueId())) {

                if (!island.getIslandMembers().containsKey(player.getUniqueId())) {
                    CustomChatMessage.sendMessage(player, "Skyblock Protection", "You are not a member of this island!");
                    event.setCancelled(true);
                    return;
                }
//
//                if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
//
//                    event.setCancelled(true);
//
//                    player.sendMessage(ChatColor.RED + "You can't do that here");
//                    return;
//                }
                if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
                    CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot build in the island temple.");
                    event.setCancelled(true);
                    return;
                }
                Block block = null;
                if (event.getBlock().getType() == Material.CHEST) {
                    block = getSign(event.getBlock());

                } else if (event.getBlock().getType() == Material.WALL_SIGN) {
                    block = event.getBlock();
                }
                if (block != null) {

                    Shop shop = island.getShop(block.getLocation());
                    if (!player.getUniqueId().equals(shop.getPlayerUuid())) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You must be owner to break shop");
                    } else {
                        island.deleteShop(shop);
                        shop.getItem().remove();
                        player.sendMessage(ChatColor.AQUA + "Deleted shop");
                    }
                }
            }
        }
    }

    public Block getSign(Block block) {
        if (block.getType() == Material.CHEST) {
            for (BlockFace blockFace : java.util.Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH)) {
                Block relative = block.getRelative(blockFace);
                if (relative != null && relative.getType() == Material.WALL_SIGN) {
                    return relative;
                }
            }
        }
        return null;
    }

    public Block getChestBlock(Location location) {
        Block signBlock = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (signBlock.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) signBlock.getState().getData();
            Block relative = signBlock.getRelative(sign.getAttachedFace());
            return (relative != null && relative.getType() == Material.CHEST) ? relative : null;
        }
        return null;
    }

    @EventHandler
    public void onPlayerInteractAtShop(PlayerInteractEvent event) {
        if (event.hasBlock() && event.hasItem() && event.getClickedBlock().getType() == Material.WALL_SIGN && ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK))) {
            Player player = event.getPlayer();

            if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end")) {
                return;
            }

            Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld((World) event.getPlayer().getWorld());


            if (islandOptional.isPresent()) {
                Island island = islandOptional.get();

                if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    Block enderChest = event.getClickedBlock();

                    if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(enderChest.getLocation().getBlockX(), enderChest.getLocation().getBlockY(), enderChest.getLocation().getBlockZ()))) {
                        CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot build in the island temple.");
                        event.setCancelled(true);
                        return;
                    }
                }

                if (island.inIslandRegion(player) && island.isProtected()) {

                    ItemStack item = event.getItem();
                    Shop shop = island.getShop(event.getClickedBlock().getLocation());
                    island.update();
                    if (shop != null) {
                        if (shop.getStage() == ShopStage.CREATION && event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            if (!island.isIslandMember(player.getUniqueId())) {

                                CustomChatMessage.sendMessage(player, "Skyblock", "You cannot do this here.");
                                event.setCancelled(true);
                                return;
                            }
                            shop.setStage(ShopStage.CREATED);
                            shop.setItemId(item.getTypeId());
                            shop.setItemData(item.getDurability());
                            player.sendMessage(ChatColor.AQUA + "Now " + shop.getType().toString().toLowerCase() + ChatColor.GRAY + shop.getAmount() + "x " + item.getType().toString() + "(s) for " + ChatColor.GRAY + "$" + new DecimalFormat("#,###.##").format(shop.getPrice()));
                            island.editShop(shop);
                            String typeName = shop.getType().toString();
                            org.bukkit.block.Sign sign = (org.bukkit.block.Sign) event.getClickedBlock().getState();
                            sign.setLine(1, ChatColor.AQUA + ((typeName.substring(0, 1).toUpperCase() + typeName.substring(1, typeName.length()).toLowerCase()) + ": " + ChatColor.BLACK) + shop.getAmount());
                            sign.setLine(2, ChatColor.AQUA + new DecimalFormat("#,###.##").format(shop.getPrice()));
                            sign.update(true);
                        } else if (shop.getStage() == ShopStage.CREATED && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            switch (shop.getType()) {
                                case SELL:
                                    shop.sell(player, shop.getAmount());
                                    event.setUseItemInHand(Event.Result.DENY);
                                    event.setUseInteractedBlock(Event.Result.DENY);
                                    event.setCancelled(true);
                                    break;
                                case BUY:
                                    shop.buy(player, shop.getAmount());
                                    event.setUseItemInHand(Event.Result.DENY);
                                    event.setUseInteractedBlock(Event.Result.DENY);
                                    event.setCancelled(true);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

//    @EventHandler(ignoreCancelled = true)
//    public void onPlayerMove(PlayerMoveEvent event) {
//        Player player = event.getPlayer();
//
//        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end")) {
//            return;
//        }
//        if (event.getTo().getBlock() != event.getFrom().getBlock()) {
//            Island island = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getFrom().getWorld());
//            Island island1 = CloudSkyblock.getPlugin().getIslandManager().getIslandByLocation(event.getTo());
//            if (island != null && island.inIslandRegion(player) && island.isProtected() && island.isIslandMember(player.getUniqueId()) && island1 == null) {
//                event.setCancelled(true);
//                player.sendMessage(ChatColor.RED + "You cannot leave your island");
//            }
//        }
//    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (islandOptional.isPresent()) {
            Island island = islandOptional.get();
            if (island.inIslandRegion(player) && island.isProtected()) {
//                if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
//                    event.setCancelled(true);
//                    player.sendMessage(ChatColor.RED + "You can't do that here");
//                    return;
//                }
                if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
                    CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot edit signs in the island temple.");
                    event.setCancelled(true);
                    return;
                }
                if (!island.isIslandMember(player.getUniqueId())) {

                    CustomChatMessage.sendMessage(player, "Skyblock", "You cannot place a sign here");
                    event.setCancelled(true);
                } else {
                    if (event.getLine(0) != null && event.getLine(0).equalsIgnoreCase("[Shop]") && event.getLines().length >= 2) {
                        double price = 0.0D;
                        int amount = 0;
                        try {
                            price = Double.parseDouble(event.getLine(1).trim());
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Please include a proper price!");
                            event.setCancelled(true);
                            return;
                        }
                        try {
                            amount = Integer.parseInt(event.getLine(2).trim());
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Please include a proper amount!");
                            event.setCancelled(true);
                            return;

                        }
                        if (amount < 1) {
                            player.sendMessage(ChatColor.RED + "Please include a proper amount!");
                            event.setCancelled(true);
                            return;
                        }
                        if (price <= 0) {
                            player.sendMessage(ChatColor.RED + "Please include a proper price!");
                            event.setCancelled(true);
                            return;
                        }
                        ShopType shopType = ShopType.getShopTypeByName(event.getLine(3).trim().toUpperCase());
                        if (shopType == null) {
                            player.sendMessage(ChatColor.RED + "Please include a proper shop type!");
                            event.setCancelled(true);
                            return;
                        }
                        Sign sign = (Sign) event.getBlock().getState().getData();
                        if (!sign.isWallSign()) {
                            player.sendMessage(ChatColor.RED + "The sign must be a wall sign!");
                            event.setCancelled(true);
                            return;
                        }
                        Block block = event.getBlock().getRelative(sign.getAttachedFace());

                        if (block == null || block.getType() != Material.CHEST) {
                            player.sendMessage(ChatColor.RED + "The sign must be attached to a chest!");
                            event.setCancelled(true);
                            return;
                        }
                        event.setLine(0, ChatColor.BLACK + ChatColor.BOLD.toString() + "[Shop]");
                        event.setLine(1, ChatColor.BLACK + "Click with an item");
                        event.setLine(2, ChatColor.BLACK + "to setup the shop");
                        event.setLine(3, ChatColor.DARK_RED + ChatColor.BOLD.toString() + player.getName());
                        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "---------------------------------------");
                        player.sendMessage(ChatColor.AQUA + "Shop placed left click with item to " + shopType.toString().toLowerCase() + ".");
                        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "---------------------------------------");

                        Shop shop = new Shop(UUID.randomUUID(), ShopStage.CREATION, shopType, price, amount, player.getUniqueId(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ(), -1, -1);
                        island.addShop(shop);

                    }
                }
            }
        }
    }

    /**
     * Disables any block breaking on an island.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return;

        Optional<Island> optionalIsland = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (optionalIsland.isPresent()) {

            Island island = optionalIsland.get();

            if (!island.isIslandMember(player.getUniqueId()) && !island.getIslandSettingEnabled(IslandSettings.PUBLIC_BUILD)) {
                CustomChatMessage.sendMessage(player, "Skyblock", "You cannot place blocks on a protected island unless you're a member!");
                event.setCancelled(true);
                return;
            }
//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
//                event.setCancelled(true);
//                player.sendMessage(ChatColor.RED + "You can't do that here");
//                return;
//            }
            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockY(), event.getBlock().getLocation().getBlockZ()))) {
                CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot build in the island temple.");
                event.setCancelled(true);
                return;
            }
            if (island.inIslandRegion(player) && island.isProtected() && island.isIslandMember(player.getUniqueId()) && event.getBlock().getType() == Material.MOB_SPAWNER) {
                GolemSpawner golemSpawner = island.getGolemSpawner(event.getBlock().getLocation());
                if (golemSpawner != null) {
                    event.setCancelled(true);
                    if (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {

                        ItemStack spawnerItemStack = SpawnerUtils.createSpawnerItemStack(new GolemSpawnerInfo(golemSpawner.getTier(), golemSpawner.getEntityType(), golemSpawner.getGolemType()));
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), spawnerItemStack);
                    }
                    if (golemSpawner.getCount() > 1) {
                        golemSpawner.setCount(golemSpawner.getCount() - 1);
                        golemSpawner.updateDisplayName();
                        island.update();
                        CustomChatMessage.sendMessage(player, "Spawner", "1x spawner removed from " + golemSpawner.getDisplayName());
                    } else {
                        island.deleteSpawner(golemSpawner.getLocation(player.getWorld()));
                        CustomChatMessage.sendMessage(player, "Spawner", "Broke spawner " + golemSpawner.getDisplayName());
                        event.getBlock().setType(Material.AIR);
                        island.decreaseSpawnerCount();
                        island.update();
                        return;
                    }
                    golemSpawner.updateDisplayName();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        if (event.getSpawner().getWorld().getName().equals("world") || event.getSpawner().getWorld().getName().equals("world_nether") || event.getSpawner().getWorld().getName().equals("world_the_end"))
            return;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(event.getEntity().getWorld());

        if (islandOptional.isPresent()) {
            Island island = islandOptional.get();

//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getSpawner().getLocation().getBlock().getLocation().getBlockX(), event.getSpawner().getLocation().getBlock().getLocation().getBlockY(), event.getSpawner().getLocation().getBlock().getLocation().getBlockZ()))) {
//                event.setCancelled(true);
//                return;
//            }

            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getSpawner().getLocation().getBlock().getLocation().getBlockX(), event.getSpawner().getLocation().getBlock().getLocation().getBlockY(), event.getSpawner().getLocation().getBlock().getLocation().getBlockZ()))) {
                event.setCancelled(true);
                return;
            }

            GolemSpawner golemSpawner = island.getGolemSpawner(event.getSpawner().getLocation());
            if (golemSpawner != null) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Handles any interaction on an island such as chests etc and disables them.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether") || player.getWorld().getName().equals("world_the_end"))
            return;

        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(player.getWorld());

        if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) return;

        if (islandOptional.isPresent()) {

            Island island = islandOptional.get();

            if (!island.isIslandMember(player.getUniqueId()) && !island.getIslandSettingEnabled(IslandSettings.PUBLIC_INTERACT)) {
                CustomChatMessage.sendMessage(player, "Skyblock", "You cannot interact with anything on a protected island unless you're a member!");
                event.setCancelled(true);
                return;
            }
//
//            if (new CuboidRegion(island.getBridgeMin(), island.getBridgeMax()).contains(new Vector(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockY(), event.getClickedBlock().getLocation().getBlockZ()))) {
//                event.setCancelled(true);
//                player.sendMessage(ChatColor.RED + "You can't do that here");
//                return;
//            }

            if (new CuboidRegion(island.getTempleMin(), island.getTempleMax()).contains(new Vector(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockY(), event.getClickedBlock().getLocation().getBlockZ()))) {
                CustomChatMessage.sendMessage(player, "Skyblock Protection", "You cannot do that in the island temple.");
                event.setCancelled(true);
                return;
            }

            if ((island.inIslandRegion(player) && island.isProtected() && island.isIslandMember(player.getUniqueId())) && event.hasBlock() && event.getAction().toString().contains("RIGHT") && event.getClickedBlock().getType() == Material.MOB_SPAWNER && (island.getGolemSpawner(event.getClickedBlock().getLocation()) != null)) {
                GolemSpawner golemSpawner = island.getGolemSpawner(event.getClickedBlock().getLocation());
                new SpawnerMenu(player, island, golemSpawner);
            }
        }
    }

    private void getTotalBlocks(Island island, Material type, Callback<Integer> blocks) {

        new BukkitRunnable() {
            @Override
            public void run() {

                int blockCount = 0;
                int radius = island.getProtectionDistance();

                for (double x = island.getCentralX() - radius; x <= island.getCentralX() + radius; x++) {
                    for (double y = 0; y <= island.getWorld().getMaxHeight(); y++) {
                        for (double z = island.getCentralZ() - radius; z <= island.getCentralZ() + radius; z++) {
                            Location location = new Location(island.getWorld(), x, y, z);
                            if (location.getBlock().getType() == Material.AIR) {
                                continue;
                            }

                            Material block = location.getBlock().getType();

                            if (block == type) {
                                blockCount += 1;
                            }
                        }
                    }
                }

                blocks.call(blockCount);
            }
        }.runTaskAsynchronously(CloudSkyblock.getPlugin());
    }

//    @EventHandler
//    public void onChange(PlayerChangedWorldEvent event) {
//        Player player = event.getPlayer();
//        World islandWorld = event.getFrom();
//
//        Optional<Island> islandOptional = CloudSkyblock.getPlugin().getIslandManager().getIslandByWorld(islandWorld);
//        boolean keepLoaded = false;
//
//        if (islandOptional.isPresent()) {
//            Island island = islandOptional.get();
//            for (Player all : islandWorld.getPlayers()) {
//                if (island.getIslandOwner().equals(player.getUniqueId()) || island.getIslandMembers().containsKey(all.getUniqueId())) {
//                    keepLoaded = true;
//                    break;
//                }
//            }
//
//            if (!keepLoaded) {
//                CloudSkyblock.getPlugin().getIslandManager().unloadIsland(island, () -> {
//                    System.out.println("Unloaded successfully.");
//                }, () -> {
//                    System.out.println("Failed to unload island.");
//                });
//            }
//        }
//    }
}
