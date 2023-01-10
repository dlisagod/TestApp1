package com.example.testapp.widget

import android.app.Presentation
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.TextureView
import android.view.View
import com.example.download.util.DownloadManager
import com.example.testapp.R
import com.example.testapp.util.manager.SongManager
import kotlinx.android.synthetic.main.presentation_video_and_pic.*
import okhttp3.Call
import java.io.File

/**
 * @ClassName: KtvSecondDisplay
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2021/2/22 14:09
 */
class KtvPresentation(outerContext: Context, display: Display) :
    Presentation(outerContext, display) {

    private val TAG = KtvPresentation::class.java.simpleName

    private var callBack: DownloadManager.CallBack = object : DownloadManager.CallBack() {
        override fun inProgress(progress: Float, total: Long, id: Int) {

//            if(!SongManager.instance.isPlaying){
//                showProgressAndTips()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    progress_bar.setProgress((progress * 100).toInt(), true)
//                } else {
//                    progress_bar.progress = (progress * 100).toInt()
//                }
//            }
//            LogUtil.d(TAG, "下载进度$progress $total")
        }

        override fun onResponse(response: File?, id: Int) {
            hideProgressAndTips()
        }

        override fun onError(call: Call?, e: Exception?, id: Int) {
            hideProgressAndTips()
        }
    }

    private var mSurface: Surface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_video_and_pic)
        initView()
        initListener()
    }

    private fun initView() {
        progress_bar.max = 100
        hideProgressAndTips()
    }

    private fun initListener() {
//        SongManager.instance.addDownCallBack(callBack)
        texture_view.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                surface?.also {
                    mSurface = Surface(surface).also {
                        SongManager.instance.setSecondSurface(it)
//                        SongManager.instance.
                    }
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

        }
    }

    //显示下载进度条 隐藏surfaceView
    private fun showProgressAndTips() {
        progress_bar.visibility = View.VISIBLE
        tips.visibility = View.VISIBLE
        texture_view.visibility = View.GONE
    }

    //隐藏进度条 显示surfaceView
    private fun hideProgressAndTips() {
        progress_bar.visibility = View.GONE
        tips.visibility = View.GONE
        texture_view.visibility = View.VISIBLE
    }

    override fun cancel() {
        mSurface?.release()
        super.cancel()
    }

    override fun onDetachedFromWindow() {
        //todo
//        SongManager.instance.removeCallBack(callBack)
        super.onDetachedFromWindow()
    }

}