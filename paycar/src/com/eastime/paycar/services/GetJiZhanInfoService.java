package com.eastime.paycar.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.eastime.paycar.bean.JiZhan;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

public class GetJiZhanInfoService extends Service {

	private String username;
	private String phonenumber;
	
	String mcc = "-1";
	String mnc = "-1";
	//电信
	int bid = -1;
	int sid = -1;
	int nid = -1;
	//移动联通
	int cid = -1;
	int lac = -1;
	private final IBinder mBinder = new GGetJiZhanInfoServiceBinder();
	private final String TAG = "GETJIZHANINFOSERVICE";
	DBHelper db;
	
	boolean flag = true;	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		username = Contants.name;
		phonenumber = Contants.name;
		startService();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	 public void startService(){
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
				/** 调用API获取基站信息 */
		        TelephonyManager mTelNet = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		        
//		       String number = mTelNet.getLine1Number();
//		       String nn = mTelNet.getDeviceId();
		        
		        
		        try{
		        	GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		            String operator = mTelNet.getNetworkOperator();
		            mcc = operator.substring(0, 3);
		            mnc = operator.substring(3);
		            cid = location.getCid();
		            lac = location.getLac();
		        }catch(Exception e){
		        	try{
			        	CdmaCellLocation location1 = (CdmaCellLocation) mTelNet.getCellLocation();
			            String operator = mTelNet.getNetworkOperator();
			            mcc = operator.substring(0, 3);
			            mnc = operator.substring(3);
	
			            bid = location1.getBaseStationId();
			            sid = location1.getSystemId();
			            nid = location1.getNetworkId();
		        	}catch(Exception e1){
		        		flag = false;
		        		Log.i(TAG, "获取基站信息失败！！！");
		        	}
		        }
		        //此处判断 是否获取了基站信息  没获取或者获取失败就不上传
		        if(flag){
		        	

			        JiZhan jizhan = new JiZhan();
			        jizhan.setUserName(username);
			        jizhan.setPhoneNumber(phonenumber);
			        jizhan.setCid(cid+"");
			        jizhan.setBid(bid+"");
			        jizhan.setSid(sid+"");
			        jizhan.setMcc(mcc);
			        jizhan.setMnc(mnc);
			        jizhan.setLac(lac+"");
			        jizhan.setNid(nid+"");
			        jizhan.setCreatetime(SystemUtils.getStr());
			        jizhan.setEastuimepaycar("eastimepaycar");
			        jizhan.setNumber(SystemUtils.getAllNumber());
			        
			        new UploadJiZhan(jizhan,1).run();
			  
			        
		        }
			}
		};
		
		
		Timer timer = new Timer();
		timer.schedule(task, 0,5*60*1000); //1分钟执行一次 获取基站信息
		
	}
	 
	    public class GGetJiZhanInfoServiceBinder extends Binder {
	    	GetJiZhanInfoService getService() {
		    return GetJiZhanInfoService.this;
	    }
    }
	    
	    

}
/*			        JSONParser httpJson = new JSONParser();

List<NameValuePair> params = new ArrayList<NameValuePair>();
params.add(new BasicNameValuePair("userName", jizhan.getUserName()));
params.add(new BasicNameValuePair("phoneNumber", jizhan.getPhoneNumber()));
params.add(new BasicNameValuePair("number", jizhan.getNumber()));
params.add(new BasicNameValuePair("eastimepaycar", jizhan.getEastuimepaycar()));

params.add(new BasicNameValuePair("mcc", jizhan.getMcc()));
params.add(new BasicNameValuePair("mnc", jizhan.getMnc()));
params.add(new BasicNameValuePair("cid", jizhan.getCid()));
params.add(new BasicNameValuePair("lac", jizhan.getLac()));
params.add(new BasicNameValuePair("bid", jizhan.getBid()));
params.add(new BasicNameValuePair("sid", jizhan.getSid()));
params.add(new BasicNameValuePair("nid", jizhan.getNid()));
params.add(new BasicNameValuePair("createtime", jizhan.getCreatetime()));

String result = httpJson.makeHttpRequest(Contants.SAVEJIZHAN_URL, Contants.METHOD_POST, params);
try {
	JSONObject json = new JSONObject(result);
	result = json.getString("result");
	if("ok".equals(result)){
		Log.i(TAG, "上传成功");
	}else{
        saveJizhan(jizhan);
		Log.i(TAG, "上传失败");
	}
} catch (JSONException e) {
    saveJizhan(jizhan);
	Log.i(TAG, "上传异常");
	e.printStackTrace();
}*/

/*			private void saveJizhan(JiZhan jizhan) {
db = DBHelper.getInstance();
ContentValues values = new ContentValues();
values.put("username",jizhan.getUserName());
values.put("mcc", jizhan.getMcc());
values.put("mnc", jizhan.getMnc());
values.put("cid", jizhan.getCid());
values.put("lac", jizhan.getLac());
values.put("bid", jizhan.getBid());
values.put("sid", jizhan.getSid());
values.put("nid", jizhan.getNid());
values.put("createtime", jizhan.getCreatetime());

Log.i("3333", lac+""+ " "+ SystemUtils.getStr());
db.insert(Contants.table3, values);
}*/
