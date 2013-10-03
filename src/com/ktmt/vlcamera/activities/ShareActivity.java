package com.ktmt.vlcamera.activities;

import com.ktmt.vlcamera.R;
import com.ktmt.vlcamera.activities.helper.ShareActivityHelper;
import com.ktmt.vlcamera.custom.BaseActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShareActivity extends BaseActivity implements
		View.OnClickListener {
	
	private static final String TAG = "ShareActivity";
	
	private ImageButton mFacebookShare;
	private ImageButton mSaveToDir;
	private Bitmap mToSaveBmp;
	private ShareActivityHelper mActivityHelper;
	
	/** default stuffs **/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
				
		setContentView(R.layout.activity_share);
		ImageView view = (ImageView) findViewById(R.id.share_preview);
		mToSaveBmp = getBaseApplication().getRawBitmap();
		mActivityHelper = new ShareActivityHelper(this, mToSaveBmp);
		view.setImageBitmap(mToSaveBmp);
		
		mFacebookShare = (ImageButton) findViewById(R.id.btn_share_facebook);
		mSaveToDir = (ImageButton) findViewById(R.id.btn_saveto_dir);
		
		mFacebookShare.setOnClickListener(this);
		mSaveToDir.setOnClickListener(this);
	}

	/** event handle stuffs **/
	@Override
	public void onClick(View view) {
		int id = view.getId();
		
		if (id == R.id.btn_share_facebook) {
			mActivityHelper.shareToFacebook();
		} else if (id == R.id.btn_saveto_dir) {
			if (mToSaveBmp == null) { 
				Log.v(TAG, "save bitmap is null");
				finish();
			}
			mActivityHelper.saveToDir();
			finish();
		} else {
			finish();
		}
	}
}
