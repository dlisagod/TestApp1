package com.example.androidvideocachedemo.util

import android.media.MediaCodec
import cn.hutool.core.io.FileUtil
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.symmetric.AES
import java.io.File

/**
 * @create zhl
 * @date 2023/9/6 11:15
 * @description
 *
 * @update
 * @date
 * @description
 **/
object DecryptUtil {
     val aes: AES = SecureUtil.aes("xingzhi321202394".toByteArray())

    fun decrypt(sourceFile: File, targetFile: File, aes: AES) {
        try {
            println("开始解密......")
            val startL = System.currentTimeMillis()
            println("-----------------start-----------------")
            val start = System.currentTimeMillis()
            val bytes: ByteArray = FileUtil.readBytes(sourceFile)
            val decrypt: ByteArray = aes.decrypt(bytes)
            val end = System.currentTimeMillis()
            println("小计耗时:" + (end - start) / 1000 + "s")
            println("------------------end----------------")
            println("解密完成,用时总计:" + (System.currentTimeMillis() - startL) / 1000 + "s")
        } catch (exception: MediaCodec.CryptoException) {
            println("解密失败，秘钥可能不正确！")
            exception.printStackTrace()
        }
    }
}