package com.eastime.paycar.util;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.eastime.paycar.bean.Gps;
import com.eastime.paycar.bean.JiZhan;
import com.eastime.paycar.services.UploadGPS;
import com.eastime.paycar.services.UploadJiZhan;

public class SystemUtils {

	public static String getStr(){
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH)+1;
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		
		return ""+year+"-"+f(month)+"-"+f(day)+" "+f(hour)+":"+f(minute)+":"+f(second);
	}
	private static String f(int aa){
		return aa<10?("0"+aa):aa+"";
	}
	
	
	//检测是否有网络
	/**
	 * 检测是否有网络
	 * @return  true 为有的
	 */
	public static boolean netWorkStatus() {
		  boolean netSataus = false;
		  ConnectivityManager cwjManager = (ConnectivityManager)AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		  cwjManager.getActiveNetworkInfo();
		  if (cwjManager.getActiveNetworkInfo() != null) {
			  netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		  }
		  return netSataus;
		 }
	
	public String getIMET(){
		
		TelephonyManager manager = (TelephonyManager) AppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
		
	}
	
	public static void uploadAll(){
		
		DBHelper db = DBHelper.getInstance();
		Cursor cursor = db.findList(false, Contants.table1);
		while(cursor.moveToNext()){
			Gps gps = new Gps();
			//username  ,a1  ,a2  ,a3  ,result  ,writetime  
			gps.setId(cursor.getInt(0));
			gps.setUserName(cursor.getString(1));
			gps.setPhoneNumber(cursor.getString(1));
			gps.setSpeed(cursor.getString(2));
			gps.setLocationWd(cursor.getString(3));
			gps.setLocationJd(cursor.getString(4));
			gps.setEastimepaycar(cursor.getString(5));
			gps.setGettime(cursor.getString(6));
			gps.setNumber(cursor.getString(7));
			new Thread(new UploadGPS(gps, 2)).start();
		}
		cursor.close();
		Cursor cursor1 = db.findList(false, Contants.table3);
		while(cursor1.moveToNext()){
			JiZhan jizhan = new JiZhan();
			//id ,username  ,mcc  ,mnc  ,nid  ,sid  ,cid  ,bid  ,lac  ,createtime  
			jizhan.setId(cursor1.getInt(0));
			jizhan.setUserName(cursor1.getString(1));
			jizhan.setMcc(cursor1.getString(2));
			jizhan.setMnc(cursor1.getString(3));
			jizhan.setNid(cursor1.getString(4));
			jizhan.setSid(cursor1.getString(5));
			jizhan.setCid(cursor1.getString(6));
			jizhan.setBid(cursor1.getString(7));
			jizhan.setLac(cursor1.getString(8));
			jizhan.setCreatetime(cursor1.getString(9));
			jizhan.setNumber(cursor1.getString(10));
			jizhan.setEastuimepaycar("eastimepaycar");
			jizhan.setPhoneNumber(cursor1.getString(1));
			new UploadJiZhan(jizhan, 2).run();
		}
		cursor1.close();
		
	}
	
	public static String getAllNumber(){
		
		String all = "";
		DBHelper db = DBHelper.getInstance();
		String[] columns = { "number" };
		String selection = "isdel = ?";
		String[] selectionArgs = { "0" };
		String orderBy = "createtime desc";

		Cursor cursor = db.findList(false, Contants.table2, columns, selection,
				selectionArgs, null, null, orderBy, null);
		// Cursor cursor = db.findList(false, Contants.table2);
		while (cursor.moveToNext()) {
			String danHaoNumber = cursor.getString(0);
			all += danHaoNumber + ";";
		}
		cursor.close();
		return all;
	}
	
	
}
