package com.ktmt.vlcamera.activities.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ktmt.vlcamera.activities.ShareActivity;
import com.ktmt.vlcamera.common.vlCameraConstant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;

public class ShareActivityHelper {
	private String TAG = "ShareActivityHelper";
	private Context mContext = null;
	boolean mIsOnSharingFb;
	Bitmap mBitmapToShare;

	public ShareActivityHelper(Context ctx, Bitmap bmp) {
		mContext = ctx;
		mBitmapToShare = bmp;
	}

	@SuppressLint("SimpleDateFormat")
	public String saveToDir() {
		ShareActivity act = (ShareActivity) mContext;
		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				vlCameraConstant.APP_NAME);

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return "";
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ vlCameraConstant.APP_NAME + timeStamp + ".jpg");

		try {
			FileOutputStream stream = new FileOutputStream(mediaFile);
			mBitmapToShare.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		} catch (IOException exception) {
			Log.w(TAG, "IOException during saving bitmap", exception);
			return "";
		}

		if (mediaFile.getPath() != null) {
			MediaScannerConnection.scanFile(act, new String[] { mediaFile.toString() },
					new String[] { "image/jpeg" }, null);
			return mediaFile.getPath();
		}
		return "";
	}

	public void shareToFacebook() {
		String path = Images.Media.insertImage(mContext.getContentResolver(), mBitmapToShare, "vlcamera", null);
		Uri screenshotUri = Uri.parse(path);

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		sharingIntent.setType("image/png");
		mContext.startActivity(sharingIntent);
	}
}
