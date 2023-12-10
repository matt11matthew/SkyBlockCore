package net.cloudescape.skyblock.schematics.newsystem.runnable;

import net.cloudescape.skyblock.CloudSkyblock;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 4/8/2018.
 */
public class CloudRunnableManager {
    private Map<String, CloudRunnable> cloudRunnableMap;
    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public CloudRunnableManager() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.cloudRunnableMap = new ConcurrentHashMap<>();
    }

    public void startTimers() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CloudSkyblock.getPlugin(), () -> {
            cloudRunnableMap.values().stream().filter(CloudRunnable::isReadyToRun).filter(cloudRunnable -> !cloudRunnable.isCancelled()).forEach(cloudRunnable -> {
                switch (cloudRunnable.getCloudRunnableType()) {
                    case ASYNC:
                        executorService.submit(cloudRunnable);
                        break;
                    case SYNC:
                        cloudRunnable.run();
                        break;
                }
            });
        }, 1, 1);
    }

    private void scheduleCloudRunnable(CloudRunnable cloudRunnable) {
        if (!cloudRunnableMap.containsKey(cloudRunnable.getName())) {
            cloudRunnableMap.put(cloudRunnable.getName(), cloudRunnable);
        }
    }

    public void schedule(Runnable runnable, String name, CloudRunnableType type, long delay, TimeUnit timeUnit) {
        scheduleCloudRunnable(new CloudRunnable(name, type, delay, timeUnit) {
            @Override
            public void cloudRun() {
                runnable.run();
            }
        });
    }

    public void cancelTask(String name) {
        if (cloudRunnableMap.containsKey(name)) {
            cloudRunnableMap.get(name).cancel();
            cloudRunnableMap.remove(name);
        }
    }

    public void cancelAllTasks() {
        cloudRunnableMap.values().forEach(CloudRunnable::cancel);
        cloudRunnableMap.clear();
    }
}
