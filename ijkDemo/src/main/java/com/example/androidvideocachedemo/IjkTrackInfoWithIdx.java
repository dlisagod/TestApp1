package com.example.androidvideocachedemo;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public class IjkTrackInfoWithIdx {
    int idx;
    ITrackInfo mTrackInfo;

    IjkTrackInfoWithIdx(int idx, ITrackInfo trackInfo) {
        this.idx = idx;
        mTrackInfo = trackInfo;
    }

    public String toString() {
        return "(idx=" + this.idx + ", mTrackInfo=" + this.mTrackInfo + ")";
//        return "{" + this.idx + "," + this.mTrackInfo + "}";
    }

    public String getTrackInfoString() {
        return this.mTrackInfo.toString();
    }
}