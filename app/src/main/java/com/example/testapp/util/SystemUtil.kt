package com.example.testapp.util

import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Create by MiLuycong on 2019/10/21
 */
class SystemUtil {

    companion object {
        /**
         * 获取设备ID
         */
        fun getDeviceId(context: Context): String {
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            id.replace("-", "")
            id.replace("_", "")
            Log.d("DeviceId", id)
            return id
        }


    }
}