package com.eastime.paycar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GetPackageAddedReceriver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("=============================", "12345678901234567890");
		 if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
	            String packageName = intent.getData().getSchemeSpecificPart();
	            Log.i("=========这个包安装成功了=========", packageName);
	            Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
	        }
	}

}
