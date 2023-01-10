package com.example.testapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * @ClassName: SecondDisplayService
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2021/4/15 11:31
 */
class SecondDisplayService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }
}