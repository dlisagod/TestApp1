package com.example.download.http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * @ClassName: DownloadService
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 13:51
 */
interface DownloadService {
    /*断点续传下载接口*/
    @Streaming
    @GET
    fun download(
        @Header("Range") start: String?,
        @Url url: String?
    ): Observable<ResponseBody?>?
}