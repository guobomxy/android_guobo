package com.eastime.paycar.activitys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastime.paycar.R;
import com.eastime.paycar.bean.YunDanItem;
import com.eastime.paycar.services.GPSService;
import com.eastime.paycar.services.UploadYundan;
import com.eastime.paycar.util.AppContext;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;
import com.eastime.paycar.zxingdecodeing.CaptureActivity;

public class PayCarActivity extends Activity {

	// 页面控件

	// 扫码步骤
	private View v0;
	private Button saoma;
	private Button shoushu;
	
	
	private View v1;
	private TextView danhaotip1;
	private TextView danhaotip2;
	private TextView danhaotip3;
	
	private Button failbtn;
	private Button succbtn;
	private Button cancle;
	// 拍照步骤
	private View v2;
	private ImageView paizhao;
	private ImageView yulan;
	private ImageView bendi;
	private Button fanhui_v2;
	private Button next;
	// 备注步骤
	private View v3;
	private EditText beizhuinfo;
	private Button fanhui_v3;
	private Button wanchengbtn;

	// 这些打辅助
	protected DBHelper db;
	private String strImgPath = ""; // 拍照文件保存路径
	private final int PAIZHAO = 1;
	private final int SAOMA = 2;
	private final int BENDI = 3;
	private boolean failOrSuccFlag = true;
	private boolean issaoma = false; // 如果没扫码 就不能点击交车失败

	private int isupload = 0;
	private String absNumber = "";

	private Bitmap bitmap;
	private Bitmap bitmap1;
	private boolean isSaomaFlag = false;
	
	private NotificationManager nm = null;
	private Handler handler;
	private AlertDialog.Builder dialog;
	private AlertDialog alertDialog;
	
	int v0v=-111,v1v=-111,v2v=-111,v3v=-111;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Contants.count += 1;
		System.out.println("这是第"+Contants.count+"次");
		setContentView(R.layout.bactivity);

		v0 = findViewById(R.id.include0);
		
		v1 = findViewById(R.id.include1);
		v1.setVisibility(View.GONE);
		v2 = findViewById(R.id.include2);
		v2.setVisibility(View.GONE);
		v3 = findViewById(R.id.include3);
		v3.setVisibility(View.GONE);
		

		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		/* ==============扫码步骤============== */
		// 扫码按钮监听
		danhaotip1 = (TextView) findViewById(R.id.paycarsaomadanhao1);
		danhaotip2 = (TextView) findViewById(R.id.paycarsaomadanhao2);
		danhaotip3 = (TextView) findViewById(R.id.paycarsaomadanhao3);
		saoma = (Button) findViewById(R.id.saomaiv);
		saoma.setOnClickListener(onClickListener);
		shoushu = (Button) findViewById(R.id.shoushuyundan_paycar);
		shoushu.setOnClickListener(onClickListener);
		// 确认成功 监听
		succbtn = (Button) findViewById(R.id.succbtn);
		succbtn.setOnClickListener(onClickListener);
		// 交车失败
		failbtn = (Button) findViewById(R.id.failbtn);
		failbtn.setOnClickListener(onClickListener);
		//取消交车
		cancle = (Button) findViewById(R.id.cancle_paycar);
		cancle.setOnClickListener(onClickListener);
		
		/* ==============拍照步骤============== */
		// 拍照 按钮触发事件
//		paizhao = (ImageView) findViewById(R.id.paizhaoiv);
//		paizhao.setOnTouchListener(onTouchListener);
//		// 本地照片
//		bendi = (ImageView) findViewById(R.id.bendi);
//		bendi.setOnTouchListener(onTouchListener);

		// 预览图片
		yulan = (ImageView) findViewById(R.id.preimage);

		// 返回按钮
		fanhui_v2 = (Button) findViewById(R.id.fanhui);
		fanhui_v2.setOnClickListener(onClickListener);

		// 下一步按钮
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(onClickListener);
		
