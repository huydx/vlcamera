package com.ktmt.vlcamera.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

public class vlCameraBitmapUtils {
	private static final String TAG = "vlCameraBitmapUtils";

	/** bitmap downsample utils **/
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	@SuppressLint("NewApi")
	public static Bitmap decodeSampledBitmap(Uri uri, int reqWidth,
			int reqHeight, Activity act) {

		// First decode with inJustDecodeBounds=true to check dimensions
		InputStream is;
		try {
			is = act.getApplicationContext().getContentResolver()
					.openInputStream(uri);
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);
			is.close(); //consider use is.mark and is.reset instead [TODO]
			
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			//open input stream again
			is = act.getApplicationContext().getContentResolver()
					.openInputStream(uri);
			options.inJustDecodeBounds = false;
			Bitmap ret = BitmapFactory.decodeStream(is, null, options);
			is.close();
			return ret;
		} catch (FileNotFoundException e) {
			Log.v(TAG, "File not found:" + uri.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.v(TAG, "I/O exception with file:" + uri.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap downSampleBitmap(Uri uri, Activity act, Boolean needRotate) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		Resources r = act.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				50, r.getDisplayMetrics()); // 50: magic num
		int targetWidth = displaymetrics.heightPixels;
		int targetHeight = displaymetrics.widthPixels - px;
		
		Bitmap resizedBitmap = decodeSampledBitmap(uri, targetWidth, targetHeight, act);
		Bitmap returnBitmap = null;
		ExifInterface exif;
		try {
			float degree = 0;
			exif = new ExifInterface(uri.toString());
			int orient = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			if (resizedBitmap != null && needRotate) {
				degree = getDegree(orient);
				if (degree != 0) {
					returnBitmap = createRotatedBitmap(resizedBitmap, degree);
				}
				returnBitmap = returnBitmap == null ? resizedBitmap : returnBitmap;
			}
		} catch (IOException e) {
			Log.v(TAG, "not found file at downsample");
			e.printStackTrace();
		}
		return returnBitmap;
	}

	public static float getDegree(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)  return 180;  
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) return 270;  
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) return 0; 
		
	    return 90;  
	}

	public static Bitmap createRotatedBitmap(Bitmap bm, float degree) {
		Bitmap bitmap = null;
		if (degree != 0) {
			Matrix matrix = new Matrix();
			matrix.preRotate(degree);
			bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		}

		return bitmap;
	}

	/** other utils ***/

}
