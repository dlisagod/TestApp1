package com.example.testapp.module.viewPager2DynamicTest

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.testapp.R
import com.example.testapp.util.BitmapAndRgbUtil
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    var adapter = IntTextAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test_tv.text = param2
        test_tv_02.text = param2
        test_tv_03.text = param2
//       val target:SimpleTarget = SimpleTarget
//        Glide.with(this).load(param2).into()
//        val palette: Palette = Palette.from()
        test_rcv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        test_rcv.adapter = adapter
        adapter.recyclerView = test_rcv
        setTvListText()
        setTvBackText()
        test_tv_reverse_back.setOnClickListener {
            isReverseBack = !isReverseBack
            setTvBackText()
            setBg()
        }

        test_tv_reverse_list.setOnClickListener {
            isReverseList = !isReverseList
            setTvListText()
            adapter.notifyDataSetChanged()
        }
        getBitmapAndReverseColor()
//        MyUsbManager.getAudioUsbDevice(context!!)
    }


    fun setTvBackText() {
        if (isReverseBack) test_tv_reverse_back.text = "背景反色已开启"
        else test_tv_reverse_back.text = "背景反色已关闭"
    }

    fun setBg() {
        if (isReverseBack) test_rcv.setBackgroundColor(bgRgbRe)
        else test_rcv.setBackgroundColor(bgRgb)
    }

    fun setTvListText() {
        if (isReverseList) test_tv_reverse_list.text = "列表反色已开启"
        else test_tv_reverse_list.text = "列表反色已关闭"
    }

    var isReverseBack = false

    var bgRgb = 0
    var bgRgbRe = 0
    fun getBitmapAndReverseColor() {
        val rgbCallback = object : BitmapAndRgbUtil.RGBCallback {
            override fun result(rgb1: Int?, rgb2: Int?, rgb3: Int?) {

                rgb1?.also {
//                    rl_text.setBackgroundColor(it)
                    test_tv.setTextColor(it)
                }
                rgb2?.also {
                    test_tv_02.setTextColor(it)
//                    test_02.setTextColor(it)
                }
                rgb3?.also {
                    test_tv_03.setTextColor(it)
                }
            }

        }
        val rgbArrCallback = object : BitmapAndRgbUtil.RGBArrayCallback {
            override fun result(arr: IntArray) {
                bgRgb = BitmapAndRgbUtil.getTranslucentColor(100F, arr[0])
                bgRgbRe = BitmapAndRgbUtil.getTranslucentColor(
                    100F,
                    BitmapAndRgbUtil.reverseColor(arr[0])
                )
                setBg()
                adapter.setNewInstance(arr.toMutableList())
            }

        }
        val bitmapCallback = object : BitmapAndRgbUtil.BitmapCallback {
            override fun result(bitmap: Bitmap?) {
                test_iv.setImageBitmap(bitmap)
//                BitmapAndRgbUtil.getMainRGBFromBitmap(bitmap, rgbCallback, true)
                BitmapAndRgbUtil.getRGBArrayFromBitmap(bitmap, rgbArrCallback, false)
            }
        }
        BitmapAndRgbUtil.getBitmapFromUrl(activity!!, param2!!, bitmapCallback)
    }

    var isReverseList = false

    inner class IntTextAdapter() : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_text) {
        override fun convert(holder: BaseViewHolder, item: Int) {
            val position = holder.adapterPosition
            if (position != 0 && (position + 1) % 3 == 0) {
                holder.setBackgroundColor(R.id.item_bottom_line, Color.BLACK)
            } else {
                holder.setBackgroundColor(R.id.item_bottom_line, Color.TRANSPARENT)
            }
            holder.setText(R.id.item_tv, "$item : $param2")
//                .setTextColor(R.id.item_tv, item)
            if (isReverseList) holder.setTextColor(
                R.id.item_tv,
                BitmapAndRgbUtil.reverseColor(item)
            )
            else holder.setTextColor(R.id.item_tv, item)
        }

    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}