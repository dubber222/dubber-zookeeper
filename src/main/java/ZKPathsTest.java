import com.dubber.zookeeper.ZkServers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.ZooKeeper;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 */
public class ZKPathsTest {

    static String LOCK_PATH = "/zkpaths_test";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(ZkServers.CONNECT_STRING)
            .retryPolicy(new ExponentialBackoffRetry(5000,3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();

        ZooKeeper zookeeper = client.getZookeeperClient().getZooKeeper();
        System.out.println(ZKPaths.fixForNamespace(LOCK_PATH,"sub"));
    }
}
