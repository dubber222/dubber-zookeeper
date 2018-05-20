package com.dubber.zookeeper.master.vote;

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


    public static void main(String[] args) {
        int num = 10;

        for (int i = 0; i < num; i++) {
            ZkClient zkClient = new ZkClient(CONNECT_STRING, 5000, 5000,
                    new SerializableSerializer());

            UserCenter userCenter = new UserCenter();
            userCenter.setPcId(i);
            userCenter.setPcName("用户中心" + i);
            MasterVote masterVote = new MasterVote(zkClient, userCenter);
            masterVote.startVote();
            try {
                TimeUnit.MILLISECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
