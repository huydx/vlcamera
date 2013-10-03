package com.ktmt.vlcamera.activities.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.ktmt.vlcamera.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ktmt.vlcamera.activities.StampChooseActivity;
import com.ktmt.vlcamera.common.vlCameraConstant;
import com.ktmt.vlcamera.custom.ImageViewNameMap;

public class StampChooseActivityHelper {
	private final String TAG = "StampChooseActivityHelper";
	private AssetManager assMngr;
	private ArrayList<String> stampTabFolderList = new ArrayList<String>();
	private Context mContext;
	private int mCurrentTabIndex;

	public StampChooseActivityHelper(Context ctx) {
		mCurrentTabIndex = -1;
		mContext = ctx;
	}

	// inflate data to view
	@SuppressLint("UseSparseArrays")
	public void inflateView() {
		StampChooseActivity act = (StampChooseActivity) mContext;
		try {
			assMngr = act.getAssets();
			String[] folders = assMngr.list(vlCameraConstant.STAMP_FOLDER);
			for (String f : folders) {
				inflateStampTabView(f);
				stampTabFolderList.add(f);
			}

		} catch (IOException e) {
			Log.v(TAG, "can not inflate asset to view");
			e.printStackTrace();
		}

	}

	// invalidate list when switch tab
	public void switchStampTab(View clickView) {
		StampChooseActivity act = (StampChooseActivity) mContext;
		int index = act.getStampTab().indexOfChild((View) clickView.getParent());
		mCurrentTabIndex = index;
		String folder = stampTabFolderList.get(index);
		try {
			inflateStampListView(folder);
		} catch (IOException e) {
			Log.v(TAG, "can not inflate asset to view");
			e.printStackTrace();
		}
		return;

	}

	/**
	 * utils stuffs
	 * 
	 * @throws IOException
	 **/
	private void inflateStampListView(String folderName) throws IOException {
		StampChooseActivity act = (StampChooseActivity) mContext;
		String folderExpandPath = vlCameraConstant.STAMP_FOLDER + "/" + folderName;
		String[] files = assMngr.list(folderExpandPath);

		act.getStampList().removeAllViews();

		for (int i = 0; i < files.length - 1; i++) {
			if (!files[i].matches("icon.*")) {
				View row = act.getLayoutInflater().inflate(R.layout.row_stamp_list, null);
				ImageViewNameMap image1 = (ImageViewNameMap) row.findViewById(R.id.stamp_view_row1);
				ImageViewNameMap image2 = (ImageViewNameMap) row.findViewById(R.id.stamp_view_row2);

				InputStream inputStream = assMngr
						.open(folderExpandPath + File.separator + files[i]);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				image1.setImageBitmap(bitmap);
				image1.setOnClickListener(act);
				image1.setName(files[i]);
				inputStream.close();

				if (i < files.length - 2) {
					i++;
					inputStream = assMngr.open(folderExpandPath + File.separator + files[i]);
					bitmap = BitmapFactory.decodeStream(inputStream);
					image2.setImageBitmap(bitmap);
					image2.setOnClickListener(act);
					image2.setName(files[i]);
					inputStream.close();
				}

				act.getStampList().addView(row);
				act.getStampList().invalidate();
			}
		}

	}

	// process a stamp folder (add icon to tab, add list)
	private void inflateStampTabView(String folderName) throws IOException {
		StampChooseActivity act = (StampChooseActivity) mContext;
		String folderExpandPath = vlCameraConstant.STAMP_FOLDER + File.separator + folderName;

		String[] files = assMngr.list(folderExpandPath);
		for (String file : files) {
			if (file.matches("icon.*")) {
				View item = act.getLayoutInflater().inflate(R.layout.item_stamp_tab, null);
				ImageView iconView = (ImageView) item.findViewById(R.id.stamp_icon);
				InputStream inputStream = assMngr.open(folderExpandPath + "/" + file);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();

				iconView.setImageBitmap(bitmap);
				iconView.setOnClickListener(act);

				act.getStampTab().addView(item);
			}
		}
	}

	/* get/set stuffs */
	public int getmCurrentTabIndex() {
		return mCurrentTabIndex;
	}

	public void setmCurrentTabIndex(int mCurrentTabIndex) {
		this.mCurrentTabIndex = mCurrentTabIndex;
	}

	public static int convertNameToColor(String name) {
		name = name.substring(0, name.lastIndexOf('.'));
		return Color.parseColor(name);
	}
}