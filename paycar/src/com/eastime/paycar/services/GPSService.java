package com.eastime.paycar.services;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.eastime.paycar.activitys.ReceiveCarActivity;
import com.eastime.paycar.bean.Gps;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;


public class GPSService extends Service {
	
	//2000ms
    private static final long minTime = 60*1000; //1����
    //��С������� 10m
    private static final float minDistance = 0;//30��
 
    String tag = this.toString();
    private String username;
    private String phonenumber;
    private LocationManager locationManager;
    private LocationListener locationListener;
 
    private final IBinder mBinder = new GPSServiceBinder();
    private DBHelper db;
	private Handler handler;
	private Criteria criteria;
	private final String TAG = "GPSSERVICE";
	
    public void startService()
    {
    	System.out.println("******************************�����˷���*************************");
    	
    	username = Contants.name;
    	phonenumber = Contants.name;
    	
	    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    
	    // ���ҵ�������Ϣ  
	    criteria = new Criteria();
	    //���þ���
	    criteria.setAccuracy(Criteria.ACCURACY_FINE); 
	    //�����Ƿ�ȡ�ú���
	    criteria.setAltitudeRequired(false);
	    //�Ƿ��÷���
	    criteria.setBearingRequired(false);
	    //�Ƿ������������
	    criteria.setCostAllowed(true);
	    //�ĵ����
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    
	    getProvider();
	    
	    locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
				if("".equals(SystemUtils.getAllNumber())){
					if("ok".equals(Contants.a1)){
						return;
					}
					Intent it = new Intent();
					it.setClass(GPSService.this, GPSService.class);
					startService(it);
					Contants.a1 = "ok";
				}
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				if(!"ok".equals(Contants.a1)){
					return;
				}
				Intent it = new Intent();
				it.setClass(GPSService.this, GPSService.class);
				stopService(it);
				Contants.a1 = "";
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				saveAndUpload(location);
			}

		};
	    
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);

	    
    }

	private void saveAndUpload(Location location1) {
		final Location location = location1;

				
				
				if (null != location) {
					
					double locationWd = location.getLatitude();
					double locationJd = location.getLongitude();
					double speed = location.getSpeed();
					long time = location.getTime();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dd = new Date();
					long time1 = dd.getTime();
					
					Locale local = new Locale("Chinese");
					Calendar c = Calendar.getInstance(local);
					
					c.setTimeInMillis(time1);
					
					Date d = new Date(time);
					
					
					
					Log.i("gpsʱ��", sdf.format(d));
					Log.i("calendar", c.get(Calendar.HOUR_OF_DAY)+"");
					

					Gps gps = new Gps();
					gps.setAddressinfo("addressInfo");
					gps.setGettime(sdf.format(d));
					gps.setPhoneNumber(phonenumber);
					gps.setUserName(username);
					gps.setEastimepaycar("eastimepaycar");
					gps.setLocationJd(locationJd+"");
					gps.setLocationWd(locationWd+"");
					gps.setSpeed(speed+"");
					gps.setNumber(SystemUtils.getAllNumber());

					new Thread(new UploadGPS(gps,1)).start();


				} else {
					Log.i(TAG, "�޷���ȡ����");
/*					db = DBHelper.getInstance();
					ContentValues initValues = new ContentValues();

					initValues.put("result", "��ȡʧ��");
					db.insert(Contants.table1, initValues);*/
				}
	}

    @Override
    public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    	return mBinder;
    }

    @Override
    public void onCreate()
    {
/*		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork() // or
		.penaltyLog().build());
StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		.penaltyLog().penaltyDeath().build());*/
    
    	startService();
    	Log.v(tag, "GPSService Started.");
    }

    @Override
    public void onDestroy()
    {
	    //endService();
	    Log.v(tag, "GPSService Ended.");
    }
    
	private void getProvider() {
		//��ȡ����ʵ�provider���ڶ���������ʾ��������ѡ����ֻ�ڿ��õĵ���ѡ��
	    String provider = locationManager.getBestProvider(criteria, true);
	    if(null == provider){
	    	return;
	    }
	    Location location = locationManager.getLastKnownLocation(provider); // ͨ��GPS��ȡλ��

	    saveAndUpload(location);
	}

    public class GPSServiceBinder extends Binder {
		    GPSService getService() {
		    return GPSService.this;
	    }
    }
    
    public void endService()
    {
	    if(locationManager != null && locationListener != null)
	    {
	    	locationManager.removeUpdates(locationListener);
	    }
    }



	@Override
	public void onStart(Intent intent, int startId) {
		//name = intent.getStringExtra("name");
		super.onStart(intent, startId);
	}
    
}

