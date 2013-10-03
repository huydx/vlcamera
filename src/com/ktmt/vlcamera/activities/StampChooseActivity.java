package com.ktmt.vlcamera.activities;

import com.ktmt.vlcamera.R;
import com.ktmt.vlcamera.activities.helper.StampChooseActivityHelper;
import com.ktmt.vlcamera.common.vlCameraConstant;
import com.ktmt.vlcamera.custom.BaseActivity;
import com.ktmt.vlcamera.custom.ImageViewNameMap;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;

public class StampChooseActivity extends BaseActivity implements View.OnClickListener {
	LinearLayout mStampTab;
	LinearLayout mStampList;
	StampChooseActivityHelper mActivityHelper;

	/** default stuffs **/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stamp);
		mActivityHelper = new StampChooseActivityHelper(this);

		mStampTab = (LinearLayout) findViewById(R.id.stamp_tab_layout);
		mStampList = (LinearLayout) findViewById(R.id.stamp_list_layout);
		mActivityHelper.inflateView();
	}

	/** event handle stuffs **/
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.stamp_icon) {
			mActivityHelper.switchStampTab(view);
		} else if (id == R.id.stamp_view_row1 || id == R.id.stamp_view_row2) {
			Intent returnIntent = new Intent();
			ImageViewNameMap image = (ImageViewNameMap) view;
			if (image.getName().startsWith("#")) {
				int color = StampChooseActivityHelper.convertNameToColor(image.getName());
				returnIntent.putExtra("color", color);
				setResult(vlCameraConstant.RESULT_CODE_ADDTEXT, returnIntent);
			} else {
				returnIntent.putExtra("data", ((BitmapDrawable) image.getDrawable()).getBitmap());
				setResult(vlCameraConstant.RESULT_CODE_ADDBITMAP, returnIntent);
			}		
			finish();
		} else {
			finish();
		}
	}

	/** get/set stuffs **/
	public LinearLayout getStampTab() {
		return mStampTab;
	}

	public void setStampTab(LinearLayout mStampTab) {
		this.mStampTab = mStampTab;
	}

	public LinearLayout getStampList() {
		return mStampList;
	}

	public void setStampList(LinearLayout mStampList) {
		this.mStampList = mStampList;
	}
}
