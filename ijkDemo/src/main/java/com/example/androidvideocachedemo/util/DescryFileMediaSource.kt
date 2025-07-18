package com.example.androidvideocachedemo.util

import android.media.MediaDataSource
import android.util.Log
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
class DescryFileMediaSource(private val filePath: String) : IMediaDataSource, MediaDataSource() {

    private var log = false
    private val tag: String = javaClass.simpleName
    private var mRaf: RandomAccessFile?
    private var mFileSize: Long = 0
    private val raf get() = mRaf!!

    init {
        mRaf = RandomAccessFile(File(filePath), "r")
//        mFileSize = file.length()
        log("File $filePath .File length (media data source size)")
    }

    private fun log(content: String) {
        if (log) Log.d(tag, content)
    }

//    @Throws(IOException::class)
//    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
//        if (file.filePointer != position)
//            file.seek(position)
//        if (size == 0) return 0
//        val bytesTemp = ByteArray(buffer.size)
//        val result = file.read(bytesTemp, offset, size)
//        val bytesDecryt = DecryptUtil.aes.decrypt(bytesTemp)
//        bytesDecryt.copyInto(buffer)
//        return result
//    }

    @Throws(IOException::class)
    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        log("File $filePath readAt position:$position   buffer size:${buffer.size}   offset:$offset   size:$size")
        if (raf.filePointer != position)
            raf.seek(position)
        if (size == 0) return 0
        val bytesTemp = ByteArray(buffer.size)
//        val bytesTemp = ByteArray(1024)
        val result = raf.read(bytesTemp, offset, size)
//        val result = raf.read(bytesTemp, offset, 1024)
        if (result > 0) {
            for (i in 0 until result) {
                buffer[i] = (bytesTemp[i].toInt() xor 200524).toByte()
            }
        }
        return result
    }

    @Throws(IOException::class)
    override fun getSize(): Long {
        log("File $filePath getSize $mFileSize")
        mFileSize = File(filePath).length()
        return mFileSize
    }

    @Throws(IOException::class)
    override fun close() {
        log("File $filePath close")
        mFileSize = 0
        mRaf?.close()
        mRaf = null
    }


}