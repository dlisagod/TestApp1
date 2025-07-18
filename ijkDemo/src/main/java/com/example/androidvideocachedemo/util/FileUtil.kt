package com.example.androidvideocachedemo.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.os.storage.StorageManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * @create zhl
 * @date 2022/7/19 16:37
 * @description
 *
 * @update
 * @date
 * @description
 **/
object FileUtil {

    /**
     * 字符串写入到文件
     *
     * @param `object`   内容
     * @param absPath  绝对路径
     * @param fileName 文件名
     * @param onSuccess 成功后执行的方法
     * @param onFailure 失败执行方法
     */
    inline fun writeObjToFile(
        any: Any,
        absPath: String,
        fileName: String,
        crossinline onSuccess: () -> Unit,
        crossinline onFailure: (mes: String) -> Unit,
    ) {
//        if (TextUtils.isEmpty(absPath) || TextUtils.isEmpty(fileName)) return;
        var content: String? = null
        content = try {
            Gson().toJson(any)
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure("写入失败，对象转json错误" + e.message)
            return
        }
        if (content == null) {
            onFailure("写入失败，对象转json错误")
            return
        }
        writeStrToFileSync(content, absPath, fileName, onSuccess, onFailure)
    }

    /**
     * 字符串写入到文件
     *
     * @param content  内容
     * @param absPath  绝对路径
     * @param fileName 文件名
     * @param onSuccess 成功后执行的方法
     * @param onFailure 失败执行方法
     */
    suspend inline fun writeStrToFile(
        content: String,
        absPath: String,
        fileName: String,
        crossinline onSuccess: () -> Unit,
        crossinline onFailure: (mes: String) -> Unit
    ) {
//        if (TextUtils.isEmpty(absPath) || TextUtils.isEmpty(fileName)) return;
        writeStrToFileSync(content, absPath, fileName, onSuccess, onFailure)
    }

    inline fun writeStrToFileSync(
        content: String,
        absPath: String,
        fileName: String,
        crossinline onSuccess: () -> Unit,
        crossinline onFailure: (mes: String) -> Unit
    ) {
        if (TextUtils.isEmpty(absPath) || TextUtils.isEmpty(fileName)) return
        try {
            val filePath = File(absPath)
            if (!filePath.exists()) {
                val createDir = filePath.mkdirs()
                if (!createDir) {
                    onFailure("文件夹" + absPath + "创建失败")
                    return
                }
            }
            val name = absPath + File.separator + fileName
            val file = File(name)
            if (!file.exists()) {
                val createFile = file.createNewFile()
                if (!createFile) {
                    onFailure("文件" + name + "创建失败")
                    return
                }
            }
            val realFileName = file.canonicalPath
            val fileOutputStream = FileOutputStream(realFileName)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
            val bufferedWriter = BufferedWriter(outputStreamWriter)
            bufferedWriter.write(content)
            bufferedWriter.close()
            outputStreamWriter.close()
            fileOutputStream.close()
            onSuccess()
        } catch (e: IOException) {
            e.printStackTrace()
            onFailure("写入失败" + e.message)
        }
    }

    /**
     * 从文件读取字符串
     *
     * @param absFileName
     * @param onSuccess 成功后执行的方法
     * @param onFailure 失败执行方法
     */
    inline fun readStrFromFile(
        absFileName: String,
        crossinline onSuccess: (str: String) -> Unit,
        crossinline onFailure: (mes: String) -> Unit,
    ) {
        try {
            val fileInputStream = FileInputStream(absFileName)
            val inputStreamReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String?
            val stringBuilder = StringBuilder()
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            onSuccess(stringBuilder.toString())
            bufferedReader.close()
            inputStreamReader.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            onFailure("读取" + absFileName + "失败" + e.message)
        }
    }

    fun uninstall(context: Context, packageName: String) {
        context.startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")))
    }

