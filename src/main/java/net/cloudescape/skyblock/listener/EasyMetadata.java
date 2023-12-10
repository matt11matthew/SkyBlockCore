package net.cloudescape.skyblock.listener;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Matthew E on 4/16/2018.
 */
public class EasyMetadata<V> extends FixedMetadataValue {
    public EasyMetadata(V o) {
        super(CloudSkyblock.getPlugin(), o);
    }
}
