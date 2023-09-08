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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.danikula.videocache.HttpProxyCacheServer;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @create zhl
 * @date 2022/4/6 11:18
 * @description
 * @update
 * @date
 * @description
 **/
public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer = new MediaPlayer();
    private Surface mSurface;
    //    private final String[] videos = new String[]{
//            "https://readingpavilion.oss-cn-beijing.aliyuncs.com/ALIOSS_IMG_/1596784890000.mp4",
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
        initSurface2();
        initMediaPlayer();
        initProxy();
        initSpinner();
        initClick();
        checkProxyEnable();
        testM3u8();
    }

    private void testM3u8() {
        M3u8Util m3u8Util = M3u8Util.INSTANCE;
        m3u8Util.init(this);
        m3u8Util.generateM3u8File(m3u8Util.getTestM3u8NoKey());
    }


    private void initClick() {
        findViewById(R.id.btn_3).setOnClickListener(v -> {
            changeProxyEnable();
        });
        findViewById(R.id.btn_1).setOnClickListener(v -> {
            startActivity(new Intent(this, IjkMainActivity.class));
        });
        findViewById(R.id.btn_2).setOnClickListener(v -> {
            startActivity(new Intent(this, IjkVideoViewAct.class));
        });
        findViewById(R.id.btn_play).setOnClickListener(v -> {
            if (!prepared) return;
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
            else mediaPlayer.start();
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

    /**
     * 初始化MediaPlayer 准备资源完成开始播放
     */
    private void initMediaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prepared = true;
                initTrackSpinner();
                mp.start();
            }
        });
    }

    /**
     * 初始化画面层
     */
    private void initSurface() {
        TextureView mTextureView = findViewById(R.id.tv);
        mTextureView.setVisibility(View.VISIBLE);
        SurfaceView sv = findViewById(R.id.sv);
        sv.setVisibility(View.GONE);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
                mediaPlayer.setSurface(mSurface);
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
//                mediaPlayer.setSurface(mSurface);
            }
        });
    }

    private String tag = MainActivity.class.getSimpleName();

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
                mediaPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(tag, "surfaceChanged");
//                ijkMediaPlayer.setDisplay(holder);
                mediaPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(tag, "surfaceDestroyed");
                mediaPlayer.setDisplay(null);
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
    private List<TrackInfoWithIdx> findSoundTrack(MediaPlayer.TrackInfo[] trackInfos) {
        List<TrackInfoWithIdx> list = new ArrayList<TrackInfoWithIdx>();
        if (trackInfos != null) {
            int length = trackInfos.length;
            for (int idx = 0; idx < length; ++idx) {
                MediaPlayer.TrackInfo item = trackInfos[idx];
                if (item.getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    list.add(new TrackInfoWithIdx(idx, item));
                }
            }
        }
        return list;
    }

    /**
     * 初始化音轨选择
     */
    private void initTrackSpinner() {
        List<TrackInfoWithIdx> soundTracks = findSoundTrack(mediaPlayer.getTrackInfo());
        List<String> strings = new ArrayList<>();
        for (TrackInfoWithIdx trackInfoWithIdx : soundTracks) {
//            strings.add(trackInfoWithIdx.toString());
            strings.add(trackInfoWithIdx.getTrackInfoString());
        }
        Spinner spinner = findViewById(R.id.spin_01);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mediaPlayer.selectTrack(soundTracks.get(position).idx);
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
    }

    /**
     * 播放器准备资源
     *
     * @param url
     */
    private boolean prepared = false;

    private void prepare(String url) {
        prepared = false;
        try {
            mediaPlayer.reset();
            if (url == IjkVideoViewActKt.getFileMpgEnc()) {
                mediaPlayer.setDataSource(new DescryFileMediaSource(url));
                mediaPlayer.prepareAsync();
                return;
            }
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prepared) mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        if (mSurface != null)
            mSurface.release();
        mediaPlayer.release();
        mServer.shutdown();
        super.onDestroy();
    }
}