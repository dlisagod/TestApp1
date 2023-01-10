package com.example.download.http

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @ClassName: DownloadInterceptor
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 13:58
 */
/**
 * 成功回调处理
 * Created by WZG on 2016/10/20.
 */
class DownloadInterceptor(private val listener: DownloadResponseBody.DownloadProgressListener) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(DownloadResponseBody(originalResponse.body()!!, listener))
            .build()
    }

}