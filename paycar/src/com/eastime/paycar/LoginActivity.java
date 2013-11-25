package com.eastime.paycar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.eastime.paycar.activitys.ManageActivity;
import com.eastime.paycar.activitys.RegistActivity;
import com.eastime.paycar.services.GPSService;
import com.eastime.paycar.services.GetJiZhanInfoService;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText userName;
	private EditText pwd;
	private Button bt;
	private Button registbt;
	
	private String user = "";
	private String password = "";
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //�����ޱ���  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
//        //����ȫ��  
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		
		//��ʼ�����������
		saveResult();
		

		
		//����û��Ƿ��ѵ�¼  ��½���˾�ֱ����ת��������
		isLogin();
		
		userName = (EditText) findViewById(R.id.editText2);
		pwd = (EditText) findViewById(R.id.editText1);
		bt = (Button) findViewById(R.id.longinbt);
		bt.setOnClickListener(new btListener());
		registbt = (Button) findViewById(R.id.registbtn);
		registbt.setOnClickListener(new btListener());
		
		
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch(msg.what){
				case 1:
					String result =  (String) msg.obj;//{"result":"ok"}
					
					try {
						JSONObject json = new JSONObject(result);
						result = json.getString("result");
						//result = "ok";
						
					} catch (JSONException e) {
						Toast.makeText(LoginActivity.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
						e.printStackTrace();
						return false;
					}
					
					if("ok".equals(result)){
						Contants.name = userName.getText().toString();
						
						saveResult();
						
						gotoManage();
					}else if("error".equals(result)){
						Toast.makeText(LoginActivity.this, "��¼ʧ��", Toast.LENGTH_LONG).show();
						
					}else{
						Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
					}
					
					break;
				case 2:
					Toast.makeText(LoginActivity.this, "��������", Toast.LENGTH_LONG).show();
					break;
				}
				return false;
			}


		});
		
	}
	

	
	private void gotoManage() {

		// ����һ��Intent����
		Intent intent = new Intent();
		// ����Intent����Ҫ������Activity
		intent.setClass(LoginActivity.this, ManageActivity.class);
		// ͨ��Intent������������һ��Activity
		LoginActivity.this.startActivity(intent);
		
		finish();
	}
	
	private void isLogin(){
		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editer = settings.edit();
		String username = settings.getString("usernameflag", "");
		if(!"".equals(username)){
			Contants.name = username;
			gotoManage();
		}
	}

	class btListener implements OnClickListener {
		// ���ɸ���Ķ��󣬲�����ע�ᵽ�ؼ��ϡ�����ÿؼ����û����£��ͻ�ִ��onClick����
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.longinbt:
				user = null == userName.getText().toString()?"":userName.getText().toString();
				password = null == pwd.getText().toString()?"":pwd.getText().toString();
				
				if("".equals(user) || "".equals(password)){
					Toast.makeText(LoginActivity.this, "�����롣����", 2000).show();
					return;
				}
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						//������֤
						if(SystemUtils.netWorkStatus()){
							JSONParser request = new JSONParser();
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("phoneNumber", user));
							params.add(new BasicNameValuePair("pwd", password));
							String result = request.makeHttpRequest(Contants.LOGIN_URL, "POST", params);
							
							Message msg = new Message();
							msg.what = 1;
							msg.obj = result;
							handler.sendMessage(msg);
						}else{
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
						}
					}
				}).start();
				
				
				
				break;
			case R.id.registbtn:
				//ʵ��ע��ҵ�� ��ת��ע��ҳ��
				Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	private void saveResult() {
		
		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editer = settings.edit();
		editer.putString("ip", Contants.IP);
		editer.putString("port", Contants.PORT);
		editer.putString("url_ftp", Contants.URL_FTP);
		editer.putString("port_ftp", Contants.PORT_FTP);
		editer.putString("username", Contants.USERNAME);
		editer.putString("password", Contants.PASSWORD);
		editer.putString("remotepath", Contants.REMOTEPATH);
		editer.putString("usernameflag", Contants.name);
		editer.commit();
	}
}

/*		//��֤gps�Ƿ��  ����gps������ ���񶼴�  ȷ����;��gpsû����
if(!openGPS()){
	Toast.makeText(Login.this, "���俪��GPS", Toast.LENGTH_SHORT).show();
//    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//    startActivityForResult(intent,0);
//    return false;
}
Intent it = new Intent();
it.setClass(Login.this, GPSService.class);
startService(it);

Intent ii = new Intent(Login.this, GetJiZhanInfoService.class);
startService(ii);*/

/*    public void turnGPSOn(){

Toast.makeText(this, "���뵽��turngpson������", Toast.LENGTH_SHORT).show();

Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");

intent.putExtra("enabled", true);
this.sendBroadcast(intent);
String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

if(!provider.contains("gps")){
	final Intent poke = new Intent();
	poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	
	poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	poke.setData(Uri.parse("3"));
	this.sendBroadcast(poke);
}
}*/

/*	private boolean openGPS() {
System.out.println("openGPS");
LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
    Toast.makeText(this, "GPS�ѿ���", Toast.LENGTH_SHORT).show();

    return true;
}else{
	return false;
}
}*/
