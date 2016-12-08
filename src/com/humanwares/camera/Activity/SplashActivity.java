package com.humanwares.camera.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.humanwares.camera.R;
import com.humanwares.camera.utils.PrefUtil;
import com.humanwares.camera.utils.PrefUtil.PrefType;

public class SplashActivity extends Activity {

	private static final int REQUEST_GUIDE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		boolean isShownGuide = PrefUtil.getInstance(PrefType.PREF_GLOBAL).getBoolean(PrefUtil.Keys.IS_SHOWN_GUIDE, false);
		if(isShownGuide) {
			// 이미 가이드를 보았음
			initUI();
		} else {
			startActivityForResult(GuideActivity.getIntent(this), REQUEST_GUIDE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_GUIDE) {
			PrefUtil.getInstance(PrefType.PREF_GLOBAL).putValue(PrefUtil.Keys.IS_SHOWN_GUIDE, true);
			Intent i = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(i);
			finish();
		}
	}

	private void initUI() {
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				finish();      
			}
		}, 3000);		
	}
	
}
