package net.cloudescape.skyblock.miscellaneous.minions.animations;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.miscellaneous.minions.minions.Miner;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 * Created by Chronic Ninjaz on 09/04/2018.
 */
public class MinerAnimation extends Animation {
    private Miner miner;
    private double armPos;
    private boolean down;
    private boolean broken;

    public MinerAnimation(Miner miner){
        this.miner = miner;
        this.armPos = -0.1;
        this.down = true;
        this.broken = false;

        runTaskTimer(CloudSkyblock.getPlugin(), 0L, 2L);
    }

    @Override
    public void displayParticles() {
        if(miner == null || miner.getMinion() == null||!miner.isSpawned()){
            return;
        }
        // just a test... change the particle to whatever... I dont care, do like a black dust, cba to find out witch one it is so change it <3
       try {
           PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_HUGE, true, 1, 1, 1, 4, 4, 4, 0, 3);
           miner.getMinion().getNearbyEntities(10, 10, 10).stream().forEach(entity -> {
               if(entity instanceof Player){
                   ((CraftPlayer)entity).getHandle().playerConnection.sendPacket(packet);
               }
           });
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public void run() {
        if(miner == null || miner.getMinion() == null||!miner.isSpawned()){
           return;
        }

        miner.getMinion().setRightArmPose(new EulerAngle(- Math.abs(armPos), 0, 0));
        if(down){
            armPos-=0.2;

            if(armPos <= 0.6)
                down = false;

            if(!broken && armPos >= 1.2 && armPos <= 1.4){
                Block block = miner.getFacingBlock();

                broken = true;

                if(block == null)
                    return;

                if(block.getType() == Material.AIR)
                    return;

                miner.getMinion().getLocation().getWorld().dropItem(miner.getMinion().getLocation().add(0, 3, 0), new ItemStack(block.getType(), 1));
                miner.setBlocksMined(miner.getBlocksMined()+1);
            }

            if (broken) {
                miner.getFacingBlock().setType(Material.AIR);
            }
        }
        else {
            armPos+=0.2;

            if(armPos>= 2){
                down = true;
                broken = false;
            }
        }
    }
}
