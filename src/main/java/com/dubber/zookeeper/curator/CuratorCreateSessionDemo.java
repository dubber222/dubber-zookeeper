package com.dubber.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by dubber on 2018/5/19.
 */

public class CuratorCreateSessionDemo {
    private String serverIpAddress;
    private int connectTimeout;

    public static CuratorCreateSessionDemo getInstance() {
        return InnerHolder.INSTANCE;
    }

    private static class InnerHolder {
        static final CuratorCreateSessionDemo INSTANCE = new CuratorCreateSessionDemo();
    }

    public void init() {
        serverIpAddress = "192.168.0.110:2181,192.168.0.104:2181,192.168.0.106:2181";
        connectTimeout = 5000;
    }

    public void start() {
        //创建会话的两种方式
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.newClient(serverIpAddress, 5000, 5000,
                        new ExponentialBackoffRetry(1000, 3));
        //start方法启动
        curatorFramework.start();

        //fluent风格
        CuratorFramework curatorFramework1 =CuratorFrameworkFactory.builder().connectString(serverIpAddress).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();

        /**
         * 重试策略
         */

        System.out.println("success");

    }
}

class MainT {
    public static void main(String[] args) {
        System.out.println("curator 开始了……");
        CuratorCreateSessionDemo curatorTest = CuratorCreateSessionDemo.getInstance();
        curatorTest.init();
        curatorTest.start();

        System.out.println("curator 结束了……");
    }
}

