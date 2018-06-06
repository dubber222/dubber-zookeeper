package getbillnorequest;

/**
 * Created on 2018/6/6.
 *
 * @author dubber
 */
public class GetBillNo {

    public static void main(String[] args) {
        ZooKeeperService zkCli = ZooKeeperService.getInstance();
        zkCli.start();
        System.out.println("得到唯一业务编号： "
                + zkCli.getBatchId("eee", "EEEA"));
    }
}
