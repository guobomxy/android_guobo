package com.eastime.paycar.receiver;

import com.eastime.paycar.services.GPSService;
import com.eastime.paycar.services.GetJiZhanInfoService;
import com.eastime.paycar.util.Contants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent it = new Intent(context, GPSService.class);
		context.stopService(it);
		Contants.a1 = "";
		Log.i("====StopService=====", "gps���������");
		it = new Intent(context, GetJiZhanInfoService.class);
		context.stopService(it);
		Contants.a2 = "";
		Log.i("====StopService=====", "��վ���������");
		
		
	}

}
