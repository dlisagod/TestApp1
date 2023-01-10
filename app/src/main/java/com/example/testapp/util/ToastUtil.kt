package com.example.testapp.util

import android.widget.Toast
import com.example.testapp.MyApplication

class ToastUtil {
    companion object {

        private val toast: Toast by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Toast.makeText(MyApplication.context, "", Toast.LENGTH_SHORT)
        }

        fun showShort(text: String?) {
//            Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT).show()
            synchronized(toast) {
                toast.duration = Toast.LENGTH_SHORT
                toast.setText(text)
                toast.show()
            }
        }

        fun showLong(text: String?) {
//            Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_LONG).show()
            synchronized(toast) {
                toast.duration = Toast.LENGTH_LONG
                toast.setText(text)
                toast.show()
            }
        }
    }
}