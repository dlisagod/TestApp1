package com.example.testapp.util.effect

abstract class IEffect(
    val REVERBERATION_COMMAND: String,
    val MICROPHONE_COMMAND: String,
    val MUSIC_COMMAND: String,
    val REVERBERATION_BACK: String,
    val MICROPHONE_BACK: String,
    val MUSIC_BACK: String,
    val MAX_REVERBERATION_GRADE: Int,
    val MAX_MICROPHONE_GRADE: Int,
    val MAX_MUSIC_GRADE: Int
) {

    abstract fun getSendRVBRTStr(volume: Int): String

    abstract fun getSendMicStr(volume: Int): String

    abstract fun getSendMusicStr(volume: Int): String

    //    abstract fun getBackRVBRTVolumeStr(buffer : ByteArray): String
//
//    abstract fun getBackMicVolumeStr(buffer : ByteArray): String
//
//    abstract fun getBackMusicVolumeStr(buffer : ByteArray): String
    abstract fun getVolumeFromBack(buffer: ByteArray): Array<String?>

    /**
     * 把10进制转为16进制字符
     *
     * @param str
     * @return 不足两个字符则在前补足一个0
     */
    fun toTwoBitHexStr(str: Int): String {
        val hex =
            StringBuilder(Integer.toHexString(str))
        if (hex.length == 1) {
            hex.insert(0, "0")
        }
        return hex.toString()
    }

    //byte数组转16进制数字的字符串
    fun byteArrayToHexStr(byteArray: ByteArray?): String? {
        if (byteArray == null) {
            return null
        }
        val sb = StringBuilder()
        for (i in byteArray.indices) {
            val high: Int = (byteArray[i].toInt() shr 4) and 0x0f // 取高4位
            val low: Int = byteArray[i].toInt() and 0x0f //取低4位
            sb.append(if (high > 9) (high - 10 + 'A'.toInt()).toChar() else (high + '0'.toInt()).toChar())
            sb.append(if (low > 9) (low - 10 + 'A'.toInt()).toChar() else (low + '0'.toInt()).toChar())
        }
        return sb.toString()
    }
}