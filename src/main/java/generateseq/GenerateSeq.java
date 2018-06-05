package generateseq;

import com.dubber.zookeeper.ZkServers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.*;

/**
 * Created on 2018/6/5.
 *
 * @author dubber
 */
public class GenerateSeq {
    /**
     * SEQ_ZNODE 提前创建好存储 Seq 的 "/eq" 结点
     * LOCK_ZNODE  //提前创建好锁对象的结点"/lock"
     */
    private static final String NAMESPACE_ZNODE = "/dubber";
    private static final String SEQ_ZNODE = "/seq";
    private static final String LOCK_ZNODE = "/lock";
    private ExecutorService executorService = null;
    private int maximumpollSize = 20;
    private ZookeeperCli zkCli;

    public void generateSeq() {

        for (int i = 0; i < maximumpollSize; i++) {

        }

        try {
            if (!executorService.isShutdown()) {
                executorService.shutdown();
                int timeout = 10;
                if (!executorService.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
        }


    }

    /**
     * 初始化
     */
    public void init() {
        // init zookeeper client
        zkCli = ZookeeperCli.getInstance();
        zkCli.start(NAMESPACE_ZNODE);

        // init thread poll
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("seq-pool-%d").build();
        executorService = new ThreadPoolExecutor(5, maximumpollSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        //创建节点
        zkCli.createNode(CreateMode.PERSISTENT, SEQ_ZNODE);
        zkCli.createNode(CreateMode.PERSISTENT, LOCK_ZNODE);
    }

    /**
     * task1
     * 通过znode数据版本实现分布式seq生成
     */
    class task1 implements Runnable{
        private String taskName;

        public task1(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public void run() {
            Stat stat = zkCli.setData(NAMESPACE_ZNODE + SEQ_ZNODE,"1");
            int versionAsSeq = stat.getVersion();
            System.out.println(taskName + " obtain seq=" + versionAsSeq);
        }
    }
}
