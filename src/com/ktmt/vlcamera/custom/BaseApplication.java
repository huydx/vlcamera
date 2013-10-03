/*
author:huydx
github:https://github.com/huydx
 */
package com.ktmt.vlcamera.custom;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

public class BaseApplication extends Application {
    private Activity mCurrentActivity = null;
    private Bitmap mRawBitmap = null;

    public Bitmap getRawBitmap() {
		return mRawBitmap;
	}

	public void setRawBitmap(Bitmap mRawBitmap) {
		this.mRawBitmap = mRawBitmap;
	}

	public void onCreate() {
        super.onCreate();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}

