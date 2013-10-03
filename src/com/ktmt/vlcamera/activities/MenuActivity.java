package com.ktmt.vlcamera.activities;

import com.ktmt.vlcamera.R;
import com.ktmt.vlcamera.activities.helper.*;
import com.ktmt.vlcamera.common.*;
import com.ktmt.vlcamera.custom.BaseActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

public class MenuActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = "MenuActivity";

	ImageButton btnTakePhoto;
	ImageButton btnFromDir;

	/**** default stuffs ****/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		btnTakePhoto = (ImageButton) findViewById(R.id.btn_take_photo);
		btnFromDir = (ImageButton) findViewById(R.id.btn_from_dir);

		btnTakePhoto.setOnClickListener(this);
		btnFromDir.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_take_photo) {
			MenuActivityHelper.clearCameraTempFile();
			MenuActivityHelper.callCameraApp(this);
		} else if (id == R.id.btn_from_dir) {
			MenuActivityHelper.clearCameraTempFile();
			MenuActivityHelper.callGalleryApp(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case vlCameraConstant.CAMERA_REQUEST:
			try {
				if (resultCode == RESULT_CANCELED) return;
				Uri cameraTempUri = MenuActivityHelper.getCameraTempFile(this); 
				if (cameraTempUri  != null) {
					Intent editIntent = new Intent(this, EditActivity.class);
					editIntent.setData(cameraTempUri);
					startActivity(editIntent);
					overridePendingTransition(0, 0);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				Log.v(TAG, "Can't create file to take picture!");
			}
			break;
		case vlCameraConstant.GALLERY_REQUEST:
			if (data != null && data.getData() != null) {
                data.setClass(this, EditActivity.class);
                startActivity(data);
            }
            break;
		default:
			// Do nothing
		}
	}

	/**** get/set stuffs ****/
}
