package com.lee.play;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

public class MyVideoView extends FrameLayout implements IfaceVideoView {

	private Context mContext;

	private VideoViewSystem mVideoView;

	public MyVideoView(Context context) {
		super(context);
		mContext = context;
	}

	public MyVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

	}

	public void initMyVideoView() {
		removeAllViews();
		mVideoView = new VideoViewSystem(mContext);
		FrameLayout.LayoutParams layoutParams;
		layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		addView(mVideoView, layoutParams);
	}

	public int getVideoWidth() {
		if (mVideoView != null)
			return mVideoView.getVideoWidth();
		return 0;
	}

	public int getVideoHeight() {
		if (mVideoView != null)
			return mVideoView.getVideoHeight();
		return 0;
	}

	public void setVideoScale(int width, int height) {
		if (mVideoView != null)
			mVideoView.setVideoScale(width, height);
	}

	@Override
	public void start() {
		if (mVideoView != null) {
			mVideoView.start();
		}
	}

	@Override
	public void resume() {
		if (mVideoView != null) {
			mVideoView.resume();
		}
	}

	@Override
	public void pause() {
		if (mVideoView != null) {
			mVideoView.pause();
		}
	}

	@Override
	public void stop() {
		if (mVideoView != null) {
			mVideoView.stop();

		}
	}

	@Override
	public void suspend() {
		if (mVideoView != null) {
			mVideoView.suspend();
		}
	}

	@Override
	public void setVideoPath(String path) {
		if (mVideoView != null) {
			mVideoView.setVideoPath(path);
		}
	}

	@Override
	public void seekTo(int msec) {
		if (mVideoView != null) {
			mVideoView.seekTo(msec);
		}
	}

	@Override
	public boolean canPause() {
		if (mVideoView != null) {
			return mVideoView.canPause();
		}
		return false;
	}

	@Override
	public boolean canSeek() {
		if (mVideoView != null) {
			return mVideoView.canSeekBackward();
		}
		return false;
	}

	@Override
	public boolean isPlaying() {
		if (mVideoView != null) {
			return mVideoView.isPlaying();
		}
		return false;
	}

	@Override
	public int getBufferPercentage() {
		if (mVideoView != null) {
			return mVideoView.getBufferPercentage();
		}
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (mVideoView != null) {
			return mVideoView.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if (mVideoView != null) {
			return mVideoView.getDuration();
		}
		return 0;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {

	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {

	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {

	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {

	}

}
