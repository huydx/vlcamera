package com.ktmt.vlcamera.model;

import com.ktmt.vlcamera.common.vlCameraConstant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DraggableText extends DraggableBitmap {
	private String mText;
	private int mStartX;
	private int mStartY;
	private int mTextSize;
	private Paint mPaint;
	 
	public DraggableText(Bitmap b, String text, int color) {
		super(b);
		mText = text;
		mTextSize = (vlCameraConstant.TEXT_BITMAP_SIZE * vlCameraConstant.TEXT_BITMAP_SCALE);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(color);
		mPaint.setTextSize(mTextSize);
		mPaint.setTypeface(vlCameraConstant.TEXT_BITMAP_FONT);

		int[] size = calculateBitmapsize(text);
		int height = size[1];
		int width = size[0];

		mStartX = 10;
		mStartY = height / 2;
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);

		drawTextByLine(canvas, mPaint, text);

	}

	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	/* util stuffs */
	private int[] calculateBitmapsize(String text) {
		int[] ret = new int[2];

		String[] lines = text.split("\\n+");
		int height = lines.length * (mTextSize + vlCameraConstant.TEXT_BITMAP_LINEMARGIN);
		int width = 0;
		for (String line : lines) {
			int measureWidth = (int) mPaint.measureText(line)
					+ vlCameraConstant.TEXT_BITMAP_LINEMARGIN;
			width = measureWidth > width ? measureWidth : width;
		}
		ret[0] = width;
		ret[1] = height;
		return ret;
	}

	private void drawTextByLine(Canvas canvas, Paint paint, String text) {
		String[] lines = text.split("\\n+");
		canvas.drawText(lines[0], mStartX, mStartY, paint);
		float nextLine = 0;
		for (int count = 0; count < lines.length; count++) {
			if (count == 0)
				continue;
			nextLine = count * vlCameraConstant.TEXT_BITMAP_LINEMARGIN + mTextSize;
			canvas.drawText(lines[count], mStartX, mStartY + nextLine, paint);
		}
		return;
	}

}
