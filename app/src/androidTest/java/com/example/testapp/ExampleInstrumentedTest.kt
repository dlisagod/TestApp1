package com.example.testapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.testapp.widget.getResIdByAttr

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.testapp", appContext.packageName)
        val data = appContext.getResIdByAttr(R.attr.img_test_01)
        print("equals ${data == R.mipmap.icon_play}")

        Thread.sleep(3000)
    }
}