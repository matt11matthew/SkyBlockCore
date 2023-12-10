package net.cloudescape.skyblock.schematics.newsystem;

import net.cloudescape.skyblock.CloudSkyblock;
import net.cloudescape.skyblock.schematics.newsystem.runnable.CloudRunnableType;

import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 4/8/2018.
 */
public class Test {
    public static void test() {
        CloudSkyblock.getCloudRunnableManager().schedule(() -> {
            System.out.println("test");
        }, "test", CloudRunnableType.ASYNC, 1, TimeUnit.MINUTES);
    }
}
