package com.example.androidvideocachedemo

import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * @create zhl
 * @date 2023/9/5 10:10
 * @description
 *
 * @update
 * @date
 * @description
 **/
class DescryFileMediaSource(file: File) : IMediaDataSource {

    private var mFile: RandomAccessFile?
    private var mFileSize: Long
    private val file get() = mFile!!

    init {
        mFile = RandomAccessFile(file, "r")
        mFileSize = file.length()
    }

    @Throws(IOException::class)
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (file.filePointer != position)
            file.seek(position)
//        val bytesTemp = ByteArray(buffer.size)
        return if (size == 0) 0 else file.read(buffer, 0, size)
    }

    fun get() {

    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        return mFileSize
    }

    @Throws(IOException::class)
    override fun close() {
        mFileSize = 0
        mFile?.close()
        mFile = null
    }


}