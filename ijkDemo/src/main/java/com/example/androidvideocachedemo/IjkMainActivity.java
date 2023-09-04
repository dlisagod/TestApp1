package com.example.androidvideocachedemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * @create zhl
 * @date 2022/4/6 11:18
 * @description
 * @update
 * @date
 * @description
 **/
public class IjkMainActivity extends AppCompatActivity {
    String tag = "IjkMainActivity";

    IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
    private Surface mSurface;
    private final String[] videos = new String[]{
            "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
            "http://183.6.57.249:8888/music/1090614.MPG",
            "http://183.6.57.249:8888/music/1065799.MPG",
            "http://183.6.57.249:8888/music/1169378.MPG",
            "http://183.6.57.249:8888/music/1094665.MPG"
    };
    HttpProxyCacheServer mServer;

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        checkPermission();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        initSurfaceTexture();
        initSurface2();
        initIjkPlayer();
        initProxy();
        initSpinner();
        initClick();
    }

    private void initClick() {
        findViewById(R.id.btn).setOnClickListener(v -> {
            startActivity(new Intent(this, IjkVideoViewAct.class));
        });
        findViewById(R.id.btn_1).setOnClickListener(v -> {
//            if (prepared) {
            if (ijkMediaPlayer.isPlaying()) ijkMediaPlayer.pause();
            else if (ijkMediaPlayer.isPlayable()) ijkMediaPlayer.start();
//            }
        });
    }

    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, 10086);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10086) {
            checkPermission();
        }
    }

    /**
     * 初始化边下边播的代理缓存
     */
    private void initProxy() {
        //缓存目录
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test";
        File file = new File(path);
        if (!file.exists()) file.mkdir();
        mServer = new HttpProxyCacheServer
                .Builder(this)
                .cacheDirectory(file)
                .fileNameGenerator(new FileNameGenerator() {
                    @Override
                    public String generate(String url) {
                        //文件名
                        String[] s = url.split("/");
                        return s[s.length - 1];
                    }
                })
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkMediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (prepared) ijkMediaPlayer.start();
    }

    /**
     * 初始化MediaPlayer 准备资源完成开始播放
     */

    private boolean prepared = false;

    private void initIjkPlayer() {
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        IjkUtil.initIjk(ijkMediaPlayer);
        ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                Log.d(tag, "prepared");
                prepared = true;
                initTrackSpinner();
//                initSurface2();
//                ijkMediaPlayer.start();
            }
        });
    }


    private void initSurface2() {
        TextureView mTextureView = findViewById(R.id.tv);
        mTextureView.setVisibility(View.GONE);
        SurfaceView sv = findViewById(R.id.sv);
        sv.setVisibility(View.VISIBLE);
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(tag, "surfaceCreated");
//                ijkMediaPlayer.setDisplay(holder);
                ijkMediaPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(tag, "surfaceChanged");
//                ijkMediaPlayer.setDisplay(holder);
                ijkMediaPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(tag, "surfaceDestroyed");
                ijkMediaPlayer.setDisplay(null);
            }
        });
    }

    /**
     * 初始化画面层
     */
    private void initSurfaceTexture() {
        SurfaceView sv = findViewById(R.id.sv);
        sv.setVisibility(View.GONE);
        TextureView mTextureView = findViewById(R.id.tv);
        mTextureView.setVisibility(View.VISIBLE);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Log.d(tag, "onSurfaceTextureAvailable");
                mSurface = new Surface(surface);
                ijkMediaPlayer.setSurface(mSurface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                mSurface = new Surface(surface);
//                ijkMediaPlayer.setSurface(mSurface);
                Log.d(tag, "onSurfaceTextureUpdated");
                if (mSurface != null) {
                    mSurface.release();
                }
                mSurface = new Surface(surface);
                ijkMediaPlayer.setSurface(mSurface);
            }
        });
    }

    /**
     * 初始化下拉选单
     */
    private void initSpinner() {
        Spinner spinner = findViewById(R.id.spin);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videos));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String url = videos[position];
//                prepare(url);
                prepareByProxy(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 多音轨相关，可忽略
     *
     * @param trackInfos
     * @return
     */
    private List<IjkTrackInfoWithIdx> findSoundTrack(IjkTrackInfo[] trackInfos) {
        List<IjkTrackInfoWithIdx> list = new ArrayList<IjkTrackInfoWithIdx>();
        if (trackInfos != null) {
            int length = trackInfos.length;
            for (int idx = 0; idx < length; ++idx) {
                IjkTrackInfo item = trackInfos[idx];
                if (item.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    list.add(new IjkTrackInfoWithIdx(idx, item));
                }
            }
        }
        return list;
    }

    private boolean firstTimeSelect = false;

    /**
     * 初始化音轨选择
     */
    private void initTrackSpinner() {
        firstTimeSelect = true;
        List<IjkTrackInfoWithIdx> soundTracks = findSoundTrack(ijkMediaPlayer.getTrackInfo());
        List<String> strings = new ArrayList<>();
        for (IjkTrackInfoWithIdx trackInfoWithIdx : soundTracks) {
            strings.add(trackInfoWithIdx.toString());
//            strings.add(trackInfoWithIdx.getTrackInfoString());
        }
        Spinner spinner = findViewById(R.id.spin_01);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ijkMediaPlayer.selectTrack(soundTracks.get(position).idx);
//                ijkMediaPlayer.
                if (firstTimeSelect) {
                    firstTimeSelect = false;
                    return;
                }
                ijkMediaPlayer.selectTrack(soundTracks.get(position).idx);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * 使用代理地址准备资源
     *
     * @param url
     */
    private void prepareByProxy(String url) {
        String proxyUrl = mServer.getProxyUrl(url);
        prepare(proxyUrl);
//        prepare(url);
    }

    /**
     * 播放器准备资源
     *
     * @param url
     */
    private void prepare(String url) {
        prepared = false;
        try {
            ijkMediaPlayer.reset();
            ijkMediaPlayer.setDataSource(url);
//            ijkMediaPlayer.setDataSource("https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4");
//            ijkMediaPlayer.setDataSource("http://183.6.57.249:8888/music/1090614.MPG");
//            ijkMediaPlayer.setDataSource("http://183.6.57.249:8888/music/1109745.MPG");
//            ijkMediaPlayer.setDataSource("http://183.6.57.249:8888/music/1109745.MPG");
            ijkMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
//        mSurface.release();
        ijkMediaPlayer.release();
        mServer.shutdown();
        super.onDestroy();
        //todo sth
    }
}