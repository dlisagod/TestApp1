package com.example.androidvideocachedemo.util

/**
 * Create by MiLuycong on 2019/10/21
 */
interface PermissionListener {
    fun granted(grantedList: List<String>)
    fun needRequest(deniedList: List<String>)
}