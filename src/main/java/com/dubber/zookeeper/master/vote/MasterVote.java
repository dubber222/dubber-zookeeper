package com.dubber.zookeeper.master.vote;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.*;

import static java.util.concurrent.Executors.*;

/**
 * Demo class
 *
 * @author dubber
 * @date 2018/5/21
 */
public class MasterVote {

    private ZkClient zkClient;

    /**
     * master 节点
     */
    private final static String MASTER_PATH = "/master";

    /**
     * 节点监听
     */
    private IZkDataListener iZkDataListener;

    /**
     * master 节点
     */
    private UserCenter master;

    /**
     * slave 其他节点
     */
    private UserCenter server;

    /**
     * 阀门
     */
    private static boolean isRunning = false;

    ScheduledExecutorService executorService = newScheduledThreadPool(10);

    public MasterVote(ZkClient zkClient, UserCenter server) {
        System.out.println("[" + server.getPcName() + "] --> 去争抢master权限");
        this.zkClient = zkClient;
        this.server = server;

        this.iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("[" + server.getPcName() + "] --> 节点被删除");
                //master 如果被删除，发起master vote 操作

                TimeUnit.MILLISECONDS.sleep(500);
                voteMaster();
            }
        };
    }


    /**
     * 开启 vote
     */
    public void startVote() {
        if (!isRunning) {
            isRunning = true;
            zkClient.subscribeDataChanges(MASTER_PATH, iZkDataListener);
            voteMaster();
        }
    }


    /**
     * 停止 vote
     */
    public void stopVote() {
        System.out.println("--> 你们都停下来！");
        if (isRunning) {
            isRunning = false;
            zkClient.unsubscribeDataChanges(MASTER_PATH, iZkDataListener);
            executorService.shutdown();
            releaseMaster();
        }
    }


    /**
     * 具体 master 选举实现逻辑
     */
    private void voteMaster() {
        if (!isRunning) {
            System.out.println("--> 当前服务没有启动");
            return;
        }

        try {
            zkClient.createEphemeral(MASTER_PATH, server);
            master = server;
            System.out.println(master.getPcName() + "--> 成为master节点，听他的");

            //出现故障，释放master节点锁，让给其他服务器
            // 定时器
            // master 释放（master 出现故障）
            // 每五秒 释放一次
            executorService.schedule(() -> {
                System.out.println(server.getPcName() + "--> 我出现故障了");
                releaseMaster();
            }, 5, TimeUnit.SECONDS);
        } catch (ZkNodeExistsException e) {
            //表示master节点已经存在
            UserCenter userCenter = zkClient.readData(MASTER_PATH, true);
            if (userCenter == null) {
                voteMaster();
            } else {
                master = server;
            }
        }
    }

    /**
     * 释放 master 节点
     */
    private void releaseMaster() {
        if (isMaster()) {
            zkClient.delete(MASTER_PATH);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isMaster() {
        UserCenter userCenter = zkClient.readData(MASTER_PATH, true);
        if (userCenter != null && userCenter.getPcName().equals(server.getPcName())) {
            master = server;
            return true;
        }
        return false;
    }
}

