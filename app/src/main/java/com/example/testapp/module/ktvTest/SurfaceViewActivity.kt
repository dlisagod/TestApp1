package com.example.testapp.module.ktvTest

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.example.testapp.MyApplication
import com.example.testapp.R
import com.example.testapp.bean.TrackInfoWithIdx
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ktv_test.*
import kotlinx.android.synthetic.main.activity_ktv_test.btn_play
import kotlinx.android.synthetic.main.activity_ktv_test.file_spinner
import kotlinx.android.synthetic.main.activity_ktv_test.speed_spinner
import kotlinx.android.synthetic.main.activity_ktv_test.track_spinner
import java.io.File

class SurfaceViewActivity : AppCompatActivity() {
    //视频url
    var videoUrl: String = ""
    var mediaPlayer: MediaPlayer? = null
    var mHolder: SurfaceHolder? = null

    //MediaPlayer获取到的trackInfo数组
    var trackInfos: Array<MediaPlayer.TrackInfo>? = null
    var canPlay: Boolean = false

    //按钮切换音轨用到的记录当前音轨的index   按钮切换已废弃
    var currentIndex = 1

    //文件字符串数组
//    var files: Array<String>? = null

    var files: Array<String>? =
//        arrayOf(
//            "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
//            "http://183.6.57.249:8888/music/1190286.MPG",
//            "http://183.6.57.249:8888/music/1107612.MPG",
//            "http://183.6.57.249:8888/music/1074555.MPG",
//            "http://183.6.57.249:8888/music/1109745.MPG"
//        )
    arrayOf(
        "http://183.6.57.249:8888/music/1090614.MPG",
        "http://183.6.57.249:8888/music/1065799.MPG",
        "http://183.6.57.249:8888/music/1169378.MPG",
        "http://183.6.57.249:8888/music/1094665.MPG"
        )

    //音轨字符串数组
    var tracks: Array<String?>? = null

    //速度数组
    var speedArray: Array<String?>? = null

    var mGson: Gson? = null
    val video1 = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4"
    val video2 = "http://183.6.57.249:8888/music/1190286.MPG"
    val video3 = "http://183.6.57.249:8888/music/1107612.MPG"
    val video4 = "http://183.6.57.249:8888/music/1074555.MPG"
    val video5 = "http://183.6.57.249:8888/music/1109745.MPG"
    val pathTest =
        Environment.getExternalStorageDirectory().canonicalPath + File.separator + "Movies" + File.separator + "1169378.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = javaClass.simpleName
        setContentView(R.layout.activity_ktv_test)
        initData()
        initView()
    }

    private fun initData() {
//        mGson = Gson()
        mediaPlayer = MediaPlayer()
//        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

//        val filesF = FilePathUtil.getLocalVideoPath().listFiles()
//        files = FilePathUtil.getLocalVideoPath().list()
//        files = arrayOf(video1, video2, pathTest)
//        files = arrayOf(video1, video2)
        speedArray = arrayOf("1.0", "1.5", "2.0", "0.5", "0.25")
//        spinner.
//        videoUrl = FilePathUtil.getLocalVideoPath().absolutePath + File.separator + "周杰伦-MOJITO-国语-1168836.mpg"

        val mediaPlayerTest = MediaPlayer()
        Toast.makeText(
            this,
            "isPlaying ${mediaPlayerTest.isPlaying},currentPosition ${mediaPlayerTest.currentPosition}",
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun initView() {

        mediaPlayer?.setOnPreparedListener {
            it.start()
            trackInfos = mediaPlayer?.trackInfo
            mediaPlayer?.setDisplay(mHolder)
            val size = trackInfos?.size ?: 0
            Log.d("trackInfo", "size " + size.toString())

            Log.d("trackInfo", trackInfos?.get(0)?.toString() ?: "null")
            Log.d("trackInfo", JSON.toJSONString(JSONArray.toJSON(trackInfos)) ?: "null")
//            Log.d("trackInfo", JSONArray.toJSON(trackInfos) as String)

            //加载音轨
            initTrack()
            //加载速度
            initSpeed()
//            Toast.makeText(this, "isPlaying " + mediaPlayer?.isPlaying, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "isPlaying " + mediaPlayer?.isPlaying, Toast.LENGTH_SHORT).show()
//            initFirstFrame()
        }
//        prepare()

        mediaPlayer?.setOnBufferingUpdateListener { mp, percent ->
//            Toast.makeText(this, "percent is $percent", Toast.LENGTH_SHORT).show()
        }
//        surfaceView 监听
        val surfaceCallback = object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}

            override fun surfaceCreated(holder: SurfaceHolder) {
                if (mHolder == null) {
                    mHolder = surfaceView?.holder
                    mHolder?.addCallback(this)
//                    mHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                    mediaPlayer?.setDisplay(mHolder)
                }
            }

        }
        mHolder = surfaceView.holder
        mHolder?.addCallback(surfaceCallback)
        mHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        //播放暂停
        btn_play.setOnClickListener {
//            mediaPlayer?.setDataSource(videoUrl)
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
            }
        }

        //文件列表 spinner
        file_spinner.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, files!!)
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
//                videoUrl =
//                    FilePathUtil.getLocalVideoPath().absolutePath + File.separator + files!![position]
//                proxy(files!![position])

