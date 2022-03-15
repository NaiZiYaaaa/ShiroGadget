package org.example.rmi;

public interface HelloInterface extends java.rmi.Remote {
    public String sayHello(String from) throws java.rmi.RemoteException;
}
