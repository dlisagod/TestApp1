package com.example.testapp.webSocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.testapp.Constant;
import com.example.testapp.util.LogUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @ClassName: SocketManager
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/12/9 9:20
 */
public class SocketManager {


    private static final String TAG = "SocketManager";
//    private WebSocketClient client = null;

    private static final String url = Constant.API_SERVER_BASE_URL_LOCAL;

    private static final String port = "deviceupdatewebsocket/";

    private static SocketManager singleTon;

    private SocketManager() {

    }

    public static SocketManager getInstance() {
        if (singleTon == null) {
            synchronized (SocketManager.class) {
                if (singleTon == null) {
                    singleTon = new SocketManager();
                }
            }
        }
        return singleTon;
    }

    private WebSocket mSocket;

    public void connect() {
        if (mSocket == null) {
            synchronized (SocketManager.class) {
                if (mSocket == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build();
//                    LogUtil.Companion.d(TAG, "deviceId " + Constant.Companion.getDeviceCode());
                    final Request request = new Request.Builder().url(url + port + Constant.Companion.getDeviceCode()).build();
                    mSocket = client.newWebSocket(request, new WebSocketListener() {
                        @Override
                        public void onOpen(WebSocket webSocket, Response response) {
                            super.onOpen(webSocket, response);

                            try {
                                LogUtil.Companion.d(TAG, "open " + (response.body() != null ? response.body().string() : null));
//                                send();
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtil.Companion.d(TAG, "open IOException");
                            }

                        }

                        @Override
                        public void onMessage(WebSocket webSocket, String text) {
                            super.onMessage(webSocket, text);

                            LogUtil.Companion.d(TAG, "onMessage String " + text);
                        }

                        @Override
                        public void onMessage(WebSocket webSocket, ByteString bytes) {
                            super.onMessage(webSocket, bytes);
                            LogUtil.Companion.d(TAG, "onMessage ByteString " + bytes.string(java.nio.charset.StandardCharsets.UTF_8));
                        }

                        @Override
                        public void onClosing(WebSocket webSocket, int code, String reason) {
                            super.onClosing(webSocket, code, reason);
                            mSocket = null;
                            LogUtil.Companion.d(TAG, "onClosing " + code + " " + reason);
                        }

                        @Override
                        public void onClosed(WebSocket webSocket, int code, String reason) {
                            super.onClosed(webSocket, code, reason);
                            mSocket = null;
                            LogUtil.Companion.d(TAG, "onClosed " + code + " " + reason);
                        }

                        @Override
                        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                            super.onFailure(webSocket, t, response);
                            mSocket = null;
                            try {

                                String bodyStr = "null";
                                if (response != null && response.body() != null) {
                                    bodyStr = response.body().string();
                                }
                                LogUtil.Companion.d(TAG, "onFailure message " + t.getMessage() + " " + bodyStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                                LogUtil.Companion.d(TAG, "onFailure " + e.getMessage());
                            }
                        }
                    });
                }
            }
        }

    }


    private final int code = 1000;
    private final String reason = "";

    public void close() {
        if (mSocket != null) {
            mSocket.close(code, reason);
            mSocket = null;
        }
    }


    public void send() {
        send("1");
    }


    public void send(@NonNull String text) {
        if (mSocket != null) {
            mSocket.send(text);
        } else {

        }
    }


    public void sendHeartBeat() {

    }


}
