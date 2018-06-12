import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created on 2018/6/12.
 *
 * @author dubber
 *
 * CyclicBarrier
 */
public class RecipesCyclicBarrier {

    static CyclicBarrier barrier = new CyclicBarrier(3);
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            String customer = i + " 号客人";
            System.out.println(customer + "已到达！");
            threadPool.submit(new BarrierRunnable(customer));
        }
        threadPool.shutdown();

    }
}

class BarrierRunnable implements Runnable{
    String name;
    public BarrierRunnable(String name) {
        this.name = name;
    }
    @Override
    public void run() {
        try {
            RecipesCyclicBarrier.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(name + "开吃!");
    }
}
