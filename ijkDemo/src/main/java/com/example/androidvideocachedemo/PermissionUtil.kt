package com.example.androidvideocachedemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionUtil {
    private var mListener: PermissionListener? = null
    const val defRequestCode = 10086

    /**
     * 申请权限
     */
    fun requestRuntimePermissions(
        permissions: Array<String>,
        listener: PermissionListener,
        activity: Activity,
        requestCode: Int = defRequestCode
    ) {
        mListener = listener
        val grantedList = ArrayList<String>()
        val needRequestList = ArrayList<String>()
        // 遍历每一个申请的权限，把没有通过的权限放在集合中
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestList.add(permission)
                Log.d("PermissionUtil", "$permission need to be requested")
            } else {
//                    mListener!!.granted(permission)
                grantedList.add(permission)
            }
        }
        if (grantedList.isNotEmpty())
            mListener!!.granted(grantedList)
        // 申请权限
        if (needRequestList.isNotEmpty()) {
            mListener!!.needRequest(needRequestList)
            ActivityCompat.requestPermissions(
                activity,
                needRequestList.toTypedArray<String>(), requestCode
            )
        }
    }

    fun initManagerExternal(activity: Activity, requestToast: String? = null): Boolean {
        if (Build.VERSION.SDK_INT >= 31) {
            if (!Environment.isExternalStorageManager()) {
                val appName =
                    activity.packageManager.getApplicationLabel(activity.applicationInfo)
//                    Toaster.show(requestToast ?: "请开启允许" + appName + "管理文件权限")
                Toast.makeText(activity, requestToast ?: "请开启允许" + appName + "管理文件权限", Toast.LENGTH_SHORT).show()
                activity.startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    ), defRequestCode
                )
                return false
            }
            return true
        }
        return true
    }

    /**
     * 初始化弹出副屏弹窗权限
     */
    fun initOverlay(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {
            val appName = context.packageManager.getApplicationLabel(context.applicationInfo)
            Toast.makeText(context, "请开启允许" + appName + "在其他应用上层显示", Toast.LENGTH_SHORT).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
//                startActivityForResult(intent,10086)
            context.startActivity(intent)
            return false
        }
        return true
    }


}