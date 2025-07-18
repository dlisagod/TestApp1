package com.example.androidvideocachedemo.util;

/**
 * @create zhl
 * @date 2023/9/5 9:24
 * @description
 * @update
 * @date
 * @description
 **/

import android.util.Log;

import tv.danmaku.ijk.media.player.annotations.NonNull;
import tv.danmaku.ijk.media.player.widget.media.IRenderView;

public class RenderCallback implements IRenderView.IRenderCallback {
        @Override
        public void onSurfaceCreated(IRenderView.ISurfaceHolder holder, int width, int height) {

        }

        @Override
        public void onSurfaceChanged(IRenderView.ISurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void onSurfaceDestroyed(IRenderView.ISurfaceHolder holder) {

        }
//    @Override
//    public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
//        if (holder.getRenderView() != mRenderView) {
//            Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
//            return;
//        }
//
//        mSurfaceWidth = w;
//        mSurfaceHeight = h;
//        boolean isValidState = (mTargetState == STATE_PLAYING);
//        boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
//        if (mMediaPlayer != null && isValidState && hasValidSize) {
//            if (mSeekWhenPrepared != 0) {
//                seekTo(mSeekWhenPrepared);
//            }
//            start();
//        }
//    }
//
//    @Override
//    public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
//        if (holder.getRenderView() != mRenderView) {
//            Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
//            return;
//        }
//
//        mSurfaceHolder = holder;
//        if (mMediaPlayer != null)
//            bindSurfaceHolder(mMediaPlayer, holder);
//        else
//            openVideo();
//    }
//
//    @Override
//    public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
//        if (holder.getRenderView() != mRenderView) {
//            Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
//            return;
//        }
//
//        // after we return from this we can't use the surface any more
//        mSurfaceHolder = null;
//        // REMOVED: if (mMediaController != null) mMediaController.hide();
//        // REMOVED: release(true);
//        releaseWithoutStop();
//    }
}
