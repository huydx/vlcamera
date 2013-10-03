package com.ktmt.vlcamera.activities.helper;

import java.io.File;

import com.ktmt.vlcamera.activities.MenuActivity;
import com.ktmt.vlcamera.common.vlCameraConstant;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class MenuActivityHelper {
	private static final String TAG = "MenuActivityHelper";

	public static void callCameraApp(MenuActivity act) {
		Uri tempImgUri = createCameraTempFile(act);
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri);
		act.startActivityForResult(cameraIntent, vlCameraConstant.CAMERA_REQUEST);
	}

	public static void clearCameraTempFile() {
		try {
			File tempDir = Environment.getExternalStorageDirectory();
			File tempImg = new File(tempDir.getAbsolutePath() + vlCameraConstant.TEMP_FILE_JPG);
			if (tempImg.exists())
				tempImg.delete();
		} catch (Exception e) {
			Log.v(TAG, "can not delete temp file");
			e.printStackTrace();
		}
	}

	public static Uri createCameraTempFile(MenuActivity activity) {
		try {
			File tempDir = Environment.getExternalStorageDirectory();
			File tempImg = new File(tempDir.getAbsolutePath() + vlCameraConstant.TEMP_FILE_JPG);
			tempImg.createNewFile();

			return Uri.fromFile(tempImg);
		} catch (Exception e) {
			Log.v(TAG, "Can't create file to take picture!");
			return Uri.EMPTY;
		}
	}

	public static Uri getCameraTempFile(MenuActivity act) {
		File tempDir = Environment.getExternalStorageDirectory();
		File tempImg = new File(tempDir.getAbsolutePath() + vlCameraConstant.TEMP_FILE_JPG);
		if (tempImg.exists())
			return Uri.fromFile(tempImg);
		return null;
	}

	public static void callGalleryApp(MenuActivity menuActivity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		menuActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				vlCameraConstant.GALLERY_REQUEST);

	}
}
