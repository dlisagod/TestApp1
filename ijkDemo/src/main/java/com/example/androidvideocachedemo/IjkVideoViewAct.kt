package com.example.androidvideocachedemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.danikula.videocache.HttpProxyCacheServer
import com.example.androidvideocachedemo.databinding.ActIjkVideoViewBinding
import com.example.androidvideocachedemo.util.DescryFileMediaSource
import com.example.androidvideocachedemo.util.IjkTrackInfoWithIdx
import com.example.androidvideocachedemo.util.ProxyCacheServer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.misc.ITrackInfo
import tv.danmaku.ijk.media.player.widget.media.AndroidMediaController
import tv.danmaku.ijk.media.player.widget.media.IjkVideoView

/**
 * @create zhl
 * @date 2023/8/30 10:21
 * @description
 *
 * @update
 * @date
 * @description
 **/
val fileMpgEnc = Environment.getExternalStorageDirectory().absolutePath + "/download/910453(1).mpg"
val fileM3u8 =
    Environment.getExternalStorageDirectory().absolutePath + "/download/910453/910453.m3u8"
val fileM3u8_1 =
    Environment.getExternalStorageDirectory().absolutePath + "/download/910453/910453.1.m3u8"
val fileM3u8_2 =
    Environment.getExternalStorageDirectory().absolutePath + "/download/910453.1.m3u8"
val fileM3u8_3 =
    Environment.getExternalStorageDirectory().absolutePath + "/download/910453.应用数据路径.m3u8"
val fileM3u8_4 = "/data/data/com.example.ijkdemo3/cache/910453.应用数据路径.m3u8"
val fileM3u8_5 = "/data/data/com.example.ijkdemo3/cache/910453.nokey.m3u8"

val videos = arrayOf(
    "https://data.360guoxue.com/18000/AI/video/user/1/1729577401730.mp4",
    "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
    "http://117.21.14.240:8888/music/852799.MPG",
    "http://117.21.14.240:8888/dvd1/972566.mpg",
    "https://data.360guoxue.com/18000/Calligraphy/test/910453/910453.m3u8",
    "https://data.360guoxue.com/18000/Ldt/video/1/js1/13/13.m3u8",
    "http://117.21.14.242:8888/ktv/0a915678574400412bb6ae6dc66611f4/0a915678574400412bb6ae6dc66611f4.m3u8",
    "http://117.21.14.242:8888/ktv/100430/100430.m3u8",
    "http://117.21.14.242:8888/ktv/972566/972566.m3u8",
//    fileMpgEnc,
//    fileM3u8,
//    fileM3u8_1,
//    fileM3u8_2,
//    fileM3u8_3,
//    fileM3u8_4,
//    fileM3u8_5,
)

class IjkVideoViewAct : AppCompatActivity() {
    val TAG = "IjkVideoViewAct"

    //    private val videos = arrayOf(
//        "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
//        fileMpgEnc,
//        fileM3u8,
//        fileM3u8_1,
//        fileM3u8_2,
//        "https://data.360guoxue.com/18000/Calligraphy/test/910453/910453.m3u8",
//        "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear1/prog_index.m3u8",
//        "http://183.6.57.249:8888/music/1090614.MPG",
//        "http://183.6.57.249:8888/music/1065799.MPG",
//        "http://183.6.57.249:8888/music/1169378.MPG",
//        "http://183.6.57.249:8888/music/1094665.MPG"
//    )
    private var mServer: HttpProxyCacheServer? = null
    private lateinit var vb: ActIjkVideoViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActIjkVideoViewBinding.inflate(layoutInflater)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        hideVirtualBtn()
        setContentView(vb.root)
        checkPermission()
        initProxy()
        initVideoView()
        initSpinner()
    }

    private fun initVideoView() {
        vb.videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW)
//        vb.videoView.setRender(IjkVideoView.RENDER_SURFACE_VIEW)
        vb.videoView.apply {
            setMediaController(AndroidMediaController(this@IjkVideoViewAct, false))
            setHudView(vb.hudView)
            setOnPreparedListener {
                Log.d(TAG, "prepare")
                initTrackSpinner()
                vb.videoView.start()
            }
        }
    }

    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 10086)
        }
    }

    fun initProxy() {
        mServer = ProxyCacheServer.getNewCacheServer(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10086) {
            checkPermission()
        }
    }

    /**
     * 初始化下拉选单
     */
    private fun initSpinner() {
        val spinner = findViewById<Spinner>(R.id.spin)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, videos)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val url = videos[position]
//                prepareByProxy(url);
                prepare(url)
//                prepare("/storage/emulated/0/ldt/aivideo/左右徘徊.mov")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private var firstTimeSelect = false

    /**
     * 初始化音轨选择
     */
    private fun initTrackSpinner() {
        firstTimeSelect = true
        val trackInfos = vb.videoView.trackInfo
        Log.d(TAG, "trackInfos Ijk $trackInfos")
        val soundTracks: List<IjkTrackInfoWithIdx> = findSoundTrack(trackInfos)
        Log.d(TAG, "trackInfos sound $soundTracks")
        val strings: MutableList<String> = ArrayList()
        for (trackInfoWithIdx in soundTracks) {
            strings.add(trackInfoWithIdx.toString());
//            strings.add(trackInfoWithIdx.trackInfoString)
        }
        val spinner = findViewById<Spinner>(R.id.spin_01)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, strings)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
//                if (firstTimeSelect) {
//                    firstTimeSelect = false
//                    return
//                }
                val track = soundTracks[position]
                vb.videoView.selectTrack(track.idx)
                Log.d(
                    TAG,
                    "onItemSelected pos:$position idx:${track.idx} trackInfo:${track.mTrackInfo}"
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "onNothingSelected")
            }
        }
    }

    /**
     * 多音轨相关，可忽略
     *
     * @param trackInfos
     * @return
     */
    private fun findSoundTrack(trackInfos: Array<ITrackInfo>?): List<IjkTrackInfoWithIdx> {
        val list: MutableList<IjkTrackInfoWithIdx> = java.util.ArrayList()
        if (trackInfos != null) {
            val length = trackInfos.size
            for (idx in 0 until length) {
                val item = trackInfos[idx]
                if (item.trackType == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    list.add(IjkTrackInfoWithIdx(idx, item))
                }
            }
        }
        return list
    }

    /**
     * 使用代理地址准备资源
     *
     * @param url
     */
    private fun prepareByProxy(url: String) {
        val proxyUrl = mServer!!.getProxyUrl(url)
        prepare(proxyUrl)
    }

    /**
     * 滑动屏幕 可重新显示出来
     */
    protected open fun hideVirtualBtn() {
        val window = window
        window!!.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.setOnSystemUiVisibilityChangeListener {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }


    private fun prepare(url: String) {
        if (url == fileMpgEnc) {
            vb.videoView.setVideoMediaDataSource(DescryFileMediaSource(url))
            return
        }
        vb.videoView.setVideoPath(url)
//        vb.videoView
//        vb.videoView.
//        vb.videoView.start()
//        initTrackSpinner()
    }

    private var mBackPressed = false
    override fun onBackPressed() {
        mBackPressed = true
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        if (mBackPressed || !vb.videoView.isBackgroundPlayEnabled) {
            vb.videoView.stopPlayback()
            vb.videoView.release(true)
            vb.videoView.stopBackgroundPlay()
        } else {
            vb.videoView.enterBackground()
        }
        IjkMediaPlayer.native_profileEnd()
    }

    override fun onDestroy() {
        super.onDestroy()
        mServer?.shutdown()
    }
}