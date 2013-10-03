/*
author:huydx
github:https://github.com/huydx
*/
package com.ktmt.vlcamera.model;

import android.graphics.Matrix;

public class BitmapOperationMap {
    DraggableBitmap mBitmap;
    Matrix mOperationMtx;
    OPERATION mOpt;
    
    public enum OPERATION {
        NEW, ADD, DELETE
    }
    
    //map between a (ref to) bitmap and (ref to) matrix
    public BitmapOperationMap(DraggableBitmap bmp, Matrix mtx, OPERATION op) {
        mBitmap = bmp;
        mOperationMtx = mtx;
        mOpt = op;
    }
    
    public DraggableBitmap getDraggableBitmap() { return mBitmap; }
    public Matrix getOperationMatrix() { return mOperationMtx; }
    public OPERATION getOption() { return mOpt; }
}
