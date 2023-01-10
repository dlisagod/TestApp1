package com.example.testapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.download.http.DownInfo
import com.example.download.http.HttpDownloadManager
import com.example.download.http.HttpProgressOnNextListener
import com.example.testapp.module.DrawerActivity
import com.example.testapp.module.ScrollingActivity
import com.example.testapp.module.TestOneActivity
import com.example.testapp.module.TextureViewTestActivity
import com.example.testapp.module.ktvTest.SurfaceViewActivity
import com.example.testapp.util.FilePathUtil
import com.example.testapp.util.Test
import com.example.testapp.util.TestK
import com.example.testapp.util.ToastUtil
import com.example.testapp.util.effect.EffectFactory
import com.example.testapp.webSocket.SocketManager
import com.example.testapp.widget.getResIdByAttr
import com.example.testapp.widget.getResIdByAttr2
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    val ad = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4"

    val baseUrl = "https://readingpavilion.oss-cn-beijing.aliyuncs.com/"
    val videoUrl = "ALIOSS_IMG_/1596784890000.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_02)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
//        Test.testBigDecimal()
        initView()
//        test()
//        testDpToPx()
        Glide.with(this)
            .load("http://data.360guoxue.com:16000/ALIOSS_IMG_/1604390194000.jpg")
            .apply(RequestOptions().circleCrop())
            .into(iv)
//        Glide.with(this)
//            .load("http://data.360guoxue.com:16000/ALIOSS_IMG_/1604390194000.jpg")
//            .apply(RequestOptions().circleCrop())
//            .into(iv)
        iv.setRadius(15F, 20F)
        localeTest()
