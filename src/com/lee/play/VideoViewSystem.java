package com.lee.play;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class VideoViewSystem extends SurfaceView implements MediaPlayerControl, IfaceVideoView {

	private static final String TAG = VideoViewSystem.class.getName();

	private Context mContext;

	private boolean mCanPause, mCanSeek;

	private int mDuration;

	private MediaPlayer mMediaPlayer;
	private SurfaceHolder mSurfaceHolder;
	private MediaController mMediaController;
	private MediaPlayer.OnCompletionListener mOnCompletionListener;
	private MediaPlayer.OnErrorListener mOnErrorListener;
	private MediaPlayer.OnInfoListener mOnInfoListener;
	private MediaPlayer.OnPreparedListener mOnPreparedListener;

	private int mVideoWidth, mVideoHeight;
	private int mSurfaceWidth, mSurfaceHeight;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	private int mCurrentState;
	private boolean mPlayImmediately;

	private Uri mUri;
	private int mSeekWhenPrepared;
	private int mCurrentBufferPercentage;

	public VideoViewSystem(Context context) {
		super(context);
		mContext = context;
		initVideoView();
	}

	public VideoViewSystem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		initVideoView();
	}

	public VideoViewSystem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initVideoView();
	}

	@SuppressWarnings("deprecation")
	private void initVideoView() {
		Log.d(TAG, "initVideoView");
		mVideoHeight = 0;
		mVideoWidth = 0;
		getHolder().addCallback(SHCallback);
		// 旧的系统版本需要调用
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;// 闲置状态
		mPlayImmediately = false;
	}

	public void setVideoScale(int width, int height) {
		LayoutParams lp = getLayoutParams();
		lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
	}

	private SurfaceHolder.Callback SHCallback = new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "surfaceholder.callback-surfaceDestroyed");
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			release();
			mPlayImmediately = false;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "surfaceholder.callback-surfaceCreated");
			mSurfaceHolder = holder;
			openVideo();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Log.d(TAG, "surfaceholder.callback-surfaceChanged");
			mSurfaceWidth = width;
			mSurfaceHeight = height;
			Log.d(TAG, "mSurfaceHeight:" + mSurfaceHeight);
			Log.d(TAG, "mSurfaceHeight:" + mSurfaceHeight);
			boolean hasValidSize = (mVideoWidth == width && mVideoHeight == height);
			if (mMediaPlayer != null && mPlayImmediately && hasValidSize) {
				if (mSeekWhenPrepared != 0) {
					seekTo(mSeekWhenPrepared);
				}
				start();
			}
		}
	};

	private void setVideoUri(Uri Uri) {
		mUri = Uri;
		mSeekWhenPrepared = 0;
		openVideo();
		setZOrderMediaOverlay(true);
		requestLayout();
		invalidate();
	}

	// 初始化mediaplayer
	private void openVideo() {
		Log.d(TAG, "openVideo");
		if (mUri == null || mSurfaceHolder == null) {
			Log.d(TAG, "mUri=" + mUri);
			Log.d(TAG, "mSurfaceHolder=" + mSurfaceHolder);
			return;
		}
		// Tell the music playback service to pause
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);
		release();
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mDuration = -1;
			mCurrentBufferPercentage = 0;
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "异常:"+e.getMessage());
			mCurrentState = STATE_ERROR;
			mPlayImmediately = false;
			if (mOnErrorListener != null)
				mOnErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
			return;
		}
	}

	private void release() {
		Log.d(TAG, "release");
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
		}
	}

	public void setMediaController(MediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());
		}
	}

	private boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	private MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			Log.d(TAG, "MediaPlayer.OnVideoSizeChangedListener");
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			Log.d(TAG, "mVideoWidth:" + mVideoWidth);
			Log.d(TAG, "mVideoHeight:" + mVideoHeight);
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			mCurrentState = STATE_PREPARED;
			Log.d(TAG, "MediaPlayer.OnPreparedListener");
			mCanPause = mCanSeek = true;

			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}
			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			Log.d(TAG, "mVideoWidth=" + mVideoWidth);
			Log.d(TAG, "mVideoHeight=" + mVideoHeight);

			if (mSeekWhenPrepared != 0) {
				seekTo(mSeekWhenPrepared);
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {

				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					if (mPlayImmediately) {
						start();
						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							mMediaController.show(0);
						}
					}
				}
			} else {
				if (mPlayImmediately) {
					start();
				}
			}
		}
	};

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			Log.d(TAG, "MediaPlayer.OnCompletionListener");
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mPlayImmediately = false;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			Log.d(TAG, "MediaPlayer.mErrorListener");
			mCurrentState = STATE_ERROR;
			mPlayImmediately = false;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}
			return true;
		}
	};

	private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			Log.d(TAG, "MediaPlayer.mInfoListener");
			if (mOnInfoListener != null) {
				if (mOnInfoListener.onInfo(mMediaPlayer, what, extra)) {
					return true;
				}
			}
			return false;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			Log.d(TAG, "MediaPlayer.mBufferingUpdateListener");
			mCurrentBufferPercentage = percent;
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				}
				return true;
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	@Override
	public void start() {
		Log.d(TAG, "start");
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mPlayImmediately = true;
	}

	@Override
	public void resume() {
		Log.d(TAG, "resume");
		openVideo();
	}

	@Override
	public void pause() {
		Log.d(TAG, "pause");
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mPlayImmediately = false;
	}

	@Override
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
		}
		mPlayImmediately = false;
	}

	@Override
	public void suspend() {
		release();
	}

	@Override
	public void setVideoPath(String path) {
		setVideoUri(Uri.parse(path));
	}

	@Override
	public void seekTo(int msec) {
		if (isInPlaybackState()) {
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		}
		mSeekWhenPrepared = msec;
	}

	@Override
	public boolean canPause() {
		return mCanPause;
	}

	@Override
	public boolean canSeek() {
		return mCanSeek;
	}

	@Override
	public boolean isPlaying() {
		if (mMediaPlayer != null) {
			return isInPlaybackState() && mMediaPlayer.isPlaying();
		}
		return false;
	}

	@Override
	public int getBufferPercentage() {
		if (mMediaPlayer != null)
			return mCurrentBufferPercentage;
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (isInPlaybackState())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}

	@Override
	public int getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0)
				return mDuration;
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}

	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mOnErrorListener = listener;
	}

	@Override
	public void setOnInfoListener(OnInfoListener listener) {
		mOnInfoListener = listener;
	}

	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		mOnPreparedListener = listener;
	}

	@Override
	public boolean canSeekBackward() {
		return mCanSeek;
	}

	@Override
	public boolean canSeekForward() {
		return mCanSeek;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}
}
