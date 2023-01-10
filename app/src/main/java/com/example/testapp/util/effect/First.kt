package com.example.testapp.util.effect

class First private constructor() : IEffect(
    "55AA08",
    "55AA0A",
    "55AA0B",
    "44BB08",
    "44BB0A",
    "44BB0B",
    15, 15, 31
) {

    override fun getSendRVBRTStr(volume: Int): String {
        return REVERBERATION_COMMAND + toTwoBitHexStr(volume)
    }

    override fun getSendMicStr(volume: Int): String {
        return MICROPHONE_COMMAND + toTwoBitHexStr(volume)
    }

    override fun getSendMusicStr(volume: Int): String {
        return MUSIC_COMMAND + toTwoBitHexStr(volume)
    }

    override fun getVolumeFromBack(buffer: ByteArray): Array<String?> {
        val backHexText = byteArrayToHexStr(buffer)
        backHexText
        //                    LogUtil.Companion.d("EffectBack", backHexText);
        val volumeHex = backHexText?.substring(6)
        //                    LogUtil.Companion.d("EffectBackVolumeHex", volumeHex);
        val volumeDec = volumeHex?.toInt(16).toString()

        return arrayOf(backHexText,volumeHex,volumeDec)
    }

//    override fun getBackRVBRTVolumeStr(buffer: ByteArray): String {
//        val backHexText = byteArrayToHexStr(buffer)
//        //                    LogUtil.Companion.d("EffectBack", backHexText);
//        val volumeHex = backHexText?.substring(6)
//        //                    LogUtil.Companion.d("EffectBackVolumeHex", volumeHex);
//        val volumeDec = volumeHex?.toInt(16).toString() + ""
//
//        return volumeDec
//    }
//
//    override fun getBackMicVolumeStr(buffer: ByteArray): String {
//    }
//
//    override fun getBackMusicVolumeStr(buffer: ByteArray): String {
//    }

    companion object {
        fun getInstance(): First = First()
    }
}