//        SocketManager.getInstance().connect()
        tv_drawer.postDelayed(r, 1000)
        tv_drawer.postDelayed(r, 2000)

        Test.logDensity(this)
        TestK.KotlinRandomTest(3)
    }

    val r = Runnable { tv_drawer.text = "${tv_drawer.text} ${System.currentTimeMillis()}" }

    var downInfo = DownInfo()
    private fun initView() {
        themeTest2()

        tv_01.setOnClickListener {
            startActivity(Intent(this, TestOneActivity::class.java))
        }

        main_tv_send.setOnClickListener {
            val text = main_edt.editableText.toString();
            if (text.isBlank()) {
                SocketManager.getInstance().send()
            } else {
                Toast.makeText(this, "send $text", Toast.LENGTH_SHORT).show()
                SocketManager.getInstance().send(text)
            }
        }
        main_tv_connect.setOnClickListener {
            SocketManager.getInstance().connect()
        }
        tv.setOnClickListener {
//            progress_bar.setMax(100)
//            progress_bar.progress = ++progress_bar.progress
//            Log.d("progressBar", progress_bar.progress.toString())
            val l = object : HttpProgressOnNextListener<DownInfo>() {
                override fun updateProgress(readLength: Long, countLength: Long) {
                    val progress: Int = (readLength.toDouble() / countLength * 100).toInt()
                    if (progress_bar.progress == progress) return
                    progress_bar.progress = progress
                    Log.d("downloadListener", "updateProgress $progress")
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

                override fun onError(e: Throwable?) {
                    Log.d("downloadListener", "onError " + e?.message)
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
//        tv.text = null

        myView.setOnClickListener {

            startActivity(Intent(this, SurfaceViewActivity::class.java))
//            iv.setRadius(20F,25F)
        }

        iv.setOnClickListener {
            startActivity(Intent(this, TextureViewTestActivity::class.java))
        }

        iv_01.setOnClickListener {
//            val intent = Intent(this, ViewPager2DynamicTestActivity::class.java)
//            startActivity(intent)
        }

        tv_collapse.setOnClickListener {
            startActivity(Intent(this, ScrollingActivity::class.java))
        }

        tv_drawer.setOnClickListener {
            startActivity(Intent(this, DrawerActivity::class.java))
        }

    }

    fun progressTest() {
        Thread(Runnable {
            var i = 0
            var j = 0
            while (i < 101) {
                j = 0
                while (j < 5) {
                    Thread.sleep(100)
                    runOnUiThread {
                        progress_bar.progress = i
                    }
                    Log.d("progress", i.toString())
                    j++
                }
                i++
            }
        }).start()
    }

    val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.INTERNET
    )
    val requestCode = 0
    private fun initPermission() {
        for (permission in permissions) {

            var b = checkSelfPermission(permission)
            if (b == PackageManager.PERMISSION_GRANTED) {
//            test()
//            FilePathUtil.deleteIfVideoOutOf2G()
//            FilePathUtil.getSize(FilePathUtil.getLocalVideoPath())
            } else {
                requestPermissions(permissions, requestCode)
//                ActivityCompat.requestPermissions(this,permissions,requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for ((count, s) in permissions.withIndex()) {
            if (s == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[count] == PackageManager.PERMISSION_GRANTED) {
//                test()
                break
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.showLong("请开启允许在其他应用上层显示")
                val intent =
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
                return
            }
        }
    }

    fun test() {
//        var b = FilePathUtil.delete(Environment.getExternalStorageDirectory().toString() + "/test/")
//        FilePathUtil.deleteAllVideo()

//        Log.d("FirstEf1",ef1.)
        val effect = EffectFactory.createEffect()
        val backVolumeStr = effect.getVolumeFromBack(byteArrayOf())
        Log.d("Effect", backVolumeStr[0] + "\n" + backVolumeStr[1] + "\n" + backVolumeStr[2])
    }

    fun testDpToPx() {
        Log.d("dpToPx", "" + 352 + Test.dp2px(this, 352F))
        Log.d("dpToPx", "" + 200 + Test.dp2px(this, 200F))
        Log.d("dpToPx", "" + 352 + Test.dip2px(this, 352F))
        Log.d("dpToPx", "" + 200 + Test.dip2px(this, 200F))
    }

    fun testRename() {
        var sourceFile =
            File(FilePathUtil.getLocalVideoPath().absolutePath + File.separator + "source.mp4.temp")
        if (!sourceFile.exists()) sourceFile.mkdirs()
        var destFile =
            File(FilePathUtil.getLocalVideoPath().absolutePath + File.separator + "source.mp4")
        val renameSuccess =
            FilePathUtil.rename(sourceFile, destFile)
        Log.d("rename", renameSuccess.toString())
    }

    private fun localeTest() {
        val china = Locale.CHINA
        Log.d(
            "localeTest",
            "Local.CHINA toString:" + china.toString() + " toLangTag:" + china.toLanguageTag()
        )
        val chinese = Locale.CHINESE
        Log.d(
            "localeTest",
            "Locale.CHINESE toString:" + chinese.toString() + " toLangTag:" + chinese.toLanguageTag()
        )
        val chs = Locale.SIMPLIFIED_CHINESE
        Log.d(
            "localeTest",
            "Locale.SIMPLIFIED_CHINESE toString:" + chs.toString() + " toLangTag:" + chs.toLanguageTag()
        )
        val cht = Locale.TRADITIONAL_CHINESE
        Log.d(
            "localeTest",
            "Locale.TRADITIONAL_CHINESE toString:" + cht.toString() + " toLangTag:" + cht.toLanguageTag()
        )
        val eng = Locale.ENGLISH
        Log.d(
            "localeTest",
            "Locale.ENGLISH toString:" + eng.toString() + " toLangTag:" + eng.toLanguageTag()
        )
    }

    private fun themeTest1() {
        val tag = "MainActivity"
        val messagePredix = "theme Test: "

        val themeActBefore = theme
        val themeAppBefore = MyApplication.context.theme
        val tpBefore = TypedValue()
        themeActBefore.resolveAttribute(R.attr.img_test_01, tpBefore, true)
        iv_01.setImageResource(tpBefore.resourceId)
        Log.d(tag, messagePredix + "themeAct before " + themeActBefore)
        Log.d(tag, messagePredix + "themeApp before " + themeAppBefore)


        setTheme(R.style.AppTheme_02)
        MyApplication.context.setTheme(R.style.AppTheme_01)
        val themeActAfter = theme
        val themeAppAfter = MyApplication.context.theme
        val tpAfter = TypedValue()
        themeActAfter.resolveAttribute(R.attr.img_test_01, tpAfter, true)
        iv.setImageResource(tpBefore.resourceId)
        Log.d(tag, messagePredix + "themeAct after " + themeActAfter)
        Log.d(tag, messagePredix + "themeApp after " + themeAppAfter)

    }

    private fun themeTest2() {
        val data = getResIdByAttr(R.attr.img_test_01)
        Log.d("getColorIdByAttr", "equals ${data == R.mipmap.icon_collection}")
        val data2 = getResIdByAttr2(R.attr.img_test_01)
        Log.d("getColorIdByAttr2", "equals ${data2 == R.mipmap.icon_play}")
    }

    override fun onDestroy() {
        SocketManager.getInstance().close()
        super.onDestroy()
        HttpDownloadManager.instance?.stopDown(downInfo)
    }
}