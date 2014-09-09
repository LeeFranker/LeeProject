package com.lee.play;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 播放器常用操作
 * 
 * @author LeeFranker
 * 
 */
public interface IfaceVideoView {

	// 启动
	public void start();

	// 恢复
	public void resume();

	// 暂停
	public void pause();

	// 停止
	public void stop();

	// 挂起
	public void suspend();

	// 设置视频地址
	public void setVideoPath(String path);

	// seek
	public void seekTo(int msec);

	// 是否可以暂停
	public boolean canPause();

	// 是否可以seek
	public boolean canSeek();

	// 是否在播放中
	public boolean isPlaying();

	// 获取当前播放百分百
	public int getBufferPercentage();

	// 获取当前播放位置
	public int getCurrentPosition();

	// 获取播放时间
	public int getDuration();

	// 监听按下事件
	public boolean onKeyDown(int keyCode, KeyEvent event);

	// 监听触摸事件
	public boolean onTouchEvent(MotionEvent ev);

	// 监听轨迹球事件
	public boolean onTrackballEvent(MotionEvent ev);

	// 监听mediaplayer的完成事件
	public void setOnCompletionListener(OnCompletionListener listener);

	// 监听mediaplayer的出错事件
	public void setOnErrorListener(OnErrorListener listener);

	// 监听mediaplayer的消息事件
	public void setOnInfoListener(OnInfoListener listener);

	// 监听mediaplayer的准备事件
	public void setOnPreparedListener(OnPreparedListener listener);

}
