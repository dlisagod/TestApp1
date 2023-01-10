package com.example.testapp.webSocket;

import org.java_websocket.client.WebSocketClient;

/**
 * @ClassName: Manager
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/12/9 9:20
 */
public class Manager {

//
//    private WebSocketClient client = null;

    private static final String url = "http://59.110.18.73:8888/ldt/";

    private static final String port = "deviceupdatewebsocket/";

    private static Manager singleTon;

    private Manager() {

    }

    public static Manager getInstance() {
        if (singleTon == null) {
            synchronized (Manager.class) {
                if (singleTon == null) {
                    singleTon = new Manager();
                }
            }
        }
        return singleTon;
    }

    public void connect() {

    }


}
