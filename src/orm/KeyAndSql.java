package orm;

import java.util.ArrayList;

/**
 * 将传入的特殊sql处理后
 * 1.存储？对应的值的集合
 * 2.正常的sql
 * 需要这两个值都需要返回，就封装成一个对象
 */
public class KeyAndSql {
    //存放正常sql
    private String sql;
    //按顺序存放‘？’所对应语义
    private ArrayList<String> list;

    public KeyAndSql(String sql, ArrayList<String> list) {
        this.sql = sql;
        this.list = list;
    }

    /**
     * @return 获取sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @return 获取‘？’语义集合
     */
    public ArrayList<String> getList() {
        return list;
    }
}
