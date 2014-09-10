package com.lee.test;

import android.os.Bundle;
import android.view.Menu;

import com.lee.R;
import com.lee.leftmenu.AbsSlidingActivity;

public class MainActivity_leftmenu_fragment extends AbsSlidingActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.b);
		showMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



}
