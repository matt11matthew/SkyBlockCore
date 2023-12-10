package net.cloudescape.skyblock.schematics.newsystem;

/**
 * Created by Matthew E on 4/8/2018.
 */
public interface QueueNode<V> {

    Queue<QueueNode<V>> getQueue();

    int getIndex();

    void process();


}
