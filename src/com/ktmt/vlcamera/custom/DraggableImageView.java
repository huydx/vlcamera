/*
author:huydx
github:https://github.com/huydx
 */
package com.ktmt.vlcamera.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import com.ktmt.vlcamera.model.BitmapOperationMap;
import com.ktmt.vlcamera.model.DraggableBitmap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;

public class DraggableImageView extends ImageView {

	// some private variable use for detect multi touch
	public enum EDITMODE {
		NONE, DRAG, ZOOM, ROTATE
	}

	private static final String TAG = "Draggable Bitmap";

	private boolean mDrawOpacityBackground = false;
	private Paint mPaint = new Paint();
	private DraggableBitmap mActiveBitmap = null;
	private RectF mInnerImageBounds = null;
	private Stack<BitmapOperationMap> mOperationStack = new Stack<BitmapOperationMap>();

	// list of stamp bitmaps
	private List<DraggableBitmap> mOverlayBitmaps;

	// constructors
	public DraggableImageView(Context context) {
		super(context);
		initMembers();
		this.setOnTouchListener(touchListener);
	}

	public DraggableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMembers();
		this.setOnTouchListener(touchListener);
	}

	private void initMembers() {
		mOverlayBitmaps = new ArrayList<DraggableBitmap>();
	}

	// listeners
	private OnTouchListener touchListener = new OnTouchListener() {
		// to get mode [drag, zoom, rotate]
		private EDITMODE mEditMode = EDITMODE.NONE;

		private float[] mLastEvent;
		private PointF mStart = new PointF();
		private PointF mMid = new PointF();
		private float mOldDistance;
		private float mNewRotation = 0f;
		private float mDist = 0f;

		// this variable use to deal with android odd touch behavior (MOVE -> UP
		// -> MOVE -> UP)
		private boolean touchMoveEndChecker = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			// switch finger events
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case (MotionEvent.ACTION_DOWN):
				touchMoveEndChecker = true;
				mDrawOpacityBackground = true;
				int activebmpIdx = getActiveBitmap(event.getX(), event.getY());

				if (activebmpIdx != -1) {
					mActiveBitmap = mOverlayBitmaps.get(activebmpIdx);
					rearrangeOverlayList();
				}
				else {
					mActiveBitmap = null;
					break;
				}
				mLastEvent = null;
				mEditMode = EDITMODE.DRAG;
				mStart.set(event.getX(), event.getY());

				if (mActiveBitmap != null) {
					mActiveBitmap.setSavedMatrix(mActiveBitmap.getCurrentMatrix());
				}
				break;

			case (MotionEvent.ACTION_POINTER_DOWN):
				touchMoveEndChecker = false;
				mDrawOpacityBackground = true;
				if (mActiveBitmap != null) {
					mOldDistance = spacing(event);
					if (mOldDistance > 10f) {
						mActiveBitmap.setSavedMatrix(mActiveBitmap.getCurrentMatrix());
						midPoint(mMid, event);
						mEditMode = EDITMODE.ZOOM;
					}

					mLastEvent = new float[4];
					mLastEvent[0] = event.getX(0);
					mLastEvent[1] = event.getX(1);
					mLastEvent[2] = event.getY(0);
					mLastEvent[3] = event.getY(1);

					mDist = rotation(event);
				}
				break;

			case (MotionEvent.ACTION_POINTER_UP):
				mEditMode = EDITMODE.NONE;
				break;

			case (MotionEvent.ACTION_MOVE):
				touchMoveEndChecker = false;
				mDrawOpacityBackground = true;

				if (mActiveBitmap != null) {
					if (mEditMode == EDITMODE.DRAG) {
						mActiveBitmap.setCurrentMatrix(mActiveBitmap.getSavedMatrix());
						mActiveBitmap.getCurrentMatrix().postTranslate(event.getX() - mStart.x,
								event.getY() - mStart.y);
					} else if (mEditMode == EDITMODE.ZOOM && event.getPointerCount() == 2) {
						float newDistance = spacing(event);
						mActiveBitmap.setCurrentMatrix(mActiveBitmap.getSavedMatrix());
						if (newDistance > 10f) {
							float scale = newDistance / mOldDistance;
							mActiveBitmap.getCurrentMatrix()
									.postScale(scale, scale, mMid.x, mMid.y);
						}

						if (mLastEvent != null) {
							mNewRotation = rotation(event);
							float r = mNewRotation - mDist;
							RectF rec = new RectF(0, 0, mActiveBitmap.mBitmap.getWidth(),
									mActiveBitmap.mBitmap.getHeight());
							mActiveBitmap.getCurrentMatrix().mapRect(rec);
							mActiveBitmap.getCurrentMatrix().postRotate(r,
									rec.left + rec.width() / 2, rec.top + rec.height() / 2);
						}
					}

				}

			case (MotionEvent.ACTION_UP):
				if (touchMoveEndChecker) { // means 2 continuous ACTION_UP, or
											// real finger up after moving
					mDrawOpacityBackground = false;
					if (mActiveBitmap != null) {
						// push a map to bitmap and clone of current matrix
						mOperationStack
								.push(new BitmapOperationMap(mActiveBitmap, new Matrix(
										mActiveBitmap.getCurrentMatrix()),
										BitmapOperationMap.OPERATION.ADD));
						mActiveBitmap.deActivate();
					}
				}
				touchMoveEndChecker = true;
			default:
				break;
			}

			invalidate();
			return true;
		}

	};

	public void addOverlayBitmap(DraggableBitmap dBitmap, float scale) {
		Matrix marginMtx = new Matrix();

		marginMtx.postTranslate(mInnerImageBounds.left, mInnerImageBounds.top);
		dBitmap.setMarginMatrix(marginMtx);

		Matrix curMtx = new Matrix();
		curMtx.postConcat(marginMtx);

		dBitmap.setCurrentMatrix(curMtx);
		mOperationStack
				.push(new BitmapOperationMap(dBitmap, null, BitmapOperationMap.OPERATION.NEW));
		mOperationStack.push(new BitmapOperationMap(dBitmap, dBitmap.getCurrentMatrix(),
				BitmapOperationMap.OPERATION.ADD));
		mOverlayBitmaps.add(dBitmap);

	}

	private int getActiveBitmap(float event_x, float event_y) {
		int size = mOverlayBitmaps.size();
		int retidx = -1;
		DraggableBitmap retBmp = null;
		// search for all bitmap to find closest to finger
		for (int i = 0; i < size; i++) {
			DraggableBitmap dBmp = mOverlayBitmaps.get(i);
			dBmp.deActivate();
			float bmp_x = 0;
			float bmp_y = 0;
			RectF r = new RectF(0, 0, dBmp.mBitmap.getWidth(), dBmp.mBitmap.getHeight());
			Matrix mtx = dBmp.getCurrentMatrix() == null ? dBmp.getMarginMatrix() : dBmp
					.getCurrentMatrix();

			mtx.mapRect(r);
			bmp_x = r.left;
			bmp_y = r.top;

			if (event_x >= bmp_x && event_x < (bmp_x + r.width()) && event_y >= bmp_y
					&& event_y < (bmp_y + r.height())) {
				retBmp = dBmp;
				retidx = i;
			}
		}
		if (retBmp != null) {
			if (!retBmp.isTouched()) {
				retBmp.setTouched(true);
			}
			retBmp.activate();
		}
		return retidx;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double rad = Math.atan2(delta_y, delta_x);

		return (float) Math.toDegrees(rad);
	}

	public List<DraggableBitmap> getOverlayList() {
		return mOverlayBitmaps;
	}

	public void undo() {
		if (!mOperationStack.empty()) {
			BitmapOperationMap prev = mOperationStack.pop();
			if (!mOperationStack.empty()) { // current stack is final operation
				prev = mOperationStack.peek();
			}
			DraggableBitmap bmp = prev.getDraggableBitmap();
			Matrix mtx = prev.getOperationMatrix();

			switch (prev.getOption()) {
			case NEW: // if action is create new, then delete
				mOverlayBitmaps.remove(bmp);
				break;
			case ADD:
				bmp.setCurrentMatrix(mtx);
				break;
			case DELETE: // not implement yet
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) { // [TODO] khi xoay man hinh error
		super.onDraw(canvas);
		RectF bitmapRect = getInnerBitmapSize();
		if (bitmapRect == null) return;
		mInnerImageBounds = bitmapRect;
		canvas.clipRect(bitmapRect);

		// loop to draw all bitmap
		Enumeration<DraggableBitmap> e = Collections.enumeration(mOverlayBitmaps);
		while (e.hasMoreElements()) {
			DraggableBitmap dBmp = (DraggableBitmap) e.nextElement();
			if (true) {
				if (dBmp.getCurrentMatrix() != null) {
					canvas.drawBitmap(dBmp.mBitmap, dBmp.getCurrentMatrix(), null);
					RectF r = getStampBounding(dBmp);
					if (mDrawOpacityBackground && dBmp == mActiveBitmap) {
						mPaint.setColor(0x00000000);
						mPaint.setStyle(Style.FILL);
						mPaint.setAlpha(20);
						canvas.drawRect(r, mPaint);

					}
				}
			}
		}
	}

	public RectF getInnerBitmapSize() {
		RectF bitmapRect = new RectF();
		if (this.getDrawable() == null) return null;
		bitmapRect.right = this.getDrawable().getIntrinsicWidth();
		bitmapRect.bottom = this.getDrawable().getIntrinsicHeight();

		Matrix m = this.getImageMatrix();
		m.mapRect(bitmapRect);
		return bitmapRect;
	}

	private RectF getStampBounding(DraggableBitmap bmp) {
		if (bmp.mBitmap == null) return null;
		RectF r = new RectF(0, 0, bmp.mBitmap.getWidth(), bmp.mBitmap.getHeight());
		bmp.getCurrentMatrix().mapRect(r);
		return r;
	}

	public void deleteActiveBitmap() {
		if (mActiveBitmap == null) return;
		mOverlayBitmaps.remove(mActiveBitmap);
	}

	public void flipActiveBitmap() {
		try {
			Matrix flipHorizontalMtx = new Matrix();
			flipHorizontalMtx.setScale(-1, 1);
			flipHorizontalMtx.postTranslate((float) (mActiveBitmap.mBitmap.getWidth()), (float) 0);
			Matrix mtx = mActiveBitmap.getCurrentMatrix();
			mtx.preConcat(flipHorizontalMtx);

			mActiveBitmap.setCurrentMatrix(mtx);
		} catch (NullPointerException e) {
			Log.v(TAG, "active bitmap is null");
		} catch (Exception e) {
			Log.v(TAG, "error ocurred");
		}
	}
	
	public void rearrangeOverlayList() {
		int idx = mOverlayBitmaps.indexOf(mActiveBitmap);
		mOverlayBitmaps.add(mActiveBitmap);
		mOverlayBitmaps.remove(idx);
	}
 }
