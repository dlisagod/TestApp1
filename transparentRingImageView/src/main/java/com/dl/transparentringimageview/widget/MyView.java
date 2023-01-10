package com.dl.transparentringimageview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dl.transparentringimageview.R;

/**
 * @ClassName: MyView
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/6 12:42
 */
public class MyView extends View {
    private Bitmap mSrcBitmap;
    private Matrix mMatrix;
    private RectF srcRect;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mMatrix = new Matrix();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

//    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        srcRect = new RectF(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initBitmap();
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        int saveCount = canvas.saveLayer(srcRect, mPaint, Canvas.ALL_SAVE_FLAG);
//        int circleRadius = 15;//圆的半径
        int verticalCenter = getHeight() / 2;
        int horizontalCenter = getWidth() / 2;
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(Color.RED);
//        canvas.drawCircle(horizontalCenter, verticalCenter, circleRadius, paint);
        mPaint.setXfermode(mPorterDuffXfermode);
        //获取到Bitmap的宽高
        int bitmapWidth = mSrcBitmap.getWidth();
        int bitmapHeight = mSrcBitmap.getHeight();
        //获取到ImageView的宽高
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        BitmapShader bitmapShader = new BitmapShader(mSrcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.reset();
        float minScale = Math.min(viewWidth / (float) bitmapWidth, viewHeight / (float) bitmapHeight);
        mMatrix.setScale(minScale, minScale);//将Bitmap保持比列根据ImageView的大小进行缩放
        bitmapShader.setLocalMatrix(mMatrix); //将矩阵变化设置到BitmapShader,其实就是作用到Bitmap
        mPaint.setShader(bitmapShader);
        canvas.drawCircle(verticalCenter, horizontalCenter, Math.min(bitmapWidth / 2, bitmapHeight / 2), mPaint);
        canvas.drawBitmap(mSrcBitmap, null, srcRect, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    private void initBitmap() {
          mSrcBitmap = ((BitmapDrawable)getResources().getDrawable(R.mipmap.icon_collection,null)).getBitmap();
    }

}