		/* ==============备注步骤============== */
		fanhui_v3 = (Button) findViewById(R.id.fanhui_beizhu);
		fanhui_v3.setOnClickListener(onClickListener);
		// 完成交车按钮
		wanchengbtn = (Button) findViewById(R.id.wanchengjiaoche_beizhu);
		wanchengbtn.setOnClickListener(onClickListener);
		// 备注信息
		beizhuinfo = (EditText) findViewById(R.id.beizhuT);
		
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
					Intent intent1 = new Intent(PayCarActivity.this,DetailInfoActivity.class);
					PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
					n1.setLatestEventInfo(PayCarActivity.this, "单号上传成功", "点击查看单号详情", pintent1);
					nm.notify(1, n1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nm.cancel(1);
					break;
				case 2://上传
					Notification n2 = new Notification();
					n2.tickerText = "单号上传失败";
					n2.icon = R.drawable.ca2;
					n2.defaults = Notification.DEFAULT_SOUND;
					n2.when = System.currentTimeMillis();
					Intent intent2 = new Intent(PayCarActivity.this,DetailInfoActivity.class);
					PendingIntent pintent2 = PendingIntent.getActivity(PayCarActivity.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
					n2.setLatestEventInfo(PayCarActivity.this, "单号上传失败", "点击查看单号详情", pintent2);
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
		
		if(Contants.count == 3){ //解决三星有些机型拍照横竖两次的问题
			danhaotip1.setText("单号：" + Contants.danhao);
			danhaotip2.setText("单号：" + Contants.danhao);
			danhaotip3.setText("单号：" + Contants.danhao);
			absNumber = Contants.danhao;
			v0.setVisibility(View.GONE);
			v1.setVisibility(View.GONE);
			v2.setVisibility(View.VISIBLE);
			Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();
			System.out.println(strImgPath+"########################");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			///bitmap1 = BitmapFactory.decodeFile(strImgPath, options);
			bitmap1 = BitmapFactory.decodeFile(Contants.imagePath, options);
			yulan.setImageBitmap(bitmap1);
			Contants.count = 0;
			return;
		}

	}

	// 扫码按钮的监听事件
	protected View.OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.saomaiv:
				// 点击 扫码 跳转到监听事件
				Intent intent = new Intent();
				intent.setClass(PayCarActivity.this, CaptureActivity.class);

				startActivityForResult(intent, SAOMA);
				break;
			}
			return false;
		}
	};
	
	//handler监听是否 上传成功  在状态栏进行提示

	
	

	// 按钮的监听事件
	protected View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.saomaiv:
				// 点击 扫码 跳转到监听事件
				Intent intent = new Intent();
				intent.setClass(PayCarActivity.this, CaptureActivity.class);

				startActivityForResult(intent, SAOMA);
				break;
			case R.id.shoushuyundan_paycar:
				alertDialog(1);
				break;
			case R.id.succbtn:
				// 交车成功  显示dialog  选择拍照还是本地
				failOrSuccFlag = true;
				//显示对话框
				alertDialog();				


				break;
			case R.id.failbtn:
				// TODO 交车失败   直接跳到备注界面
				v1.setVisibility(View.GONE);
				v3.setVisibility(View.VISIBLE);
				failOrSuccFlag = false;


				break;
			case R.id.cancle_paycar:
				//取消交车  隐藏v1  显示v0
				v1.setVisibility(View.GONE);
				v0.setVisibility(View.VISIBLE);
				danhaotip1.setText("");
				danhaotip2.setText("");
				danhaotip3.setText("");
				issaoma = false;
				
				break;
			case R.id.fanhui:
				failOrSuccFlag = false;
				v2.setVisibility(View.GONE);
				v1.setVisibility(View.VISIBLE);
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.paizhao3);
				//paizhaotip.setImageBitmap(bitmap);
				break;
			case R.id.next:
				// 拍照后的下一步 隐掉v2 显示 v3 切换tip
				v2.setVisibility(View.GONE);
				v3.setVisibility(View.VISIBLE);
				wanchengbtn.setText(R.string.wanchengbtn);
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.beizhu2);
				//exinfotip.setImageBitmap(bitmap);

				break;
			case R.id.fanhui_beizhu:
				// 备注的返回 可以添加询问 是否清空 备注
				if (!failOrSuccFlag) { // 由v1来的
					v3.setVisibility(View.GONE);
					v1.setVisibility(View.VISIBLE);
					failOrSuccFlag = true;

					break;
				}
				v3.setVisibility(View.GONE);
				v2.setVisibility(View.VISIBLE);

				break;
			case R.id.wanchengjiaoche_beizhu:

				//先更新数据库这条运单的状态
				db = DBHelper.getInstance();
				ContentValues values = new ContentValues();				
				values.put("isdel", 1);
				String whereClause = "number = ?";
				String[] whereArgs = { absNumber };
				boolean iss = db.update(Contants.table2, values, whereClause, whereArgs);
				
				//查询是否还与任务  没有的话发送广播结束后台服务
				String selectionArgs[] = {"0"};
				Cursor cursor1 = db.findList(false, Contants.table2, null, "isdel = ?", selectionArgs, null, null, null, null);
				
				if(!cursor1.moveToNext()){
					Intent intent2 = new Intent("STOPSERVICE");
					sendBroadcast(intent2);
				}
				cursor1.close();
				//上传操作 1 数据上传  2  ftp图片上传 TODO  另外开辟线程完成
				
				boolean flag = false;
				//先数据 后ftp						
				String danhaoinfo = "";
				if(failOrSuccFlag){
					danhaoinfo = beizhuinfo.getText().toString() == "" ? "交车成功":beizhuinfo.getText().toString();
					
				}else{
					danhaoinfo = beizhuinfo.getText().toString() == "" ? "交车失败了":beizhuinfo.getText().toString();
					
				}
				//获得运单的接车时间
				db = DBHelper.getInstance();
				String a[] = {absNumber};
				Cursor cursor = db.findOne(false, Contants.table2, null, "number = ?", a, null, null, null, null);
				String receivecartime = cursor.getString(9);
				cursor.close();
				
				YunDanItem item = new YunDanItem();
				item.setImgpath(strImgPath);
				item.setFailinfo(beizhuinfo.getText().toString() == "" ? "交车失败了":beizhuinfo.getText().toString());
				item.setSuccinfo(beizhuinfo.getText().toString() == "" ? "交车成功":beizhuinfo.getText().toString());
				item.setPayTime(receivecartime);
				item.setIssucc(failOrSuccFlag?1:0);
				item.setNumber(absNumber);
				
				if(!SystemUtils.netWorkStatus()){
					saveAll();
					Log.i("交车", "联网失败");
					
				}else{
					//在状态栏通知 开始上传
					nm = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
					Notification n = new Notification();
					n.tickerText = "单号已经加入上传队列";
					n.icon = R.drawable.ca2;
					n.defaults = Notification.DEFAULT_SOUND;
					n.when = System.currentTimeMillis();
					Intent intentnm = new Intent(PayCarActivity.this,PayCarActivity.class);
					PendingIntent pintent = PendingIntent.getActivity(PayCarActivity.this, 0, intentnm, PendingIntent.FLAG_UPDATE_CURRENT);
					n.setLatestEventInfo(PayCarActivity.this, "单号上传", "点击查看单号详情", pintent);
					nm.notify(1, n);
					
					Thread t = new Thread(new UploadYundan(item,handler,"update"));
					t.start();
				}
					
	//				
					//结束该界面
					if (null != bitmap1) {
						bitmap1.recycle();
					}
					finish();
					// 跳转到接车界面
					Intent intent1 = new Intent(PayCarActivity.this, ManageActivity.class);
					startActivity(intent1);
					
				break;
				
			case R.id.xiangji:
				// 调用相机 拍照 并形成预览
				alertDialog.cancel();
				cameraMethod();
				break;
			case R.id.xiangce:
				alertDialog.cancel();
				// 添加本地图片 并预览
				Intent intent2 = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent2.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent2, BENDI);
				break;
			}
		}
	};

	
	
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(v0v != -111 && v1v != -111 && v2v != -111 && v3v != -111){
		v0.setVisibility(v0v);
		v1.setVisibility(v1v);
		v2.setVisibility(v2v);
		v3.setVisibility(v3v);
	}
		danhaotip1.setText("单号：" + Contants.danhao);
		danhaotip2.setText("单号：" + Contants.danhao);
		danhaotip3.setText("单号：" + Contants.danhao);

		strImgPath = Contants.imagePath;
		super.onRestoreInstanceState(savedInstanceState);
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Contants.imagePath = strImgPath;
		Contants.danhao = danhaotip2.getText().toString().substring(3);
		v0v = v0.getVisibility();
		v1v = v1.getVisibility();
		v2v = v2.getVisibility();
		v3v = v3.getVisibility();
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void onPause() {
		
//		v0.setVisibility(View.VISIBLE);
//		v1.setVisibility(View.GONE);
//		v2.setVisibility(View.GONE);
//		v3.setVisibility(View.GONE);
		
		//danhaotip.setText(getResources().getString(R.string.defaultdanhao));
		//issaoma = false;
		//TODO
		v0v = v0.getVisibility();
		v1v = v1.getVisibility();
		v2v = v2.getVisibility();
		v3v = v3.getVisibility();
		super.onPause();
	}
	
	

	@Override
	protected void onResume() {
//		if(v0v != -111 && v1v != -111 && v2v != -111 && v3v != -111){
//			v0.setVisibility(v0v);
//			v1.setVisibility(v1v);
//			v2.setVisibility(v2v);
//			v3.setVisibility(v3v);
//		}
		super.onResume();
	}



	// 重写方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// 扫描后
		if (20 == resultCode) {

			String result = data.getStringExtra("result");
			if (null == result || "".equals(result)) {
				Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();

				// 此处添加手工输入条码功能 dialog
				alertDialog(0);
				return;
			}

			Toast.makeText(this, result, 2000).show();
			absNumber = result;
			show(result);

		}
		// 扫描不成功或者按back键返回
		if (0 == resultCode && SAOMA == requestCode) {
			// 此处添加手工输入条码功能 dialog
			//alertDialog(1);
			//finish();
		}

		// 拍照成功后
		if (PAIZHAO == requestCode && 0 != resultCode) {
			if (resultCode == RESULT_OK) {
				danhaotip1.setText("单号：" + Contants.danhao);
				danhaotip2.setText("单号：" + Contants.danhao);
				danhaotip3.setText("单号：" + Contants.danhao);
				v1.setVisibility(View.GONE);
				v2.setVisibility(View.VISIBLE);
				Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();
				System.out.println(strImgPath+"########################");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				///bitmap1 = BitmapFactory.decodeFile(strImgPath, options);
				bitmap1 = BitmapFactory.decodeFile(strImgPath, options);
				yulan.setImageBitmap(bitmap1);
//				strImgPath = Contants.imagePath;
//				Contants.imagePath = "";

			}
		}
		// 拍照不成功
		if (PAIZHAO == requestCode && 0 == resultCode) {
			Contants.imagePath = "";
			Toast.makeText(PayCarActivity.this, "拍照异常", Toast.LENGTH_LONG)
					.show();
		}
		
		//       file:///mnt/sdcard/%E6%88%91%E7%9A%84%E5%BF%AB%E7%9B%98/guobo1991%40126.com/IMG0282A.jpg
		//       content://media/external/images/media/84641
		// 浏览图片成功
		if (BENDI == requestCode && 0 != resultCode) {
			Uri uri = data.getData();
			String uriPath = uri.toString();
			//判断uri是否是来自系统的图片管理器 还是来自其他第三方的文件夹
			if(uriPath.startsWith("file:")){
				
				strImgPath = uriPath.substring(7);
				strImgPath = URLDecoder.decode(strImgPath);
				
			}else{
				Log.e("uri", uri.toString()+"---------------------------------------------------------------------");
				ContentResolver cr = this.getContentResolver();
				
				Cursor cursor = cr.query(uri, null, null, null, null);
	        	cursor.moveToFirst();
	        	strImgPath = cursor.getString(1);
	        	cursor.close();
	        	Log.e("图片路径",strImgPath);
			}
			InputStream is = null;
			try {
				//File file = new File(strImgPath);
				is = new FileInputStream(strImgPath);
				BitmapFactory.Options opts = new BitmapFactory.Options();

				opts.inJustDecodeBounds = true;
				// BitmapFactory.decodeFile(imageFile, opts);
				BitmapFactory.decodeStream(is, null, opts);//cr.openInputStream(uri)

				opts.inSampleSize = computeSampleSize(opts, -1, 200 * 200);
				opts.inJustDecodeBounds = false;
				bitmap1 = BitmapFactory.decodeFile(strImgPath);//cr.openInputStream(uri)

				/* 将Bitmap设定到ImageView */
				yulan.setImageBitmap(bitmap1);
				v1.setVisibility(View.GONE);
				v2.setVisibility(View.VISIBLE);

			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}finally{
				try {
					is.close();
				} catch (IOException e) {
					Log.i("关闭显示图片文件流", "失败");
					e.printStackTrace();
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// 调用相机 拍照并返回结果
	private void cameraMethod() {
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/paycarcamera/";// 存放照片的文件夹
		// strImgPath = getFilesDir().getAbsolutePath()+"/";
		String fileName = makeMediaName() + ".jpg";// 照片命名
		File out = new File(strImgPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;// 该照片的绝对路径
		System.out.println(strImgPath);
		
		//Contants.imagePath = strImgPath; //////////////////////////
		
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureIntent, PAIZHAO);

	}

	// 图片起名
	private String makeMediaName() {
		String result = "";
		// 设定路径以及文件名
		Calendar cad = Calendar.getInstance();
		int year = cad.get(Calendar.YEAR);
		int month = cad.get(Calendar.MONTH);
		int day = cad.get(Calendar.DAY_OF_MONTH);
		int hour = cad.get(Calendar.HOUR_OF_DAY);
		int minute = cad.get(Calendar.MINUTE);
		int second = cad.get(Calendar.SECOND);
		result += "" + year + f(month + 1) + f(day) + f(hour) + f(minute)
				+ f(second);

		return result;
	}

	private String f(int dd) {
		String ddd = "" + (dd < 10 ? ("0" + dd) : dd);
		return ddd;
	}

	// 此处添加手工输入条码功能 dialog
	// 显示手动输入的界面
	@SuppressLint("ResourceAsColor")
	protected void alertDialog() {

		dialog = new Builder(this);
		
		TextView title = new TextView(PayCarActivity.this);
		title.setBackgroundColor(Color.rgb(51, 51, 51));
		title.setText("交车成功，请选择交接单照片：");
		title.setTextColor(Color.rgb(47, 132, 164));
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
	    float value  = value = dm.scaledDensity;
		
	    title.setPadding(30, 40, 0, 30);
		title.setTextSize(40/value);
		dialog.setCustomTitle(title);
		dialog.setInverseBackgroundForced(true);
		
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View zpxq = inflater.inflate(R.layout.dialog_imagetype, null);
		
		TextView xiangji = (TextView) zpxq.findViewById(R.id.xiangji);
		xiangji.setOnClickListener(onClickListener);
		TextView xiangce = (TextView) zpxq.findViewById(R.id.xiangce);
		xiangce.setOnClickListener(onClickListener);
		
		dialog.setView(zpxq);
		alertDialog = dialog.create();
		alertDialog.show();
	}

	// 显示手动输入的界面
		protected void alertDialog(int flag) {
			// 此处添加手工输入条码功能 dialog
			dialog = new Builder(this);

			// LayoutInflater inflater = LayoutInflater.from(this);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View sgtmT = inflater.inflate(R.layout.dialog_tiaomao, null);
			TextView tip = (TextView) sgtmT.findViewById(R.id.smtip);
			if(flag == 1){
				dialog.setTitle("手工输入");	
				tip.setText("请手动输入条码");
			}else{
				dialog.setTitle("识别错误");			
				tip.setText("未识别到条码，请手动输入");
			}
			
			dialog.setView(sgtmT);

			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					EditText e = (EditText) sgtmT.findViewById(R.id.sgtm);
					String result = e.getText().toString();
					// 必要的时候要在这里添加条码校验 
					absNumber = result;
					show(result);

				}
			});

			dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog.dismiss();
				}
			});

			dialog.create().show();
		}

	// 显示条码到界面 需要查询数据库 比对 显示提示信息
	// 此处 有逻辑问题 手工输入的没有 条码类型 只有代号 数据库保存的是类型+代号
	// 手工输入的保存到数据库中也没有类型
	protected void show(String result) {
		
		db = DBHelper.getInstance();
		
		String[] s = { result };
		Cursor cursor = db.findList(false, Contants.table2, null, "number = ?",
				s, null, null, null, null);
		int i = 0;
		while (cursor.moveToNext()) {
			int isdel = cursor.getInt(5);
			if (0 == isdel) {// 未删除
				i++;
			}
		}
		cursor.close();
		if (i == 0) {
			Toast.makeText(PayCarActivity.this, "该单号不存在！", Toast.LENGTH_LONG).show();
			
		} else {
			isSaomaFlag = true;
			v0.setVisibility(View.GONE);
			v1.setVisibility(View.VISIBLE);
			danhaotip1.setText("单号：" + result.substring(result.indexOf(':') + 1));
			danhaotip2.setText("单号：" + result.substring(result.indexOf(':') + 1));
			danhaotip3.setText("单号：" + result.substring(result.indexOf(':') + 1));
			issaoma = true;
		}

	}

	// 保存所有数据到数据库
	protected boolean saveAll() {
		// 
		db = DBHelper.getInstance();
		// number imgpath succinfo failinfo isdel
		ContentValues values = new ContentValues();
		
		values.put("isdel", 1);
		if (failOrSuccFlag) { // 成功交车
			String succinfo = beizhuinfo.getText().toString() == "" ? "成功交车了"
					: beizhuinfo.getText().toString();
			values.put("imgpath", strImgPath);
			values.put("succinfo", succinfo);
			values.put("issucc", 1);
		} else {
			String failinfo = beizhuinfo.getText().toString() == "" ? "交车失败了"
					: beizhuinfo.getText().toString();
			values.put("failinfo", failinfo);
			values.put("issucc", 0);
		}
		values.put("paytime", SystemUtils.getStr()); // 交车时间戳
		values.put("isdel", 1); // 为1时 标记为删除
		values.put("isupload", isupload);
		String whereClause = "number = ?";
		String[] whereArgs = { absNumber };
		
		boolean iss = db
				.update(Contants.table2, values, whereClause, whereArgs);
		return iss;
	}

	// 位图自动缩放 内存不溢出的方法
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

}



