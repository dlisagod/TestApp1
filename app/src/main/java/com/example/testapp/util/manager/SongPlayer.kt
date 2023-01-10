package com.example.testapp.util.manager

import android.media.MediaPlayer
import android.view.Surface
import com.example.testapp.bean.TrackInfoWithIdx
import com.example.testapp.util.LogUtil
import com.example.testapp.util.ToastUtil
import java.lang.ref.WeakReference

/**
 * @ClassName: SongPlayer
 * @Description: 播放器简单封装
 * @Author: zhl
 * @Date: 2021/3/15 9:24
 */
class SongPlayer {

    private val TAG = SongPlayer::class.java.simpleName

    /**
     * 媒体播放器
     */
    private var mMediaPlayer: MediaPlayer = MediaPlayer()

    /**
     * 播放状态
     */
//    private var playing = false
    val isPlaying: Boolean
        get() = mMediaPlayer.isPlaying

    /**
     * 准备状态
     */
    private var mPrepared = false

    /**
     * 音轨
     */
    private var mSoundTracks: MutableList<TrackInfoWithIdx> = mutableListOf()

    /**
     * 准备监听
     */
    @Deprecated("")
    private var preparedCallBacks2: MutableList<WeakReference<MediaPlayer.OnPreparedListener>> =
        mutableListOf()

    /**
     * 准备监听
     */
    private val preparedCallBacks = HashSet<MediaPlayer.OnPreparedListener>()

    /**
     * 完成监听
     */
    private val completeListeners = HashSet<MediaPlayer.OnCompletionListener>()

    /**
     * 错误监听
     */
    private val errorListeners = HashSet<MediaPlayer.OnErrorListener>()

    /**
     * 原唱true/伴唱false
     */
    private var mOrigin: Boolean = true

    /**
     * 获取伴唱、原唱状态
     * true 原唱；false 伴唱
     */
    val isOrigin: Boolean
        get() = mOrigin
//        set(value) {
//            mOrigin = value
//        }

    /**
     * 默认音轨index
     */
    private var mDefAudioTrackIdx: Int = 0

    /**
     * 加载完成时是否需要改变音轨
     */
    var needChangeSoundTrack: Boolean = true

    /**
     * 视频默认音轨信息
     */
    private var mDefTrackInfo: TrackInfoWithIdx? = null

    /**
     * 是否在错误监听后调用完成监听
     */
    var completeAfterError: Boolean = false

    init {
        //设置准备监听
        mMediaPlayer.setOnPreparedListener {
            mPrepared = true
            findSoundTrack(mMediaPlayer.trackInfo)
            for (listener in preparedCallBacks) {
                listener.onPrepared(it)
            }
            LogUtil.d("SongLog$TAG", "duration ${it.duration}")
        }
        //设置完成监听
        mMediaPlayer.setOnCompletionListener {
            for (listener in completeListeners) {
                listener.onCompletion(it)
            }
        }
        //设置错误监听
        mMediaPlayer.setOnErrorListener { mp, what, extra ->
            for (listener in errorListeners) {
                listener.onError(mp, what, extra)
            }
            return@setOnErrorListener completeAfterError
        }
    }

    /**
     * 准备资源
     */
    fun prepare(urlOrFilePath: String) {
        try {
            mMediaPlayer.reset()
            mPrepared = false
            mMediaPlayer.setDataSource(urlOrFilePath)
            mMediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 外部获取准备状态
     */
    val prepared: Boolean
        get() = mPrepared

    /**
     * 添加准备监听
     */
    @Deprecated("Use addPreparedListener instead")
    fun addPreparedListener2(listener: MediaPlayer.OnPreparedListener) {
        preparedCallBacks2.add(WeakReference(listener))
    }

    /**
     * 添加准备监听
     */
    fun addPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        preparedCallBacks.add(listener)
    }

    /**
     * 移除准备监听
     */
    fun removePreparedListener(listener: MediaPlayer.OnPreparedListener) {
        preparedCallBacks.remove(listener)
    }

    /**
     * 添加完成播放监听
     */
    fun addCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        completeListeners.add(listener)
    }

