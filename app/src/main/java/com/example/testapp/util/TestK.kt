package com.example.testapp.util

import java.util.*


/**
 * @ClassName: TestK
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2021/1/15 16:44
 */
object TestK {
    fun KotlinRandomTest(range: Int) {
        for (i in 0 until 10) LogUtil.d("RandomTest", Random().nextInt(3)+Random().nextDouble() )
    }
}