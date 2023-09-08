package com.example.androidvideocachedemo

import android.app.Application
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import androidx.core.view.children

object FloatViewHelper {

    var app: Application? = null
    fun initAppContext(app: Application) {
        this.app = app
    }

    /**
     * 显示悬浮窗
     */
    @JvmOverloads
    fun showFloatView(
        context: Context,
        view: View,
        x: Int = 0,
        y: Int = 0,
        width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        gravity: Int = Gravity.TOP or Gravity.START
    ): Boolean {
        if (!PermissionUtil.initOverlay(context)) return false
        if (view.parent != null) {
            throw IllegalStateException("This view is already added in a ViewGroup.Please remove it from it's parent")
        }
        val windowManager =
            context.getSystemService(WINDOW_SERVICE) as? WindowManager
        windowManager?.also {
            val layoutParams = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else WindowManager.LayoutParams.TYPE_PHONE
                format = PixelFormat.RGBA_8888
                flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                this.width = width
                this.height = height
                this.gravity = gravity
                this.x = x
                this.y = y
            }
//            view.setOnTouchListener(FloatingOnTouchListener(layoutParams, it))
            if (view is ViewGroup) {
                view.children.forEach {
                    it.setOnTouchListener(FloatingOnTouchListener(view))
                }
            }
            view.setOnTouchListener(FloatingOnTouchListener())
            it.addView(view, layoutParams)
            return true
        }
        return false
    }

//    fun showFloatViewWrap(
//        context: Context,
//        view: View,
//        x: Int = 0,
//        y: Int = 0,
//        gravity: Int = Gravity.TOP or Gravity.START
//    ): Boolean {
//        return showFloatView(
//            context,
//            view,
//            x,
//            y,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            gravity
//        )
//    }

    fun closeFloatView(view: View) {
        val windowManager =
//        app!!.getSystemService(WINDOW_SERVICE) as? WindowManager
            view.context.getSystemService(WINDOW_SERVICE) as? WindowManager
        windowManager?.apply {
            removeView(view)
        }
    }

//    private fun initOverlay(): Boolean {
//        if (Build.VERSION.SDK_INT >= 23) {
//            val context = BaseApp.context
//            if (!Settings.canDrawOverlays(context)) {
//                Toaster.show("请开启允许在其他应用上层显示")
//                val intent =
////                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//                    Intent(
//                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + context.packageName)
//                    )
////                startActivity(intent)
//                context.startActivity(intent)
//                return false
//            }
//        }
//        return true
//    }

    class FloatingOnTouchListener(
//        private var mLayoutParams: WindowManager.LayoutParams,
//        private var mWindowManager: WindowManager
        var parent: ViewGroup? = null
    ) : View.OnTouchListener {
        private var clickTime = 500L
        private var lastDownTime = 0L

        //        private var clickX = 5
//        private var clickY = 5
        private var x = 0
        private var y = 0
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastDownTime = System.currentTimeMillis()
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    val mLayoutParams = view.layoutParams as? WindowManager.LayoutParams
                    mLayoutParams?.also {
                        mLayoutParams.x = mLayoutParams.x + movedX
                        mLayoutParams.y = mLayoutParams.y + movedY

                        val mWindowManager =
                            view.context.getSystemService(WINDOW_SERVICE) as? WindowManager
                        // 更新悬浮窗控件布局
                        if (parent != null) mWindowManager?.updateViewLayout(parent, mLayoutParams)
                        else mWindowManager?.updateViewLayout(view, mLayoutParams)
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (System.currentTimeMillis() - lastDownTime < clickTime) {
                        view.performClick()
                        return true
                    }
                }
            }
            return false
        }
    }
}