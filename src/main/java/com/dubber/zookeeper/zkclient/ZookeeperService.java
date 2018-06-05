package com.dubber.zookeeper.zkclient;

import com.dubber.zookeeper.ZkServers;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/5/18.
 *
 * @author dubber
 */
public class ZookeeperService {

    private Logger logger = Logger.getLogger(ZookeeperService.class);
    public final String SEP = "/";

    public String serverIpAddress;
    public String connectString;
    public int connectTimeout;
    public String versionPath;
    public String snapshotsPath;
    public String batchIdPath;

    public ZkClient zkClient;

    public static ZookeeperService getInstance(){
        return InnerHolder.INSTANCE;
    }

    private static class InnerHolder{
        static final ZookeeperService INSTANCE = new ZookeeperService();
    }


    public void init(){
        serverIpAddress = ZkServers.CONNECT_STRING;
        connectTimeout = 5000;
    }

    public void start() throws InterruptedException {
        ZkClient zkClient = new ZkClient(serverIpAddress,connectTimeout);
        System.out.println("Connection  success!!");
        // 递归创建父节点的操作
        //zkClient.createPersistent("/node_012/zz",true);
        //zkClient.createPersistent("/zkClient1","1");

        //递归删除
        //zkClient.deleteRecursive("node_012");



        zkClient.createPersistent("/node","1");

        // 订阅数据变化 监听
        zkClient.subscribeDataChanges("/node", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("dataPath:" + dataPath + " ,  data:" + data);
            }
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        });
        zkClient.writeData("/node","12");
        TimeUnit.SECONDS.sleep(1);
    }

}
