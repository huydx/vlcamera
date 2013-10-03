package com.ktmt.vlcamera.common;

import android.graphics.Typeface;

enum TEXT_COLOR {
	BLUE, RED, WHITE, BLACK, BROWN
};

public class vlCameraConstant {
	public static final String APP_NAME = "vlcamera";
	
	public static final int CAMERA_REQUEST = 200‚P;
	public static final int STAMP_REQUEST = 2002;
	public static final int GALLERY_REQUEST = 2003;
	
	public static final int RESULT_CODE_ADDTEXT = -2;
	public static final int RESULT_CODE_ADDBITMAP = -3;
	
    public static final String TEMP_FILE_JPG = "/tmp_file.jpg";
    public static final String STAMP_FOLDER = "stamp";

    public static final int TEXT_BITMAP_WIDTH = 300;
    public static final int TEXT_BITMAP_HEIGHT = 300;
    public static final int TEXT_BITMAP_LINEMARGIN = 20;
    public static final int TEXT_BITMAP_SIZE = 12;
    public static final int TEXT_BITMAP_SCALE = 5;
    public static final Typeface TEXT_BITMAP_FONT = Typeface.SANS_SERIF;

}
