package com.dubber.zookeeper.zkclient;

/**
 * Created on 2018/5/18.
 *
 * @author dubber
 */
public class Test {

    public static void main(String[] args) throws Exception {
        System.out.println("zkClient 表演开始……");

        ZookeeperService zookeeperService = ZookeeperService.getInstance();
        zookeeperService.init();
        zookeeperService.start();

        System.out.println("zkClient 表演结束……");
    }
}
