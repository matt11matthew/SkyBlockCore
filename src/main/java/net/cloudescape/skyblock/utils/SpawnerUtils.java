package net.cloudescape.skyblock.utils;

import com.cloudescape.customenchant.utilities.NbtTagUtils;
import net.cloudescape.skyblock.island.spawner.GolemSpawnerInfo;
import net.cloudescape.skyblock.island.spawner.GolemType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Matthew E on 4/22/2018.
 */
public class SpawnerUtils {
    public static ItemStack createSpawnerItemStack(GolemSpawnerInfo golemSpawnerInfo) {
        ItemStack itemStack = new ItemStack(Material.MOB_SPAWNER, 1);
        itemStack = SpawnerUtils.setEntityTypeOfSpawnerItemStack(itemStack, golemSpawnerInfo.getEntityType());

        ItemMeta itemMeta = itemStack.getItemMeta();
        int percent = golemSpawnerInfo.getTier() * 20;

        if (golemSpawnerInfo.isIronGolem()) {
            itemMeta.setDisplayName(ChatColor.AQUA + StringUtil.capitalizeWords(golemSpawnerInfo.getEntityType().getName(), "_") + " Spawner " + ChatColor.GRAY + "(" + ChatColor.WHITE + percent + "%" + ChatColor.GRAY + ") " + ChatColor.GRAY + "(" + golemSpawnerInfo.getGolemType().getColor() + golemSpawnerInfo.getGolemType().getName() + ChatColor.GRAY + ")");
        } else {
            itemMeta.setDisplayName(ChatColor.AQUA + StringUtil.capitalizeWords(golemSpawnerInfo.getEntityType().getName(), "_") + " Spawner " + ChatColor.GRAY + "(" + ChatColor.WHITE + percent + "%" + ChatColor.GRAY + ")");
        }
        itemStack.setItemMeta(itemMeta);
        itemMeta.addItemFlags(ItemFlag.values());

        itemStack = NbtTagUtils.setTagInt(itemStack, "skySpawnerTier", golemSpawnerInfo.getTier());
        itemStack = NbtTagUtils.setTagString(itemStack, "skySpawnerType", golemSpawnerInfo.getEntityType().toString());

        if (golemSpawnerInfo.isIronGolem()) {
            itemStack = NbtTagUtils.setTagString(itemStack, "skySpawnerGolemType", golemSpawnerInfo.getGolemType().toString());
        }
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);//Make it glowing
        return itemStack;
    }

    public static GolemSpawnerInfo getSpawnerInfoFromSpawnerItemStack(ItemStack itemStack) {
        EntityType entityTypeFromItemStack = getEntityTypeFromItemStack(itemStack);
        if (entityTypeFromItemStack != null && (NbtTagUtils.hasTagKey(itemStack, "skySpawnerType") && (NbtTagUtils.hasTagKey(itemStack, "skySpawnerTier")))) {
            GolemType golemType = null;
            EntityType entityType = EntityType.valueOf(NbtTagUtils.getTagString(itemStack, "skySpawnerType"));
            int tier = NbtTagUtils.getTagInt(itemStack, "skySpawnerTier");
            if (entityTypeFromItemStack == EntityType.IRON_GOLEM) {
                golemType = GolemType.valueOf(NbtTagUtils.getTagString(itemStack, "skySpawnerGolemType"));
            }
            if (entityType == entityTypeFromItemStack) {
                return new GolemSpawnerInfo(tier, entityType, golemType);
            }
        }
        return null;
    }

    public static EntityType getEntityTypeFromItemStack(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getType() == Material.MOB_SPAWNER) {
            if (NbtTagUtils.hasTagKey(itemStack, "skySpawnerType")) {
                return EntityType.valueOf(NbtTagUtils.getTagString(itemStack, "skySpawnerType"));
            }
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
            if (blockStateMeta.hasBlockState()) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
                return creatureSpawner.getSpawnedType();
            }
        }
        return null;
    }

    public static ItemStack setEntityTypeOfSpawnerItemStack(ItemStack itemStack, EntityType entityType) {
        if (itemStack.getType() == Material.MOB_SPAWNER) {
            itemStack = NbtTagUtils.setTagString(itemStack, "skySpawnerType", entityType.toString());
            BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
            if (blockStateMeta.hasBlockState()) {
                CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
                creatureSpawner.setSpawnedType(entityType);
                blockStateMeta.setBlockState(creatureSpawner);
                itemStack.setItemMeta(blockStateMeta);
            }
        }
        return itemStack;
    }

    public static Block setEntityTypeOfSpawner(Block block, EntityType entityType) {
        if (block.getType() == Material.MOB_SPAWNER) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            creatureSpawner.setSpawnedType(entityType);
            creatureSpawner.update(true);
        }
        return block;
    }

//    public static void spawnCopyOfEntity(LivingEntity entity) {
//        LivingEntity livingEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
//        livingEntity.setMetadata("gtier", new EasyMetadata<>(entity.getMetadata("gtier").get(0)));
//        livingEntity.setMetadata("gcount", new EasyMetadata<>(entity.getMetadata("gcount").get(0).asInt()-1));
//        if (entity instanceof IronGolem) {
//            livingEntity.setMetadata("gtype", new EasyMetadata<>(entity.getMetadata("gtype").get(0).asInt()-1));
//        }
//        livingEntity.setCustomName(entity.getCustomName());
//    }
}
