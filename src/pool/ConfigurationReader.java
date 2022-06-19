package pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 这个类为了读取configuration.properties文件里的内容
 * 读一次将其存储到Map中---缓存 减少IO操作提高性能
 */
public class ConfigurationReader {
    private static Map<String, String> map = new HashMap<>();

    static {
        try {
            Properties pro = new Properties();
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("configuration.properties");
            pro.load(inputStream);
            Enumeration en = pro.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String value = pro.getProperty(key);
                map.put(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return map.get(key);
    }
}
