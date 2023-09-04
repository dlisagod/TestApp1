package com.example.androidvideocachedemo;

import android.media.MediaPlayer;

import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * @create zhl
 * @date 2022/4/6 14:27
 * @description
 * @update
 * @date
 * @description
 **/
public class TrackInfoWithIdx {
    int idx;
    MediaPlayer.TrackInfo mTrackInfo;

    TrackInfoWithIdx(int idx, MediaPlayer.TrackInfo trackInfo) {
        this.idx = idx;
        mTrackInfo = trackInfo;
    }

    public String toString() {
        return "TrackInfoWithIdx(idx=" + this.idx + ", mTrackInfo=" + this.mTrackInfo + ")";
//        return "{" + this.idx + "," + this.mTrackInfo + "}";
    }

    public String getTrackInfoString() {
        return this.mTrackInfo.toString();
    }
}