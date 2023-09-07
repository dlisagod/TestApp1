package com.example.androidvideocachedemo

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*

/**
 * @create zhl
 * @date 2023/9/7 10:54
 * @description
 *
 * @update
 * @date
 * @description
 **/
object M3u8Util {
    val testM3u8NoKey =
        Environment.getExternalStorageDirectory().absolutePath + "/download/910453/910453.nokey.m3u8"

    fun init(context: Context) {
        cacheDir = context.cacheDir.absolutePath + File.separator
        generateKeyPath(context, keyPath)
    }

    val tag = "M3u8Util"
    const val keyAsset = "enc.key"
    const val keyFileName = keyAsset
    const val tempSuffix = ".temp"
    const val fullKey =
        """#EXT-X-KEY:METHOD=AES-128,URI="file:///storage/emulated/0/Download/910453/enc.key",IV=0x417056d34b8a3a9444c82ba7b5cfaf5f"""
    const val keyStr1 = """#EXT-X-KEY:METHOD=AES-128,URI=""""
    const val keyStr2 = """",IV=0x417056d34b8a3a9444c82ba7b5cfaf5f"""
    val keyPath get() = cacheDir + keyFileName

    /**
     * 默认空占位符
     */
    lateinit var cacheDir: String

    /**
     * 生成内部用m3u8，存放于应用的内部缓存路径
     * @param orgM3u8Path 原m3u8绝对路径
     */
    fun generateM3u8File(orgM3u8Path: String) {
        generateM3u8File(orgM3u8Path, cacheDir + File(orgM3u8Path).name, keyPath)
    }

    /**
     * @param orgM3u8Path 原m3u8绝对路径
     * @param generateM3u8Path 生成的新m3u8绝对路径
     * @param keyPath 密钥绝对路径
     */
    fun generateM3u8File(orgM3u8Path: String, generateM3u8Path: String, keyPath: String) {
        val fileOrg = File(orgM3u8Path)
        if (!fileOrg.exists()) {
            Log.d(tag, "generateFile 文件不存在:$orgM3u8Path")
            return
        }
        if (fileOrg.isDirectory) {
            Log.d(tag, "generateFile 目标不是文件，而是目录:$orgM3u8Path")
            return
        }
        val fileNew = File(generateM3u8Path)
        if (fileNew.exists()){
            Log.d(tag, "generateFile 目标文件已存在 generateM3u8Path：$generateM3u8Path")
            return
        }
        //读取文件
        val fis = FileInputStream(fileOrg)
        val br = BufferedReader(InputStreamReader(fis))
        val lines = br.readLines().toMutableList()
        br.close()
        var i = 0
        while (i < lines.size) {
            //修改切片路径
            if (lines[i].contains("#EXTINF:")) {
                //切片路径在下一行
                i++
                lines[i] = fileOrg.parent + File.separator + lines[i]
                i++
                continue
            }
            //如果存在移除原本的key
            if (lines[i].contains("#EXT-X-KEY:")) {
                lines.removeAt(i)
                continue
            }
            i++
        }
        //
        lines.add(1, keyStr1 + keyPath + keyStr2)
        val fileTemp = File(generateM3u8Path + tempSuffix)
        //写入文件
        val fos = FileOutputStream(fileTemp)
        for (l in lines) {
            fos.write(l.toByteArray())
            fos.write("\n".toByteArray())
        }
        fos.flush()
        fos.close()
        fileTemp.renameTo(File(generateM3u8Path))
    }

    /**
     * @param context 用于获取asset资源
     * @param generateKeyPath 绝对路径
     */
    fun generateKeyPath(context: Context, generateKeyPath: String) {
        val keyPathTemp = generateKeyPath + tempSuffix
        val ketFile = File(generateKeyPath)
        if (ketFile.exists()) {
            Log.d(tag, "generate key already exists $generateKeyPath")
            return
        }
        Log.d(tag, "generate key start $generateKeyPath")
        //获取原asset中的密钥文件的输入流
        val sfd = context.assets.open(keyAsset)
        val fos = FileOutputStream(File(keyPathTemp))
        //写入到新的密钥文件
        fos.write(sfd.readBytes())
        fos.flush()
        fos.close()
        FileUtil.rename(File(keyPathTemp), File(generateKeyPath))
        Log.d(tag, "generate key success $generateKeyPath")
    }
}