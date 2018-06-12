import com.dubber.zookeeper.ZkServers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.CountDownLatch;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 *
 * 分布式计数器
 */
public class RecipesDistAtomicInt {

    static String LOCK_PATH = "/recipes_distribute_atomic_int_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(ZkServers.CONNECT_STRING)
            .retryPolicy(new ExponentialBackoffRetry(5000,3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();

        DistributedAtomicInteger dai
                = new DistributedAtomicInteger(client,LOCK_PATH,new RetryNTimes(3,1000));
        AtomicValue<Integer> atomicValue = dai.add(9);

        System.out.println("操作： " + atomicValue.succeeded());

    }

}
