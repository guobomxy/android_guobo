package com.eastime.paycar.activitys;

import com.eastime.paycar.R;
import com.eastime.paycar.util.Contants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

public class SystemSettingActivity extends Activity{

	private TextView serverTv;
	private TextView localTv;
	private EditText ipev;
	private EditText portev;
	private EditText ftpip;
	private EditText ftpport;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.eactivity);
		
		serverTv = (TextView) findViewById(R.id.serverconfig);
		localTv = (TextView) findViewById(R.id.localconfig);
		serverTv.setOnTouchListener(onTouchListener);
		localTv.setOnTouchListener(onTouchListener);
		
		
		
	}
	
	protected OnTouchListener onTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(v.getId()){
			case R.id.serverconfig:
				
				alertDialog();
				
				break;
			case R.id.localconfig:
				break;
			}
			return false;
		}
	};
	
	protected void alertDialog(){
			
			AlertDialog.Builder dialog = new Builder(this);
			dialog.setTitle("����");
			
			//LayoutInflater inflater = LayoutInflater.from(this);
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View sgtmT = inflater.inflate(R.layout.settingsxml, null);
			
			dialog.setView(sgtmT);
			ipev = (EditText) sgtmT.findViewById(R.id.ip);
			portev = (EditText) sgtmT.findViewById(R.id.port);
			ftpip = (EditText) sgtmT.findViewById(R.id.ftpip);
			ftpport = (EditText) sgtmT.findViewById(R.id.ftpport);
			
			setResult();
			dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//��ȡip port
					
					String ip = ipev.getText().toString();
					String port = portev.getText().toString();
					String ftpIp = ftpip.getText().toString();
					String ftpPort = ftpport.getText().toString();
					saveResult(ip,port,ftpIp,ftpPort);
					
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

	private void saveResult(String ip, String port,String ftpIp,String ftpPort) {
		
		SharedPreferences settings = getSharedPreferences("setting", 0);
		SharedPreferences.Editor editer = settings.edit();
		Contants.IP = ip;
		Contants.PORT = port;
		Contants.PORT_FTP = ftpPort;
		Contants.URL_FTP = ftpIp;
		editer.putString("ip", ip);
		editer.putString("port", port);
		editer.putString("ftpip", ftpIp);
		editer.putString("ftpport", ftpPort);
		editer.commit();		
	}
	private void setResult() {
		
		SharedPreferences settings = getSharedPreferences("setting", 0);
		String ip = null == settings.getString("ip", "0.0.0.0") ? "":settings.getString("ip", "0.0.0.0");
		String port = null == settings.getString("port", "-1")?"":settings.getString("port", "-1");
		String ftpIp = null == settings.getString("url_ftp", "-1")?"":settings.getString("url_ftp", "-1");
		String ftpPort = null == settings.getString("port_ftp", "-1")?"":settings.getString("port_ftp", "-1");
		ipev.setText(ip);
		portev.setText(port);
		ftpip.setText(ftpIp);
		ftpport.setText(ftpPort);
	}
	
}

