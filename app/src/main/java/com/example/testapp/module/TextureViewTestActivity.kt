package com.example.testapp.module

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.example.testapp.MyApplication
import com.example.testapp.R
import com.example.testapp.bean.TrackInfoWithIdx
import com.example.testapp.util.LogUtil
import com.example.testapp.util.manager.SongManager
import com.example.testapp.widget.KtvPresentation
import kotlinx.android.synthetic.main.activity_texture_view_text.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

class TextureViewTestActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null

    //MediaPlayer获取到的trackInfo数组
    var trackInfos: Array<MediaPlayer.TrackInfo>? = null
    var prepared: Boolean = false

    //视频url
    var videoUrl: String = ""

    //按钮切换音轨用到的记录当前音轨的index   按钮切换已废弃
    var currentIndex = 1

    val video1 = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4"
    val video2 = "http://183.6.57.249:8888/music/1190286.MPG"
    val video3 = "http://183.6.57.249:8888/music/1107612.MPG"
    val video4 = "http://183.6.57.249:8888/music/1074555.MPG"
    val video5 = "http://183.6.57.249:8888/music/1109745.MPG"
    val video6 = "http://183.6.57.249:8888/music/1090614.MPG"
    //文件字符串数组
//    var files: Array<String>? = null
    var files: Array<String>? = arrayOf(
        "http://183.6.57.249:8888/music/1090614.MPG",
        "http://183.6.57.249:8888/music/1065799.MPG",
        "http://183.6.57.249:8888/music/1169378.MPG",
        "http://183.6.57.249:8888/music/1094665.MPG"
    )

    //音轨字符串数组
    var tracks: Array<String?>? = null

    //速度数组
    var speedArray: Array<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = javaClass.simpleName
        setContentView(R.layout.activity_texture_view_text)
        initData()
        initTextureView()
        initView()
    }

    private fun initData() {
//        startKtv()
        initMediaPlayer()
//        mediaPlayer = MediaPlayer()
//        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        files = FilePathUtil.getLocalVideoPath().list()
//        files = arrayOf(video1, video2, video3, video4, video5)
        speedArray = arrayOf("1.0", "1.5", "2.0", "0.5", "0.25")
//        spinner.
//        videoUrl = FilePathUtil.getLocalVideoPath().absolutePath + File.separator + "周杰伦-MOJITO-国语-1168836.mpg"-
    }

    private fun initView() {
        themeTest()
        //播放暂停
        btn_play.visibility = View.GONE
        btn_play.setOnClickListener {
//            mediaPlayer?.setDataSource(videoUrl)
//            if (mediaPlayer?.isPlaying!!) {
//                mediaPlayer?.pause()
//            } else {
//                mediaPlayer?.start()
//                iv.visibility = View.GONE
//            }
//            SongManager.instance.playOrPause()
        }

        sound_track.visibility = View.GONE
        sound_track.setOnClickListener {
//            SongManager.instance.changeSoundTrack()
        }

        //文件列表组件
        file_spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, files!!)
        file_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                SongManager.instance.prepareToPlay(files!![position])
//                prepare(files!![position])
                prepareByProxy(files!![position])
            }

        }
        file_spinner.setSelection(0)
