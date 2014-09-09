package com.lee;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lee.play.MyVideoView;

public class MainActivity extends Activity {

	private MyVideoView mVideoView;
	private Button buttonPlay,buttonPause,buttonStop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mVideoView=(MyVideoView)this.findViewById(R.id.videoview);
		buttonPlay=(Button)this.findViewById(R.id.button_play);
		buttonPause=(Button)this.findViewById(R.id.button_pause);
		buttonStop=(Button)this.findViewById(R.id.button_stop);
		buttonPause.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mVideoView.pause();
			}
		});
		buttonPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mVideoView.setVideoPath("/storage/emulated/0/.pps/test.mp4");
				mVideoView.start();
			}
		});
		buttonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mVideoView.stop();
			}
		});

		mVideoView.initMyVideoView();
		File file=Environment.getExternalStorageDirectory();
		String path=file.getPath()+"/.pps/test.mp4";
		Log.d("wangli", "path:"+path);
		file=new File(path);
		if(file.exists()){
			Log.d("wangli", file.getAbsolutePath());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
