package com.example.testapp.util.effect

class Second private constructor() : IEffect(
    "E0A206B70A0616",
    "E0A206B70A0316",
    "E0A206B70A0016",
    "E0A206B00A0616",
    "E0A206B00A0316",
    "E0A206B00A0016",
    30, 30, 30
) {
    val COMMAND_TAIL = "FF"

    override fun getSendRVBRTStr(volume: Int): String {
        return REVERBERATION_COMMAND + toTwoBitHexStr(volume) + COMMAND_TAIL
    }

    override fun getSendMicStr(volume: Int): String {
        return MICROPHONE_COMMAND + toTwoBitHexStr(volume) + COMMAND_TAIL
    }

    override fun getSendMusicStr(volume: Int): String {
        return MUSIC_COMMAND + toTwoBitHexStr(volume) + COMMAND_TAIL
    }

    override fun getVolumeFromBack(buffer: ByteArray): Array<String?> {
        var backHexText = byteArrayToHexStr(buffer)
        //                    LogUtil.Companion.d("EffectBack", backHexText);
        backHexText = MUSIC_BACK + "1c" + COMMAND_TAIL
        val volumeHex = backHexText?.substring(14,16)
        //                    LogUtil.Companion.d("EffectBackVolumeHex", volumeHex);
        val volumeDec = volumeHex?.toInt(16).toString()
        return arrayOf(backHexText,volumeHex,volumeDec)
    }

//    override fun getBackRVBRTVolumeStr(buffer : ByteArray): String {
//
//    }
//
//    override fun getBackMicVolumeStr(buffer : ByteArray): String {
//
//    }
//
//    override fun getBackMusicVolumeStr(buffer : ByteArray): String {
//
//    }

    companion object {
        fun getInstance(): Second = Second()
    }
}