//加载速度
        initSpeed()
    }

    private fun prepareByProxy(url: String) {
        val proxy = MyApplication.getVideoProxy()
        stopPreCache()
        videoUrl = proxy.getProxyUrl(url)
        proxy.registerCacheListener(object : CacheListener {
            override fun onCacheAvailable(
                cacheFile: File?,
                url: String?,
                percentsAvailable: Int
            ) {
                Toast.makeText(
                    this@TextureViewTestActivity,
                    "$percentsAvailable",
                    Toast.LENGTH_LONG
                ).show()
//                if (percentsAvailable > 3 && mediaPlayer?.isPlaying == true) {
//                    stopPreCache()
//                }
                if (percentsAvailable == 100) {
                    stopPreCache()
                }
            }
        }, videoUrl)
        if (proxy.isCached(url)) {//已缓存完毕
//            playAfterPrepare = true

        } else {//未缓存完毕
            preCache(url, proxy)
        }
        prepare(videoUrl)
    }

    //预缓存任务
    private var preCacheJob: Job? = null

    //是否取消预缓存
    private var cancel: Boolean = false

    fun preCache(url: String, proxy: HttpProxyCacheServer) {
        try {
            cancel = true
            preCacheJob?.cancel()
            //此地址已缓存完毕
            if (proxy.isCached(url)) return
            preCacheJob = GlobalScope.launch(Dispatchers.IO) {
                try {
                    cancel = false
                    val mUrl = URL(proxy.getProxyUrl(url))
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


    private fun initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().also {
                it.setOnPreparedListener {
//            it.seekTo(0)
                    it.start()
//            initFirstFrame()
                    trackInfos = mediaPlayer?.trackInfo

                    val size = trackInfos?.size ?: 0
                    Log.d("trackInfo", "size $size")
                    Log.d("trackInfo", trackInfos?.get(0)?.toString() ?: "null")
                    Log.d("trackInfo", JSON.toJSONString(JSONArray.toJSON(trackInfos)) ?: "null")
//            Log.d("trackInfo", JSONArray.toJSON(trackInfos) as String)

                    //加载音轨
                    initTrack()
                    //重置速度
                    initSpeed()
                    Toast.makeText(this, "isPlaying " + mediaPlayer?.isPlaying, Toast.LENGTH_SHORT)
                        .show()
                    it.setOnBufferingUpdateListener { mp, percent ->
//                Toast.makeText(this, "percent is $percent", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun prepare(url: String) {
        mediaPlayer?.also {
            it.reset()
            videoUrl = url
            it.setDataSource(videoUrl)
            it.prepareAsync()
        }
    }

    private fun themeTest() {
        val tag = javaClass.simpleName
        val messagePredix = "theme Test: "

        val themeActBefore = theme
        val themeAppBefore = MyApplication.context.theme
        Log.d(tag, messagePredix + "themeAct before " + themeActBefore)
        Log.d(tag, messagePredix + "themeApp before " + themeAppBefore)

        setTheme(R.style.AppTheme_02)
        MyApplication.context.setTheme(R.style.AppTheme_01)
        val themeActAfter = theme
        val themeAppAfter = MyApplication.context.theme
        Log.d(tag, messagePredix + "themeAct after " + themeActAfter)
        Log.d(tag, messagePredix + "themeApp after " + themeAppAfter)

    }

    var ktvPresentation: KtvPresentation? = null
    private fun startKtv() {
        SongManager.instance.init()
        startPresentation()
    }

    private fun startPresentation() {
        val displayManager =
            getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays: Array<Display> = displayManager.displays
        if (displays.size >= 2) {
//            ktvPresentation = KtvPresentation(MyApplication.context, displays[1])
//            val window = ktvPresentation?.window
//            window?.setType(WindowManager.LayoutParams.TYPE_PHONE)
            ktvPresentation = KtvPresentation(this, displays[1])
            ktvPresentation?.show()
        }
    }

    private fun initTextureView() {

        texture_view.setSurfaceTextureListener(object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {

            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return true
            }

            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                val surface = Surface(surfaceTexture)
                mediaPlayer?.setSurface(surface)
//                SongManager.instance.setMainSurface(surface)
            }

        })
    }

    /**
     * 速度调节
     */
    fun initSpeed() {
        speed_spinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speedArray!!)
        speed_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                var params = mediaPlayer?.playbackParams
//                params?.also {
//                    var speed = speedArray!![position]?.toFloat()!!
//                    try {
//
//                        if (mediaPlayer?.isPlaying!!) {
//
//                            params.speed = speed
////                    Toast.makeText(this@TextureViewTestActivity, "" + speed, Toast.LENGTH_SHORT)
////                        .show()
//                            mediaPlayer?.playbackParams = params
//                        } else {
//                            params.speed = speed
////                    Toast.makeText(this@TextureViewTestActivity, "" + speed, Toast.LENGTH_SHORT)
////                        .show()
//                            mediaPlayer?.playbackParams = params
//                            mediaPlayer?.pause()
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
            }

        }
        speed_spinner.setSelection(0)

    }

    var listSoundTrackInfo: MutableList<TrackInfoWithIdx>? = null//音轨

    /**
     * 加载音轨
     */
    fun initTrack() {

        listSoundTrackInfo = findSoundTrack(trackInfos)
        //音轨数组生成字符串数组，展示到spinner上
        tracks = arrayOfNulls(listSoundTrackInfo?.size!!)
        if (!listSoundTrackInfo.isNullOrEmpty())
            for ((idx, item) in listSoundTrackInfo!!.withIndex()) {
//                tracks!![idx] = mGson?.toJson(item)
                tracks!![idx] = JSON.toJSONString(item)
            }
        track_spinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tracks!!)
        track_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("trackInfo", JSON.toJSONString(JSONArray.toJSON(trackInfos)) ?: "null")
//                mediaPlayer?.deselectTrack(position)
//                if (selectIdx != -1){
//                    mediaPlayer?.deselectTrack(selectIdx)
//                }

                Log.d("selectIdx", position.toString())
                val idx = listSoundTrackInfo?.get(position)?.idx!!
//                mediaPlayer?.selectTrack(idx)
            }

        }
    }

    /**
     * 从获取到的trackinfo数组里找出音轨
     * @param trackInfos 前面获取到的
     */
    private fun findSoundTrack(trackInfos: Array<MediaPlayer.TrackInfo>?): MutableList<TrackInfoWithIdx> {
        val list: MutableList<TrackInfoWithIdx> = mutableListOf()
        trackInfos?.also {
            for ((idx, item) in trackInfos.withIndex()) {
                if (item.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    list.add(TrackInfoWithIdx(idx, item))
                }
            }
        }
        return list
    }

    private val mediaMetadataRetriever = MediaMetadataRetriever()
    private fun initFirstFrame() {

        mediaMetadataRetriever.setDataSource(videoUrl)
        val firstFrame = mediaMetadataRetriever.getFrameAtTime(0)
        iv.visibility = View.VISIBLE
        iv.setImageBitmap(firstFrame)
//        mediaMetadataRetriever.release()
    }


    override fun onDestroy() {
        super.onDestroy()
        ktvPresentation?.dismiss()
        mediaPlayer?.release()
        mediaMetadataRetriever.release()
        SongManager.instance.release()
    }
}