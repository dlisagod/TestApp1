package com.example.testapp.module

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.R
import com.youth.banner.Banner
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.activity_banner.*

/**
 *
 * @ClassName: BannerActivity
 * @Description: 类作用描述
 * @Author: zhl
 * @CreateDate: 2020/10/13 9:54
 */
class BannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        initView()
    }

    fun initView(){
        iv_banner_2.start()
//        var banner : Banner
        iv_banner_2.isAutoPlay(true)
        iv_banner_2.setImageLoader(object : ImageLoader(){
            override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {

            }

        })
    }
}