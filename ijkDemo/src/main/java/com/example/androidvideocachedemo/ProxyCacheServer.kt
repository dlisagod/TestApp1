package com.example.androidvideocachedemo

import android.content.Context
import android.os.Environment
import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import java.io.File
import java.net.URL

/**
 * @create zhl
 * @date 2023/9/5 18:23
 * @description
 *
 * @update
 * @date
 * @description
 **/
object ProxyCacheServer {
    val tag = "ProxyCacheServer"

    val keyUrl = "https://data.360guoxue.com/18000/Calligraphy/video/enc.key"

    var enable = false

    var mServer: HttpProxyCacheServer? = null

    /**
     * 初始化边下边播的代理缓存
     */
    fun initProxy(context: Context) {
        //缓存目录
        val path = Environment.getExternalStorageDirectory().absolutePath + "/test"
        val file = File(path)
        if (!file.exists()) file.mkdir()
        mServer = HttpProxyCacheServer.Builder(context)
            .cacheDirectory(file)
            .fileNameGenerator { url -> //文件名
                val s = url.split("/").toTypedArray()
                s[s.size - 1]
            }
            .build()
    }

    fun getProxy(url: String): String? {
        return mServer?.getProxyUrl(url)
    }

    fun getNewCacheServer(context: Context): HttpProxyCacheServer {
        //缓存目录
        val path = Environment.getExternalStorageDirectory().absolutePath + "/test"
        val file = File(path)
        if (!file.exists()) file.mkdir()
        return HttpProxyCacheServer.Builder(context)
            .cacheDirectory(file)
            .fileNameGenerator { url -> //文件名
                val s = url.split("/").toTypedArray()
                s[s.size - 1]
            }
            .build()
    }

    fun preCacheKey() {
        object : Thread() {
            override fun run() {
                super.run()
                realPreCacheKey()
            }
        }.start()
    }

    fun realPreCacheKey() {
        //                ijkMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(url), null);
        val urlKey = mServer!!.getProxyUrl(keyUrl)
        Log.d(tag, "proxy urlKey:$urlKey")
        val con = URL(urlKey).openConnection()
        con.getInputStream().use {
            val byte = ByteArray(1024)
            while (it.read(byte) != -1) {

            }
            Log.d(tag,"realPreCacheKey success")
        }
        Log.d(tag,"realPreCacheKey end")
    }
}