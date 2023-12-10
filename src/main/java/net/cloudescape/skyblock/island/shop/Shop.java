package net.cloudescape.skyblock.island.shop;

import com.cloudescape.CloudCore;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerContainer;
import com.cloudescape.skyblock.skyblockplayer.SkyblockPlayerWrapper;
import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 4/9/2018.
 */
public class Shop {
    private UUID uuid;
    private ShopStage stage;
    private ShopType type;
    private double price;
    private int amount;
    private UUID playerUuid;
    private int x;
    private int y;
    private int z;
    private int itemId;
    private Item item;
    private int itemData;

    public Shop(UUID uuid, ShopStage stage, ShopType type, double price, int amount, UUID playerUuid, int x, int y, int z, int itemId, int itemData) {
        this.uuid = uuid;
        this.stage = stage;
        this.type = type;
        this.price = price;
        this.amount = amount;
        this.playerUuid = playerUuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.itemId = itemId;
        this.itemData = itemData;
    }

    public boolean isOutOfMoney(double amount) {
        return SkyblockPlayerWrapper.getBalance(playerUuid) < amount;
    }

    public boolean buy(Player player, int amount) {

        SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());
        SkyblockPlayerContainer skyBlockPlayer1 = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(playerUuid);

        Block chestBlock = getChestBlock(player.getWorld());
        if (chestBlock != null) {
            Chest chest = (Chest) chestBlock.getState();
            if (!chest.getInventory().contains(Material.getMaterial(itemId))) {
                player.sendMessage(ChatColor.RED + "The shop is sold out");
                return false;
            }


            if (player.getUniqueId().toString().equals(playerUuid.toString())) {
                player.sendMessage(ChatColor.RED + "You cannot buy from yourself.");
                return false;
            }
            if (SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID()) < price) {
                player.sendMessage(ChatColor.RED + "You don't have enough money");
                return false;
            }

            int totalAmount = 0;
            List<Integer> toRemoveSlotList = new ArrayList<>();
            for (int i = 0; i < chest.getInventory().getContents().length; i++) {
                ItemStack itemStack = player.getInventory().getContents()[i];
                if (itemStack != null && itemStack.getType().getId() == itemId && itemStack.getDurability() == itemData) {
                    if (totalAmount >= amount) {
                        break;
                    }
                    if (itemStack.getAmount() > amount) {
                        totalAmount += amount;
                        itemStack.setAmount(itemStack.getAmount() - amount);
                        continue;
                    } else {
                        totalAmount += itemStack.getAmount();
                        toRemoveSlotList.add(i);
                    }
                }
            }
            for (Integer integer : toRemoveSlotList) {
                chest.getInventory().setItem(integer, new ItemStack(Material.AIR));
            }
            chest.update(true,true);
            for (HumanEntity humanEntity : chest.getInventory().getViewers()){

                humanEntity.closeInventory();
                humanEntity.openInventory(chest.getInventory());
            }
            player.updateInventory();
            player.sendMessage(ChatColor.AQUA + "Bought " + amount + " " + Material.getMaterial(itemId).toString().toLowerCase() + "(s) for $" + new DecimalFormat("#,###.##").format(price));
            player.getInventory().addItem(new ItemStack(Material.getMaterial(itemId), amount, (short) itemData));

