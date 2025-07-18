package com.example.androidvideocachedemo.util;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class IjkTrackInfoWithIdx {
    public int idx;
    public ITrackInfo mTrackInfo;

    public IjkTrackInfoWithIdx(int idx, ITrackInfo trackInfo) {
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