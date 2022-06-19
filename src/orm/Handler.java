package orm;

import orm.exception.TypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具类
 */
public class Handler {
    /**
     * 解析特殊结构的SQL
     *
     * @param sql 带有‘？’语义的要求sql
     * @return 存放sql和语义集合的实例对象
     */
    public KeyAndSql parseSql(String sql) {
        int front = sql.indexOf("#{");
        int after = sql.indexOf("}");
        StringBuilder result = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        while (front != -1 && after != -1 && after > front) {
            result.append(sql, 0, front);
            result.append("?");
            list.add(sql.substring(front + 2, after));
            sql = sql.substring(after + 1);
            front = sql.indexOf("#{");
            after = sql.indexOf("}");

        }
        result.append(sql);
        return new KeyAndSql(result.toString(), list);

    }

    /**
     * 将sql与‘？’组装完成  设置‘？’对应值
     *
     * @param statement 状态对象
     * @param list      '?'对应的语义集合
     * @param obj       存有参数的bean
     * @throws SQLException sql异常
     */
    public void handlerParameter(PreparedStatement statement, ArrayList<String> list, Object obj) throws SQLException, TypeException {
        Class valueType = obj.getClass();
        if (valueType == int.class || valueType == Integer.class) {
            statement.setInt(1, (Integer) obj);
        } else if (valueType == double.class || valueType == Double.class) {
            statement.setDouble(1, (Double) obj);
        } else if (valueType == float.class || valueType == Float.class) {
            statement.setFloat(1, (Float) obj);
        } else if (valueType == String.class) {
            statement.setString(1, (String) obj);
        } else if (valueType.isArray()) {
            throw new TypeException("数组类型不支持");
        } else {
            if (obj instanceof Map)
                this.setMap(statement, list, obj);
            else
                this.setObject(statement, list, obj);
        }

    }

    /**
     * 负责 map类型的对象拼接
     */
    private void setMap(PreparedStatement statement, ArrayList<String> list, Object obj) throws SQLException {
        Map map = (HashMap) obj;
        int j = list.size();
        for (int i = 0; i < j; i++) {
            statement.setObject(i + 1, map.get(list.get(i)));
        }
    }

    /**
     * 负责bean类型的对象拼接
     */
    private void setObject(PreparedStatement statement, ArrayList<String> list, Object obj) throws SQLException {
        Class clazz = obj.getClass();
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                Object value = field.get(obj);
                statement.setObject(i + 1, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 处理结果集
     */
    public Object handlerResultSet(ResultSet set, Class clazz) {
        if (clazz == int.class || clazz == Integer.class) {
            try {
                return set.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else if (clazz == float.class || clazz == Float.class) {
            try {
                return set.getFloat(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else if (clazz == double.class || clazz == Double.class) {
            try {
                return set.getDouble(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else if (clazz == Map.class) {

        } else {
            return setResultObject(clazz, set);

        }
        return null;
    }

    /**
     * 拼接对象返回指定类型对象
     */
    private Object setResultObject(Class clazz, ResultSet set) {
        Object obj = null;
        try {
            Constructor constructor = clazz.getConstructor();
            obj = constructor.newInstance();
            ResultSetMetaData resultSetMetaData = set.getMetaData();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String columnName = resultSetMetaData.getColumnName(i);
                Field field = clazz.getDeclaredField(columnName);
                field.setAccessible(true);
                field.set(obj, set.getObject(columnName));

            }
            return obj;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException | SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
