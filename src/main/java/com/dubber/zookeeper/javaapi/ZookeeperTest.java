package com.dubber.zookeeper.javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/5/17.
 *
 * @author dubber
 */
public class ZookeeperTest implements Watcher {
    private final static String CONNECT_STRING = "192.168.49.134:2181,192.168.49.132:2181,192.168.49.133:2181";
    static ZooKeeper zookeeper = null;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        System.out.println("开幕……");
        try {
            zookeeper = new ZooKeeper(CONNECT_STRING, 5000, new ZookeeperTest());
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //create("/data3","123");

//        delete("/data3");
        createChildren("/data2/data21", "333");
        listNode();

        System.out.println("落幕……");
    }

    public static void create(String path, Object value) throws Exception {
        //创建节点
        if (zookeeper.exists(path, new ZookeeperTest()) == null) {
            zookeeper.create(path, value.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(2);
        } else {
            //修改节点
            zookeeper.setData(path, value.toString().getBytes(), -1);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public static void delete(String path) throws Exception {
        //删除节点
        if (zookeeper.exists(path, new ZookeeperTest()) != null) {
            zookeeper.delete(path, -1);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public static void createChildren(String zpath, Object value) throws Exception {
        if (zookeeper.exists(zpath, new ZookeeperTest()) == null) {
            //创建子节点
            zookeeper.create(zpath, value.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            TimeUnit.SECONDS.sleep(2);
        } else {
            // 修改子节点
            zookeeper.setData(zpath, value.toString().getBytes(), -1);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    public static void listNode() throws Exception {
        //获取指定节点下的子节点
        List<String> childrenArry = zookeeper.getChildren("/", true);
        System.out.println(Arrays.toString(childrenArry.toArray()));
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("process……");
        Watcher.Event.KeeperState keeperState = event.getState();
        // SyncConnected 为zookeeper连接状态
        Watcher.Event.KeeperState connected = Watcher.Event.KeeperState.SyncConnected;

        //如果处于连接状态，监控客户端的操作   NodeCreated NodeDeleted NodeDataChanged NodeChildrenChanged
        if (keeperState == connected) {
            countDownLatch.countDown();
            Watcher.Event.EventType eventType = event.getType();
            System.out.println("eventType == >" + eventType);
            switch (eventType) {
                case None:
                    event_None(event);
                    break;
                case NodeCreated:
                    event_NodeCreated(event);
                    break;
                case NodeDeleted:
                    event_NodeDeleted(event);
                    break;
                case NodeDataChanged:
                    event_NodeDataChanged(event);
                    break;
                case NodeChildrenChanged:
                    event_NodeChildrenChanged(event);
                    break;
                default:
                    System.out.println("state:" + event.getState() + ", type" + event.getType());
            }

        } else {
            System.out.println("zookeeper 连接未成功！！");
        }
    }


    // Event.EventType : None
    public void event_None(WatchedEvent event) {
        System.out.println("\nNone =====> " + event.getState() + ", " + event.getType());
    }

    // Event.EventType : None
    public void event_NodeCreated(WatchedEvent event) {
        System.out.println("\nNodeCreated =====> " + event.getPath());
    }

    // Event.EventType : None
    public void event_NodeDeleted(WatchedEvent event) {
        System.out.println("\nNodeDeleted =====> " + event.getPath());
    }

    // Event.EventType : None
    public void event_NodeDataChanged(WatchedEvent event) {
        System.out.println("\nNodeDataChanged =====> " + event.getPath());
    }

    // Event.EventType : None
    public void event_NodeChildrenChanged(WatchedEvent event) {
        System.out.println("\nNodeChildrenChanged =====> " + event.getPath());
    }
}