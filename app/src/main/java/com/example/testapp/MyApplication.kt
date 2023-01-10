package com.example.testapp

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.FileNameGenerator
import com.example.download.http.HttpDownloadManager
import com.example.testapp.util.FilePathUtil
import com.example.testapp.util.SystemUtil
import com.example.testapp.util.Utils

/**
 * @ClassName: com.example.testapp.MyApplication
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/5 15:12
 */
class MyApplication : Application() {

    @Override
    override fun onCreate() {
        mContext = this
        setTheme(R.style.AppTheme_02)
        super.onCreate()
        HttpDownloadManager.init(this)
        Constant.deviceCode = SystemUtil.getDeviceId(this)
        Utils.init(this)
    }


    companion object {
        private var mProxy: HttpProxyCacheServer? = null
        private var mContext: Application? = null

        fun getVideoProxy(): HttpProxyCacheServer {
            if (mProxy == null)
                synchronized(this) {
                    if (mProxy == null) mProxy = getProxy()
                    return mProxy!!
                }
            return mProxy!!
        }

        private fun getProxy(): HttpProxyCacheServer {
            return HttpProxyCacheServer.Builder(mContext)
                .cacheDirectory(FilePathUtil.getLocalVideoPath())
                .fileNameGenerator(object : FileNameGenerator {
                    override fun generate(url: String): String {
                        return FilePathUtil.getFileName(url)
                    }
                })
                .maxCacheSize(512 * 1024 * 1024L)
                .build()
        }

        val context: Context
            get() = mContext!!
    }
}