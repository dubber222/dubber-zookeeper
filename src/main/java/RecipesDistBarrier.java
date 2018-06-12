import com.dubber.zookeeper.ZkServers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 *
 * 分布式 CyclicBarrier
 */
public class RecipesDistBarrier {

    static String LOCK_PATH = "/recipes_distribute_barrier_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(ZkServers.CONNECT_STRING)
            .retryPolicy(new ExponentialBackoffRetry(5000,3))
            .build();
    public static void main(String[] args) throws Exception {

        client.start();
        DistributedBarrier barrier = new DistributedBarrier(client,LOCK_PATH);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("num 1 到达！");
                try {
                    barrier.setBarrier();
                    barrier.waitOnBarrier();
                    System.out.println("num 1 开吃！");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(10);
        barrier.removeBarrier();
    }
}
