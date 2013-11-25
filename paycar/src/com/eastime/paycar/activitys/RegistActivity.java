package com.eastime.paycar.activitys;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eastime.paycar.R;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.JSONParser;
import com.eastime.paycar.util.SystemUtils;

public class RegistActivity extends Activity {

	private TextView tip;
	private EditText userNameT;
	private EditText phoneNumberT;
	private EditText pwd1T;
	private EditText pwd2T;
	private Button registBtn;
	private Button fanhuiBtn;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist);
		
		tip = (TextView) findViewById(R.id.tip_regist);
		userNameT = (EditText) findViewById(R.id.username_regist);
		phoneNumberT = (EditText) findViewById(R.id.phonenumber_regist);
		//phoneNumberT.setOnTouchListener(onTouchListener);
		pwd1T = (EditText) findViewById(R.id.pwd1_regist);
		pwd2T = (EditText) findViewById(R.id.pwd2_regist);
		
		registBtn = (Button) findViewById(R.id.querenregist);
		fanhuiBtn = (Button) findViewById(R.id.fanhui_registui);
		
		registBtn.setOnClickListener(onClickListener);
		fanhuiBtn.setOnClickListener(onClickListener);
		
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				switch(msg.what){
				case 0:
					//注册成功
					Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_LONG).show();
					finish();
					break;
				case 1:
					//失败
					Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_LONG).show();
					break;
				case 2:
					//已存在
					Toast.makeText(RegistActivity.this, "用户已经存在", Toast.LENGTH_LONG).show();
					break;
				}
				return false;
			}
		});
		
	}

/*	View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			String c = "";
			switch(v.getId()){
			case R.id.phonenumber:
				c = userNameT.getText().toString();
				if(null == c || "".equals(c)){
					tip.setTextColor(Color.RED);
					tip.setText("请输入昵称");
				}
				break;
			case R.id.pwd1_regist:
				break;
			case R.id.pwd2_regist:
				break;
			}
			return false;
		}
	};*/
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.querenregist:
				//实现注册
				goRegist();
				break;
			case R.id.fanhui_registui:
				//实现返回
				finish();
				break;
			}
		}

	};
	
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	private void goRegist() {
		tip.setText("");
		//获取注册信息
		String phoneNumber = phoneNumberT.getText().toString();
		if(null == phoneNumber || "".equals(phoneNumber)){
			tip.setTextColor(Color.RED);
			tip.setText("请输入电话号码");
			return;
		}
		String pwd1 = pwd1T.getText().toString();
		if(null == pwd1 || "".equals(pwd1)){
			tip.setTextColor(Color.RED);
			tip.setText("请输入密码");
			return;
		}
		String pwd2 = pwd2T.getText().toString();
		if(null == pwd2 || "".equals(pwd2)){
			tip.setTextColor(Color.RED);
			tip.setText("请输入确认密码");
			return;
		}
		String userName = userNameT.getText().toString();
		if(null == userName || "".equals(userName)){
			tip.setTextColor(Color.RED);
			tip.setText("请输入昵称");
			return;
		}
		if(!pwd1.equals(pwd2)){
			tip.setTextColor(Color.RED);
			pwd2T.setText("");
			tip.setText("确认密码不对 请重输");
			return;
		}
		
		//数据校验后 联网校验
		if(!SystemUtils.netWorkStatus()){
			Toast.makeText(RegistActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
			return;
		}
		
		params.add(new BasicNameValuePair("userName", userName));
		params.add(new BasicNameValuePair("phoneNumber", phoneNumber));
		params.add(new BasicNameValuePair("pwd", pwd1));
		params.add(new BasicNameValuePair("eastimepaycar", "eastimepaycar"));
		new Thread(new Runnable() {
			Message msg = new Message();
			@Override
			public void run() {
				// TODO Auto-generated method stub
				JSONParser httpJson = new JSONParser();
				String jsonResult = httpJson.makeHttpRequest(Contants.SAVEUSERS_URL, Contants.METHOD_POST, params);
				
				try {
					JSONObject json = new JSONObject(jsonResult);
					String result = (String) json.get("result");
					if("0".equals(result)){
						//注册成功
						msg.what = 0;
					}else if("1".equals(result)){
						//注册失败
						msg.what = 1;
					}else{
						//用户名已存在
						msg.what = 2;
					}
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}).start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.regist, menu);
		return true;
	}

}
