package com.eastime.paycar.activitys;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.eastime.paycar.LoginActivity;
import com.eastime.paycar.PreActivity;
import com.eastime.paycar.R;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ManageActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);
		
		TabHost tabHost = getTabHost();
		
		
		tabHost.addTab(tabHost.newTabSpec("aa").setIndicator("��  ��").setContent(new Intent(this, ReceiveCarActivity.class)));
		
		tabHost.addTab(tabHost.newTabSpec("ba").setIndicator("��  ��").setContent(new Intent(this, PayCarActivity.class)));
		
		tabHost.addTab(tabHost.newTabSpec("ca").setIndicator("��  ѯ").setContent(new Intent(this, SelectActivity.class)));
		
		tabHost.addTab(tabHost.newTabSpec("ea").setIndicator("ϵ  ͳ").setContent(new Intent(this, SystemSettingActivity.class)));
		
		for(int i = 0;i<4;i++){
			
			TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			
			//tv.setBackgroundColor(Color.BLACK);
			//tv.setBackgroundColor(Color.BLACK);
			tv.setTextColor(Color.WHITE);
			
			tv.setPadding(0, 0, 0, 10);
		}
		isTimeOut();
	}
	
	private void isTimeOut(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//������ȡʹ�õ�ʱ��  
				if(SystemUtils.netWorkStatus()){
					JSONParser request = new JSONParser();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("username", Contants.name));
					String result = request.makeHttpRequest(Contants.CHECKTIMEOUT_URL, "POST", params);
					JSONObject json;
					try {
						json = new JSONObject(result);
						result = json.getString("result");
						if("error".equals(result)){  // �ѹ��� ��ת������ҳ��  TODO
							Intent intenti = new Intent(ManageActivity.this, PreActivity.class);
							startActivity(intenti);
							Intent intent2 = new Intent("STOPSERVICE");
							sendBroadcast(intent2);
							finish();
						}
					} catch (JSONException e) {
						
						Toast.makeText(ManageActivity.this, "���ݻ�ȡʧ��", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					//
				}else{
					Toast.makeText(ManageActivity.this, "��������", Toast.LENGTH_LONG).show();
				}//
				
			}
		}).start();
	}
	

}
