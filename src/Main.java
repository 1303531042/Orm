import domain.Car;
import orm.SqlSession;
import pool.ConnectionPool;
import pool.NullConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws NullConnection, SQLException {

//        SqlSession session = new SqlSession();
//        String parameter = "black";
//        String sql = "select * from t_car where color = #{color}";
//        ArrayList<Car> list = session.selectList(sql, parameter, Car.class);
//        System.out.println(list);


        //常量池测试
//        int i = 0;
//        ConnectionPool pool = ConnectionPool.getInstanceOf();
//        while (i < 30) {
//            Thread thread1 = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                       Connection c=pool.getConnection();
//                        System.out.println(c);
//
//                    } catch (NullConnection nullConnection) {
//                        nullConnection.printStackTrace();
//                    }
//                }
//            });
//            thread1.start();
//            i++;
//        }


        SqlSession session = new SqlSession();
//        Object obj = new Car(12, "adfggh", "adfggh", 11);
//        Map obj = new HashMap<String, String>();
//        obj.put("cno", "36");
//        obj.put("cname", "3fafag");
//        obj.put("color", "ggga");
//        obj.put("price", "999");
//        session.insert("insert into t_car values(#{cno},#{cname},#{color},#{price})", obj);
        System.out.println((Car) session.selectOne("select cno from t_car where cno = #{cno}", 11, Car.class));

//        PreparedStatement sta =null;
//        Connection connection = null;
//        try {
//            ConnectionPool pool = ConnectionPool.getInstanceOf();
//            connection = pool.getConnection();
//            String sql = "insert into t_car values (9,'b','red',80)";
//            sta = connection.prepareStatement(sql);
//            sta.executeUpdate();
//        } catch (NullConnection nullConnection) {
//            nullConnection.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if (sta!= null) {
//                    sta.close();
//                }
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

    }
}
