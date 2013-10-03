package com.ktmt.vlcamera.activities;

import com.ktmt.vlcamera.R;
import com.ktmt.vlcamera.activities.helper.EditActivityHelper;
import com.ktmt.vlcamera.common.vlCameraConstant;
import com.ktmt.vlcamera.custom.BaseActivity;
import com.ktmt.vlcamera.custom.DraggableImageView;
import com.ktmt.vlcamera.model.DraggableBitmap;

import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

public class EditActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = "EditActivity";
	private DraggableImageView mImageView;
	private Bitmap mRawBitmap;
	private int mCurrentTextColor;

	ImageButton mButtonAdd;
	ImageButton mButtonDelete;
	ImageButton mButtonAddText;
	ImageButton mButtonFlip;
	ImageButton mButtonFinish;
	ImageButton mButtonBack;
	
	EditActivityHelper mActivityHelper;
	
	/** listerner **/
	EditActivityHelper.popupTextEditListener addtextListener = 
			new EditActivityHelper.popupTextEditListener() {
		@Override
        public void onOkClick(String addText) {
			if (addText.length() == 0) return;
			mActivityHelper.addTextToBitmap(addText, getCurrentTextColor());
        }

        @Override
        public void onCancelClick() {}
	};

	/*** default stuffs ***/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
		mActivityHelper = new EditActivityHelper(this);

		mImageView = (DraggableImageView) findViewById(R.id.edit_imageview);
		mButtonAdd = (ImageButton) findViewById(R.id.btn_add);
		mButtonDelete = (ImageButton) findViewById(R.id.btn_delete);
		mButtonFlip = (ImageButton) findViewById(R.id.btn_flip);
		mButtonFinish = (ImageButton) findViewById(R.id.btn_finish); 
		mButtonBack = (ImageButton) findViewById(R.id.btn_back);
		
		mButtonAdd.setOnClickListener(this);
		mButtonDelete.setOnClickListener(this);
		mButtonFlip.setOnClickListener(this);
		mButtonFinish.setOnClickListener(this);
		mButtonBack.setOnClickListener(this);
		
		Uri selectedImageUri;

		final Intent intent = getIntent();
		if (intent != null && intent.getData() != null) {
			selectedImageUri = intent.getData();
			mActivityHelper.displayPreviewImage(selectedImageUri, this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_add) {
			Intent stampIntent = new Intent(this, StampChooseActivity.class);
			startActivityForResult(stampIntent, vlCameraConstant.STAMP_REQUEST);
		} else if (id == R.id.btn_flip) {
			mActivityHelper.flipActiveBitmap();
		} else if (id == R.id.btn_finish) {
			Bitmap bmpToSave = mActivityHelper.saveCurrentBitmap();
			this.getBaseApplication().setRawBitmap(bmpToSave);
			Intent shareIntent = new Intent(this, ShareActivity.class);
			startActivity(shareIntent);
		} else if (id == R.id.btn_delete){
			getImageView().deleteActiveBitmap();
			getImageView().invalidate();
		} else if (id == R.id.btn_back) {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case vlCameraConstant.STAMP_REQUEST:
			try {
				if (resultCode == vlCameraConstant.RESULT_CODE_ADDBITMAP) {
					DraggableBitmap stamp = new DraggableBitmap((Bitmap) data.getExtras().get("data"));			
					getImageView().addOverlayBitmap(stamp, (float)1.0);
					getImageView().invalidate();
				} else if (resultCode == vlCameraConstant.RESULT_CODE_ADDTEXT) {
					int color = (Integer) data.getExtras().get("color");
					setCurrentTextColor(color);
					mActivityHelper.popupTextEdit(addtextListener);
				}
				break;
			} catch (Exception e) {
				Log.v(TAG, "get extras error");
				e.printStackTrace();
			}
		default:
			break;
		}
	}

	/*** get/set stuffs ***/
	public DraggableImageView getImageView() {
		return mImageView;
	}

	public Bitmap getRawBitmap() {
		return mRawBitmap;
	}

	public void setRawBitmap(Bitmap mRawBitmap) {
		this.mRawBitmap = mRawBitmap;
	}
	
	
	public int getCurrentTextColor() {
		return mCurrentTextColor;
	}

	public void setCurrentTextColor(int mCurrentTextColor) {
		this.mCurrentTextColor = mCurrentTextColor;
	}

}
