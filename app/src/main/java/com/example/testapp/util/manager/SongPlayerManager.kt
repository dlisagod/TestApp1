package com.example.testapp.util.manager

import android.media.MediaPlayer
import android.view.Surface
import androidx.annotation.CallSuper

/**
 * @ClassName: SongPlayerManager
 * @Description: 播放管理
 * @Author: zhl
 * @Date: 2021/3/15 10:47
 */
open class SongPlayerManager protected constructor() {
    /**
     * 主播放器
     */
    private var mainPlayer: SongPlayer? = null

    /**
     * 副播放器
     */
    private var secondPlayer: SongPlayer? = null

    /**
     * 全屏播放器
     */
//    private var fullPlayer: SongPlayer? = null

    /**
     * 获取主播放器准备状态
     */
    protected val isMainPrepared: Boolean
        get() = mainPlayer?.prepared ?: false

    /**
     * 获取副播放器准备状态
     */
    protected val isSecondPrepared: Boolean
        get() = secondPlayer?.prepared ?: false

    /**
     * 错误后是否调用完成播放回调
     */
    protected var mainCompleteAfterError: Boolean
        get() = mainPlayer?.completeAfterError ?: false
        set(value) {
            mainPlayer?.completeAfterError = value
        }

    /**
     * 全屏播放器
     */
//    protected val isFullPrepared: Boolean
//        get() = fullPlayer?.prepared ?: false
//    var hasSecond = false
    //    init {
//        mainPlayer.addPreparedListener(MediaPlayer.OnPreparedListener {
//
//        })
//        secondPlayer.addPreparedListener(MediaPlayer.OnPreparedListener {
//
//        })
//        mainPlayer.addCompleteListener(MediaPlayer.OnCompletionListener {
//
//        })
//        secondPlayer.addCompleteListener(MediaPlayer.OnCompletionListener {
//
//        })
//    }

    /**
     * 初始化播放器
     */
    open fun initPlayer() {
        if (mainPlayer == null) mainPlayer = SongPlayer()
        if (secondPlayer == null) secondPlayer = SongPlayer()
//        if (fullPlayer == null) fullPlayer = SongPlayer()
        secondPlayer?.setVolume(0F)
//        fullPlayer?.setVolume(0F)
    }

    /**
     * 添加准备完成监听
     */
    protected fun addPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mainPlayer?.addPreparedListener(listener)
        secondPlayer?.addPreparedListener(listener)
//        fullPlayer?.addPreparedListener(listener)
    }

    /**
     * 移除准备完成监听
     */
    protected fun removePreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mainPlayer?.removePreparedListener(listener)
        secondPlayer?.removePreparedListener(listener)
//        fullPlayer?.removePreparedListener(listener)
    }

    /**
     * 添加主播放器准备监听
     */
    protected fun addMainPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mainPlayer?.addPreparedListener(listener)
    }


    /**
     * 移除主播放器准备监听
     */
    protected fun removeMainPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mainPlayer?.removePreparedListener(listener)
    }

    /**
     * 添加播放结束监听
     */
    protected fun addCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mainPlayer?.addCompletionListener(listener)
        secondPlayer?.addCompletionListener(listener)
//        fullPlayer?.addCompletionListener(listener)
    }

    /**
     * 移除播放结束监听
     */
    protected fun removeCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mainPlayer?.removeCompletionListener(listener)
        secondPlayer?.removeCompletionListener(listener)
//        fullPlayer?.removeCompletionListener(listener)
    }

    /**
     * 添加主播放结束监听
     */
    protected fun addMainCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mainPlayer?.addCompletionListener(listener)
    }

    /**
     * 移除主播放结束监听
     */
    protected fun removeMainCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mainPlayer?.removeCompletionListener(listener)
    }

    /**
     * 添加错误监听
     */
    protected fun addMainErrorListener(listener: MediaPlayer.OnErrorListener) {
        mainPlayer?.addErrorListener(listener)
    }

    /**
     * 移除错误监听
     */
    protected fun removeMainErrorListener(listener: MediaPlayer.OnErrorListener) {
        mainPlayer?.removeErrorListener(listener)
    }

    /**
     * 设置主播放器播放画面
     */
    fun setMainSurface(surface: Surface) {
        mainPlayer?.setSurface(surface)
    }

    /**
     * 设置副播放器播放画面
     */
    fun setSecondSurface(surface: Surface) {
        secondPlayer?.setSurface(surface)
//        hasSecond = true
    }

    /**
     * 设置全屏播放器
     */
