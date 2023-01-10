package com.example.androidvideocachedemo;

import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public class IjkTrackInfoWithIdx {
    int idx;
    IjkTrackInfo mTrackInfo;

    IjkTrackInfoWithIdx(int idx, IjkTrackInfo trackInfo) {
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