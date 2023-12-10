package net.cloudescape.skyblock.schematics.newsystem.runnable;

import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew E on 4/8/2018.
 */
public abstract class CloudRunnable implements Runnable {

    private String name;
    private CloudRunnableType cloudRunnableType;
    private long runDelay;
    private boolean cancelled;
    private long delay;
    private TimeUnit timeUnit;

    public CloudRunnable(String name, CloudRunnableType cloudRunnableType, long delay, TimeUnit timeUnit) {
        this.name = name;
        this.cancelled = false;

        this.cloudRunnableType = cloudRunnableType;
        this.delay = delay;
        this.timeUnit = timeUnit;
        run();
    }

    public void setRunDelay(long runDelay) {
        this.runDelay = runDelay;
    }

    public String getName() {
        return name;
    }

    public CloudRunnableType getCloudRunnableType() {
        return cloudRunnableType;
    }

    public long getRunDelay() {
        return runDelay;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public boolean isReadyToRun() {
        return  System.currentTimeMillis()>runDelay;
    }

    public abstract void cloudRun();

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void run() {
        if (this.cancelled) {
            return;
        }
        cloudRun();
        this.runDelay = System.currentTimeMillis() + timeUnit.toMillis(delay);
    }

    public void cancel() {
        this.cancelled = true;
    }
}
