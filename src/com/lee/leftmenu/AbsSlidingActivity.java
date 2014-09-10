package com.lee.leftmenu;

import android.os.Bundle;

import com.lee.R;
import com.lee.leftmenu.SlidingMenu.OnClosedListener;
import com.lee.leftmenu.SlidingMenu.OnOpenListener;

public abstract class AbsSlidingActivity extends SlidingActivity implements OnOpenListener, OnClosedListener {

	private SlidingMenu sm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.a);
		sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		sm.setBehindScrollScale(10f);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindWidthRes(R.dimen.behindWidth);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setSlidingEnabled(true);
		sm.setOnOpenListener(this);
		sm.setOnClosedListener(this);
	}

	@Override
	public void onOpen() {

	}

	@Override
	public void onClosed() {

	}
}
