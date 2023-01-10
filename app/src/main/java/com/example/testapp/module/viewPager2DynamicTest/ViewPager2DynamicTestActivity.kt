package com.example.testapp.module.viewPager2DynamicTest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapp.R
import kotlinx.android.synthetic.main.activity_view_pager2_dynamic_test.*

class ViewPager2DynamicTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2_dynamic_test)
        initData()
        initView()
    }

    private fun initView() {
//        vp.adapter
        tv_left.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
//                vp.setCurrentItem()
            }
        }
        tv_right.setOnClickListener {
            if (currentPage < pageSize) {
                currentPage++
//                vp.setCurrentItem()
            }
        }

    }

    val pageSize = 10
    var currentPage = 0
    var adapter: DynamicFragmentAdapter? = null
    var sparseArray: SparseArray<Fragment> = SparseArray(4)
    private fun initData() {

        adapter = DynamicFragmentAdapter(this, sparseArray)

        sparseArray.put(
            0,
            TestFragment.newInstance(
                "",
                "http://data.360guoxue.com:16000/ALIOSS_IMG_/1604390194000.jpg"
            )
        )
        sparseArray.put(
            1,
            TestFragment.newInstance(
                "",
                "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591148844000.jpg"
            )
        )
//        https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591148844000.jpg
//        https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591148878000.jpg
//        https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591149235000.jpg
        sparseArray.put(
            2,
            TestFragment.newInstance(
                "",
                "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591148878000.jpg"
            )
        )
        sparseArray.put(
            3,
            TestFragment.newInstance(
                "",
                "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1591149235000.jpg"
            )
        )
        vp.adapter = adapter
//        vp.
        Toast.makeText(this, "ViewPager2DynamicTestActivity", Toast.LENGTH_SHORT).show()
    }


}