    /**
     * 移除完成播放监听
     */
    fun removeCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        completeListeners.remove(listener)
    }

    /**
     * 添加错误监听
     */
    fun addErrorListener(listener: MediaPlayer.OnErrorListener) {
        errorListeners.add(listener)
    }

    /**
     * 移除错误监听
     */
    fun removeErrorListener(listener: MediaPlayer.OnErrorListener) {
        errorListeners.remove(listener)
    }

    /**
     * 设置播放画面显示
     */
    fun setSurface(surface: Surface?) {
        mMediaPlayer.setSurface(surface)
    }

    /**
     * 播放
     */
    fun play() {
        if (mPrepared && !mMediaPlayer.isPlaying) {
            mMediaPlayer.start()
        }
    }

    /**
     * 重新播放
     */
    fun seekToZero(needPlay: Boolean = false) {
        seekTo(0, needPlay)
    }

    /**
     * 定到播放位置
     */
    fun seekTo(position: Int, needPlay: Boolean = false) {
        if (!mPrepared) return
        if (!mMediaPlayer.isPlaying && needPlay) {
            mMediaPlayer.seekTo(position)
            mMediaPlayer.start()
        } else {
            mMediaPlayer.seekTo(position)
        }
    }

    /**
     * 暂停
     */
    fun pause() {
        if (mMediaPlayer.isPlaying) mMediaPlayer.pause()
    }

    /**
     * 停止
     */
    fun stop() {
        if (prepared) {
            mMediaPlayer.stop()
        }
    }

    /**
     * 释放
     */
    fun release() {
        mMediaPlayer.release()
    }

    /**
     * 重建
     */
    fun recreateMediaPlayer() {
        mMediaPlayer.release()
        mMediaPlayer = MediaPlayer()
    }

    /**
     * 声音大小
     */
    fun setVolume(volume: Float) {
        mMediaPlayer.setVolume(volume, volume)
    }


    /**
     * 播放位置，毫秒
     */
    val currentTimeMills: Int
        get() = mMediaPlayer.currentPosition

    /**
     * 总时长，毫秒
     */
    val duration: Int
        get() = mMediaPlayer.duration

    /**
     * 从获取到的trackinfo数组里找出音轨
     * @param trackInfos 前面获取到的
     */
    private fun findSoundTrack(trackInfos: Array<MediaPlayer.TrackInfo>?) {
        mSoundTracks.clear()
        trackInfos?.also {
            for ((idx, item) in trackInfos.withIndex()) {
                if (item.trackType == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                    mSoundTracks.add(TrackInfoWithIdx(idx, item))
                }
            }
        }
        getDefAudioTrack()
        selectAudioTrack()
    }

    /**
     * 获取默认音轨相关信息
     */
    private fun getDefAudioTrack() {
        mDefAudioTrackIdx =
            mMediaPlayer.getSelectedTrack(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO)
        for (track in mSoundTracks) {
            if (track.idx == mDefAudioTrackIdx) {
                mDefTrackInfo = track
                break
            }
        }
    }

    /**
     * 当设置了伴唱（非默认原音轨）时，切换为伴唱（选择另一个音轨）
     */
    private fun selectAudioTrack() {
        if (!needChangeSoundTrack) {
            return
        }
        if (!mOrigin) {
            if (mSoundTracks.size < 2) {
                ToastUtil.showShort("当前歌曲不支持切换伴唱或原唱")
                return
            }
            for (track in mSoundTracks) {
                if (track.idx != mDefAudioTrackIdx) {
                    mMediaPlayer.selectTrack(track.idx)
                    break
                }
            }
        }
    }

    /**
     * 切换音轨，即切换原唱与伴唱
     */
    fun changeSoundTrack() {
        mOrigin = !mOrigin
        if (!needChangeSoundTrack) {
            return
        }
        //改变原唱/伴唱
        if (mSoundTracks.size < 2) {
            ToastUtil.showShort("当前歌曲不支持切换伴唱或原唱")
            return
        }

        if (!mOrigin) {//改成了伴唱
            for (track in mSoundTracks) {
                if (track.idx != mDefAudioTrackIdx) {
                    mMediaPlayer.selectTrack(track.idx)
                    break
                }
            }
        } else {//改成了原唱
            for (track in mSoundTracks) {
                if (track.idx == mDefAudioTrackIdx) {
                    mMediaPlayer.selectTrack(track.idx)
                    break
                }
            }
        }
    }

}