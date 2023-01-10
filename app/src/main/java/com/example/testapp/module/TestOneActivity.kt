package com.example.testapp.module

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.download.http.DownInfo
import com.example.download.http.HttpDownloadManager
import com.example.download.http.HttpProgressOnNextListener
import com.example.testapp.R
import com.example.testapp.util.FilePathUtil
import kotlinx.android.synthetic.main.activity_test_one.*
import java.io.File

class TestOneActivity : AppCompatActivity() {
    val ad = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4"

    val baseUrl = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/"
    val videoUrl = "ALIOSS_IMG_/1596784890000.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_one)
        initView()
    }


    var downInfo = DownInfo()
    private fun initView() {
        tv.setOnClickListener {
//            progress_bar.setMax(100)
//            progress_bar.progress = ++progress_bar.progress
//            Log.d("progressBar", progress_bar.progress.toString())
            val l = object : HttpProgressOnNextListener<DownInfo>() {
                override fun updateProgress(readLength: Long, countLength: Long) {
                    val progress: Int = (readLength.toDouble() / countLength * 100).toInt()
                    if (progress_bar.progress == progress) return
                    progress_bar.progress = progress
                    Log.d("downloadListener", "updateProgress " + progress)
//                    progress_bar.max = countLength.toInt()
//                    progress_bar.progress = readLength.toInt()
//                    Log.d("downloadListener", "updateProgress " + readLength)
                }

                override fun onNext(t: DownInfo) {
                    Log.d("downloadListener", "onNext")
                }

                override fun onStart() {
                    Log.d("downloadListener", "start")
                }

                override fun onComplete() {
                    Log.d("downloadListener", "complete")
                }

            }
            downInfo.listener = l
            downInfo.baseUrl = baseUrl
            downInfo.url = videoUrl
            downInfo.savePath =
                FilePathUtil.getLocalVideoPath().absolutePath + File.separator + FilePathUtil.getFileName(
                    ad
                )
            HttpDownloadManager.instance?.startDown(downInfo)

        }
    }
}