package net.cloudescape.skyblock.database.skyblockplayer;

import java.util.UUID;

/**
 * Created by Matthew E on 4/14/2018.
 */
public class SkyBlockPlayer {
    private final UUID uuid;
    private UUID islandUuid;
    private long flyTime;
    private boolean isActive;

    public SkyBlockPlayer(UUID uuid, UUID islandUuid, boolean flyActive, long flyTime) {
        this.uuid = uuid;
        this.islandUuid = islandUuid;
        this.isActive = flyActive;
        this.flyTime = flyTime;
    }


    public UUID getUuid() {
        return uuid;
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public void setIslandUuid(UUID islandUuid) {
        this.islandUuid = islandUuid;
    }

    public long getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(long time) {
        this.flyTime = time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
