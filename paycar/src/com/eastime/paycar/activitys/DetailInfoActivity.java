package com.eastime.paycar.activitys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface.OnClickListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastime.paycar.R;
import com.eastime.paycar.bean.YunDanItem;
import com.eastime.paycar.services.UploadYundan;
import com.eastime.paycar.util.AppContext;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;

public class DetailInfoActivity extends Activity {

	private YunDanItem item = new YunDanItem();
	private DBHelper db;
	private Handler handler;
	NotificationManager nm = null;
	//view
	private TextView danhao;
	private TextView status;
	private TextView date;
	private TextView date2;
	private TextView describe;
	private ImageView image;
	private Bitmap bm;
	private Button uploadbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detailinfo);
		//获取 id
		int id = getIntent().getIntExtra("id", -1);
		if(-1 == id){
			Toast.makeText(DetailInfoActivity.this, "获取失败", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		danhao = (TextView) findViewById(R.id.danhao_detail);
		status = (TextView) findViewById(R.id.status_detail);
		date = (TextView) findViewById(R.id.date_detail);
		date2 = (TextView) findViewById(R.id.date_detai2);
		describe = (TextView) findViewById(R.id.miaoshu_detail);
		image = (ImageView) findViewById(R.id.image_detail);
		uploadbtn = (Button) findViewById(R.id.upload);
		uploadbtn.setOnClickListener(onClickListener);
		//点击看大图 屏蔽
		//image.setOnTouchListener(imagOnTouchListener);
		item = getDataFromDb(id);
		
		//显示到界面
		setDataToView(item);
		
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch(msg.what){
				case 1: //上传成功
					Notification n1 = new Notification();
					n1.tickerText = "单号上传成功";
					n1.icon = R.drawable.ca2;
					n1.defaults = Notification.DEFAULT_SOUND;
					n1.when = System.currentTimeMillis();
					Intent intent1 = new Intent(DetailInfoActivity.this,DetailInfoActivity.class);
					PendingIntent pintent1 = PendingIntent.getActivity(DetailInfoActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
					n1.setLatestEventInfo(DetailInfoActivity.this, "单号上传成功", "点击查看单号详情", pintent1);
					nm.notify(1, n1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nm.cancel(1);
					Intent ii = new Intent(DetailInfoActivity.this, SelectActivity.class);
					startActivity(ii);
					finish();
					
					break;
				case 2://上传
					
					Notification n2 = new Notification();
					n2.tickerText = "单号上传失败";
					n2.icon = R.drawable.ca2;
					n2.defaults = Notification.DEFAULT_SOUND;
					n2.when = System.currentTimeMillis();
					Intent intent2 = new Intent(DetailInfoActivity.this,DetailInfoActivity.class);
					PendingIntent pintent2 = PendingIntent.getActivity(DetailInfoActivity.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
					n2.setLatestEventInfo(DetailInfoActivity.this, "单号上传失败", "点击查看单号详情", pintent2);
					nm.notify(1, n2);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nm.cancel(1);
					break;
				}
				return false;
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		if(null != bm){
			bm.recycle();
		}
		super.onDestroy();
	}

	private View.OnTouchListener imagOnTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//把图片放大显示
			bm.recycle();
			Intent intent = new Intent(DetailInfoActivity.this,ShowBigImgActivity.class);
			intent.putExtra("path", item.getImgpath());
			startActivity(intent);
			return false;
		}
	};
	
	private void setDataToView(YunDanItem item) {
		// TODO Auto-generated method stub
		danhao.setText(item.getNumber().substring(item.getNumber().indexOf(':')));
		int issucc = item.getIssucc();
		int isupload = item.getIsUpload();
		String statusStr = issucc == 0?"交车失败":(issucc == 1?"交车成功":"未完成");
		status.setText(statusStr);
		
		date.setText(item.getCreateTime());
		date2.setText(item.getPayTime()==null?"xxxx-xx-xx xx:xx:xx":item.getPayTime());
		describe.setText(issucc==1?item.getSuccinfo():(issucc==0?item.getFailinfo():"运单待完成。。。"));
		
		//上传成功或者未完成订单 都不能上传
		if(1 == isupload || issucc == 2){
			uploadbtn.setVisibility(View.GONE);
		}else{
			uploadbtn.setText("上    传");
			uploadbtn.setClickable(true);
			uploadbtn.setVisibility(View.VISIBLE);
		}
		
		if(1==issucc){
        	BitmapFactory.Options opts = new BitmapFactory.Options();

        	opts.inJustDecodeBounds = true;
        	//BitmapFactory.decodeFile(imageFile, opts);
        	
			try {
				InputStream is = new FileInputStream(item.getImgpath());
				BitmapFactory.decodeStream(is, null, opts);
				
				opts.inSampleSize = computeSampleSize(opts, -1, 300*400);  
				opts.inJustDecodeBounds = false;
				bm = BitmapFactory.decodeFile(item.getImgpath());
				image.setImageBitmap(bm);

				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	//上传
	protected View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//实现上传操作
			uploadData();
		}


	};

	private void uploadData() {
		// 上传数据 TODO
		
		//在状态栏通知 开始上传
		nm = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification();
		n.tickerText = "单号已经加入上传队列";
		n.icon = R.drawable.ca2;
		n.defaults = Notification.DEFAULT_SOUND;
		n.when = System.currentTimeMillis();
		Intent intent = new Intent(DetailInfoActivity.this,DetailInfoActivity.class);
		PendingIntent pintent = PendingIntent.getActivity(DetailInfoActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(DetailInfoActivity.this, "单号上传", "点击查看单号详情", pintent);
		nm.notify(1, n);
		
		new Thread(new UploadYundan(item, handler,"update")).start();
	}
	
	private YunDanItem getDataFromDb(int id){
		
		db = DBHelper.getInstance();
		String select = "id = ?";
		String [] selectionArgs = {id+""};
		Cursor cursor = db.findOne(true, Contants.table2, null, select, selectionArgs, null, null, null, null);
		if(null!= cursor){
			//YunDanItem item = new YunDanItem();
			item.setId(id);
			item.setNumber(cursor.getString(1));
			item.setImgpath(cursor.getString(2));
			item.setSuccinfo(cursor.getString(3));
			item.setFailinfo(cursor.getString(4));
			item.setIsdel(cursor.getInt(5));
			item.setIssucc(cursor.getInt(6));
			item.setIsUpload(cursor.getInt(7));
			item.setPayTime(cursor.getString(8));
			item.setCreateTime(cursor.getString(9));
			
		}else{
			Toast.makeText(this, "数据加载失败！", Toast.LENGTH_LONG).show();
			
		}
		cursor.close();
		return item;
		
	}
	
	
	//位图自动缩放 内存不溢出的方法
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
		getMenuInflater().inflate(R.menu.detail_info, menu);
		return true;
	}

}