            SkyblockPlayerWrapper.setBalance(skyBlockPlayer1.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer1.getUniqueID())+price);
            SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())-price);
            Player player1 = Bukkit.getPlayer(playerUuid);
            if (player1 != null && player1.isOnline()) {
                player1.sendMessage(ChatColor.AQUA + player.getName() + " has bought " + +amount + " " + Material.getMaterial(itemId).toString().toLowerCase() + "(s) for $" + new DecimalFormat("#,###.##").format(price));
            }

            return true;
        }
        return false;
    }

    public boolean sell(Player player, int amount) {

        SkyblockPlayerContainer skyBlockPlayer = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(player.getUniqueId());
        SkyblockPlayerContainer skyBlockPlayer1 = CloudCore.getInstance().getSkyblockPlayerWrapper().getPlayerByUUID(playerUuid);

        if (isOutOfMoney(price)) {
            player.sendMessage(ChatColor.RED + "The shop owner doesn't have enough money.");
            return false;
        }
        if (player.getUniqueId().toString().equals(playerUuid.toString())) {
            player.sendMessage(ChatColor.RED + "You cannot sell to yourself.");
            return false;
        }
        if (isShopFull(player.getWorld())) {
            player.sendMessage(ChatColor.RED + "The shop is full.");
            return false;
        }
        int totalAmount = 0;
        List<Integer> toRemoveSlotList = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getContents()[i];
            if (itemStack != null && itemStack.getType().getId() == itemId && itemStack.getDurability() == itemData) {
                if (totalAmount >= amount) {
                    break;
                }
                if (itemStack.getAmount() > amount) {
                    totalAmount += amount;
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    continue;
                } else {
                    totalAmount += itemStack.getAmount();
                    toRemoveSlotList.add(i);
                }
            }
        }
        for (Integer integer : toRemoveSlotList) {
            player.getInventory().setItem(integer, new ItemStack(Material.AIR));
        }

        player.updateInventory();
        player.sendMessage(ChatColor.AQUA + "Sold " + amount + " " + Material.getMaterial(itemId).toString().toLowerCase() + "(s) for $" + new DecimalFormat("#,###.##").format(price));

        SkyblockPlayerWrapper.setBalance(skyBlockPlayer1.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer1.getUniqueID())-price);
        SkyblockPlayerWrapper.setBalance(skyBlockPlayer.getUniqueID(),SkyblockPlayerWrapper.getBalance(skyBlockPlayer.getUniqueID())+price);

        Player player1 = Bukkit.getPlayer(playerUuid);
        if (player1 != null && player1.isOnline()) {
            player1.sendMessage(ChatColor.AQUA + player.getName() + " has sold " + +amount + " " + Material.getMaterial(itemId).toString().toLowerCase() + "(s) for $" + new DecimalFormat("#,###.##").format(price));
        }

        Block chestBlock = getChestBlock(player.getWorld());
        if (chestBlock != null) {
            Chest chest = (Chest) chestBlock.getState();
            chest.getInventory().addItem(new ItemStack(Material.getMaterial(itemId), amount, (short) itemData));
            chest.update(true);
        }
        return true;
    }

    public Block getChestBlock(World world) {
        Block signBlock = world.getBlockAt(x, y, z);
        if (signBlock.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) signBlock.getState().getData();
            Block relative = signBlock.getRelative(sign.getAttachedFace());
            return (relative != null && relative.getType() == Material.CHEST) ? relative : null;
        }
        return null;
    }

    public boolean isShopFull(World world) {
        Block chestBlock = getChestBlock(world);
        if (chestBlock != null) {
            Chest chest = (Chest) chestBlock.getState();
            Inventory blockInventory = chest.getBlockInventory();
            int spaceRemaining = 0;
            for (ItemStack itemStack : blockInventory.getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    spaceRemaining++;
                }
            }
            return spaceRemaining == 0;
        }
        return false;
    }

    public void setStage(ShopStage stage) {
        this.stage = stage;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isItemSpawned() {
        return item != null && item.isValid() && (!item.isDead());
    }

    public Item spawnItem(World world) {
        Location location = new Location(world, this.x, this.y, this.z);

        ItemStack itemStack = new ItemStack(Material.getMaterial(this.itemId), (this.amount > 64) ? 64 : this.amount, (short) this.itemData);
        this.item = world.dropItem(location, itemStack);
        this.item.teleport(location);
        this.item.setSilent(true);
        this.item.setInvulnerable(true);
        this.item.setMetadata("no_pickup", new FixedMetadataValue(CloudSkyblock.getPlugin(), true));
        this.item.setMetadata("no_merge", new FixedMetadataValue(CloudSkyblock.getPlugin(), true));
        this.item.setCustomNameVisible(true);
        this.item.setPickupDelay(Integer.MAX_VALUE);
        this.item.setCustomName(ChatColor.AQUA + this.item.getCustomName());
        Location location1 = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        location1.add(-0.3e5, 0, 0.65);
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location1, EntityType.ARMOR_STAND);
        armorStand.setAI(false);
        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(true);
        armorStand.setVisible(false);
        armorStand.setBodyPose(EulerAngle.ZERO);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        armorStand.setSmall(true);
        armorStand.setMarker(false);
        armorStand.setBasePlate(false);
        armorStand.setCanPickupItems(false);
        armorStand.addPassenger(item);
        return item;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemData() {
        return itemData;
    }

    public void setItemData(int itemData) {
        this.itemData = itemData;
    }

    public void setType(ShopType type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public ShopStage getStage() {
        return stage;
    }

    public ShopType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isItemNotSpawned() {
        return !isItemSpawned();
    }
}
