package com.example.download.http

import android.util.Log
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * @ClassName: DownloadResponseBody
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/11/4 13:54
 */
/**
 * 自定义进度的body
 *
 * @author wzg
 */
class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: DownloadProgressListener?
) : ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                if (null != progressListener) {
                    progressListener.update(
                        totalBytesRead,
                        responseBody.contentLength(),
                        bytesRead == -1L
                    )
//                    Log.d("downloadRead", "listener not null")
                }
//                Log.d("downloadRead", "" + totalBytesRead + " " + responseBody.contentLength())
                return bytesRead
            }
        }
    }

    /**
     * 成功回调处理
     * Created by WZG on 2016/10/20.
     */
    interface DownloadProgressListener {
        /**
         * 下载进度
         *
         * @param read
         * @param count
         * @param done
         */
        fun update(read: Long, count: Long, done: Boolean)
    }

}