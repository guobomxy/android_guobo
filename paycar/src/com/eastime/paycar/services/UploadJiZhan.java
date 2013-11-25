package com.eastime.paycar.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.eastime.paycar.bean.JiZhan;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

public class UploadJiZhan {

	private JiZhan jizhan;
	private int flag;
	private DBHelper db;
	private static String TAG = "uploadjizhan";
	
	public UploadJiZhan(JiZhan jizhan ,int flag){
		this.jizhan = jizhan;
		this.flag = flag;
	}
	

	public void run() {
    	
		//检测是否有网
		if(!SystemUtils.netWorkStatus()){
			Log.i("uploadjizhan", "没有网络");
			saveJizhan();
			return;
		}
		
        JSONParser httpJson = new JSONParser();
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userName", Contants.name));
        params.add(new BasicNameValuePair("phoneNumber", Contants.name));
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
				if(2 == flag){
					deleteJizhan();
				}
			}else{
				if(1 == flag){
					saveJizhan();
				}
		        
				Log.i(TAG, "上传失败");
			}
		} catch (JSONException e) {
			if(1 == flag){
				saveJizhan();
			}
			Log.i(TAG, "上传异常");
			e.printStackTrace();
		}
        if(1 == flag){
        	SystemUtils.uploadAll();
        }
        

	}
	
	private void saveJizhan() {
		db = DBHelper.getInstance();
		ContentValues values = new ContentValues();
		values.put("username",Contants.name);
		values.put("mcc", jizhan.getMcc());
		values.put("mnc", jizhan.getMnc());
		values.put("cid", jizhan.getCid());
		values.put("lac", jizhan.getLac());
		values.put("bid", jizhan.getBid());
		values.put("sid", jizhan.getSid());
		values.put("nid", jizhan.getNid());
		values.put("number", jizhan.getNumber());
		values.put("createtime", jizhan.getCreatetime());

		db.insert(Contants.table3, values);
	}
	
	private void deleteJizhan(){
		db = DBHelper.getInstance();
		String whereArgs[] = {jizhan.getId() + ""};
		db.delete(Contants.table3, "id = ?", whereArgs);
	}
	

}
