package net.cloudescape.skyblock.schematics.newsystem;

/**
 * Created by Matthew E on 4/8/2018.
 */
public interface Queue<V extends QueueNode<?>> {
    V[] getQueueNodes();

    long getDelay();

    void finish();


    int getCurrentIndex();


    int getProcessPerQueue();

    default void process() {
        for (int i = 0; i < getProcessPerQueue(); i++) {
            if (getCurrentIndex() >= getQueueNodes().length) {
                finish();
                return;
            }
            V v = getQueueNodes()[getCurrentIndex()];
            v.process();
            setCurrentIndex(getCurrentIndex() + 1);
        }

    }

    void setCurrentIndex(int index);

}
