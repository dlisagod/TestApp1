package com.example.testapp.widget

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

/**
 * @create zhl
 * @date 2021/12/20 17:12
 * @description
 *
 * @update
 * @date
 * @description
 **/


fun Context.getResIdByAttr(@AttrRes id: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(id, tv, true)
    Log.d("getColorIdByAttr", "data ${tv.data}")
    Log.d("getColorIdByAttr", "resourceId ${tv.resourceId}")
    Log.d("getColorIdByAttr", "typeValue $tv")
    return tv.resourceId
}

fun Context.getResIdByAttr2(@AttrRes id: Int): Int {
//    Resources.
    val tv = TypedValue()
    theme.resolveAttribute(id, tv, true)
    val arr = IntArray(1)
    arr[0] = id
    val typeArr = theme.obtainStyledAttributes(tv.resourceId, arr)
    val resourceId = typeArr.getResourceId(0, -1)
    typeArr.recycle()
    Log.d("getColorIdByAttr2", "typeArr $typeArr")
    return resourceId
}

