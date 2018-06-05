package generateseq;

import com.dubber.zookeeper.ZkServers;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;


/**
 * Created on 2018/6/5.
 *
 * @author dubber
 *
 * zookeeper Curator方式创建客户端
 */
public class ZookeeperCli {

    private CuratorFramework curatorFramework;
    private boolean init = false;

    public static ZookeeperCli getInstance(){
        return InnerHold.Instance;
    }

    private static class InnerHold{
        private static ZookeeperCli Instance = new ZookeeperCli();
    }

    public void start(String nameSpace) {
        if(init){
            if(StringUtils.isBlank(nameSpace)){
                nameSpace = "/";
            }
            // init zookeeper
            curatorFramework = CuratorFrameworkFactory.builder().connectString(ZkServers.CONNECT_STRING)
                    .connectionTimeoutMs(5000).namespace(nameSpace).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
            curatorFramework.start();
            init = true;
        }
    }

    public CuratorFramework getCuratorFramework(){
        start("");
        return curatorFramework;
    }

    /**
     * 创建节点
     *
     * @param node
     */
    public void createNode(CreateMode createMode, String node) {
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 节点赋值
     */
    public Stat setData(String node, String data){
        Stat stat = null;
        try {
            stat = curatorFramework.setData().forPath(node, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }

    /**
     * 关闭zookeeper
     */
    public void close(){
        curatorFramework.close();
    }
}
