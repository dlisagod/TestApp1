package com.example.download.util

import android.util.SparseArray
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import okhttp3.Call
import java.io.File
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * @ClassName: DownloadManager
 * @Description: 下载管理类，用于同一个下载地址一次只能创建一个
 * @Author: zhl
 * @Date: 2020/11/5 11:57
 */
class DownloadManager private constructor() {
    private val downloadMap: HashMap<String, MyCallBack> = HashMap()

    fun downLoad(url: String, callBack: CallBack, destFileDir: String, destFileName: String) {
        //查询是否已经存在下载任务
        synchronized(this) {

            var mCallBack = downloadMap.get(url)
            if (mCallBack == null) {
                //不存在则新建
                //内部监听
                mCallBack = MyCallBack(url, destFileDir, destFileName)
                //设置外部监听
                mCallBack.addCallBack(callBack)
                OkHttpUtils.get().url(url).build().execute(mCallBack)
                //内部监听添加到map中
                downloadMap.put(url, mCallBack)
            } else {
                mCallBack.addCallBack(callBack)
            }
        }
    }


    //从下载表中删除
    fun clear(url: String) {
        if (downloadMap.get(url) != null) {
            downloadMap.remove(url)
        }
    }

    inner class MyCallBack(val url: String, destFileDir: String, destFileName: String) :
        FileCallBack(destFileDir, destFileName) {
        //        private var mCallBack: WeakReference<CallBack>? = null
        private var mCallBacks = mutableListOf<CallBack>()

        //        private var callBack: CallBack? = null
        override fun inProgress(progress: Float, total: Long, id: Int) {
            super.inProgress(progress, total, id)
//            if (mCallBack?.get() != null) {
//                mCallBack?.get()?.inProgress(progress, total, id)
//            }
            for (callback in mCallBacks)
                callback.inProgress(progress, total, id)
        }

        override fun onResponse(response: File?, id: Int) {
//            if (mCallBack?.get() != null) {
//                mCallBack?.get()?.onResponse(response, id)
//            }
            for (callback in mCallBacks)
                callback.onResponse(response, id)
            mCallBacks.clear()
            //从下载表中删除
            clear(url)
        }

        override fun onError(call: Call?, e: Exception?, id: Int) {
//            if (mCallBack?.get() != null) {
//                mCallBack?.get()?.onError(call, e, id)
//            }
            for (callback in mCallBacks)
                callback.onError(call, e, id)
            mCallBacks.clear()
            //从下载表中删除
            clear(url)
        }

//        fun setCallBackWeak(callBack: CallBack) {
//            mCallBack = WeakReference(callBack)
//        }


        /**
         * 添加外部监听
         */
        fun addCallBack(callBack: CallBack) {
            mCallBacks.add(callBack)
        }
    }

    abstract class CallBack {
        abstract fun inProgress(progress: Float, total: Long, id: Int)
        abstract fun onResponse(response: File?, id: Int)
        abstract fun onError(call: Call?, e: Exception?, id: Int)
    }


    companion object {
        private var INSTANCE: DownloadManager? = null
        fun getInstance(): DownloadManager {
            if (INSTANCE == null) {
                synchronized(DownloadManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = DownloadManager()
                    }
                }
            }
            return INSTANCE!!

        }
    }
}