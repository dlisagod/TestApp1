package com.example.download.http

/**
 * @ClassName: DownInfo
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 14:02
 */
 open class DownInfo {
    /*存储位置*/
    var savePath: String? = null

    /*下载url*/
    var url: String? = null

    /*基础url*/
    var baseUrl: String? = null

    /*文件总长度*/
    var countLength: Long = 0

    /*下载长度*/
    var readLength: Long = 0

    /*下载唯一的HttpService*/
    var service: DownloadService? = null

    /*回调监听*/
    var listener: HttpProgressOnNextListener<*>? = null

    /*超时设置*/
    var DEFAULT_TIMEOUT = 6

    /*下载状态*/
    var state: DownState? = null

    val connectionTime: Long
        get() = DEFAULT_TIMEOUT.toLong()
}