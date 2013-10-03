/*
author:huydx
github:https://github.com/huydx
 */
package com.ktmt.vlcamera.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class DraggableBitmap {
	private boolean activated;
	
	public Bitmap mBitmap;
	private Matrix marginMatrix;
	private Matrix currentMatrix;
	private Matrix savedMatrix;
	private int mId;

	private boolean touched;

	public DraggableBitmap(Bitmap b) {
		mId = -1;
		currentMatrix = null;
		savedMatrix = null;
		mBitmap = b;
		activated = false;
	}

	public void setCurrentMatrix(Matrix m) {
		this.currentMatrix = null;
		this.currentMatrix = new Matrix(m);
	}

	public void setSavedMatrix(Matrix m) {
		this.savedMatrix = null;
		this.savedMatrix = new Matrix(m);
	}

	public Matrix getCurrentMatrix() {
		return this.currentMatrix;
	}

	public Matrix getSavedMatrix() {
		return this.savedMatrix;
	}

	public void activate() {
		this.activated = true;
	}

	public void deActivate() {
		this.activated = false;
	}

	public boolean isActivate() {
		return activated;
	}

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public Matrix getMarginMatrix() {
		return marginMatrix;
	}

	public void setMarginMatrix(Matrix marginMatrix) {
		this.marginMatrix = null;
		this.marginMatrix = new Matrix(marginMatrix);
	}
	
	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

}
