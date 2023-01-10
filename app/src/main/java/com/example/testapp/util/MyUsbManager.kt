package com.example.testapp.util

import android.content.Context
import android.content.Context.USB_SERVICE
import android.hardware.usb.*
import android.util.Log

import androidx.core.content.ContextCompat.getSystemService


/**
 * @ClassName: UsbManager
 * @Description: 类作用描述
 * @Author: zhl
 * @Date: 2020/12/7 11:41
 */
class MyUsbManager {
    companion object {


        private val TAG = MyUsbManager::class.java.simpleName
        private fun detectUsbAudioDevice(context: Context): Boolean {
            val deviceHashMap: HashMap<String, UsbDevice> =
                (context.getSystemService(USB_SERVICE) as UsbManager).getDeviceList()
            for ((_, value) in deviceHashMap.entries) {
                val device: UsbDevice = value as UsbDevice
                if (null != device) {
                    for (i in 0 until device.getConfigurationCount()) {
                        val configuration: UsbConfiguration = device.getConfiguration(i)
                        if (null != configuration) {
                            for (j in 0 until configuration.getInterfaceCount()) {
                                val usbInterface: UsbInterface = configuration.getInterface(j)
                                if (null != usbInterface) {
                                    if (UsbConstants.USB_CLASS_AUDIO === usbInterface.getInterfaceClass()) {
                                        Log.i(TAG, "has usb audio device. ${device.deviceName}")
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Log.i(TAG, "have no usb audio device")
            return false
        }

        fun getAudioUsbDevice(context: Context) {
            val deviceHashMap: HashMap<String, UsbDevice> =
                (context.getSystemService(USB_SERVICE) as UsbManager).deviceList
            for ((str, device) in deviceHashMap.entries) {
                Log.d(TAG, str + " deviceClass:${device.deviceClass} ||  deviceName:${device.deviceName} || manufacturerName:${device.manufacturerName} || productName:${device.productName}")
            }
        }

    }
}