//    fun setFullSurface(surface: Surface?) {
//        fullPlayer?.setSurface(surface)
//    }

    /**
     * 播放
     */
    protected open fun play() {
        if (!isMainPrepared || !isSecondPrepared) return
        mainPlayer?.play()
        secondPlayer?.play()
//        fullPlayer?.play()
    }

    /**
     * 暂停
     */
    protected open fun pause() {
        mainPlayer?.pause()
        secondPlayer?.pause()
//        fullPlayer?.pause()
    }

    protected open fun stop() {
        mainPlayer?.stop()
        secondPlayer?.stop()
    }

    /**
     * 重新播放
     */
    protected fun playToZero() {
        if (!isMainPrepared || !isSecondPrepared) return
        mainPlayer?.seekToZero(true)
        secondPlayer?.seekToZero(true)
//        fullPlayer?.seekToZero(true)
    }

    var mTimePause: Int = 0
    fun pauseSyncTime() {
        pause()
        mTimePause = mainPlayer?.currentTimeMills ?: 0
        mTimePause -= 300
        if (mTimePause < 0) mTimePause = 0
    }

    fun playSyncTime() {
        mainPlayer?.seekTo(mTimePause)
        secondPlayer?.seekTo(mTimePause)
//        fullPlayer?.seekTo(mTimePause)
        play()
    }

    /**
     * 播放或暂停切换
     * @return true 表示在操作改为了播放；false则表示改为了暂停
     */
    open fun playOrPause() {
        if (mainPlayer?.isPlaying == true) mainPlayer?.pause() else mainPlayer?.play()
        if (secondPlayer?.isPlaying == true) secondPlayer?.pause() else secondPlayer?.play()
//        if (fullPlayer?.isPlaying == true) fullPlayer?.pause() else fullPlayer?.pause()
    }


    /**
     * 播放状态。只根据主播放器的状态来判断
     */
    val isPlaying: Boolean
        get() = mainPlayer?.isPlaying ?: false

    /**
     * 准备歌曲
     */
    protected open fun prepare(filePath: String) {
        mainPlayer?.prepare(filePath)
        secondPlayer?.prepare(filePath)
//        fullPlayer?.prepare(filePath)
    }

    /**
     * 设置是否需要准备完成时播放切换音轨
     */
    protected fun setNeedSelectSoundTrack(needSelectSoundTrack: Boolean) {
        mainPlayer?.needChangeSoundTrack = needSelectSoundTrack
    }

    /**
     * 播放位置，毫秒
     */
    val currentPositionMills: Int
        get() = mainPlayer?.currentTimeMills ?: 0

    /**
     * 时长，毫秒
     */
    val durationMills: Int
        get() = mainPlayer?.duration ?: 0

    /**
     * 获取伴唱、原唱状态
     * true 原唱；false 伴唱
     */
    val isOrigin: Boolean
        get() = mainPlayer?.isOrigin ?: false
//        set(value) {
//            mainPlayer?.isOrigin = value
//        }

    /**
     * 切换音轨
     */
    open fun changeSoundTrack() {
        mainPlayer?.changeSoundTrack()
    }

    /**
     * 释放
     */
    @CallSuper
    protected open fun release() {
        mainPlayer?.release()
        secondPlayer?.release()
//        fullPlayer?.release()
//        hasSecond = false

        mainPlayer = null
        secondPlayer = null
//        fullPlayer = null
    }

//    companion object {
//        val instance: SongPlayerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {//等同于java双重检测线程安全
//            SongPlayerManager()
//        }
//    }
}