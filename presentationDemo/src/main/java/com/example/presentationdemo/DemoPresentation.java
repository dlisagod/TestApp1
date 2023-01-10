package com.example.presentationdemo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 创建Presentation的子类，prsentation是一种特殊的对话框，它用来在另外的屏幕上显示内容
 * 需要注意的是在创建它之前就必须要和它的目标屏幕进行绑定，指定目标屏幕的方式有两种一是利用MediaRouter
 * 二是利用DisplayManager，本例演示的是后一种方法
 * @author aaaa
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
@SuppressLint("NewApi")
public class DemoPresentation extends Presentation {

    private PresentationContents mContents;
    public DemoPresentation(Context outerContext, Display display,PresentationContents contents) {
        super(outerContext, display);
        this.mContents=contents;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //根据prsentation的上下文获取到资源文件
        Resources r=getContext().getResources();
        setContentView(R.layout.presentation);

        //获取到与之关联的屏幕
        int photo=mContents.getPhoto();
        Display display=getDisplay();
        int displayId=display.getDisplayId();
        String diplayName=display.getName();
        TextView tv_desc=(TextView) findViewById(R.id.presentation_text);
        ImageView iv_photo=(ImageView) findViewById(R.id.presentation_image);

        //设置文本显示的内容：描述显示的图片的Id，屏幕的Id，屏幕的名字等信息
        tv_desc.setText(r.getString(R.string.prsentation_photo_text,1,displayId,diplayName));
        iv_photo.setImageDrawable(r.getDrawable(photo));

        //GradientDrawable 支持使用渐变色来绘制图形,用其为activity设置渐变的背景色
        GradientDrawable drawable=new GradientDrawable();
        //设置图形模式为矩形
        drawable.setShape(GradientDrawable.RECTANGLE);
        //设置渐变的模式为圆形渐变
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);

        //获取到屏幕的大小
        Point outSize=new Point();
        getDisplay().getSize(outSize);
        //设置圆形渐变的半径为屏幕长和宽的最大值的一半
        drawable.setGradientRadius((Math.max(outSize.x, outSize.y))/2);
        //设置渐变的颜色
        drawable.setColors(mContents.getColors());
        //为presentation的主界面添加渐变背景色
        findViewById(android.R.id.content).setBackground(drawable);
    }

    public PresentationContents getmContents() {
        return mContents;
    }

}