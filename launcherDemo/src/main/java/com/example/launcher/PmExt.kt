package com.example.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * @create zhl
 * @date 2023/1/10 16:14
 * @description
 *
 * @update
 * @date
 * @description
 **/

/**
 * 获取可启动应用信息
 */
fun Context.LaunchResolveInfos() =
    packageManager.queryIntentActivities(
        Intent(
            Intent.ACTION_MAIN,
            null
        ).apply { addCategory(Intent.CATEGORY_LAUNCHER) },
        0
    )

fun Context.ResolvestoPackage(): List<Package> {
    val list = mutableListOf<Package>()
    LaunchResolveInfos().forEach {
        list.add(
            Package(
                it.activityInfo.applicationInfo.loadLabel(packageManager).toString(),
                it.activityInfo.applicationInfo.loadIcon(packageManager),
                it.activityInfo.packageName
            )
        )
    }
    return list
}

fun Context.InstalledPackages(): List<PackageInfo> = packageManager.getInstalledPackages(0)

fun Context.InstalledPackagesToPackages(): List<Package> {
    val list = mutableListOf<Package>()
    InstalledPackages().forEach {
        list.add(
            Package(
                it.applicationInfo.loadLabel(packageManager).toString(),
                it.applicationInfo.loadIcon(packageManager),
                it.packageName
            )
        )
    }
    return list
}

fun PackageManager.LaunchIntent(packageName: String): Intent? =
    getLaunchIntentForPackage(packageName)

data class Package(val label: String, val icon: Drawable, val appId: String)