/*					
// �ϴ��������
JSONParser json = new JSONParser();
List<NameValuePair> params = new ArrayList<NameValuePair>();
params.add(new BasicNameValuePair("gettime", gps.getGettime()));

params.add(new BasicNameValuePair("phoneNumber", gps.getPhoneNumber()));
params.add(new BasicNameValuePair("eastimepaycar",
		gps.getEastimepaycar()));
params.add(new BasicNameValuePair("addressInfo",
		gps.getAddressinfo()));
params.add(new BasicNameValuePair("userName", gps.getUserName()));
params.add(new BasicNameValuePair("locationWd", gps.getLocationWd()));
params.add(new BasicNameValuePair("locationJd", gps.getLocationJd()));
params.add(new BasicNameValuePair("speed", gps.getSpeed()));
params.add(new BasicNameValuePair("number", gps.getNumber()));

String result = json.makeHttpRequest(
		Contants.SAVEGPSDATA_URL, Contants.METHOD_POST,
		params);
try {
	JSONObject jsonData = new JSONObject(result);
	String resultStr = jsonData.getString("result");
	if ("ok".equals(resultStr)) {
		Log.i(TAG, "�ϴ��ɹ�");
	} else {
		db = DBHelper.getInstance();
		ContentValues initValues = new ContentValues();
		initValues.put("username", gps.getUserName());
		initValues.put("a1", gps.getSpeed());
		initValues.put("a2", gps.getLocationWd());
		initValues.put("a3", gps.getLocationJd());
		initValues.put("result", "�ƶ�����");
		initValues.put("writetime", gps.getGettime());
		db.insert(Contants.table1, initValues);
		Log.i(TAG, "�ϴ�ʧ��");
	}
} catch (JSONException e) {
	db = DBHelper.getInstance();
	ContentValues initValues = new ContentValues();
	initValues.put("username", gps.getUserName());
	initValues.put("a1", gps.getSpeed());
	initValues.put("a2", gps.getLocationWd());
	initValues.put("a3", gps.getLocationJd());
	initValues.put("result", "�ƶ�����");
	initValues.put("writetime", gps.getGettime());
	db.insert(Contants.table1, initValues);
	Log.i(TAG, "�ϴ��쳣");
	e.printStackTrace();
}

Log.i(TAG, Contants.a2 + "  " + Contants.a3 + " "
		+ SystemUtils.getStr());*/









/*	    TimerTask task = new TimerTask() {
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
		getProvider();
	}
};

Timer timer = new Timer();
timer.schedule(task, 30*1000, 2*60*1000); //ÿ2����ִ��һ��
*/
//locationListener = new GPSServiceListener(db,locationManager);
/*	    handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			Location location = (Location) msg.obj;
			if(null != location){
				db = DBHelper.getInstance();
				ContentValues initValues = new ContentValues();
				
				Contants.a1 = location.getSpeed()+"";
				Contants.a2 = location.getLatitude()+"";
				Contants.a3 = location.getLongitude()+"";
				
				initValues.put("a1", location.getSpeed()+"");
				initValues.put("a2", location.getLatitude()+"");
				initValues.put("a3", location.getLongitude()+"");
				initValues.put("result", "������������");
				db.insert(Contants.table1, initValues);
				db.closeConnection();
				Log.i(TAG, Contants.a2 + "  " + Contants.a3);
			} else {
	            Log.i(TAG, "�޷���ȡ����");
	            db = DBHelper.getInstance();
				ContentValues initValues = new ContentValues();

				initValues.put("result", "��ȡʧ��");
				db.insert(Contants.table1, initValues);
	        }
			return false;
		}
	});*/
