import com.dubber.zookeeper.ZkServers;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 *         <p>
 *         并发生成时间戳，有分布式锁
 *         一定不会有重复的编号
 * @Curator 实现分布式锁。
 */
public class Recipes_Lock {
    static String LOCK_PATH = "/recipes_lock_path";
    static int num = 10;
    static CountDownLatch countDown = new CountDownLatch(1);
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(ZkServers.CONNECT_STRING)
            .retryPolicy(new ExponentialBackoffRetry(5000,3))
            .build();

    public static void main(String[] args) {
        client.start();
        InterProcessMutex lock = new InterProcessMutex(client,LOCK_PATH);
        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDown.await();
                        lock.acquire();
                        System.out.println("编号：" + System.currentTimeMillis());
                        lock.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        countDown.countDown();
    }

}
