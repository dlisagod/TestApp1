package com.example.download.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences工具
 */
class SPUtil private constructor(context: Context, name: String) {
    private var sp: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    companion object {
        fun getSPInstance(context: Context?, name: String?): SPUtil {
            return SPUtil(context!!, name!!)
        }
    }

    init {
        if (sp == null)
            sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun put(key: String?, value: String?) {
        if (editor == null) editor = sp?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    operator fun get(key: String?): String? {
        return sp!!.getString(key, "")
    }

    /**
     * 效果器sp工具
     */
    class DownloadSPUtil private constructor(context: Context) {
        private var mDownloadSpUtil: SPUtil? = null

        /**
         * @param str 下载地址
         * @param progress 下载进度
         */
        fun putDownloadLog(str: String?, progress: Long) {
            mDownloadSpUtil?.put(
                str,
                progress.toString()
            )
        }

        /**
         * @param str 下载地址
         */
        fun getDownloadLog(str: String?): Long {
            if (mDownloadSpUtil?.get(str).isNullOrBlank())
                return 0
            return mDownloadSpUtil?.get(str)?.toLong() ?: 0
        }

        companion object {
            private var instance: DownloadSPUtil? = null
            private var mContext: Context? = null
            fun getInstance(): DownloadSPUtil? {
                if (instance == null) {
                    synchronized(DownloadSPUtil::class.java) {
                        if (instance == null) {
                            instance = DownloadSPUtil(mContext!!)
                        }
                    }
                }
                return instance
            }
            fun init(context:Context){
                mContext = context
            }
        }

        init {
            mDownloadSpUtil = getSPInstance(context,"DownloadManager")
        }

    }
}