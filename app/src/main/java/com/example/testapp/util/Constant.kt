package com.example.testapp.util



/**
 * Create by MiLuycong on 2019/10/18
 */
class Constant {

//    object set {
//        var timer: Long = 1000000
//    }

    /**
     * 用于记录识别当前app对应的效果器，将会决定app的更新(查询参数)和效果器相关调整
     */
    enum class AppEffectVersion(val efVersion: String) {
        //朗读亭使用的第一种阿曼达效果器
        First("ldt"),

        //朗读亭使用的第二种阿曼达效果器
        Second("ldt_second_effect")
    }

    companion object {
        //        public const val API_SERVER_BASE_URL = "http://192.168.50.21:8081/"
//        public const val API_SERVER_BASE_URL = "http://10.0.0.9:8081/ldt/"
//        public const val API_SERVER_BASE_URL = "http://ld.360guoxue.com:8443/ldt/"
        public const val API_SERVER_BASE_URL = "http://59.110.18.73:8888/ldt/"    //线上

        //        public const val API_SERVER_BASE_URL = "http://2a1732764u.imwork.net/ldt/"
//        public const val API_SERVER_BASE_URL = "http://192.168.50.184:8081/ldt/" //本地
//        http://ld.360guoxue.com:8443/ldt
//        public const val API_SERVER_BASE_URL = "http://192.168.50.21:8081/ldt/"
//        正式的
//         public const val API_SERVER_BASE_URL = "http://47.113.113.133:8080/ldt/"
//        http://47.113.113.133:8080/ldt
//        public const val API_SERVER_BASE_URL = "http://119.23.66.37:8080/ldt/"
        public const val KEDAXUNEI_APP_ID = "5dd66f73"
        public const val VXApp_ID = "wxf5f1daf725122a62"
        public const val XUNHUAN_PATH = "xunhuan_path"
        var XUNHUAN_IMG = "xunhuan_img"
        var defaultVideo: String = ""
        var deviceCode: String = "1"
        var List = ArrayList<String>()
        var timer: Long = 1000000

        //        默认0可以循环播放
        var canReplay = 0
        var backImg = ""
        var backMusic = ""
        var isHide = true
        var isTimeDown = false
        var logo = ""

        var token: String = ""
        var type: Int = 0

        var time: Int = 11
        var isStart: Boolean = false

        //当前app使用的效果器
        val EFFECT_VERSION = AppEffectVersion.Second
    }

}