package net.cloudescape.skyblock.miscellaneous.quest;

import com.cloudescape.utilities.countdown.executors.Executor;
import com.cloudescape.utilities.itemstack.ItemFactory;
import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.utils.EntityUtil;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class QuestPart {

    private int id;

    private Quest questBase;

    private String partName;
    private String[] description;
    private Location partLocation;

    private EntityType entityType;
    private Entity entity;

    private Executor executor;
    private ItemStack requiredToContinue;

    public QuestPart(Quest questBase, int id, String partName, String[] description, Location partLocation, EntityType entityType) {
        this.id = id;
        this.questBase = questBase;
        this.partName = partName;
        this.description = description;
        this.partLocation = partLocation;
        this.entityType = entityType;
    }

    public QuestPart(Quest questBase, int id, String partName, String[] description, Location partLocation, EntityType entityType, Executor executor) {
        this(questBase, id, partName, description, partLocation, entityType);
        this.executor = executor;
    }

    public int getId() {
        return id;
    }

    public String getPartName() {
        return partName;
    }

    public String[] getDescription() {
        return description;
    }

    public Location getPartLocation() {
        return partLocation;
    }

    public void setRequiredToContinue(ItemStack requiredToContinue) {
        this.requiredToContinue = requiredToContinue;
    }

    public ItemStack getRequiredToContinue() {
        return requiredToContinue;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void spawnEntity(Player player) {

        entity = partLocation.getWorld().spawnEntity(partLocation, entityType);

        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', partName));
        entity.setCustomNameVisible(true);

        entity.setMetadata("questPartBase", new FixedMetadataValue(CloudSkyblock.getPlugin(), questBase));
        entity.setMetadata("questPartId", new FixedMetadataValue(CloudSkyblock.getPlugin(), id));

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            EntityUtil.removeAI(livingEntity, false);
            livingEntity.setRemoveWhenFarAway(false);
            livingEntity.setInvulnerable(true);
        }

        if (entity instanceof Villager) {
            Villager villager = ((Villager) entity);
            villager.getInventory().setStorageContents(new ItemStack[] { new ItemFactory(Material.CHEST).setDisplayName("&c&lDO NOT OPEN").build() });
        }

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entity.getEntityId());

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == player) return;
            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(destroy);
        }
    }

    public void removeEntity() {
        entity.remove();
    }
}
