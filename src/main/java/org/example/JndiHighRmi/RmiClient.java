package org.example.JndiHighRmi;

import javax.naming.InitialContext;

public class RmiClient {
    public static void main(String[] args) throws Exception{
        Object object=new InitialContext().lookup("rmi://127.0.0.1:1099/Object");
    }
}
