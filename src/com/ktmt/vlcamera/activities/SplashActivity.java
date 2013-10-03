package com.ktmt.vlcamera.activities;

import com.ktmt.vlcamera.R;
import com.ktmt.vlcamera.custom.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, MenuActivity.class));
				finish();
			}
		}, 1500);
	}
}
