package pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 这个类是我们自己描述的
 * 目的是将一个真实连接和一个状态绑定在一起
 */
public class MyConnection extends AbstractConnection {
    //创建连接所需要参数
    private static String driver = ConfigurationReader.getValue("driver");
    private static String url = ConfigurationReader.getValue("url");
    private static String user = ConfigurationReader.getValue("user");
    private static String password = ConfigurationReader.getValue("password");
    //真实连接
    private Connection connection;
    //连接状态
    private boolean used = false;

    //让加载驱动执行一次
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //为了在创建实例对象时给connection赋值
    {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断该连接状态
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * 设置该连接状态
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * 获取该连接
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 静态代理 让真正的连接属性做事
     */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.getConnection().prepareStatement(sql);
    }

    /**
     * 静态代理 重写close方法，释放连接操作 更改连接状态
     */
    @Override
    public void close() {
        this.setUsed(false);
    }
}
