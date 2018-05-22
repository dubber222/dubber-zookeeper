package com.dubber.zookeeper.curator.leadervote;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/5/21.
 *
 * @author dubber
 *
 */
public class LeaderVote {

    private final static String CONNECT_STRING = "192.168.0.110:2181,192.168.0.104:2181,192.168.0.106:2181";
    private final static String CONNECT_STRING2 = "192.168.49.134:2181,192.168.49.132:2181,192.168.49.133:2181";

    private static String master_path = "/master_curator_path";

    public static void main(String[] args) throws IOException {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(CONNECT_STRING2)
                .retryPolicy(new ExponentialBackoffRetry(1000,3)).build();

        LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, master_path, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                //获得Leader
                System.out.println("获得leader成功");
                TimeUnit.SECONDS.sleep(2);
            }
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

            }
        });

        leaderSelector.autoRequeue();
        leaderSelector.start();//开始选举
    }

}