/*				new Thread(new Runnable() {

@Override
public void run() {
	
	
	//在状态栏通知 开始上传
	nm = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
	Notification n = new Notification();
	n.tickerText = "单号已经加入上传队列";
	n.icon = R.drawable.ca2;
	n.defaults = Notification.DEFAULT_SOUND;
	n.when = System.currentTimeMillis();
	Intent intent = new Intent(PayCarActivity.this,PayCarActivity.class);
	PendingIntent pintent = PendingIntent.getActivity(PayCarActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	n.setLatestEventInfo(PayCarActivity.this, "单号上传", "点击查看单号详情", pintent);
	nm.notify(1, n);
	
	String fileNamePath = strImgPath.substring(0,strImgPath.lastIndexOf('/')+1);
	String fileName = strImgPath.substring(strImgPath.lastIndexOf('/')+1);
	
	JSONParser uploadJson = new JSONParser();
	List<NameValuePair> params = new ArrayList<NameValuePair>();					
	params.add(new BasicNameValuePair("userName", Contants.name));
	params.add(new BasicNameValuePair("phoneNumber", Contants.phoneNumber));
	params.add(new BasicNameValuePair("number", absNumber));
	params.add(new BasicNameValuePair("status", failOrSuccFlag?"1":"0"));
	params.add(new BasicNameValuePair("info", danhaoinfo));
	params.add(new BasicNameValuePair("imagepath",Contants.REMOTEPATH + fileName ));
	params.add(new BasicNameValuePair("receivecartime", receivecartime));
	params.add(new BasicNameValuePair("paycartime", SystemUtils.getStr()));
	params.add(new BasicNameValuePair("eastimepaycar", ""));
	
	String jsonResult = uploadJson.makeHttpRequest(Contants.SAVEDANHAO_URL, Contants.METHOD_POST, params);
	try {
		JSONObject json = new JSONObject(jsonResult);
		String result = json.getString("result");
		if("ok".equals(result)){
			flag = true;
			Log.i("交车", "上传成功");
		}else{
			Log.i("交车","上传失败");
		}
	} catch (JSONException e) {
		Log.i("交车","上传异常");
		e.printStackTrace();
	}
	
	//ftp
	String result = "";
	if(flag){ //成功的话  ftp上传图片

		result = uploadJson.uploadFTP(fileNamePath, fileName);
		Log.i("ftp上传信息", result);
		if("1".equals(result)){
			//上传成功 TODO
			isupload = 1;
			nm.cancel(1);
		}else{
			//上传失败 TODO
			
			Notification n1 = new Notification();
			n1.tickerText = "单号上传失败1";
			n1.icon = R.drawable.ca2;
			n1.defaults = Notification.DEFAULT_SOUND;
			n1.when = System.currentTimeMillis();
			Intent intent1 = new Intent(PayCarActivity.this,PayCarActivity.class);
			PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			n1.setLatestEventInfo(PayCarActivity.this, "单号上传", "点击查看单号详情", pintent1);
			nm.notify(1, n1);
			nm.cancel(1);
			
		}
	}else{
		//Toast.makeText(PayCarActivity.this, result, Toast.LENGTH_LONG).show();
		Notification n1 = new Notification();
		n1.tickerText = "单号上传失败2";
		n1.icon = R.drawable.ca2;
		n1.defaults = Notification.DEFAULT_SOUND;
		n1.when = System.currentTimeMillis();
		Intent intent1 = new Intent(PayCarActivity.this,PayCarActivity.class);
		PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		n1.setLatestEventInfo(PayCarActivity.this, "单号上传失败", "点击查看单号详情", pintent1);
		nm.notify(1, n1);
		nm.cancel(1);
		
	}
	
	//保存本地  TODO
	// 完成交车按钮 保存到本地数据库
	
	if (!saveAll()) {
		if (null != bitmap1) {
			bitmap1.recycle();
		}
		return;
	}
	if (null != bitmap1){
		bitmap1.recycle();
	}
	
}
}).start();*/

/*		String items[] = {"相机拍照","相册选取"};
dialog.setItems(items, new OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		switch(which){
			case 0:
				cameraMethod();
				
				break;
			case 1:
				// 添加本地图片 并预览
				Intent intent2 = new Intent();
				 开启Pictures画面Type设定为image 
				intent2.setType("image/*");
				 使用Intent.ACTION_GET_CONTENT这个Action 
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				 取得相片后返回本画面 
				startActivityForResult(intent2, BENDI);
				
				break;
		};
		
		// TODO Auto-generated method stub
		//Toast.makeText(PayCarActivity.this, which, Toast.LENGTH_LONG).show();
	}
});*/