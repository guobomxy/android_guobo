package com.eastime.paycar.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.eastime.paycar.R;
import com.eastime.paycar.activitys.PayCarActivity;
import com.eastime.paycar.bean.YunDanItem;
import com.eastime.paycar.util.AppContext;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

public class UploadYundan implements Runnable {

	private YunDanItem item;
	private Handler handler;
	
	boolean failOrSuccFlag = false;
	int isupload = 0;
	DBHelper db ;
	private String optFlag;
	
	public UploadYundan(YunDanItem item,Handler handler,String optFlag){
		this.handler = handler;
		this.item = item;
		this.optFlag = optFlag;
	}
	
	@Override
	public void run() {

		boolean flag = false;		
		String strImgPath = item.getImgpath();
		String absNumber = item.getNumber();
		if(item.getIssucc() == 1){
			failOrSuccFlag = true;
		}
		
		JSONParser uploadJson = new JSONParser();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		String danhaoinfo = failOrSuccFlag?item.getSuccinfo():item.getFailinfo();
		String receivecartime = item.getPayTime();
		String fileNamePath = "";
		String fileName = "";
		if(1 == item.getIssucc()){
			fileNamePath = strImgPath.substring(0,strImgPath.lastIndexOf('/')+1);
			fileName = strImgPath.substring(strImgPath.lastIndexOf('/')+1);
		}

		params.add(new BasicNameValuePair("imagepath", "".equals(fileName)?"":Contants.REMOTEPATH + fileName));
		params.add(new BasicNameValuePair("userName", Contants.name));
		params.add(new BasicNameValuePair("phoneNumber", Contants.name));
		params.add(new BasicNameValuePair("number", absNumber));
		//params.add(new BasicNameValuePair("status", failOrSuccFlag?"1":"0"));
		params.add(new BasicNameValuePair("status", item.getIssucc()+""));
		params.add(new BasicNameValuePair("info", danhaoinfo));
		params.add(new BasicNameValuePair("receivecartime", receivecartime));
		params.add(new BasicNameValuePair("paycartime", SystemUtils.getStr()));
		params.add(new BasicNameValuePair("eastimepaycar", ""));
		params.add(new BasicNameValuePair("optFlag", optFlag));
		
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
		Message m = new Message();
		m.what = 1;
		String result = "";
		if(flag){ //�ɹ��Ļ�  ftp�ϴ�ͼƬ
			if(1 == item.getIssucc()){
				result = uploadJson.uploadFTP(fileNamePath, fileName);
				Log.i("ftp�ϴ���Ϣ", result);
				if(!"1".equals(result)){
					//�ϴ��ɹ� TODO
					isupload = 0;
					m.what = 2;
				}else{
					isupload = 1;
				}
			}else{
				isupload = 1;
			}
		}
		if(null != handler){
			handler.sendMessage(m);			
		}
		
		if("update".equals(optFlag)){
			// ��ɽ�����ť ���浽�������ݿ�
			updateAll();
		}
		//�ϴ�δ�ϴ��Ķ�λ��Ϣ
		SystemUtils.uploadAll();
		
	}
		
	// �����������ݵ����ݿ�
	protected boolean updateAll() {
		// 
		db = DBHelper.getInstance();
		// number imgpath succinfo failinfo isdel
		ContentValues values = new ContentValues();
		
		values.put("isdel", 1);
		if (failOrSuccFlag) { // �ɹ�����
			String succinfo = item.getSuccinfo();
			values.put("imgpath", item.getImgpath());
			values.put("succinfo", succinfo);
			values.put("issucc", 1);
		} else {
			String failinfo = item.getFailinfo();
			values.put("failinfo", failinfo);
			values.put("issucc", 0);
		}
		values.put("paytime", SystemUtils.getStr()); // ����ʱ���
		values.put("isdel", 1); // Ϊ1ʱ ���Ϊɾ��
		values.put("isupload", isupload);
		String whereClause = "number = ?";
		String[] whereArgs = { item.getNumber() };
		
		boolean iss = db
				.update(Contants.table2, values, whereClause, whereArgs);
		return iss;
	}
	

}
