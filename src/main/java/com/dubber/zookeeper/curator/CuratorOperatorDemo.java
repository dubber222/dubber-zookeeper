package com.dubber.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dubber on 2018/5/19.
 */
public class CuratorOperatorDemo {
    private String serverIpAddress;
    private int connectTimeout;

    private CuratorFramework curatorFramework;

    public CuratorOperatorDemo() {
    }

    public CuratorOperatorDemo(String init) {
        init();
    }

    public static CuratorOperatorDemo getInstance() {
        return InnerHolder.INSTANCE;
    }

    public static CuratorFramework getCuratorFramework() {
        return InnerHolder2.INSTANCE.curatorFramework;
    }

    private static class InnerHolder {
        static final CuratorOperatorDemo INSTANCE = new CuratorOperatorDemo();
    }

    private static class InnerHolder2{
        static final CuratorOperatorDemo INSTANCE = new CuratorOperatorDemo("123");

    }

    public void init() {
        serverIpAddress = "192.168.0.110:2181,192.168.0.104:2181,192.168.0.106:2181";
        connectTimeout = 5000;
        //fluent风格
        // namespace  node  在指定根节点操作
        curatorFramework = CuratorFrameworkFactory.builder().connectString(serverIpAddress).sessionTimeoutMs(5000)
                .namespace("node").retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        curatorFramework.start();
        System.out.println("连接 === > success");
    }

    public void start() throws InterruptedException {
        /**
         * 创建节点
         */
        /*try {
            String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .forPath("/curator/curator1-1/curator1-1-1");
            System.out.println("curator create result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /**
         * 删除节点
         */
        /*try {
            curatorFramework.delete().deletingChildrenIfNeeded().forPath("/curator");
            System.out.println("curator delete success! ");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /**
         * 查询节点数据
         */
       /* Stat stat = new Stat();
        try {
            byte[] result = curatorFramework.getData().storingStatIn(stat).forPath("/curator");
            System.out.println(new String(result) + "stat --> " + stat);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /**
         * 修改节点数据
         */
        /*try {
            Stat stat1 = curatorFramework.setData().forPath("/", "777".getBytes());
            System.out.println(stat1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /**
         * 异步操作
         */
       /* final CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        try {
            String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            System.out.println(Thread.currentThread().getName() + "resultCode --> "
                                    + event.getResultCode() + "-->" + event.getType());
                            countDownLatch.countDown();
                        }
                    }, executorService).forPath("/curator2");
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.await();*/

        /**
         * 事务操作（curator独有的）
         */
        try {
            Collection<CuratorTransactionResult> resultCollections = curatorFramework.inTransaction().create()
                    .forPath("/curator3").and()
                    .setData().forPath("/curator2", "89".getBytes()).and().commit();

            for (CuratorTransactionResult result : resultCollections
                    ) {
                System.out.println(result.getForPath() + "-- >  "+ result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

class MainT2 {
    public static void main(String[] args) throws Exception {
        System.out.println("curator 开始了……");
        CuratorOperatorDemo curatorOperatorDemo = CuratorOperatorDemo.getInstance();
        curatorOperatorDemo.init();
        curatorOperatorDemo.start();

        System.out.println("curator 结束了……");
    }
}