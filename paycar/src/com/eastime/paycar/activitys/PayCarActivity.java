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

	// ҳ��ؼ�

	// ɨ�벽��
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
	// ���ղ���
	private View v2;
	private ImageView paizhao;
	private ImageView yulan;
	private ImageView bendi;
	private Button fanhui_v2;
	private Button next;
	// ��ע����
	private View v3;
	private EditText beizhuinfo;
	private Button fanhui_v3;
	private Button wanchengbtn;

	// ��Щ����
	protected DBHelper db;
	private String strImgPath = ""; // �����ļ�����·��
	private final int PAIZHAO = 1;
	private final int SAOMA = 2;
	private final int BENDI = 3;
	private boolean failOrSuccFlag = true;
	private boolean issaoma = false; // ���ûɨ�� �Ͳ��ܵ������ʧ��

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
		System.out.println("���ǵ�"+Contants.count+"��");
		setContentView(R.layout.bactivity);

		v0 = findViewById(R.id.include0);
		
		v1 = findViewById(R.id.include1);
		v1.setVisibility(View.GONE);
		v2 = findViewById(R.id.include2);
		v2.setVisibility(View.GONE);
		v3 = findViewById(R.id.include3);
		v3.setVisibility(View.GONE);
		

		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		/* ==============ɨ�벽��============== */
		// ɨ�밴ť����
		danhaotip1 = (TextView) findViewById(R.id.paycarsaomadanhao1);
		danhaotip2 = (TextView) findViewById(R.id.paycarsaomadanhao2);
		danhaotip3 = (TextView) findViewById(R.id.paycarsaomadanhao3);
		saoma = (Button) findViewById(R.id.saomaiv);
		saoma.setOnClickListener(onClickListener);
		shoushu = (Button) findViewById(R.id.shoushuyundan_paycar);
		shoushu.setOnClickListener(onClickListener);
		// ȷ�ϳɹ� ����
		succbtn = (Button) findViewById(R.id.succbtn);
		succbtn.setOnClickListener(onClickListener);
		// ����ʧ��
		failbtn = (Button) findViewById(R.id.failbtn);
		failbtn.setOnClickListener(onClickListener);
		//ȡ������
		cancle = (Button) findViewById(R.id.cancle_paycar);
		cancle.setOnClickListener(onClickListener);
		
		/* ==============���ղ���============== */
		// ���� ��ť�����¼�
