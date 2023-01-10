package com.example.download.http

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor


/**
 * Create by MiLuycong on 2019/10/21
 */
class LoggerInterceptor : HttpLoggingInterceptor.Logger{
    override fun log(message: String) {
        var msg :String = message
//        val max_str_length = 2001 - message.length
        //大于4000时
        while (msg.length > 500) {
            Log.e("LoggerInterceptor", msg.substring(0, 500))
            msg = msg.substring(500)
        }
//        剩余部分
        Log.e("LoggerInterceptor", msg)
//        Log.e("LoggerInterceptor", message)
    }
}