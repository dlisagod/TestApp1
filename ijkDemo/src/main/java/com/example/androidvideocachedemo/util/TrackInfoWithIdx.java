package com.example.androidvideocachedemo.util;

import android.media.MediaPlayer;

/**
 * @create zhl
 * @date 2022/4/6 14:27
 * @description
 * @update
 * @date
 * @description
 **/
public class TrackInfoWithIdx {
    public int idx;
    public MediaPlayer.TrackInfo mTrackInfo;

    public TrackInfoWithIdx(int idx, MediaPlayer.TrackInfo trackInfo) {
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