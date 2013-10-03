package com.ktmt.vlcamera.activities.helper;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.ktmt.vlcamera.activities.EditActivity;
import com.ktmt.vlcamera.common.vlCameraConstant;
import com.ktmt.vlcamera.common.vlCameraBitmapUtils;
import com.ktmt.vlcamera.model.DraggableBitmap;
import com.ktmt.vlcamera.model.DraggableText;

import com.ktmt.vlcamera.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

public class EditActivityHelper {
	private static final String TAG = "EditActivityHelper";
	private static Context mContext;
	
	public EditActivityHelper(Context ctx) {
		mContext = ctx;
	}

	public void displayPreviewImage(Uri uri, EditActivity act) {
		// InputStream is;
		try {
			Boolean needRotate = true;
			Bitmap downSample = vlCameraBitmapUtils.downSampleBitmap(uri, act, needRotate);
			act.getImageView().setImageBitmap(downSample);
			act.setRawBitmap(downSample);
		} catch (Exception e) {
			Log.v(TAG, "some error occur");
			e.printStackTrace();
		}
		Log.i(TAG, "display ok");
	}

	public void popupTextEdit(final popupTextEditListener listener) {
		AlertDialog.Builder addTextDialog = new AlertDialog.Builder(mContext);

		addTextDialog.setTitle(R.string.addtext);
		
		// Set an EditText view to get user input
		final EditText input = new EditText(mContext);
		addTextDialog.setView(input);

		addTextDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				listener.onOkClick(input.getText().toString());
			}
		});

		addTextDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				return;
			}
		});

		addTextDialog.show();
	}

	public void addTextToBitmap(String text, int color) {
		EditActivity act = (EditActivity) mContext;
		DraggableText textBmp = new DraggableText(null, text, color);
		act.getImageView().addOverlayBitmap(textBmp, (float) 1.0);
	}

	public interface popupTextEditListener {
		void onOkClick(String tweet);

		void onCancelClick();
	}

	public void flipActiveBitmap() {
		EditActivity act = (EditActivity) mContext;
		act.getImageView().flipActiveBitmap();
		act.getImageView().invalidate();

	}

	public Uri getRawTempFile() throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		File tempImg = new File(tempDir.getAbsolutePath() + vlCameraConstant.TEMP_FILE_JPG);
		if (!tempImg.exists())
			throw new Exception("file not exist");

		return Uri.fromFile(tempImg);
	}

	@SuppressLint("NewApi")
	public Bitmap saveCurrentBitmap() {
		EditActivity act = (EditActivity) mContext;

		Bitmap origRawImage = act.getRawBitmap();
		//copy to mutable
		Bitmap rawImage = origRawImage.copy(Bitmap.Config.ARGB_8888, true);
		if (rawImage == null)
			return null;

		Canvas canvas = new Canvas(rawImage);

		// get scale factor
		RectF scaledImg = act.getImageView().getInnerBitmapSize();
		float scale = rawImage.getWidth() / scaledImg.width();

		List<DraggableBitmap> stampList = act.getImageView().getOverlayList();
		if (stampList.size() > 0) {
			Enumeration<DraggableBitmap> e = Collections.enumeration(stampList);
			while (e.hasMoreElements()) {
				DraggableBitmap dBmp = (DraggableBitmap) e.nextElement();

				Matrix finalMtx = new Matrix();

				// calculate margin and move back
				Matrix marginMtx = dBmp.getMarginMatrix();
				float[] moveArr = new float[9];
				marginMtx.getValues(moveArr);
				float x = -(moveArr[2]);
				float y = -(moveArr[5]);
				Matrix moveBackMtx = new Matrix();
				moveBackMtx.postTranslate(x, y);

				// current manipulate matrix (rotate, zoom, move..)
				Matrix manipulateMtx = dBmp.getCurrentMatrix();
				Matrix scaleMtx = new Matrix();

				// scale to original size
				scaleMtx.postScale(scale, scale, 0, 0);

				manipulateMtx = (manipulateMtx == null) ? new Matrix() : manipulateMtx;
				finalMtx.postConcat(manipulateMtx);
				finalMtx.postConcat(moveBackMtx);
				finalMtx.postConcat(scaleMtx);
				canvas.drawBitmap(dBmp.mBitmap, finalMtx, null);
			}
		}

		return rawImage;
	}
}
