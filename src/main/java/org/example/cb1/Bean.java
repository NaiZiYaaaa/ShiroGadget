package org.example.cb1;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
public class Bean {

    private String name = "cb";
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        System.out.println(PropertyUtils.getProperty(new Bean(),"name"));
        //在commons-beanutils中提供了静态方法PropertyUtils.getProperty，通过调用这个静态方法，可以直接调用任意JavaBean中的getter方法。
        System.out.println("cb");
    }
}
