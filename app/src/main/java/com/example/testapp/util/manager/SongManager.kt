package com.example.testapp.util.manager


import android.media.MediaPlayer
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.example.testapp.MyApplication
import com.example.testapp.util.FilePathUtil
import com.example.testapp.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

/**
 * @ClassName: SongManager
 * @Description: ktv已点歌曲管理类(包括列表和部分播放控制)
 * @Author: zhl
 * @Date: 2021/1/29 9:11
 */
class SongManager private constructor() : SongPlayerManager() {
    private val TAG: String = SongManager::class.java.simpleName
    private lateinit var mProxy: HttpProxyCacheServer
    private var playAfterPrepare: Boolean = false

    /**
     * 初始化播放器等，初次使用以及 {@link #release()} 后再次使用时都需要调用初始化
     */
    fun init() {
        mProxy = MyApplication.getVideoProxy()
        super.initPlayer()
        addPreparedListener(MediaPlayer.OnPreparedListener {
            if (playAfterPrepare)
                play()
        })
//        addCompletionListener(MediaPlayer.OnCompletionListener {
//            nextSongByCompletion()
//        })
        addMainCompletionListener(MediaPlayer.OnCompletionListener {
            LogUtil.d(TAG, "onCompletion")
            LogUtil.d(
                "SongLog$TAG",
                " currentPosition ${it.currentPosition} , duration ${it.duration}"
            )

        })

        addMainErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            LogUtil.d(TAG, "onError")
            return@OnErrorListener true
        })
        mainCompleteAfterError = false
//        playDef()
    }

    fun prepareToPlay(urlOrPath: String) {
        prepareByProxy(urlOrPath)
//        prepareOrg(urlOrPath)
    }


    private fun prepareOrg(urlOrPath: String) {
        playAfterPrepare = true
        prepare(urlOrPath)

    }


    private fun prepareByProxy(urlOrPath: String) {
        val name = FilePathUtil.getFileName(urlOrPath)
        if (FilePathUtil.isVideoExist(urlOrPath)) {
            val fileAbsPath = FilePathUtil.getLocalVideoPath().absolutePath + File.separator + name
            playAfterPrepare = true
            prepare(fileAbsPath)
        } else {
            val proxyUrl = mProxy.getProxyUrl(urlOrPath)
            mProxy.registerCacheListener({ cacheFile, url, percentsAvailable ->
                LogUtil.d(TAG, "percentsAvailable $percentsAvailable")
                if (percentsAvailable >= 5 && isMainPrepared && !isPlaying) {
                    LogUtil.d(TAG, "percentsAvailable $percentsAvailable play invoked")
                    play()
                }
            }, urlOrPath)
//            playAfterPrepare = false
            playAfterPrepare = true
            prepare(proxyUrl)
        }
    }

    /**
     * 改变音轨
     */
    override fun changeSoundTrack() {
        super.changeSoundTrack()
    }

    private var lastTimeNext: Long = System.currentTimeMillis()

    protected override fun play() {
        super.play()
    }

//    public override fun pause() {
//        super.pause()
//    }

    /**
     * 播放暂停并发送事件更新
     */
    override fun playOrPause() {
        super.playOrPause()
    }


    var mCurrentVideoTag: String = ""

    var preparedInDown: Boolean = false


    /**
     * 释放资源，在不需要使用后调用释放资源；再次使用需要初始化
     */
    public override fun release() {
//        mProxy.shutdown()
        super.release()
    }

    //预缓存任务
    private var preCacheJob: Job? = null

    //是否取消预缓存
    private var cancel: Boolean = false

    /**
     * 访问代理地址尝试预缓存
     */
    fun preCache(url: String) {
        try {
            cancel = true
            preCacheJob?.cancel()
            //此地址已缓存完毕
            if (mProxy.isCached(url)) return
            preCacheJob = GlobalScope.launch(Dispatchers.IO) {
                try {
                    cancel = false
                    val mUrl = URL(mProxy.getProxyUrl(url))
                    val inputStream = mUrl.openStream()
                    val buffer = ByteArray(1024)
                    while (inputStream.read(buffer) != -1 && !cancel) {

                    }
                    inputStream.close()
                } catch (e: Exception) {
                    LogUtil.d(
                        "MyVideoProxy",
                        e.message ?: "null message while MyVideoProxy#preCache"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止预缓存
     */
    fun stopPreCache() {
        cancel = true
        preCacheJob?.cancel()
    }


    companion object {
        val instance: SongManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {//等同于java双重检测线程安全
            SongManager()
        }
    }

//    class MSongPlayerManager private constructor() : SongPlayerManager() {
//        companion object {
//            val instance: MSongPlayerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {//等同于java双重检测线程安全
//                MSongPlayerManager()
//            }
//        }
//    }


}