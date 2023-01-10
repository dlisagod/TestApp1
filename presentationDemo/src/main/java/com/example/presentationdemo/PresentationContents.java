package com.example.presentationdemo;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 创建类，用于定义在presentation上显示的内容
 * 该类实现了parcelable接口用于实现对象的序列化，
 * 序列化之后就可以方便地在多个activity之间传递复杂的数据
 * 实现parcelable接口必须实现三个方法
 * 1.public int describeContents() 默认返回0即可
 * 2.public void writeToParcel将类中的数据写入到包中（打包的过程）
 * 3.public static final Creator<T> CREATOR=new Creator<T>(){}
 * public static final一个都不能少，方法名CREATOR不能更改
 * @author aaaa
 *
 */
public class PresentationContents implements Parcelable {
    private int photo;
    private int[] colors;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(photo);
        dest.writeInt(colors[0]);
        dest.writeInt(colors[1]);
    }

    public static final Creator<PresentationContents> CREATOR=new Creator<PresentationContents>() {
        /**
         * 返回一组对象
         */
        @Override
        public PresentationContents[] newArray(int size) {
            return new PresentationContents[size];
        }
        /**
         * 返回单个对象
         */
        @Override
        public PresentationContents createFromParcel(Parcel source) {
            return new PresentationContents(source);
        }
    };

    //从包中解析数据
    private PresentationContents(Parcel source) {
        photo=source.readInt();
        colors=new int[]{source.readInt(),source.readInt()};
    }

    public PresentationContents(int photo) {
        super();
        this.photo = photo;
        colors=new int[]{
            //获取两种随机颜色
            ((int) (Math.random()*Integer.MAX_VALUE))|0xff000000,
            ((int) (Math.random()*Integer.MAX_VALUE))|0xff000000
        };
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int[] getColors() {
        return colors;
    }

}