package com.example.androidvideocachedemo;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.danikula.videocache.HttpProxyCacheServer;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;
import tv.danmaku.ijk.media.player.widget.media.IRenderView;
import tv.danmaku.ijk.media.player.widget.media.IjkUtil;
import tv.danmaku.ijk.media.player.widget.media.SurfaceRenderView;

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
    //    private final String[] videos = new String[]{
//            "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
//            IjkVideoViewActKt.getFileMpgEnc(),
//            IjkVideoViewActKt.getFileM3u8(),
//            IjkVideoViewActKt.getFileM3u8_1(),
//            IjkVideoViewActKt.getFileM3u8_2(),
//            "https://data.360guoxue.com/18000/Calligraphy/test/910453/910453.m3u8",
//            "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear1/prog_index.m3u8",
//            "http://183.6.57.249:8888/music/1090614.MPG",
//            "http://183.6.57.249:8888/music/1065799.MPG",
//            "http://183.6.57.249:8888/music/1169378.MPG",
//            "http://183.6.57.249:8888/music/1094665.MPG"
//    };
    private final String[] videos = IjkVideoViewActKt.getVideos();
    HttpProxyCacheServer mServer;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        checkPermission();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        initSurfaceTexture();
//        initSurface2();
        initSurfaceRender();
        initIjkPlayer();
        initProxy();
        initSpinner();
        initClick();
        checkProxyEnable();
    }

    private FrameLayout fl;

    private void initClick() {
        fl = findViewById(R.id.fl);
        findViewById(R.id.btn_3).setOnClickListener(v -> {
            changeProxyEnable();
        });
        Button btn1 = findViewById(R.id.btn_1);
        btn1.setText("悬浮显示");
        btn1.setOnClickListener(v -> {
            showOrHideFloatSrv();
        });
        findViewById(R.id.btn_2).setOnClickListener(v -> {
            startActivity(new Intent(this, IjkVideoViewAct.class));
        });
        findViewById(R.id.btn_play).setOnClickListener(v -> {
//            if (prepared) {
            if (ijkMediaPlayer.isPlaying()) ijkMediaPlayer.pause();
            else if (ijkMediaPlayer.isPlayable()) ijkMediaPlayer.start();
//            }
        });
    }

    private void changeProxyEnable() {
        ProxyCacheServer.INSTANCE.setEnable(!ProxyCacheServer.INSTANCE.getEnable());
        checkProxyEnable();
    }

    private boolean enable = ProxyCacheServer.INSTANCE.getEnable();

    private void checkProxyEnable() {
        Button btn = findViewById(R.id.btn_3);
        enable = ProxyCacheServer.INSTANCE.getEnable();
        if (enable) btn.setText("代理已开启");
        else btn.setText("代理已关闭");
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
        mServer = ProxyCacheServer.INSTANCE.getNewCacheServer(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!showFloat) ijkMediaPlayer.pause();
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
//        IjkUtil.initIjk3(ijkMediaPlayer);
//        if (mHolder != null) {
//            mHolder.bindToMediaPlayer(ijkMediaPlayer);
//        }
//        ijkMediaPlayer.setSpeed(2);
        ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
//                IjkUtil.initIjk3(ijkMediaPlayer);
//                ijkMediaPlayer.setSpeed(2);
                Log.d(tag, "prepared");
                prepared = true;
                initTrackSpinner();
//                initSurface2();
                ijkMediaPlayer.start();

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

    private IRenderView.ISurfaceHolder mHolder;

    SurfaceRenderView srv;

    private void initSurfaceRender() {
        TextureView mTextureView = findViewById(R.id.tv);
        mTextureView.setVisibility(View.GONE);
        SurfaceView sv = findViewById(R.id.sv);
        sv.setVisibility(View.GONE);
        srv = findViewById(R.id.srv);
        srv.setVisibility(View.VISIBLE);
        srv.addRenderCallback(new IRenderView.IRenderCallback() {
            @Override
            public void onSurfaceCreated(IRenderView.ISurfaceHolder holder, int width, int height) {
                mHolder = holder;
                holder.bindToMediaPlayer(ijkMediaPlayer);
            }

            @Override
            public void onSurfaceChanged(IRenderView.ISurfaceHolder holder, int format, int width, int height) {
                mHolder = holder;
                holder.bindToMediaPlayer(ijkMediaPlayer);
            }

            @Override
            public void onSurfaceDestroyed(IRenderView.ISurfaceHolder holder) {
                mHolder = null;
                holder.bindToMediaPlayer(null);
            }
        });
    }

    private boolean showFloat = false;

    private void showOrHideFloatSrv() {
        if (showFloat) closeFloatSrv();
        else showFloatSrv();
    }

    private void showFloatSrv() {
        showFloat = true;
        fl.removeView(srv);
        FloatViewHelper.INSTANCE.showFloatView(this, srv, 0, 0, 320, 180);
    }

    private void closeFloatSrv() {
        showFloat = false;
        FloatViewHelper.INSTANCE.closeFloatView(srv);
        fl.addView(srv);
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
                if (enable) prepareByProxy(url);
                else prepare(url);
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
//                if (firstTimeSelect) {
//                    firstTimeSelect = false;
//                    return;
//                }
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
            IjkUtil.initIjk3(ijkMediaPlayer);
            if (mHolder != null) {
                mHolder.bindToMediaPlayer(ijkMediaPlayer);
            }
            if (url == IjkVideoViewActKt.getFileM3u8()) {
//                ijkMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(url), null);
                if (ProxyCacheServer.INSTANCE.getMServer() == null) {
                    ProxyCacheServer.INSTANCE.initProxy(this);
                }
                ProxyCacheServer.INSTANCE.preCacheKey();
//                String urlKey = mServer.getProxyUrl(ProxyCacheServer.INSTANCE.getKeyUrl());
//                Log.d(tag, "proxy urlKey:" + urlKey);
            }
            if (url == IjkVideoViewActKt.getFileMpgEnc()) {
                ijkMediaPlayer.setDataSource(new DescryFileMediaSource(url));
                ijkMediaPlayer.prepareAsync();
                return;
            }
//            else
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
        if (mSurface != null) mSurface.release();
        ijkMediaPlayer.release();
        mServer.shutdown();
        super.onDestroy();
        //todo sth
    }
}