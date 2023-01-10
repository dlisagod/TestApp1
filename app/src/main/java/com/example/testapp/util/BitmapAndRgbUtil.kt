package com.example.testapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.IntDef
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import java.lang.ref.WeakReference


/**
 * @ClassName: BitmapUtil
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/12/4 15:23
 */
class BitmapAndRgbUtil {

    class BitmapAsyncTask(val url: String, context: Context, callback: BitmapCallback?) :
        AsyncTask<Void, Void, Bitmap?>() {
        var callbackWr: WeakReference<BitmapCallback?> = WeakReference(callback)
        var contextWr: WeakReference<Context?> = WeakReference(context)
        override fun doInBackground(vararg params: Void?): Bitmap? {
            val context = contextWr.get()
            context?.also {

                val myBitmap: Bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit().get()
//                val bitmap = Bitmap.createBitmap(
//                    myBitmap,
//                    0,
//                    0,
//                    myBitmap.width,
//                    myBitmap.height
//                )
                return myBitmap
            }
            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            val callback = callbackWr.get()
//            if (callback != null){
            callback?.result(result)
//            }
        }

    }


    interface BitmapCallback {
        fun result(bitmap: Bitmap?)
    }

    interface RGBCallback {
        fun result(rgb: Int?, rgb2: Int?, rgb3: Int?)
    }

    interface RGBArrayCallback {
        fun result(arr: IntArray)
    }

    companion object {
        fun getBitmapFromUrl(context: Context, url: String, callback: BitmapCallback) {
            val asyncTask = BitmapAsyncTask(url, context, callback).execute()
        }

        fun getMainRGBFromBitmap(bitmap: Bitmap?, callback: RGBCallback?, isReverse: Boolean) {
            bitmap?.also {

                Palette.from(it).generate { pl ->
                    pl?.also {
//                        val swatch = pl.vibrantSwatch
//                        val swatch = pl.lightVibrantSwatch
                        val swatch = pl.darkVibrantSwatch
                        var color1: Int = swatch?.rgb ?: Color.WHITE
//                        var color1 = pl.getVibrantColor(Color.WHITE)
//                        color1 = getTranslucentColor(80F, color1)
                        if (isReverse) {
                            color1 = reverseColor(color1)
                        }
                        var color2: Int = swatch?.titleTextColor ?: Color.WHITE
                        if (isReverse) color2 = reverseColor(color2)

                        var color3: Int = swatch?.bodyTextColor ?: Color.WHITE
                        if (isReverse) color3 = reverseColor(color3)
                        callback?.result(color1, color2, color3)
                    }
                }
            }
        }

        fun getRGBArrayFromBitmap(
            bitmap: Bitmap?,
            callback: RGBArrayCallback?,
            isReverse: Boolean = false
        ) {
            bitmap?.also {
                Palette.from(bitmap).generate { pl ->
                    pl?.also {

                        val arr: IntArray = IntArray(18)
                        val vibrantSwatch = pl.vibrantSwatch
                        val lightVibrantSwatch = pl.lightVibrantSwatch
                        val darkVibrantSwatch = pl.darkVibrantSwatch
                        val mutedSwatch = pl.mutedSwatch
                        val lightMutedSwatch = pl.lightMutedSwatch
                        val darkMutedSwatch = pl.darkMutedSwatch

                        var mainColor = vibrantSwatch?.rgb ?: Color.WHITE
                        var titleColor = vibrantSwatch?.titleTextColor ?: Color.WHITE
                        var bodyColor = vibrantSwatch?.bodyTextColor ?: Color.WHITE
//                        arr[0] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[0] = mainColor
                        arr[1] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[2] = if (isReverse) reverseColor(bodyColor) else bodyColor

                        mainColor = lightVibrantSwatch?.rgb ?: Color.WHITE
                        titleColor = lightVibrantSwatch?.titleTextColor ?: Color.WHITE
                        bodyColor = lightVibrantSwatch?.bodyTextColor ?: Color.WHITE
                        arr[3] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[4] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[5] = if (isReverse) reverseColor(bodyColor) else bodyColor

                        mainColor = darkVibrantSwatch?.rgb ?: Color.WHITE
                        titleColor = darkVibrantSwatch?.titleTextColor ?: Color.WHITE
                        bodyColor = darkVibrantSwatch?.bodyTextColor ?: Color.WHITE
                        arr[6] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[7] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[8] = if (isReverse) reverseColor(bodyColor) else bodyColor

                        mainColor = mutedSwatch?.rgb ?: Color.WHITE
                        titleColor = mutedSwatch?.titleTextColor ?: Color.WHITE
                        bodyColor = mutedSwatch?.bodyTextColor ?: Color.WHITE
                        arr[9] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[10] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[11] = if (isReverse) reverseColor(bodyColor) else bodyColor


                        mainColor = lightMutedSwatch?.rgb ?: Color.WHITE
                        titleColor = lightMutedSwatch?.titleTextColor ?: Color.WHITE
                        bodyColor = lightMutedSwatch?.bodyTextColor ?: Color.WHITE
                        arr[12] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[13] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[14] = if (isReverse) reverseColor(bodyColor) else bodyColor

                        mainColor = darkMutedSwatch?.rgb ?: Color.WHITE
                        titleColor = darkMutedSwatch?.titleTextColor ?: Color.WHITE
                        bodyColor = darkMutedSwatch?.bodyTextColor ?: Color.WHITE
                        arr[15] = if (isReverse) reverseColor(mainColor) else mainColor
                        arr[16] = if (isReverse) reverseColor(titleColor) else titleColor
                        arr[16] = if (isReverse) reverseColor(bodyColor) else bodyColor

                        callback?.result(arr)
                    }
                }
            }
        }

        fun getTranslucentColor(percent: Float, rgb: Int): Int {
            // 10101011110001111
            val blue = Color.blue(rgb)
            val green = Color.green(rgb)
            val red = Color.red(rgb)
            var alpha = Color.alpha(rgb)
            //      int blue = rgb & 0xff;
//      int green = rgb>>8 & 0xff;
//      int red = rgb>>16 & 0xff;
//      int alpha = rgb>>>24;
            alpha = Math.round(alpha * percent)
            Log.d("getTranslucentColor", "alpha:$alpha,red:$red,green:$green")
            return Color.argb(alpha, red, green, blue)
        }

        fun reverseColor(color: Int): Int {
            val red = color and 0xff0000 shr 16
            val green = color and 0x00ff00 shr 8
            val blue = color and 0x0000ff
            Log.d("reverseColor", "red$red green$green blue$blue")
            return Color.rgb(255 - red, 255 - green, 255 - blue)
        }

        fun reverseColor2(color: Int): Int {
            val red: Int = color shr 16 and 0x0ff
            val green: Int = color shr 8 and 0x0ff
            val blue: Int = color and 0x0ff
            Log.d("reverseColor2", "red$red green$green blue$blue")
            return Color.rgb(255 - red, 255 - green, 255 - blue)
        }
    }

    object AppConstant {
        const val AWAIT = 0
        const val ING = 1
        const val FINISH = 2

        @IntDef(AWAIT, ING, FINISH)
        annotation class DoorState
    }
}