package com.dubber.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.concurrent.TimeUnit;

/**
 * Created by dubber on 2018/5/19.
 */
public class CuratorEventDemo {
    /**
     * 三种 watcher 来做节点的监听
     * PathChildrenCache 监视一个路劲下子节点的创建、删除、节点数据更新
     * NodeCache 监视一个节点的创建、更新、删除
     * TreeCache PathChildrenCache 和 TreeCache的结合。
     */

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorOperatorDemo.getCuratorFramework();

        /**
         * 节点cache
         */
        final NodeCache nodeCache = new NodeCache(curatorFramework, "/curator",false);
        nodeCache.start();

        // jdk8 lambdas表达式
        nodeCache.getListenable().addListener(()->{
            System.out.println("节点数据变化，变化后的结果：" + new String(nodeCache.getCurrentData().getData()));
        });

        // 正常
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点数据变化，变化后的结果：" + new String(nodeCache.getCurrentData().getData()));
            }
        });

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, "/curator", true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            System.out.println(pathChildrenCacheEvent.getType());
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("创建子节点");
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除子节点");
                    break;
                case CHILD_UPDATED:
                    System.out.println("更新子节点");
                    break;
            }
        });

        curatorFramework.inTransaction()
                .create().forPath("/curator/curator1", "444".getBytes()).and()
                .create().forPath("/curator/curator2", "444".getBytes()).and()
                .delete().forPath("/curator/curator1").and()
                .setData().forPath("/curator/curator2", "555".getBytes()).and()
                .commit();

        System.in.read();

    }
}
