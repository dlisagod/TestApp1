package com.example.download.http

import android.content.Context
import com.example.download.util.SPUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @ClassName: HttpDownloadManager
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 14:22
 */
class HttpDownloadManager private constructor() {
    /*回调sub队列*/
    private val subMap: HashMap<String?, ProgressDownSubscriber<DownInfo>?>
    private val downInfos: HashSet<DownInfo>

    /**
     * 开始下载
     */
    var downloadSPUtil: SPUtil.DownloadSPUtil? = null
    fun startDown(info: DownInfo): DownInfo {
//        if (info == null || subMap.get(info.getUrl()) != null) {
//            return;
//        }

        val mSubscriber: ProgressDownSubscriber<DownInfo>? = subMap[info.url]
        if (mSubscriber != null) {
            mSubscriber.setListener((info.listener as HttpProgressOnNextListener<DownInfo>?)!!)

            return mSubscriber.downInfo
        }
        /*添加回调处理类*/
        val subscriber = ProgressDownSubscriber<DownInfo>(info)
        //记录回调sub*
        subMap[info.url] = subscriber
//        info.state = DownState.DOWN
        //获取service，多次请求公用一个service
        val httpService: DownloadService?
        if (downInfos.contains(info)) {
            httpService = info.service
        } else {
            val interceptor = DownloadInterceptor(subscriber)
            val builder = OkHttpClient.Builder()
            //手动创建一个OkHttpClient并设置超时时间
            builder.connectTimeout(info.connectionTime, TimeUnit.SECONDS)
            builder.addInterceptor(interceptor)

            val loggingInterceptor = HttpLoggingInterceptor(LoggerInterceptor())
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)

            val retrofit = Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(info.baseUrl)
                .build()
            httpService =
                retrofit.create(DownloadService::class.java)
            info.service = httpService
        }
        /*得到rx对象-上一次下載的位置開始下載*/
        if (downloadSPUtil == null) downloadSPUtil = SPUtil.DownloadSPUtil.getInstance()
        info.readLength = downloadSPUtil?.getDownloadLog(info.baseUrl + info.url)!!
        httpService!!.download(
            "bytes=" + info.readLength + "-" ,
            info.url
        ) /*指定线程*/
            ?.subscribeOn(Schedulers.io())
            ?.unsubscribeOn(Schedulers.io()) /*失败后的retry配置*/ //
            //    .retryWhen(new RetryWhenNetworkException())
            /*读取下载写入文件*/
            ?.map(Function<ResponseBody?, DownInfo?> { responseBody ->
                try {
                    writeCache(responseBody, File(info.savePath), info)
                } catch (e: IOException) {
                    /*失败抛出异常*/
//                            throw new HttpTimeException(e.getMessage());
                    e.printStackTrace()
                }
                info
            }) /*回调线程*/
            ?.observeOn(AndroidSchedulers.mainThread()) /*数据回调*/
            ?.subscribe(subscriber)
        //                subscriber.dispose();
        return subscriber.downInfo
    }

    /**
     * 写入文件
     *
     * @param file
     * @param info
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeCache(
        responseBody: ResponseBody,
        file: File,
        info: DownInfo
    ) {
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        val allLength: Long
        allLength = if (info.countLength == 0L) {
            responseBody.contentLength()
        } else {
            info.countLength
        }
        var channelOut: FileChannel? = null
        var randomAccessFile: RandomAccessFile? = null
        randomAccessFile = RandomAccessFile(file, "rwd")
        channelOut = randomAccessFile.channel
        val mappedBuffer = channelOut.map(
            FileChannel.MapMode.READ_WRITE,
            info.readLength, allLength - info.readLength
        )
        val buffer = ByteArray(1024 * 8)
        var len: Int
        var record = 0
        while (responseBody.byteStream().read(buffer).also { len = it } != -1) {
            mappedBuffer.put(buffer, 0, len)
            record += len
        }
        responseBody.byteStream().close()
        channelOut?.close()
        randomAccessFile?.close()
    }

    /**
     * 停止下载
     */
    fun stopDown(info: DownInfo?) {
        if (info == null) return
        info.state = DownState.STOP
        info.listener?.onStop()
        if (subMap.containsKey(info.url)) {
            val subscriber = subMap[info.url]
            subscriber?.dispose()
            subMap.remove(info.url)
        }
        /*同步数据库*/
    }

    /**
     * 暂停下载
     *
     * @param info
     */
    fun pause(info: DownInfo?) {
        if (info == null) return
        info.state = DownState.PAUSE
        info.listener?.onPause()
        if (subMap.containsKey(info.url)) {
            val subscriber = subMap[info.url]
            subscriber?.dispose()
            subMap.remove(info.url)
        }
        /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
    }

    /**
     * 停止全部下载
     */
    fun stopAllDown() {
        for (downInfo in downInfos) {
            stopDown(downInfo)
        }
        subMap.clear()
        downInfos.clear()
    }

    /**
     * 暂停全部下载
     */
    fun pauseAll() {
        for (downInfo in downInfos) {
            pause(downInfo)
        }
        subMap.clear()
        downInfos.clear()
    }

    companion object {
        private var INSTANCE: HttpDownloadManager? = null
        private var mContext : Context? = null
        @JvmStatic
        val instance: HttpDownloadManager?
            get() {
                if (INSTANCE == null) {
                    synchronized(HttpDownloadManager::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = HttpDownloadManager()
                        }
                    }
                }
                return INSTANCE
            }
        fun init(context: Context){
            mContext = context
            SPUtil.DownloadSPUtil.init(mContext!!)
        }

    }

    init {
        downInfos = HashSet()
        subMap = HashMap()
    }
}