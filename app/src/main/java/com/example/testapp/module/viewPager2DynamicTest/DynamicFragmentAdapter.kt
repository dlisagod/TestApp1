package com.example.testapp.module.viewPager2DynamicTest

import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @ClassName: DynamicFragmentAdapter
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/12/1 17:49
 */
class DynamicFragmentAdapter(fragmentActivity: FragmentActivity, var sa: SparseArray<Fragment>) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return sa.size()
    }

    override fun createFragment(position: Int): Fragment {
        return sa.get(position)
    }

}