//                prepare(files!![position])
//                prepare2()
                prepareByProxy(files!![position])
            }
        }


        file_spinner.setSelection(0)

        //按钮切换音轨，已废弃
        btn_sound_track.setOnClickListener {
            if (!trackInfos.isNullOrEmpty() && trackInfos?.size!! > 1) {
                val idx1 =
                    mediaPlayer?.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO)
                val idx2 =
                    mediaPlayer?.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO)
                        ?: -1
                Log.d(
                    "trackInfo",
                    "MEDIA_TRACK_TYPE_VIDEO $idx1 MEDIA_TRACK_TYPE_AUDIO $idx2"
                )
                currentIndex = if (currentIndex == 1) {
                    mediaPlayer?.selectTrack(0)
//                    mediaPlayer?.sets
                    0
                } else {
                    mediaPlayer?.selectTrack(1)
                    1
                }
            } else {
                Toast.makeText(this, "无可切换音轨", Toast.LENGTH_SHORT).show()
            }
        }
//
//        mediaPlayer?.setOnBufferingUpdateListener { mp, percent ->
//
//        }
//        mediaPlayer?.prepareAsync()

        MyApplication.getVideoProxy().registerCacheListener({ cacheFile, url, percentsAvailable ->
            Toast.makeText(
                this@SurfaceViewActivity,
                "$percentsAvailable",
                Toast.LENGTH_LONG
            ).show()
        }, videoUrl)
    }


    private fun prepareByProxy(url: String) {

        videoUrl = MyApplication.getVideoProxy().getProxyUrl(url)

        mediaPlayer?.also {
//                    if (it.isPlaying) {
//                        it.stop()
//                    }
//                    it.release()
//                    videoUrl = "http://data.360guoxue.com:16000/ALIOSS_IMG_/1594278423000.mp4"
            it.reset()
//                    it.setDataSource(videoUrl)
//                    val path =
//                        Environment.getExternalStorageDirectory().canonicalPath + File.separator +"Movies"+ File.separator +"1169378.mp4"
//                    it.setDataSource(path)
            it.setDataSource(videoUrl)
            it.prepareAsync()
//                    it.prepare()
//                    it.start()
        }
    }

    private fun prepare(url: String) {
        mediaPlayer?.also {
//                    if (it.isPlaying) {
//                        it.stop()
//                    }
//                    it.release()
//                    videoUrl ="http://data.360guoxue.com:16000/ALIOSS_IMG_/1594278423000.mp4"
            it.reset()
//                    it.setDataSource(videoUrl)
            videoUrl = url
            it.setDataSource(videoUrl)
            it.prepareAsync()
//                    it.prepare()
//                    it.start()
        }
    }

    private fun prepare2() {
        mediaPlayer?.also {
//                    if (it.isPlaying) {
//                        it.stop()
//                    }
//                    it.release()
//                    videoUrl ="http://data.360guoxue.com:16000/ALIOSS_IMG_/1594278423000.mp4"
            it.reset()
//                    it.setDataSource(videoUrl)

            val name = "1.mpg"
            val path =
                Environment.getExternalStorageDirectory().canonicalPath + File.separator + "Movies" + File.separator + name
            it.setDataSource(path)
            it.prepareAsync()
//                    it.prepare()
//                    it.start()
        }
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
                val params = mediaPlayer?.playbackParams
                params?.also {
                    val speed = speedArray!![position]?.toFloat()!!
                    params.speed = speed
                    Toast.makeText(this@SurfaceViewActivity, "" + speed, Toast.LENGTH_SHORT).show()
                    mediaPlayer?.playbackParams = params
                }
            }

        }
        speed_spinner.setSelection(0)

    }

    var listSoundTrackInfo: MutableList<TrackInfoWithIdx>? = null//音轨
    var selectIdx = -1//用于取消选中

    /**
     * 加载音轨
     */
    fun initTrack() {
        selectIdx = -1
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
                selectIdx = position
                Log.d("selectIdx", position.toString())
                val idx = listSoundTrackInfo?.get(position)?.idx!!
                mediaPlayer?.selectTrack(idx)
            }

        }
    }

    /**
     * 从获取到的trackinfo数组里找出音轨
     * @param trackInfos 前面获取到的
     */
    fun findSoundTrack(trackInfos: Array<MediaPlayer.TrackInfo>?): MutableList<TrackInfoWithIdx> {
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
        mediaPlayer?.release()
        MyApplication.getVideoProxy().shutdown()
    }

}