    fun installBelowN(apkFile: File, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Android 7.0以上
     * AndroidManifest中的android:authorities值需要有同ApplicationID 的 fileprovider，如com.example.test.fileprovider
     */
    fun installN(apkFile: File, context: Context) {
        val pn = context.packageName
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "$pn.fileprovider",
            apkFile
        ) //在AndroidManifest中的android:authorities值
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //给目标设置一个临时授权
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * 判断同版本apk是否存在
     *
     * @param filePath
     * @return
     */
    fun apkIsExists(
        filePath: String,
        versionName: String,
        context: Context
    ): File? {
        try {
            val file = File(filePath)
            if (file.exists()) {
                return apkInfo(filePath, versionName, context)
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun apkInfo(absPath: String, versionName: String?, context: Context): File? {
        val pm = context.packageManager
        val pkgInfo =
            pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES)
        if (pkgInfo != null) {
            val appInfo = pkgInfo.applicationInfo
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */appInfo.sourceDir = absPath
            appInfo.publicSourceDir = absPath
            //            String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
//            String packageName = appInfo.packageName; // 得到包名
            val version = pkgInfo.versionName // 得到版本信息
            //            /* icon1和icon2其实是一样的 */
//            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
//            Drawable icon2 = appInfo.loadIcon(pm);
//                val packInfo =
//                    pm.getPackageInfo(context.packageName, 0)
//                val version1 = packInfo.versionName
//                val versionint = version.replace(".", "").toInt()
//                val versionint1 = version1.replace(".", "").toInt()
//                if (versionint > versionint1) {
//                    return File(absPath)
//                }
            if (version == versionName) return File(absPath)
        }
        return null
    }

    const val charNotAllow = "/\\:*?\"<>|"

    //        private val charNotAllowP = Regex("[^\\s\\\\/:*?\"<>|]")
    val charNotAllowP = Pattern.compile("[/\\\\:*?\"<>|]")
//        private val charNotAllowP = Regex("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$")

    /**
     * 重命名
     */
    fun rename(sourceFile: File, destFile: File): Boolean {
//            if (charNotAllowP.matcher(destFile.name).matches()) {
//            if (destFile.name.matches(charNotAllowP)) {
//                Log.d("FilePathUtil", "文件包含非法字符$charNotAllow")
//                return false
//            }
        val matcher = charNotAllowP.matcher(destFile.name)
        while (matcher.find()) {
            Log.d("FilePathUtil", "文件包含非法字符$charNotAllow")
            return false
        }

        if (!sourceFile.exists()) {
//                throw FileNotFoundException("sourceFile is not found!")
            return false
        }
        return sourceFile.renameTo(destFile)
    }

    /**
     * 文件(夹)大小
     * @return MByte,兆字节
     */
    fun getSizeMByte(file: File): Double {
        var length = 0.00
        if (file.exists() && file.isDirectory) {
            val files: Array<File>? = file.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file1 in files) {
                    val bigDecimal = if (file1.isDirectory) {
                        BigDecimal(getSizeMByte(file1))
                    } else {
                        BigDecimal(file1.length().toDouble() / 1024 / 1024).setScale(
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
//            Log.d("Size", length.toString())
        return length
    }

    /**
     * 文件(夹)大小
     * @return MByte,兆字节
     */
    fun getSizeByte(file: File): Long {
        var length = 0L
        if (!file.exists()) return length
        if (file.isDirectory) {
            val files: Array<File>? = file.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (file1 in files) {
                    val thisSize = if (file1.isDirectory) {
                        getSizeByte(file1)
                    } else {
                        file1.length()
                    }
                    length += thisSize
                }
            }
        } else {
            length = file.length()
        }
//            Log.d("Size", length.toString())
        return length
    }

    /**
     * 根据字节数获取兆字节数，文件大小等
     */
    fun getSizeMByte(sizeByte: Long): Double {
        return BigDecimal(sizeByte.toDouble() / 1024 / 1024).setScale(
            2,
            BigDecimal.ROUND_HALF_UP
        ).toDouble()
    }

    /**
     * 根据字节数获取文件大小，自动转成Byte、KByte、MByte、GByte
     */
    fun getSizeByByte(sizeByte: Long): String {
        val str: String
        val sizeDouble = sizeByte.toDouble()
        if (sizeByte < 0) throw IllegalArgumentException("sizeByte < 0")

        if (sizeByte < 1024) {
            str = "B"
            return "$sizeByte$str"
        }

        val divider1 = sizeDouble / 1024
        if (divider1 < 1024.0) {
            str = "KB"
            return "${formatDouble(divider1)}$str"
        }

        val divider2 = sizeDouble / 1024 / 1024
        if (divider2 < 1024.0) {
            str = "MB"
            return "${formatDouble(divider2)}$str"
        }

        val divider3 = sizeDouble / 1024 / 1024 / 1024
        str = "GB"
        return "${formatDouble(divider3)}$str"

    }

    private fun formatDouble(d: Double): String {
        return String.format("%.2f", d)
    }

    /**
     * 使用协程删除文件，无需使用回调
     */
    suspend fun deleteAll(file: File) {
        withContext(Dispatchers.IO) {
            deleteAllFiles1(file)
        }
//            Thread(Runnable {
//                deleteAllFiles1(file)
//            }).start()
    }

    private fun deleteAllFiles1(file: File) {
//            Thread(Runnable {
        if (file.exists()) {
            if (file.isDirectory) {
                val files: Array<File>? = file.listFiles()
                if (!files.isNullOrEmpty()) {
                    for ((idx, file1) in files.withIndex()) {
                        deleteAllFiles1(file1)
                        if (idx == files.size - 1)
                            file1.delete()
                    }
                } else file.delete()
            } else {
                file.delete()
            }
        }
//            }).start()
    }

    /**
     * 删除文件
     */
    fun delete(path: String): Boolean {
        val file = File(path)
//            return if (file.exists()) file.delete()
//            else true
        return file.delete()
    }


    /**
     * 计算文件夹下的文件数量
     */
    fun countFileNum(file: File): Int {
        var count = 0
        if (file.isDirectory) {
            val children = file.listFiles()
            if (!children.isNullOrEmpty()) {
                children.forEach {
                    if (!it.isDirectory) count++
                    else count += countFileNum(it)
                }
            }
        }
        return count
    }

    /**
     *
     * 把一个目录A下的文件移动到另一个目录B下，保留A的原有子目录结构进行移动。
     * 若源目录A下有目录A1,A1下有文件A2，移动后为：B/A1/A2。
     * 源目录F:\A，要移动到 F:\B,而存在: F:\A\a.txt， F:\A\A1\a1.txt， F:\A\A1\A2\a2.txt；
     *
     * 则在操作完成后， F:\B\a.txt， F:\B\A1\a1.txt， F:\B\A1\A2\a2.txt
     */
    fun fileMove(dirOrg: File, dirTarget: File) {
        if (!dirTarget.isDirectory) return
        if (dirOrg.isFile) {
            dirOrg.renameTo(dirTarget)
            return
        }
        val list = getFileFromDir(dirOrg)
        val sDirOrg = dirOrg.canonicalPath
        val sDirTarget = dirTarget.canonicalPath
        list.forEach {
            val s = it.canonicalPath
            val sAfter = s.replace(sDirOrg, sDirTarget)
            println("$s renameTo $sAfter " + it.renameTo(File(sAfter).apply {
                parentFile?.also {
                    if (!it.exists()) it.mkdirs()
                }
            }))
        }
    }

    fun getFileFromDir(dir: File): List<File> {
        val list = arrayListOf<File>()
        dir.listFiles()?.also {
            if (it.isNotEmpty()) {
                it.forEach {
                    if (it.isDirectory) {
                        val l = getFileFromDir(it)
                        if (l.isNotEmpty()) list.addAll(getFileFromDir(it))
                    } else {
                        list.add(it)
                    }
                }
            }
        }
        return list
    }

    fun getStoragePath(context: Context): String? {
        val is_removable = Build.VERSION.SDK_INT >= 23
        val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        var storageVolumeClazz: Class<*>? = null
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            val result = getVolumeList.invoke(mStorageManager)
            val length = java.lang.reflect.Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement = java.lang.reflect.Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                if (is_removable == removable) {
//                    getTotal(path)
                    Log.d("getStoragePath", path)
                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 根据label获取外部存储路径(此方法适用于android7.0以上系统)
     * @param context
     * @param label 内部存储:Internal shared storage    SD卡:SD card    USB:USB drive(USB storage)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getExternalPath(context: Context, label: String): String? {
        var path: String? = ""
        val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        //获取所有挂载的设备（内部sd卡、外部sd卡、挂载的U盘）
        val volumes = mStorageManager.storageVolumes //此方法是android 7.0以上的
        try {
            val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            //通过反射调用系统hide的方法
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            //       Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");//userLabel和description是一样的
            for (i in volumes.indices) {
                val storageVolume = volumes[i] //获取每个挂载的StorageVolume
                // 通过反射调用getPath、isRemovable、userLabel
                val storagePath = getPath.invoke(storageVolume) as String //获取路径
                val isRemovableResult = isRemovable.invoke(storageVolume) as Boolean //是否可移除
                val description = storageVolume.getDescription(context) //此方法是android 7.0以上的
                if (label == description) {
                    Log.e(
                        "getExternalPath--",
                        " i=$i ,storagePath=$storagePath ,description=$description"
                    )
                    path = storagePath
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("getExternalPath--", " e:$e")
        }
        return path
    }

    fun getTotal(path: String?): Long {
        val f = StatFs(path)
        println("getTotalBytes " + f.totalBytes)
        println("getFreeBytes " + f.freeBytes)
        println("getAvailableBytes " + f.availableBytes)
        val unit = 1024.0
        return f.blockCountLong
    }

    fun getSize(path:String) {
//        val path = Environment.getExternalStorageDirectory().absolutePath
        StatFs(path).apply {
            val unit = 1024.0
            val unit3 = unit * unit * unit
            println("getSize getTotalBytes $totalBytes  ${totalBytes / unit3}")
            println("getSize getFreeBytes $freeBytes  ${freeBytes / unit3}")
            println("getSize getAvailableBytes $availableBytes  ${availableBytes / unit3}")
        }
    }

}