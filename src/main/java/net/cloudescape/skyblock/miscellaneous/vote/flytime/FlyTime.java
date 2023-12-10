package net.cloudescape.skyblock.miscellaneous.vote.flytime;

import net.cloudescape.skyblock.database.skyblockplayer.SkyBlockPlayer;
import net.cloudescape.skyblock.database.skyblockplayer.SkyblockPlayerWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlyTime implements Listener {

    public void addFlyTime(long amount){}

    public void removeFlyTime(long amount){}

    public void sendFlyTime(SkyBlockPlayer player, SkyBlockPlayer target){}

    @EventHandler
    public void move(PlayerMoveEvent event){
        SkyBlockPlayer skyBlockPlayer = SkyblockPlayerWrapper.playerMap.get(event.getPlayer().getUniqueId());

        if(skyBlockPlayer.isActive()) {

        }
            // check time
    }
}
