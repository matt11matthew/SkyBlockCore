package net.cloudescape.skyblock.island.temple.npc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew E on 5/12/2018.
 */
public class NpcManager {
    private Map<String, TempleNpc> npcMap;

    public NpcManager() {
        this.npcMap = new ConcurrentHashMap<>();
    }
}
