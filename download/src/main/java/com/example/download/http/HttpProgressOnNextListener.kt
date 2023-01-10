package com.example.download.http

/**
 * @ClassName: HttpProgressOnNextListener
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 14:04
 */
/**
 * 下载过程中的回调处理
 * Created by WZG on 2016/10/20.
 */
abstract class HttpProgressOnNextListener<T:DownInfo> {
    /**
     * 成功后回调方法
     * @param t
     */
    abstract fun onNext(t: T)

    /**
     * 开始下载
     */
    abstract fun onStart()

    /**
     * 完成下载
     */
    abstract fun onComplete()

    /**
     * 下载进度
     * @param readLength
     * @param countLength
     */
    abstract fun updateProgress(readLength: Long, countLength: Long)

    /**
     * 失败或者错误方法
     * 主动调用，更加灵活
     * @param e
     */
    open fun onError(e: Throwable?) {}

    /**
     * 暂停下载
     */
    open fun onPause() {}

    /**
     * 停止下载销毁
     */
    open fun onStop() {}
}