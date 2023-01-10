package com.example.testapp.util

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors.toList

/**
 * 朗读亭相关本地资源工具
 */
class FilePathUtil {

    companion object {

        /**
         * 视频是否存在（广告与默认请使用其它方法）
         */
        fun isVideoExist(pathOrUrl: String): Boolean {
            var name = getFileName(pathOrUrl)
            var file = File(getLocalVideoPath().absolutePath + File.separator + name)
            return file.exists()
        }

        /**
         * 默认视频是否存在
         */
        fun isDefaultVideoExist(pathOrUrl: String): Boolean {
            var name = getFileName(pathOrUrl)
            var file = File(getLocalDefaultVideoPath().absolutePath + File.separator + name)
            return file.exists()
        }


        /**
         * 广告视频是否存在
         */
        fun isAdVideoExist(pathOrUrl: String): Boolean {
            var name = getFileName(pathOrUrl)
            var file = File(getLocalAdvertisementVideoPath().absolutePath + File.separator + name)
            return file.exists()
        }

        /**
         * 录音音频是否存在
         */
        fun isAudioExist(pathOrUrl: String): Boolean {
            var name = getFileName(pathOrUrl)
            var file = File(getLocalAdvertisementVideoPath().absolutePath + File.separator + name)
            return file.exists()
        }

        /**
         * 从url或路径中获取文件名
         */
        fun getFileName(pathOrUrl: String): String {
            return pathOrUrl.split(File.separator).last()
        }

        /**
         * 朗读亭资源、缓存存放路径
         */
        fun getMyFilePath(): File {
            var file: File =
                File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "ldt")
            if (!file.exists()) file.mkdirs()
            return file
        }

        /**
         * 视频存放路径
         */
        fun getLocalVideoPath(): File {
            var file: File =
                File(getMyFilePath().absolutePath + File.separator + "video")
            if (!file.exists())
                file.mkdirs()
            return file
        }

        /**
         * 默认视频存放路径
         */
        fun getLocalDefaultVideoPath(): File {
            var file: File =
                File(getLocalVideoPath().absolutePath + File.separator + "default")
            if (!file.exists())
                file.mkdirs()
            return file
        }

        /**
         * 广告视频存放路径
         */
        fun getLocalAdvertisementVideoPath(): File {
            var file: File =
                File(getLocalDefaultVideoPath().absolutePath + File.separator + "ad" )
            if (!file.exists())
                file.mkdirs()
            return file
        }

        /**
         * 录音音频存放路径
         */
        fun getLocalAudioPath(): File {
            var file: File =
                File(getMyFilePath().absolutePath + File.separator + "audio")
            if (!file.exists())
                file.mkdirs()
            return file
        }

        /**
         * 删除文件
         */
        fun delete(path: String): Boolean {
            var file = File(path)
//            return if (file.exists()) file.delete()
//            else true
            return file.delete()
        }

        /**
         * 删除目标的文件
         * 如是目录则删除其下所有文件
         */
        private fun deleteAllFiles(file: File) {
            Thread(Runnable {
                deleteAllFiles1(file)
            }).start()
        }

        private fun deleteAllFiles1(file: File) {
//            Thread(Runnable {
            if (file.exists()) {
                if (file.isDirectory) {
                    var files: Array<File> = file.listFiles()
                    if (files != null && files.isNotEmpty()) {
                        for ((count, file) in files.withIndex()) {
                            deleteAllFiles1(file)
                            if (count == files.size - 1)
                                file.delete()
                        }
                    } else file.delete()
                } else {
                    file.delete()
                }
            }
//            }).start()
        }

        /**
         * 删除所有视频
         */
        fun deleteAllVideo() {
            deleteAllFiles(getLocalVideoPath())
        }


        /**
         * 文件大小 MB
         */
        fun getSize(file: File): Double {
            var length: Double = 0.00
            if (file.exists() && file.isDirectory) {
                var files: Array<File>? = file.listFiles()
                if (files != null && files.isNotEmpty()) {
                    for (file in files) {
                        var bigDecimal = if (file.isDirectory) {
                            BigDecimal(getSize(file))
                        } else {
                            BigDecimal(file.length().toDouble() / 1024 / 1024).setScale(
                                2,
                                BigDecimal.ROUND_HALF_UP
                            )
                        }
                        length = BigDecimal(length).plus(bigDecimal).toDouble()
                    }
                }
            } else {
                length = BigDecimal(file.length().toDouble() / 1024 / 1024).setScale(
                    2,
                    BigDecimal.ROUND_HALF_UP
                ).toDouble()
            }
            Log.d("Size", length.toString())
            return length
        }

        fun deleteIfVideoOutOf2G() {
            deleteIfVideoOutOf(50)
        }

        private fun deleteIfVideoOutOf(size: Int) {
            Thread(Runnable {
                deleteVideo(size)
            }).start()

        }

        private fun deleteVideo(size: Int) {
            if (getSize(getLocalVideoPath()) > size) {
                var file = getLocalVideoPath()
                if (file.exists() && file.isDirectory) {
                    var files = file.listFiles()
                    if (files.isNotEmpty()) {
                        insertSortFromSmallToBig(files)
                        var f = files.first()
                        var b = f.delete()
                        var fs: MutableList<File> = files.toMutableList()
                        while (b && getSize(getLocalVideoPath()) > size) {
                            fs.removeAt(0)
                            b = fs[0].delete()
                        }

                    }
                }
            }
        }

        /**
         * 插入排序，按照时间戳从小到大（即旧到新）
         */
        private fun insertSortFromSmallToBig(files: Array<File>) {
            lateinit var temp: File
            var j: Int
            for (i in 1 until files.size) {
                temp = files[i]
                j = i
                while (j > 0) {

                    //如果当前数前面的数大于当前数，则把前面的数向后移一个位置
                    if (files[j - 1].lastModified() > temp.lastModified()) {
                        files[j] = files[j - 1]

                        //第一个数已经移到第二个数，将当前数放到第一个位置，这一趟结束
                        if (j == 1) {
                            files[j - 1] = temp
                            break
                        }
                    } else { //如果不大于，将当前数放到j的位置，这一趟结束
                        files[j] = temp
                        break
                    }
                    j--
                }
            }
        }

        public fun rename(sourceFile: File, destFile: File): Boolean {
            if (!sourceFile.exists()) {
                throw FileNotFoundException("sourceFile is not found!")
                return false
            }
            return sourceFile.renameTo(destFile)

        }

    }


}