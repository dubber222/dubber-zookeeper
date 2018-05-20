package com.dubber.zookeeper.distributedlock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author dubber
 * @date 2018/5/20
 *
 */
public class LockWatcher implements Watcher {
    private CountDownLatch countDownLatch;

    public LockWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType().equals(Event.EventType.NodeDeleted)){
            countDownLatch.countDown();
        }
    }
}
