package com.dubber.zookeeper.zkclient.vote;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.concurrent.TimeUnit;

/**
 * Demo class
 *
 * @author dubber
 * @date 2018/5/21
 */
public class MasterVoteTest {

    private final static String CONNECT_STRING = "192.168.0.110:2181,192.168.0.104:2181,192.168.0.106:2181";
    private final static String CONNECT_STRING2 = "192.168.49.134:2181,192.168.49.132:2181,192.168.49.133:2181";


    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient(CONNECT_STRING2, 5000, 5000,
                new SerializableSerializer());

        UserCenter userCenter = new UserCenter();

        userCenter.setPcName("用户中心" + 01);
        MasterVote masterVote = new MasterVote(zkClient, userCenter);
        masterVote.startVote();
        try {
            TimeUnit.MILLISECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
