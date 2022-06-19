package orm;

import orm.SqlSession;
import orm.annotation.Delete;
import orm.annotation.Insert;
import orm.annotation.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;

public class InvocationHandlerImp implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = new SqlSession();
        //当dao层调用 他里面的方法 就会触发这个方法
        //proxy 被代理的对象 dao
        //method 被代理的方法
        //args 调用方法是传的参数 都按顺序方法这个数组里了

        //1.获取方法上的注解
        Annotation annotation = method.getAnnotations()[0];
        //2.获取注解类型
        Class type = annotation.annotationType();//这里的annotation是对象 annotationType()相当于其他对象的getClass方法
        //3获取value方法执行获取sql
        Method value = type.getDeclaredMethod("value");
        value.setAccessible(true);
        String sql = (String) value.invoke(annotation);
        //4.处理参数
        Object object = args == null ? null : args[0];
        //根据Type类型调用相应的方法
        if (type == Insert.class) {
            return session.insert(sql, object);

        } else if (type == Update.class) {
            return session.update(sql, object);

        } else if (type == Delete.class) {
            return session.delete(sql, object);
        } else {
            Class methodReturnType = method.getReturnType();
            if (methodReturnType == ArrayList.class) {
                Type type1 = method.getGenericReturnType();//返回一个具体的类型
                //上面的返回值应该是个Class 但是Class没有操作泛型的方法所以我们将它还原成Type
                //Type 是一个接口好多子类实现 我们再讲Type造型成可以操作泛型的类 ParameterizedType
                ParameterizedType real = (ParameterizedType) type1;//可以操作返回值中的泛型的一个类
                Type[] patternType = real.getActualTypeArguments();//java.util.list<domain.Car> 获取这个中的泛型
                // 因为可能是<string,object>两个类型 为了通用用返回值是数组 为了将它转成Class类型所以我们用的Type接收

                Class resultType = (Class) patternType[0];
                return session.selectList(sql, object, resultType);
            } else {
                return session.selectOne(sql, object, methodReturnType);
            }
        }
    }
}


