package com.dubber.zookeeper.javaapi.distributedlock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Demo class
 *
 * @author dubber
 * @date 2018/5/20
 */
public class ZookeeperSession implements Watcher{

    private final static String CONNECT_STRING = "192.168.0.110:2181,192.168.0.104:2181,192.168.0.106:2181";
    private final static String CONNECT_STRING_2 = "192.168.49.137:2181,192.168.49.138:2181,192.168.49.139:2181";

    private static int sessionTimeout = 5000;
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static ZooKeeper getZookeeperCli() throws IOException {

        ZooKeeper zookeeper = new ZooKeeper(CONNECT_STRING_2, 5000, new ZookeeperSession());
        System.out.println(zookeeper.getState());
        try {
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "--> Zookeeper session established");
        return zookeeper;
    }
    @Override
    public void process(WatchedEvent event) {
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
    public static int getSessionTimeout() {
        return sessionTimeout;
    }

}
