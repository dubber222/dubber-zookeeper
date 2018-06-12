import java.util.concurrent.CountDownLatch;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 *
 * 并发生成时间戳，无分布式锁;
 * 一定会有重复的编号
 */
public class Recipes_NoLock {
    static int num = 10;
    static CountDownLatch countDown = new CountDownLatch(1);
    public static void main(String[] args) {

        for (int i = 0; i < num; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDown.await();

                        System.out.println("编号：" + System.currentTimeMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        countDown.countDown();
    }
}
