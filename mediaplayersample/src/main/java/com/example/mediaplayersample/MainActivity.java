package com.example.mediaplayersample;

import android.Manifest;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Spinner speedSpinner;
    private Button button;
    private String[] speeds;
    private MediaPlayer mediaPlayer;
    private boolean prepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.WRITE_SETTINGS
        }, 0);
        initData();
        initView();
    }

    void initData() {
        System.out.println("path " + FileUtil.getStoragePath(this));
        speeds = new String[]{"1.0", "1.7", "2.0", "0.5", "0.2"};
        mediaPlayer = MediaPlayer.create(this, R.raw.shall_we_talk);
//        mediaPlayer = new MediaPlayer();
//        try {
//            String path = FileUtil.getStoragePath(this) + "/audios/86426.m4a";
//            Log.d("MainActivity", "path is " + path);
//            mediaPlayer.setDataSource(path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d("MainActivity", System.currentTimeMillis() + "");
                prepared = true;
                mp.start();
                String text = "duration " + mp.getDuration();
                Log.d("MainActivity", "duration is " + mp.getDuration());
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
//       Log.d("MainActivity",System.currentTimeMillis()+"");
//        mediaPlayer.prepareAsync();
        //        mediaPlayer = new MediaPlayer();
//        String path = "http://data.360guoxue.com:18000/Audio/ReadingAudio/《山羊不吃天堂草》/《山羊不吃天堂草》第1章.mp3";
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    void initView() {

        speedSpinner = findViewById(R.id.speed_spinner);
        button = findViewById(R.id.btn);
        speedSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_item, speeds));
        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                float speed = 0F;
//                float speed = Float.parseFloat(speeds[position]);
//                PlaybackParams params = mediaPlayer.getPlaybackParams();
//                params.setSpeed(speed);
//                mediaPlayer.setPlaybackParams(params);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prepared) return;
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                else mediaPlayer.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
    }
}
