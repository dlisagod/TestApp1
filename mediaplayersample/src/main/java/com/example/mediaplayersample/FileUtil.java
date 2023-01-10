package com.example.mediaplayersample;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @create zhl
 * @date 2022/5/30 18:38
 * @description
 * @update
 * @date
 * @description
 **/
public class FileUtil {
    public static String getStoragePath(Context mContext) {
        boolean is_removale = Build.VERSION.SDK_INT >= 23;
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    getTotal(path);
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getTotal(String path) {
        android.os.StatFs f = new android.os.StatFs(path);
        System.out.println("getTotalBytes "+f.getTotalBytes());
        System.out.println("getFreeBytes "+f.getFreeBytes());
        System.out.println("getAvailableBytes "+f.getAvailableBytes());
        return f.getBlockCountLong();
    }
}
