package orm;

import orm.exception.TypeException;
import pool.ConnectionPool;
import pool.NullConnection;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SqlSession {
    private Handler handler = new Handler();

    public static <T> T getMapper(Class c) {
        //采用动态代理 静态代理的是对象 动态代理的是方法 我们在这里代理dao对象
        //代理的必须是个接口
        //需要三个参数
        //    1.类加载器
        ClassLoader loader = c.getClassLoader();
        //    2.Class[]  加载的类  一般数组中只有一个class
        Class[] interfaces = new Class[]{c};
        //    3.具体怎么做事InvocationHandler 接口 在参数里具体实现 告知具体怎么做事
        return (T) Proxy.newProxyInstance(loader, interfaces, new InvocationHandlerImp());

    }

    /**
     * 对数据库更新的操作
     */
    private int up(String sql, Object ob) {
        ConnectionPool pool;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            pool = ConnectionPool.getInstanceOf();
            connection = pool.getConnection();
            KeyAndSql keyAndSql = handler.parseSql(sql);
            statement = connection.prepareStatement(keyAndSql.getSql());
            if (ob != null) {
                handler.handlerParameter(statement, keyAndSql.getList(), ob);
            }
            return statement.executeUpdate();
        } catch (NullConnection | SQLException | TypeException nullConnection) {
            nullConnection.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 增操作
     */
    public int insert(String sql, Object ob) {
        if (sql.trim().substring(0, 6).equalsIgnoreCase("insert")) {
            return this.up(sql, ob);
        }
        return -2;

    }

    /**
     * 删操作
     */
    public int delete(String sql, Object ob) {
        if (sql.trim().substring(0, 6).equalsIgnoreCase("delete")) {
            return this.up(sql, ob);
        }
        return -2;

    }

    /**
     * 改操作
     */
    public int update(String sql, Object ob) {
        if (sql.trim().substring(0, 6).equalsIgnoreCase("update")) {
            return this.up(sql, ob);
        }
        return -2;

    }

    /**
     * 查询单条语句
     *
     * @param resultType 拼接成对象的类型
     */
    public <T> T selectOne(String sql, Object obj, Class resultType) {
        try {
            //1.获取连接池
            ConnectionPool pool = ConnectionPool.getInstanceOf();
            //2.获取连接
            Connection connection = pool.getConnection();
            //3.处理sql语句
            KeyAndSql keyAndSql = handler.parseSql(sql);
            //4.获取状态参数并赋真正的sql语句
            PreparedStatement statement = connection.prepareStatement(keyAndSql.getSql());
            //5.赋值
            if (obj != null) {
                handler.handlerParameter(statement, keyAndSql.getList(), obj);
            }
            //6.执行语句并接受返回值
            ResultSet set = statement.executeQuery();
            //7.处理返回值
            if (set.next()) {
                return (T) handler.handlerResultSet(set, resultType);

            }
        } catch (NullConnection | TypeException | SQLException nullConnection) {
            nullConnection.printStackTrace();
        }

        return null;
    }

    public <T> ArrayList<T> selectList(String sql, Object obj, Class resultType) {
        ArrayList<T> arrayList = new ArrayList<>();
        try {
            //1.获取连接池
            ConnectionPool pool = ConnectionPool.getInstanceOf();
            //2.获取连接
            Connection connection = pool.getConnection();
            //3.处理sql语句
            KeyAndSql keyAndSql = handler.parseSql(sql);
            //4.获取状态参数并赋真正的sql语句
            PreparedStatement statement = connection.prepareStatement(keyAndSql.getSql());
            //5.？赋值
            if (obj != null) {
                handler.handlerParameter(statement, keyAndSql.getList(), obj);
            }
            //6.执行语句并接受返回值
            ResultSet set = statement.executeQuery();
            //7.处理返回值
            while (set.next()) {
                arrayList.add((T) handler.handlerResultSet(set, resultType));

            }
        } catch (NullConnection | SQLException | TypeException nullConnection) {
            nullConnection.printStackTrace();
        }

        return arrayList;


    }

}
