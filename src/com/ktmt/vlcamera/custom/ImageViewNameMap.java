package com.ktmt.vlcamera.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewNameMap extends ImageView {
	private String name;
	
	public ImageViewNameMap(Context context) {
		super(context);
	}
	
	public ImageViewNameMap(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
	
    public ImageViewNameMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
