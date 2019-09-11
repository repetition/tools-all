package com.tools.socket.observer;

public class ObserverManager {
    private static SocketServerObserverable socketServerObserver= null;

    public static SocketServerObserverable getSocketServerObserver(){
        if (socketServerObserver==null) {
            socketServerObserver = new SocketServerObserverable();
        }
        return socketServerObserver;
    }


    private static SocketClientObserverable socketClientObserverable = null;

    public static SocketClientObserverable getSocketClientObserverable(){
        if (socketClientObserverable ==null) {
            socketClientObserverable = new SocketClientObserverable();
        }
        return socketClientObserverable;
    }
}
