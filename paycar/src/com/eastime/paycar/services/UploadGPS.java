package com.eastime.paycar.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.eastime.paycar.bean.Gps;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

public class UploadGPS implements Runnable {

	private Gps gps;
	private int flag;
	private DBHelper db;
	
	private static String TAG = "uploadgps";
	
	public UploadGPS(Gps gps,int flag){
		this.gps = gps;
		this.flag = flag;
	}
	
	
	@Override
	public void run() {
		
		if(!SystemUtils.netWorkStatus()){
			Log.i("uploadjizhan", "没有网络");
			saveGPS();
			return;
		}
		
		// 上传到服务端
		JSONParser json = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("gettime", gps.getGettime()));

		params.add(new BasicNameValuePair("phoneNumber", Contants.name));
		params.add(new BasicNameValuePair("eastimepaycar",
				gps.getEastimepaycar()));
		params.add(new BasicNameValuePair("addressInfo",
				gps.getAddressinfo()));
		params.add(new BasicNameValuePair("userName", Contants.name));
//		params.add(new BasicNameValuePair("locationWd", 30.321281173691+""));
//		params.add(new BasicNameValuePair("locationJd", 120.26666736509+""));
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
				Log.i(TAG, "上传成功");
				if(2 == flag){
					deleteGPS();
				}
			} else {
				if(1 == flag){
					saveGPS();
				}
				Log.i(TAG, "上传失败");
			}
		} catch (JSONException e) {
			if(1 == flag){
				saveGPS();
			}
			Log.i(TAG, "上传异常");
			e.printStackTrace();
		}
		if(1 == flag){
			SystemUtils.uploadAll();
		}
		Log.i(TAG, Contants.a2 + "  " + Contants.a3 + " "
				+ SystemUtils.getStr());
		
	}


	private void saveGPS() {
		db = DBHelper.getInstance();
		ContentValues initValues = new ContentValues();
		initValues.put("username", gps.getUserName());
		initValues.put("a1", gps.getSpeed());
		initValues.put("a2", gps.getLocationWd());
		initValues.put("a3", gps.getLocationJd());
		initValues.put("result", "移动监听");
		initValues.put("number", gps.getNumber());
		initValues.put("writetime", gps.getGettime());
		db.insert(Contants.table1, initValues);
	}
	private void deleteGPS(){
		db = DBHelper.getInstance();
		String whereArgs[] = {gps.getId()+""};
		db.delete(Contants.table1, "id = ?", whereArgs);
	}

}