//		paizhao = (ImageView) findViewById(R.id.paizhaoiv);
//		paizhao.setOnTouchListener(onTouchListener);
//		// ������Ƭ
//		bendi = (ImageView) findViewById(R.id.bendi);
//		bendi.setOnTouchListener(onTouchListener);

		// Ԥ��ͼƬ
		yulan = (ImageView) findViewById(R.id.preimage);

		// ���ذ�ť
		fanhui_v2 = (Button) findViewById(R.id.fanhui);
		fanhui_v2.setOnClickListener(onClickListener);

		// ��һ����ť
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(onClickListener);
		
		/* ==============��ע����============== */
		fanhui_v3 = (Button) findViewById(R.id.fanhui_beizhu);
		fanhui_v3.setOnClickListener(onClickListener);
		// ��ɽ�����ť
		wanchengbtn = (Button) findViewById(R.id.wanchengjiaoche_beizhu);
		wanchengbtn.setOnClickListener(onClickListener);
		// ��ע��Ϣ
		beizhuinfo = (EditText) findViewById(R.id.beizhuT);
		
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch(msg.what){
				case 1: //�ϴ��ɹ�
					Notification n1 = new Notification();
					n1.tickerText = "�����ϴ��ɹ�";
					n1.icon = R.drawable.ca2;
					n1.defaults = Notification.DEFAULT_SOUND;
					n1.when = System.currentTimeMillis();
					Intent intent1 = new Intent(PayCarActivity.this,DetailInfoActivity.class);
					PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
					n1.setLatestEventInfo(PayCarActivity.this, "�����ϴ��ɹ�", "����鿴��������", pintent1);
					nm.notify(1, n1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nm.cancel(1);
					break;
				case 2://�ϴ�
					Notification n2 = new Notification();
					n2.tickerText = "�����ϴ�ʧ��";
					n2.icon = R.drawable.ca2;
					n2.defaults = Notification.DEFAULT_SOUND;
					n2.when = System.currentTimeMillis();
					Intent intent2 = new Intent(PayCarActivity.this,DetailInfoActivity.class);
					PendingIntent pintent2 = PendingIntent.getActivity(PayCarActivity.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
					n2.setLatestEventInfo(PayCarActivity.this, "�����ϴ�ʧ��", "����鿴��������", pintent2);
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
		
		if(Contants.count == 3){ //���������Щ�������պ������ε�����
			danhaotip1.setText("���ţ�" + Contants.danhao);
			danhaotip2.setText("���ţ�" + Contants.danhao);
			danhaotip3.setText("���ţ�" + Contants.danhao);
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

	// ɨ�밴ť�ļ����¼�
	protected View.OnTouchListener onTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.saomaiv:
				// ��� ɨ�� ��ת�������¼�
				Intent intent = new Intent();
				intent.setClass(PayCarActivity.this, CaptureActivity.class);

				startActivityForResult(intent, SAOMA);
				break;
			}
			return false;
		}
	};
	
	//handler�����Ƿ� �ϴ��ɹ�  ��״̬��������ʾ

	
	

	// ��ť�ļ����¼�
	protected View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.saomaiv:
				// ��� ɨ�� ��ת�������¼�
				Intent intent = new Intent();
				intent.setClass(PayCarActivity.this, CaptureActivity.class);

				startActivityForResult(intent, SAOMA);
				break;
			case R.id.shoushuyundan_paycar:
				alertDialog(1);
				break;
			case R.id.succbtn:
				// �����ɹ�  ��ʾdialog  ѡ�����ջ��Ǳ���
				failOrSuccFlag = true;
				//��ʾ�Ի���
				alertDialog();				


				break;
			case R.id.failbtn:
				// TODO ����ʧ��   ֱ��������ע����
				v1.setVisibility(View.GONE);
				v3.setVisibility(View.VISIBLE);
				failOrSuccFlag = false;


				break;
			case R.id.cancle_paycar:
				//ȡ������  ����v1  ��ʾv0
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
				// ���պ����һ�� ����v2 ��ʾ v3 �л�tip
				v2.setVisibility(View.GONE);
				v3.setVisibility(View.VISIBLE);
				wanchengbtn.setText(R.string.wanchengbtn);
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.beizhu2);
				//exinfotip.setImageBitmap(bitmap);

				break;
			case R.id.fanhui_beizhu:
				// ��ע�ķ��� ��������ѯ�� �Ƿ���� ��ע
				if (!failOrSuccFlag) { // ��v1����
					v3.setVisibility(View.GONE);
					v1.setVisibility(View.VISIBLE);
					failOrSuccFlag = true;

					break;
				}
				v3.setVisibility(View.GONE);
				v2.setVisibility(View.VISIBLE);

				break;
			case R.id.wanchengjiaoche_beizhu:

				//�ȸ������ݿ������˵���״̬
				db = DBHelper.getInstance();
				ContentValues values = new ContentValues();				
				values.put("isdel", 1);
				String whereClause = "number = ?";
				String[] whereArgs = { absNumber };
				boolean iss = db.update(Contants.table2, values, whereClause, whereArgs);
				
				//��ѯ�Ƿ�������  û�еĻ����͹㲥������̨����
				String selectionArgs[] = {"0"};
				Cursor cursor1 = db.findList(false, Contants.table2, null, "isdel = ?", selectionArgs, null, null, null, null);
				
				if(!cursor1.moveToNext()){
					Intent intent2 = new Intent("STOPSERVICE");
					sendBroadcast(intent2);
				}
				cursor1.close();
				//�ϴ����� 1 �����ϴ�  2  ftpͼƬ�ϴ� TODO  ���⿪���߳����
				
				boolean flag = false;
				//������ ��ftp						
				String danhaoinfo = "";
				if(failOrSuccFlag){
					danhaoinfo = beizhuinfo.getText().toString() == "" ? "�����ɹ�":beizhuinfo.getText().toString();
					
				}else{
					danhaoinfo = beizhuinfo.getText().toString() == "" ? "����ʧ����":beizhuinfo.getText().toString();
					
				}
				//����˵��Ľӳ�ʱ��
				db = DBHelper.getInstance();
				String a[] = {absNumber};
				Cursor cursor = db.findOne(false, Contants.table2, null, "number = ?", a, null, null, null, null);
				String receivecartime = cursor.getString(9);
				cursor.close();
				
				YunDanItem item = new YunDanItem();
				item.setImgpath(strImgPath);
				item.setFailinfo(beizhuinfo.getText().toString() == "" ? "����ʧ����":beizhuinfo.getText().toString());
				item.setSuccinfo(beizhuinfo.getText().toString() == "" ? "�����ɹ�":beizhuinfo.getText().toString());
				item.setPayTime(receivecartime);
				item.setIssucc(failOrSuccFlag?1:0);
				item.setNumber(absNumber);
				
				if(!SystemUtils.netWorkStatus()){
					saveAll();
					Log.i("����", "����ʧ��");
					
				}else{
					//��״̬��֪ͨ ��ʼ�ϴ�
					nm = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
					Notification n = new Notification();
					n.tickerText = "�����Ѿ������ϴ�����";
					n.icon = R.drawable.ca2;
					n.defaults = Notification.DEFAULT_SOUND;
					n.when = System.currentTimeMillis();
					Intent intentnm = new Intent(PayCarActivity.this,PayCarActivity.class);
					PendingIntent pintent = PendingIntent.getActivity(PayCarActivity.this, 0, intentnm, PendingIntent.FLAG_UPDATE_CURRENT);
					n.setLatestEventInfo(PayCarActivity.this, "�����ϴ�", "����鿴��������", pintent);
					nm.notify(1, n);
					
					Thread t = new Thread(new UploadYundan(item,handler,"update"));
					t.start();
				}
					
	//				
					//�����ý���
					if (null != bitmap1) {
						bitmap1.recycle();
					}
					finish();
					// ��ת���ӳ�����
					Intent intent1 = new Intent(PayCarActivity.this, ManageActivity.class);
					startActivity(intent1);
					
				break;
				
			case R.id.xiangji:
				// ������� ���� ���γ�Ԥ��
				alertDialog.cancel();
				cameraMethod();
				break;
			case R.id.xiangce:
				alertDialog.cancel();
				// ���ӱ���ͼƬ ��Ԥ��
				Intent intent2 = new Intent();
				/* ����Pictures����Type�趨Ϊimage */
				intent2.setType("image/*");
				/* ʹ��Intent.ACTION_GET_CONTENT���Action */
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				/* ȡ����Ƭ�󷵻ر����� */
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
		danhaotip1.setText("���ţ�" + Contants.danhao);
		danhaotip2.setText("���ţ�" + Contants.danhao);
		danhaotip3.setText("���ţ�" + Contants.danhao);

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



	// ��д����
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ɨ���
		if (20 == resultCode) {

			String result = data.getStringExtra("result");
			if (null == result || "".equals(result)) {
				Toast.makeText(this, "ɨ��ʧ��", Toast.LENGTH_LONG).show();

				// �˴������ֹ��������빦�� dialog
				alertDialog(0);
				return;
			}

			Toast.makeText(this, result, 2000).show();
			absNumber = result;
			show(result);

		}
		// ɨ�費�ɹ����߰�back������
		if (0 == resultCode && SAOMA == requestCode) {
			// �˴������ֹ��������빦�� dialog
			//alertDialog(1);
			//finish();
		}

		// ���ճɹ���
		if (PAIZHAO == requestCode && 0 != resultCode) {
			if (resultCode == RESULT_OK) {
				danhaotip1.setText("���ţ�" + Contants.danhao);
				danhaotip2.setText("���ţ�" + Contants.danhao);
				danhaotip3.setText("���ţ�" + Contants.danhao);
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
		// ���ղ��ɹ�
		if (PAIZHAO == requestCode && 0 == resultCode) {
			Contants.imagePath = "";
			Toast.makeText(PayCarActivity.this, "�����쳣", Toast.LENGTH_LONG)
					.show();
		}
		
		//       file:///mnt/sdcard/%E6%88%91%E7%9A%84%E5%BF%AB%E7%9B%98/guobo1991%40126.com/IMG0282A.jpg
		//       content://media/external/images/media/84641
		// ���ͼƬ�ɹ�
		if (BENDI == requestCode && 0 != resultCode) {
			Uri uri = data.getData();
			String uriPath = uri.toString();
			//�ж�uri�Ƿ�������ϵͳ��ͼƬ������ ���������������������ļ���
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
	        	Log.e("ͼƬ·��",strImgPath);
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

				/* ��Bitmap�趨��ImageView */
				yulan.setImageBitmap(bitmap1);
				v1.setVisibility(View.GONE);
				v2.setVisibility(View.VISIBLE);

			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}finally{
				try {
					is.close();
				} catch (IOException e) {
					Log.i("�ر���ʾͼƬ�ļ���", "ʧ��");
					e.printStackTrace();
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// ������� ���ղ����ؽ��
	private void cameraMethod() {
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/paycarcamera/";// �����Ƭ���ļ���
		// strImgPath = getFilesDir().getAbsolutePath()+"/";
		String fileName = makeMediaName() + ".jpg";// ��Ƭ����
		File out = new File(strImgPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;// ����Ƭ�ľ���·��
		System.out.println(strImgPath);
		
		//Contants.imagePath = strImgPath; //////////////////////////
		
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureIntent, PAIZHAO);

	}

	// ͼƬ����
	private String makeMediaName() {
		String result = "";
		// �趨·���Լ��ļ���
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

	// �˴������ֹ��������빦�� dialog
	// ��ʾ�ֶ�����Ľ���
	@SuppressLint("ResourceAsColor")
	protected void alertDialog() {

		dialog = new Builder(this);
		
		TextView title = new TextView(PayCarActivity.this);
		title.setBackgroundColor(Color.rgb(51, 51, 51));
		title.setText("�����ɹ�����ѡ�񽻽ӵ���Ƭ��");
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

	// ��ʾ�ֶ�����Ľ���
		protected void alertDialog(int flag) {
			// �˴������ֹ��������빦�� dialog
			dialog = new Builder(this);

			// LayoutInflater inflater = LayoutInflater.from(this);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View sgtmT = inflater.inflate(R.layout.dialog_tiaomao, null);
			TextView tip = (TextView) sgtmT.findViewById(R.id.smtip);
			if(flag == 1){
				dialog.setTitle("�ֹ�����");	
				tip.setText("���ֶ���������");
			}else{
				dialog.setTitle("ʶ�����");			
				tip.setText("δʶ�����룬���ֶ�����");
			}
			
			dialog.setView(sgtmT);

			dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					EditText e = (EditText) sgtmT.findViewById(R.id.sgtm);
					String result = e.getText().toString();
					// ��Ҫ��ʱ��Ҫ��������������У�� 
					absNumber = result;
					show(result);

				}
			});

			dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog.dismiss();
				}
			});

			dialog.create().show();
		}

	// ��ʾ���뵽���� ��Ҫ��ѯ���ݿ� �ȶ� ��ʾ��ʾ��Ϣ
	// �˴� ���߼����� �ֹ������û�� �������� ֻ�д��� ���ݿⱣ���������+����
	// �ֹ�����ı��浽���ݿ���Ҳû������
	protected void show(String result) {
		
		db = DBHelper.getInstance();
		
		String[] s = { result };
		Cursor cursor = db.findList(false, Contants.table2, null, "number = ?",
				s, null, null, null, null);
		int i = 0;
		while (cursor.moveToNext()) {
			int isdel = cursor.getInt(5);
			if (0 == isdel) {// δɾ��
				i++;
			}
		}
		cursor.close();
		if (i == 0) {
			Toast.makeText(PayCarActivity.this, "�õ��Ų����ڣ�", Toast.LENGTH_LONG).show();
			
		} else {
			isSaomaFlag = true;
			v0.setVisibility(View.GONE);
			v1.setVisibility(View.VISIBLE);
			danhaotip1.setText("���ţ�" + result.substring(result.indexOf(':') + 1));
			danhaotip2.setText("���ţ�" + result.substring(result.indexOf(':') + 1));
			danhaotip3.setText("���ţ�" + result.substring(result.indexOf(':') + 1));
			issaoma = true;
		}

	}

	// �����������ݵ����ݿ�
	protected boolean saveAll() {
		// 
		db = DBHelper.getInstance();
		// number imgpath succinfo failinfo isdel
		ContentValues values = new ContentValues();
		
		values.put("isdel", 1);
		if (failOrSuccFlag) { // �ɹ�����
			String succinfo = beizhuinfo.getText().toString() == "" ? "�ɹ�������"
					: beizhuinfo.getText().toString();
			values.put("imgpath", strImgPath);
			values.put("succinfo", succinfo);
			values.put("issucc", 1);
		} else {
			String failinfo = beizhuinfo.getText().toString() == "" ? "����ʧ����"
					: beizhuinfo.getText().toString();
			values.put("failinfo", failinfo);
			values.put("issucc", 0);
		}
		values.put("paytime", SystemUtils.getStr()); // ����ʱ���
		values.put("isdel", 1); // Ϊ1ʱ ���Ϊɾ��
		values.put("isupload", isupload);
		String whereClause = "number = ?";
		String[] whereArgs = { absNumber };
		
		boolean iss = db
				.update(Contants.table2, values, whereClause, whereArgs);
		return iss;
	}

	// λͼ�Զ����� �ڴ治����ķ���
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
	
	
	//��״̬��֪ͨ ��ʼ�ϴ�
	nm = (NotificationManager) AppContext.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
	Notification n = new Notification();
	n.tickerText = "�����Ѿ������ϴ�����";
	n.icon = R.drawable.ca2;
	n.defaults = Notification.DEFAULT_SOUND;
	n.when = System.currentTimeMillis();
	Intent intent = new Intent(PayCarActivity.this,PayCarActivity.class);
	PendingIntent pintent = PendingIntent.getActivity(PayCarActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	n.setLatestEventInfo(PayCarActivity.this, "�����ϴ�", "����鿴��������", pintent);
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
			Log.i("����", "�ϴ��ɹ�");
		}else{
			Log.i("����","�ϴ�ʧ��");
		}
	} catch (JSONException e) {
		Log.i("����","�ϴ��쳣");
		e.printStackTrace();
	}
	
	//ftp
	String result = "";
	if(flag){ //�ɹ��Ļ�  ftp�ϴ�ͼƬ

		result = uploadJson.uploadFTP(fileNamePath, fileName);
		Log.i("ftp�ϴ���Ϣ", result);
		if("1".equals(result)){
			//�ϴ��ɹ� TODO
			isupload = 1;
			nm.cancel(1);
		}else{
			//�ϴ�ʧ�� TODO
			
			Notification n1 = new Notification();
			n1.tickerText = "�����ϴ�ʧ��1";
			n1.icon = R.drawable.ca2;
			n1.defaults = Notification.DEFAULT_SOUND;
			n1.when = System.currentTimeMillis();
			Intent intent1 = new Intent(PayCarActivity.this,PayCarActivity.class);
			PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
			n1.setLatestEventInfo(PayCarActivity.this, "�����ϴ�", "����鿴��������", pintent1);
			nm.notify(1, n1);
			nm.cancel(1);
			
		}
	}else{
		//Toast.makeText(PayCarActivity.this, result, Toast.LENGTH_LONG).show();
		Notification n1 = new Notification();
		n1.tickerText = "�����ϴ�ʧ��2";
		n1.icon = R.drawable.ca2;
		n1.defaults = Notification.DEFAULT_SOUND;
		n1.when = System.currentTimeMillis();
		Intent intent1 = new Intent(PayCarActivity.this,PayCarActivity.class);
		PendingIntent pintent1 = PendingIntent.getActivity(PayCarActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		n1.setLatestEventInfo(PayCarActivity.this, "�����ϴ�ʧ��", "����鿴��������", pintent1);
		nm.notify(1, n1);
		nm.cancel(1);
		
	}
	
	//���汾��  TODO
	// ��ɽ�����ť ���浽�������ݿ�
	
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

/*		String items[] = {"�������","���ѡȡ"};
dialog.setItems(items, new OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		switch(which){
			case 0:
				cameraMethod();
				
				break;
			case 1:
				// ���ӱ���ͼƬ ��Ԥ��
				Intent intent2 = new Intent();
				 ����Pictures����Type�趨Ϊimage 
				intent2.setType("image/*");
				 ʹ��Intent.ACTION_GET_CONTENT���Action 
				intent2.setAction(Intent.ACTION_GET_CONTENT);
				 ȡ����Ƭ�󷵻ر����� 
				startActivityForResult(intent2, BENDI);
				
				break;
		};
		
		// TODO Auto-generated method stub
		//Toast.makeText(PayCarActivity.this, which, Toast.LENGTH_LONG).show();
	}
});*/