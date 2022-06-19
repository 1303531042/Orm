package pool;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * 用来管理连接的类
 * 连接数量有默认值
 * 用户使用时 去集合里寻找可用连接
 */
public class ConnectionPool {
    private ConnectionPool() {
    }

    private static volatile ConnectionPool pool;

    public static ConnectionPool getInstanceOf() {
        //防止指令重排
        if (pool == null) {
            synchronized (ConnectionPool.class) {
                if (pool == null) {
                    pool = new ConnectionPool();
                }
            }
        }
        return pool;
    }

    //默认连接数量
    private static final int DEFAULT_SIZE = 10;

    //管理连接IOC
    private ArrayList<MyConnection> list;

    //初始化连接池 添加链接
    {
        list = new ArrayList<>();
        String aDefault = ConfigurationReader.getValue("default");
        int j;
        if (aDefault == null) {
            j = DEFAULT_SIZE;
        } else {
            j = Integer.parseInt(aDefault);
        }
        for (int i = 0; i < j; i++) {
            list.add(new MyConnection());
        }
    }

    /**
     * 获取可用连接 并设置等待机制
     *
     * @return 可用连接
     * @throws NullConnection 等待超时抛出异常 无连接可用
     */
    public Connection getConnection() throws NullConnection {
        MyConnection myConnection = this.getMc();
        int count = 0;
        while (myConnection == null && count != 50) {
            try {

                Thread.sleep(100);
                count++;
                myConnection = this.getMc();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if (myConnection == null) {
            throw new NullConnection();
        }

        return myConnection;
    }

    /**
     * 同步方法 并发安全的获取连接
     *
     * @return 可用连接
     */
    private MyConnection getMc() {
        MyConnection c = null;
        for (MyConnection a : list) {
            if (!a.isUsed()) {
                synchronized (a) {
                    if (!a.isUsed()) {
                        a.setUsed(true);
                        c = a;
                        break;
                    }
                }
            }
        }
        return c;
    }
}
