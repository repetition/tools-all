package com.tools.socket.manager;

import com.tools.socket.client.SocketClient;
import com.tools.socket.server.SocketServer;

public class SocketManager {

    private static SocketServer socketServer;
    public static SocketServer getSocketServer(int port) {

        if (null == socketServer) {
            socketServer = new SocketServer();
            socketServer.setPort(port);
        }
        return socketServer;
    }


    private static SocketClient socketClient;
    public static SocketClient getSocketClient() {

        if (null == socketClient) {
            socketClient = new SocketClient();
        }
        return socketClient;
    }

    /**
     * 获取拉取配置的socket
     */
    private static SocketClient socketPullClient;
    public static SocketClient getSocketPullClient() {

        if (null == socketPullClient) {
            socketPullClient = new SocketClient();
        }
        return socketPullClient;
    }
}
