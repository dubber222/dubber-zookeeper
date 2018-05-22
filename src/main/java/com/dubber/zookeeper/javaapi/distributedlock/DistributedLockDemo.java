package com.dubber.zookeeper.javaapi.distributedlock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Demo class
 *
 * @author dubber
 * @date 2018/5/20
 *
 * 分布式锁的实现
 */
public class DistributedLockDemo {
    /**
     * 根节点
     */
    private static final String ROOT_LOCKS = "/LOCKS";

    private ZooKeeper zookeeper;
    /**
     * 会话超时时间
     */
    private int sessionTimeOut;
    /**
     * 记录锁id
     */
    private String lockId;

    private final static byte[] data = {1, 2};

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public DistributedLockDemo() throws IOException {
        this.zookeeper = ZookeeperSession.getZookeeperCli();
        this.sessionTimeOut = ZookeeperSession.getSessionTimeout();
    }

    /**
     *  // 获取锁
     * @return
     */
    public boolean lock() {
        try {
            //临时有序节点  如：LOCKS/0000000012
            lockId = zookeeper.create(ROOT_LOCKS + "/", data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "--> 成功创建了lock节点[" + lockId + "]，开始去竞争锁");

            List<String> childrenNodes = zookeeper.getChildren(ROOT_LOCKS, true);
            //排序， 从小到大
            TreeSet<String> sortedSet = new TreeSet<>();

            for (String childrenNode : childrenNodes) {
                sortedSet.add(ROOT_LOCKS + "/" + childrenNode);
            }
            //拿到最小的节点
            String first = sortedSet.first();
            if (lockId.equals(first)) {
                System.out.println(Thread.currentThread().getName() + "--> 成功获得锁，lock节点为：[" + lockId + "]");
                return true;
            }

            // lessSortedSet 获取比当前lockId节点小的节点集
            SortedSet<String> lessSortedSet = sortedSet.headSet(lockId);
            if (lessSortedSet != null) {
                //拿到比当前lockId节点小的上一个节点（临近节点）
                String preLockId = lessSortedSet.last();

                // 会话超时或者preLockId节点被释放，当前节点获得锁
                zookeeper.exists(preLockId, new LockWatcher(countDownLatch));
                countDownLatch.await(sessionTimeOut, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + "--> 成功获得锁：[" + lockId + "]");
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 释放锁
     */
    public boolean unlock() {
        System.out.println(Thread.currentThread().getName() + "--> 开始释放锁：[" + lockId + "]");
        try {
            zookeeper.delete(lockId, -1);
            System.out.println(Thread.currentThread().getName() + "--> 节点[" + lockId + "]成功被删除");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {
        int num = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(num);

        ExecutorService fixedThreadPool = newFixedThreadPool(10);
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            fixedThreadPool.execute(() -> {
                DistributedLockDemo lock = null;
                try {
                    lock = new DistributedLockDemo();
                    countDownLatch.countDown();
                    countDownLatch.await();
                    lock.lock();
                    Thread.sleep(random.nextInt(500));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (lock != null) {
                        lock.unlock();
                    }
                }
            });
        }
    }
}
