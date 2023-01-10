package com.example.androidvideocachedemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
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
    private final String[] videos = new String[]{
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
        initSurface();
        initMediaPlayer();
        initProxy();
        initSpinner();
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

    /**
     * 初始化MediaPlayer 准备资源完成开始播放
     */
    private void initMediaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
    private void prepare(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mSurface.release();
        mediaPlayer.release();
        mServer.shutdown();
        super.onDestroy();
    }
}