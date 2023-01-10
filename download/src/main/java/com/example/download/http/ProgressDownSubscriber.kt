package com.example.download.http


import com.example.download.util.SPUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.observers.DisposableObserver
import java.lang.ref.WeakReference

/**
 * @ClassName: ProgressDownSubscriber
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 14:06
 */
/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
class ProgressDownSubscriber<T : DownInfo>(downInfo: DownInfo) : DisposableObserver<T>(),
    DownloadResponseBody.DownloadProgressListener {
    //弱引用结果回调
    private var mSubscriberOnNextListener: WeakReference<HttpProgressOnNextListener<T>?>

    /*下载数据*/
    val downInfo: DownInfo

    override fun onStart() {
        super.onStart()
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get()!!.onStart()
        }
        downInfo.state = DownState.START
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    override fun onComplete() {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get()!!.onComplete()
        }
        downInfo.state = DownState.FINISH
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    override fun onError(e: Throwable) {
        /*停止下载*/
        HttpDownloadManager.instance!!.stopDown(downInfo)
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get()!!.onError(e)
        }
        downInfo.state = DownState.ERROR
    }
    //    @Override
    //    public void onSubscribe(Disposable d) {
    //        if (mSubscriberOnNextListener.get() != null) {
    //            mSubscriberOnNextListener.get().onStart();
    //        }
    //        downInfo.setState(DownState.START);
    //    }
    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    override fun onNext(t: T) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get()!!.onNext(t)
        }
    }

    var downloadSPUtil: SPUtil.DownloadSPUtil? = null
    var consumer: Consumer<Long>? = null
    override fun update(
        read: Long,
        count: Long,
        done: Boolean
    ) {
        var read = read
        if (downInfo.countLength > count) {
            read = downInfo.countLength - count + read
        } else {
            downInfo.countLength = count
        }
        downInfo.readLength = read
        downloadSPUtil
        if (downloadSPUtil == null) downloadSPUtil = SPUtil.DownloadSPUtil.getInstance()
        downloadSPUtil?.putDownloadLog(downInfo.baseUrl + downInfo.url, read)
        if (mSubscriberOnNextListener.get() != null) {
            /*接受进度消息，造成UI阻塞，如果不需要显示进度可去掉实现逻辑，减少压力*/
            if (consumer == null) {
                consumer = object : Consumer<Long> {
                    override fun accept(t: Long) {
                        /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
//                        Log.d("downloadOnNext", "" + aLong)
                        if (downInfo.state === DownState.PAUSE || downInfo.state === DownState.STOP) return
//                        Log.d("downloadOnNext", "not stop not pause " + t)
                        downInfo.state = DownState.DOWN
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get()!!
                                .updateProgress(t, downInfo.countLength)
                        }
                    }
                }
            }
            Observable.just(read)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
        }
    }

    fun setListener(l: HttpProgressOnNextListener<T>) {
        this.downInfo.listener = l
        mSubscriberOnNextListener =
            WeakReference(downInfo.listener as HttpProgressOnNextListener<T>?)
    }

    init {
        mSubscriberOnNextListener =
            WeakReference(downInfo.listener as HttpProgressOnNextListener<T>?)
        this.downInfo = downInfo
    }
}