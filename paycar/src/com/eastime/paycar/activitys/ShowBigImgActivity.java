package com.eastime.paycar.activitys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.eastime.paycar.R;
import com.eastime.paycar.R.layout;
import com.eastime.paycar.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ShowBigImgActivity extends Activity {

	
	private ImageView image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//ȫ�� 
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,    
				WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_big_img);
	       
	    String path = getIntent().getStringExtra("path");
	    image = (ImageView) findViewById(R.id.img_showbi);
       	showImage(path);
	       
	}


	private void showImage(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();

       	opts.inJustDecodeBounds = true;
       	//BitmapFactory.decodeFile(imageFile, opts);
       	
			try {
				InputStream is = new FileInputStream(path);
				//opts.inSampleSize = 3;
				BitmapFactory.decodeStream(is, null, opts);
				
				opts.inSampleSize = computeSampleSize(opts, -1, 400*300);  
				opts.inJustDecodeBounds = false;
				Bitmap bm = BitmapFactory.decodeFile(path);
				image.setImageBitmap(bm);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	//λͼ�Զ����� �ڴ治����ķ���
	public static int computeSampleSize(BitmapFactory.Options options,

	        int minSideLength, int maxNumOfPixels) {

	    int initialSize = computeInitialSampleSize(options, minSideLength,

	            maxNumOfPixels);



	    int roundedSize;

	    if (initialSize <= 8) {

	        roundedSize = 1;

	        while (roundedSize < initialSize) {

	            roundedSize <<= 1;

	        }

	    } else {

	        roundedSize = (initialSize + 7) / 8 * 8;

	    }



	    return roundedSize;

	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	        int minSideLength, int maxNumOfPixels) {

	    double w = options.outWidth;

	    double h = options.outHeight;



	    int lowerBound = (maxNumOfPixels == -1) ? 1 :

	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

	    int upperBound = (minSideLength == -1) ? 128 :

	            (int) Math.min(Math.floor(w / minSideLength),

	            Math.floor(h / minSideLength));



	    if (upperBound < lowerBound) {

	        // return the larger one when there is no overlapping zone.

	        return lowerBound;

	    }

	    if ((maxNumOfPixels == -1) &&

	            (minSideLength == -1)) {

	        return 1;

	    } else if (minSideLength == -1) {

	        return lowerBound;

	    } else {

	        return upperBound;

	    }

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_big_img, menu);
		return true;
	}

}
