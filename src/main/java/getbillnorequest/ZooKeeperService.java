package getbillnorequest;

import com.dubber.zookeeper.ZkServers;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2018/6/6.
 *
 * @author dubber
 */
public class ZooKeeperService {

    private static final Logger logger = Logger.getLogger(ZooKeeperService.class);

    public final String SEP = "/";

    public String connectString;
    public int connectTimeout;
    public String batchIdPath;
    public ZkClient zkClient;
    public boolean isDebug;

    private ZooKeeperService() {
        initProperties();
    }

    public static ZooKeeperService getInstance() {
        return InnerHolder.INSTANCE;
    }

    private static class InnerHolder {
        static final ZooKeeperService INSTANCE = new ZooKeeperService();
    }

    public void start() {
        try {
            initProperties();
            zkClient = new ZkClient(connectString, connectTimeout);
            zkClient.subscribeStateChanges(new IZkStateListener() {

                @Override
                public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                }

                @Override
                public void handleNewSession() throws Exception {
                    logger.warn("after session expired,session recreated,init componets!");
                }
            });
            if (!zkClient.exists(batchIdPath)) {
                zkClient.createPersistent(batchIdPath, null);
            }
        } catch (Exception e) {
            logger.error("start version controll error!", e);
        }
    }

    private void initProperties() {
        connectString = ZkServers.CONNECT_STRING;
        connectTimeout = 10000;
        batchIdPath = "/batchId-seq";
    }

    /**
     * 创建节点，存在则返回false
     *
     * @param path
     * @param data
     * @return
     */
    public boolean createNode(String path, byte[] data, CreateMode mode) {
        if (isDebug) {
            return true;
        }
        return createOrUpdate(path, data, false, mode);
    }

    /**
     * 创建或更新节点，存在则覆盖
     *
     * @param path
     * @param data
     * @return
     */
    public boolean updateNode(String path, byte[] data, CreateMode mode) {
        if (isDebug) {
            return true;
        }
        return createOrUpdate(path, data, true, mode);
    }

    /**
     * 删除节点，不存在则返回false
     *
     * @param path
     * @return
     */
    public boolean deleteNode(String path) {
        try {
            if (isDebug) {
                return true;
            }
            // 存在则删除
            if (zkClient.exists(path)) {
                return zkClient.delete(path);
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("delete zookeeper path:" + path + " error!", e);
            return false;
        }
    }

    /**
     * 新增or更新 节点
     *
     * @param path
     * @param data
     * @param override
     * @return
     */
    private boolean createOrUpdate(String path, byte[] data, boolean override, CreateMode mode) {
        try {
            // 存在 && 不覆写
            if (zkClient.exists(path)) {
                if (override) {
                    deleteNode(path);
                } else {
                    return false;
                }
            }

            String[] folds = path.replaceFirst(SEP, "").split(SEP);
            boolean result = true;
            StringBuffer dir = new StringBuffer();
            // 除末级节点：无则新增
            for (int i = 0; i < folds.length - 1; i++) {
                dir.append(SEP + folds[i]);
                if (!zkClient.exists(dir.toString())) {
                    zkClient.create(dir.toString(), null, mode);
                }
            }
            // 末级节点：新增或覆盖
            dir.append(SEP + folds[folds.length - 1]);
            if (!zkClient.exists(dir.toString())) {
                zkClient.create(dir.toString(), data, mode);
            } else {
                zkClient.writeData(dir.toString(), data);
            }
            return result;

        } catch (Exception e) {
            logger.error(String.format("create or update path error,path:%s", path), e);
            return false;
        }
    }

    /**
     * 去除首尾: SEP
     *
     * @param path
     * @return
     */
    public String trim(String path) {
        String path_ = path;
        if (path_.startsWith(SEP)) {
            path_ = path_.substring(1);
        }
        if (path_.endsWith(SEP)) {
            path_ = path_.substring(0, path_.length() - 1);
        }
        return path_;
    }

    /**
     * @param zkRoot 组根节点
     * @return 默认序列号6位
     */
    public String getBatchId(String zkRoot) {
        return getBatchId(zkRoot, "", "yyyyMMdd", true, 6, null);
    }

    /**
     * @param zkRoot 组根节点
     * @param prefix 生成前缀
     */
    public String getBatchId(String zkRoot, String prefix) {
        return getBatchId(zkRoot, prefix, "yyMMdd", true, 6, null);
    }

    /**
     * @param zkRoot 组根节点
     * @param prefix 生成前缀
     * @param length 序列号长度
     */
    public String getBatchId(String zkRoot, String prefix, int length) {
        return getBatchId(zkRoot, prefix, "yyMMdd", false, length, null);
    }

    /**
     * @param zkRoot        唯一编码的组根节点
     * @param prefix        唯一编码的前缀
     * @param datePattern   唯一编码包含的日期格式
     * @param containDate   唯一编码的是否包含日期,默认true
     * @param length        唯一编码的序列号长度,默认6
     * @param uniqueSeqDate 唯一编码唯一性所在日期,默认当前日期
     * @return zkRoot
     */
    public String getBatchId(String zkRoot, String prefix, String datePattern, Boolean containDate, Integer length, Date uniqueSeqDate) {
        String path = batchIdPath + "/" + zkRoot;
        if (!zkClient.exists(path)) {
            zkClient.create(path, null, CreateMode.PERSISTENT);
        }

        String uniquePathDate = "";
        String pathDate = "";
        datePattern = StringUtils.isBlank(datePattern) ? "yyyyMMdd" : datePattern;
        containDate = containDate == null ? true : containDate;
        length = length == null ? 6 : length;

        if (uniqueSeqDate == null) {
            uniquePathDate = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
            pathDate = (new SimpleDateFormat(datePattern)).format(new Date());
        } else {
            uniquePathDate = (new SimpleDateFormat("yyyyMMdd")).format(uniqueSeqDate);
            pathDate = (new SimpleDateFormat(datePattern)).format(uniqueSeqDate);
        }

        path += "/" + uniquePathDate;
        if (!zkClient.exists(path)) {
            zkClient.create(path, null, CreateMode.PERSISTENT);
        }

        path += "/seq_";
        String node = zkClient.create(path, null, CreateMode.PERSISTENT_SEQUENTIAL);
        Long seqNo = Long.valueOf(node.substring(path.length())) + 1;
        return prefix + (containDate ? pathDate : "") + String.format("%0" + length + "d", seqNo);
    }

}
