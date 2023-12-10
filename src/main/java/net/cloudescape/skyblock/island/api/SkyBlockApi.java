package net.cloudescape.skyblock.island.api;

import net.cloudescape.skyblock.utils.IslandUtils;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 5/4/2018.
 */
public class SkyBlockApi implements Api {
    private static SkyBlockApi api;

    public SkyBlockApi() {
        api = this;
    }

    public static SkyBlockApi getApi() {
        if (api == null) {
            api = new SkyBlockApi();
        }
        return api;
    }

    @Override
    public boolean isIslandLoaded(Player player) {
        return IslandUtils.getLoadedIsland(player) != null;
    }
}
