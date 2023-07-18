package com.example.launcher

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.launcher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var vb: ActivityMainBinding
    private lateinit var adapter: AppAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        init()
    }

    private fun init() {
        adapter = AppAdapter(this)
        vb.rv.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5)
            adapter = this@MainActivity.adapter
        }
        ResolvestoPackage().also {
//        InstalledPackagesToPackages().also {
            Log.d("Main", "installed packages size ${it.size}")
            Log.d("Main", "installed packages $it")
            adapter.setNewData(it)
        }
//        packageManager.getInstalledApplications(0)
    }

//    for (int i = 0; i < packlist.size(); i++) {
//        PackageInfo pak = (PackageInfo) packlist.get(i);
//        // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
//        if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
//            // 添加自己已经安装的应用程序
//            // apps.add(pak);
//        } else{ // 系统应用
//        }
//        apps.add(pak);
//    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}