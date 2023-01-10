package com.example.testapp.util

import android.util.Log

/**
 * Create by MiLuycong on 2019/10/21
 */
class LogUtil {

    companion object {
        private val VERBOSE = 1
        private val DEBUG = 2
        private val INFO = 3
        private val WARN = 4
        private val ERROR = 5
        private val sLevel = VERBOSE

        fun v(tag: String, msg: Any) {
            if (sLevel <= VERBOSE) {
                val listContent = getMsgArray(msg.toString())
                for (str in listContent) {
                    Log.v(tag, str)
                }
            }
        }

        fun d(tag: String, msg: Any) {
            if (sLevel <= DEBUG) {
                val listContent = getMsgArray(msg.toString())
                for (str in listContent) {
                    Log.d(tag, str)
                }
            }
        }

        fun i(tag: String, msg: Any) {
            if (sLevel <= INFO) {
                val listContent = getMsgArray(msg.toString())
                for (str in listContent) {
                    Log.i(tag, str)
                }
            }
        }

        fun w(tag: String, msg: Any) {
            if (sLevel <= WARN) {
                val listContent = getMsgArray(msg.toString())
                for (str in listContent) {
                    Log.w(tag, str)
                }
            }
        }

        fun e(tag: String, msg: Any) {
            if (sLevel <= ERROR) {
                val listContent = getMsgArray(msg.toString())
                for (str in listContent) {
                    Log.e(tag, str)
                }
            }
        }

        private const val Max_Size = 2000
        private fun getMsgArray(src: String): MutableList<String> {
            return if (src.length <= Max_Size) {
                mutableListOf(src)
            } else {
                val list = mutableListOf<String>()
                val length = src.length
                var idx = 0
                var endIdx = 0
                while (idx < length) {
                    //0~1999 2000~3999 ...
                    endIdx = idx + Max_Size
                    if (endIdx > length) endIdx = length
                    list.add(src.substring(idx, endIdx))
                    idx += Max_Size
                }

                list
            }
        }
    }
}