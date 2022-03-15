package org.example.urlSer;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
public class UrlDnsSer {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 反序列化对象
        FileInputStream fileInputStream = new FileInputStream("./urldns.ser");
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);

        ois.readObject();
    }
}


