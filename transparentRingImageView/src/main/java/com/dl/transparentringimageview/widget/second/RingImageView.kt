package com.dl.transparentringimageview.widget.second

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.dl.transparentringimageview.R


/**
 * @ClassName: RingImageView
 * @Description: 可挖空ImageView
 * @Author: zhl
 * @CreateDate: 2020/11/6 10:34
 * @UpdateDate: 2020/11/16 16:45
 */
class RingImageView : androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    //    private var mMatrix: Matrix? = null
//    private var srcRect: RectF? = null
    //画笔
    private var mPaint: Paint? = null

    //渲染模式
    private var mPorterDuffXfermode: PorterDuffXfermode? = null

    //内圆半径
    private var mRadiusInner: Float = 0F

    //外圆半径
    private var mRadiusOuter: Float = 0F
//    private var mSrcBitmap: Bitmap? = null

    private fun init(attrs: AttributeSet?) {
        val ta: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RingImageView)
        mRadiusInner = ta.getDimensionPixelSize(R.styleable.RingImageView_radius_inner, 0).toFloat()
        mRadiusOuter = ta.getDimensionPixelSize(R.styleable.RingImageView_radius_outer, 0).toFloat()
        ta.recycle()
        mPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
//        mMatrix = Matrix()
//        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    @Override
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        srcRect = RectF(0F, 0F, w.toFloat(), h.toFloat())

    }

    @Override
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        initBitmap()
//        if (mSrcBitmap == null) return
        checkRadius()
        if (canvas == null) return
//        canvas!!.drawColor(Color.TRANSPARENT)
        //组件中心点
        val verticalCenter = height / 2
        val horizontalCenter = width / 2

//        val saveCount =
//            canvas.saveLayer(srcRect, mPaint, Canvas.ALL_SAVE_FLAG)

//        canvas.drawBitmap(mSrcBitmap!!, null, srcRect!!, mPaint)
        //外圆半径不为零且大于内圆半径时，绘制外圆
        if (mRadiusOuter > 0 && mRadiusOuter > mRadiusInner) {

            canvas.drawCircle(
                verticalCenter.toFloat(),
                horizontalCenter.toFloat(),
                mRadiusOuter,
                mPaint!!
            )
        }

        //内圆半径大于零，绘制内圆
        if (mRadiusInner > 0) {

            mPaint?.setXfermode(mPorterDuffXfermode)
            canvas.drawCircle(
                verticalCenter.toFloat(),
                horizontalCenter.toFloat(),
                mRadiusInner,
                mPaint!!
            )
            mPaint?.setXfermode(null)
        }
//        canvas.restoreToCount(saveCount)
    }

//    private fun initBitmap() {
//        if (drawable == null) return
//        mSrcBitmap = drawable.toBitmap()
//    }

    /**
     * 设置半径
     */
    fun setRadius(radiusInner: Float, radiusOuter: Float) {
        mRadiusInner = dp2px(context, radiusInner)
        mRadiusOuter = dp2px(context, radiusOuter)
        invalidate()
    }


    /***
     * 设置内圆半径
     */
    fun setRadiusInner(radius: Float) {
        mRadiusInner = dp2px(context, radius)
        invalidate()
    }

    /***
     * 设置外圆半径
     */
    fun setRadiusOuter(radius: Float) {
        mRadiusOuter = dp2px(context, radius)
        invalidate()
    }

    //根据组件宽高对半径进行检测和限制
    private fun checkRadius() {
        mRadiusInner = Math.min(Math.min(width / 2, height / 2).toFloat(), mRadiusInner)
        mRadiusOuter = Math.min(Math.min(width / 2, height / 2).toFloat(), mRadiusOuter)
    }

    /**
     * dp转px
     */
    private fun dp2px(context: Context, dpVal: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        )
    }

}