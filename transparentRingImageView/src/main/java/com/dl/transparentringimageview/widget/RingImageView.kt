package com.dl.transparentringimageview.widget

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
 * @Date: 2020/11/6 10:34
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

    private var mMatrix: Matrix? = null
    private var srcRect: RectF? = null
    private var mPaint: Paint? = null
    private var mPorterDuffXfermode: PorterDuffXfermode? = null
    private var mRadius: Float = 0F
    private var mSrcBitmap: Bitmap? = null

    private fun init(attrs: AttributeSet?) {
        val ta: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RingImageView)
        mRadius = ta.getDimensionPixelSize(R.styleable.RingImageView_radius_inner, 0).toFloat()
        ta.recycle()
        mPaint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        mMatrix = Matrix()
        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    @Override
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        srcRect = RectF(0F, 0F, w.toFloat(), h.toFloat())
    }

    @Override
    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        initBitmap()
        if (mSrcBitmap == null) return
        checkRadius()
        canvas!!.drawColor(Color.TRANSPARENT)
        //组件中心点
        val verticalCenter = height / 2
        val horizontalCenter = width / 2

        //获取到Bitmap的宽高
        val bitmapWidth: Int = mSrcBitmap!!.getWidth()
        val bitmapHeight: Int = mSrcBitmap!!.getHeight()

        //获取到ImageView的宽高
        val viewWidth = width
        val viewHeight = height

        val saveCount =
            canvas.saveLayer(srcRect, mPaint, Canvas.ALL_SAVE_FLAG)
        mPaint?.setXfermode(mPorterDuffXfermode)
        val bitmapShader = BitmapShader(
            mSrcBitmap!!,
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        mMatrix?.reset()
        //缩放比例
        val minScale = Math.min(
            viewWidth / bitmapWidth.toFloat(),
            viewHeight / bitmapHeight.toFloat()
        )
        mMatrix?.setScale(minScale, minScale) //将Bitmap保持比列根据ImageView的大小进行缩放

        bitmapShader.setLocalMatrix(mMatrix) //将矩阵变化设置到BitmapShader,其实就是作用到Bitmap
        mPaint?.setShader(bitmapShader)
        canvas.drawCircle(
            verticalCenter.toFloat(),
            horizontalCenter.toFloat(),
            mRadius,
            mPaint!!
        )
        canvas.drawBitmap(mSrcBitmap!!, null, srcRect!!, mPaint)
        mPaint?.setXfermode(null)
        canvas.restoreToCount(saveCount)
    }

    private fun initBitmap() {
        if (drawable == null) return
        mSrcBitmap = drawable.toBitmap()
    }

    /***
     * 设置半径
     */
    public fun setRadius(radius: Float) {
        mRadius = dp2px(context, radius)
        invalidate()
    }

    private fun checkRadius() {
        mRadius = Math.min(Math.min(width / 2, height / 2).toFloat(), mRadius)
    }

    /**
     * dp转px
     */
    fun dp2px(context: Context, dpVal: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        )
    }

}