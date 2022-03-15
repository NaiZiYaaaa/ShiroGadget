package org.example.shiroCheck;

import org.apache.shiro.subject.SimplePrincipalCollection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ShiroCheck {
    public static void main(String[] args) throws IOException {
        SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection();
        ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("1.ser"));
        obj.writeObject(simplePrincipalCollection);
        obj.close();
    }
}


