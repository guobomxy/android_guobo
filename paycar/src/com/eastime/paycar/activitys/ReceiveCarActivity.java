package com.eastime.paycar.activitys;

import java.util.ArrayList;
import java.util.List;

import com.eastime.paycar.LoginActivity;
import com.eastime.paycar.R;
import com.eastime.paycar.R.id;
import com.eastime.paycar.R.layout;
import com.eastime.paycar.adapters.ListViewAdapter;
import com.eastime.paycar.bean.YunDanItem;
import com.eastime.paycar.services.GPSService;
import com.eastime.paycar.services.GetJiZhanInfoService;
import com.eastime.paycar.services.UploadYundan;
import com.eastime.paycar.util.Contants;
import com.eastime.paycar.util.DBHelper;
import com.eastime.paycar.util.SystemUtils;
import com.eastime.paycar.zxingdecodeing.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveCarActivity extends Activity {

	private Button saomiao;
	private Button shoushu;
	private ListView listView;
	public List<String> dataMap = new ArrayList<String>();
	ListViewAdapter lvAdapter;
	DBHelper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.aactivity);

		saomiao =  (Button) findViewById(R.id.saomiaoyundan_receive);
		// 添加触摸事件 跳转到扫描程序
		saomiao.setOnClickListener(onClickListener);
		shoushu = (Button) findViewById(R.id.shoushuyundan_receive);
		shoushu.setOnClickListener(onClickListener);

		// 获取listview
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemLongClickListener(onItemLongClick);

		// 获取本地表单数据
		getDataFromLocal();
		setDataToView();
		
		if(!"".equals(SystemUtils.getAllNumber())){
			startService();
		}
		
	}

	private void startService() {
		if("".equals(Contants.a1)){
			//验证gps是否打开  不管gps开不开 服务都打开  确保中途打开gps没服务
			if(!openGPS()){
				Toast.makeText(ReceiveCarActivity.this, "请其开启GPS", Toast.LENGTH_SHORT).show();
//			    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//			    startActivityForResult(intent,0);
//			    return false;
			}
			Intent it = new Intent();
			it.setClass(ReceiveCarActivity.this, GPSService.class);
			startService(it);
			Contants.a1 = "ok";
		}
		if("".equals(Contants.a2)){
			Intent ii = new Intent(ReceiveCarActivity.this, GetJiZhanInfoService.class);
			startService(ii);
			Contants.a2 = "ok";
		}
	}

	private void getDataFromLocal() {
		db = DBHelper.getInstance();
		dataMap.clear();
		String[] columns = { "number" };
		String selection = "isdel = ?";
		String[] selectionArgs = { "0" };
		String orderBy = "createtime desc";

		Cursor cursor = db.findList(false, Contants.table2, columns, selection,
				selectionArgs, null, null, orderBy, null);
		// Cursor cursor = db.findList(false, Contants.table2);
		while (cursor.moveToNext()) {
			String danHaoNumber = cursor.getString(0);
			dataMap.add(danHaoNumber);
		}
		cursor.close();

	}

	// 长按监听事件
	private AdapterView.OnItemLongClickListener onItemLongClick = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {

			final int position1 = position;

			AlertDialog.Builder dialog = new AlertDialog.Builder(
					ReceiveCarActivity.this);

			dialog.setTitle("是否删除?");

			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							// 数据哭删除
							deleteDanHao(position1);
							// 重新显示到界面上
							setDataToView();
							dialog.dismiss();
							Toast.makeText(ReceiveCarActivity.this, "删除成功！",
									Toast.LENGTH_LONG).show();
						}

					});

			dialog.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

						}
					});

			dialog.create().show();

			return false;
		}
	};
	
	//按钮触发事件
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.saomiaoyundan_receive:
				Intent intent = new Intent();
				intent.setClass(ReceiveCarActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 1);
				break;
			case R.id.shoushuyundan_receive:
				alertDialog(1);
				break;
			}
			
		}
	};

	// 扫描返回后 处理程序
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);

		if (20 == resultCode) {

			String result = data.getStringExtra("result");
			if (null == result || "".equals(result)) {
				Toast.makeText(this, "扫描失败", Toast.LENGTH_LONG).show();

				// 此处添加手工输入条码功能 dialog
				alertDialog(2);
				return;
			}

			Toast.makeText(this, result, 2000).show();

			saveResult(result);

		}
	}

	// 显示手动输入的界面
	protected void alertDialog(int flag) {
		// 此处添加手工输入条码功能 dialog
		AlertDialog.Builder dialog = new Builder(this);

		// LayoutInflater inflater = LayoutInflater.from(this);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View sgtmT = inflater.inflate(R.layout.dialog_tiaomao, null);
		TextView tip = (TextView) sgtmT.findViewById(R.id.smtip);
		if(flag == 1){
			dialog.setTitle("手工输入");	
			tip.setText("请手动输入条码");
		}else{
			dialog.setTitle("识别错误");			
			tip.setText("未识别到条码，请手动输入");
		}
		
		dialog.setView(sgtmT);

		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				EditText e = (EditText) sgtmT.findViewById(R.id.sgtm);
				String result = e.getText().toString();
				// 必要的时候要在这里添加条码校验 TODO
				saveResult(result);

			}
		});

		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog.dismiss();
			}
		});

		dialog.create().show();
	}

	// 保存结果到数据库 并调用显示函数
	protected void saveResult(String result) {
		db = DBHelper.getInstance();
		String s1 = "number = ?";
		String s2[] = { result };
		// 先判断单号存在不存在
		Cursor cursor = db.findOne(false, Contants.table2, null, s1, s2, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			Toast.makeText(this, "单号已存在！", Toast.LENGTH_LONG).show();
			return;
		}
		cursor.close();
		
		ContentValues values = new ContentValues();
		values.put("number", result);
		values.put("isdel", 0);
		values.put("issucc", 2);
		values.put("isupload", 0);
		long r = db.insert(Contants.table2, values);
		if (r < 0) {
			Toast.makeText(this, "数据保存失败!", Toast.LENGTH_LONG).show();
			
			return;
		}else{
			//更新到界面
			dataMap.add(result);
			setDataToView();
			//上传单号信息  TODO
			YunDanItem item = new YunDanItem();
			item.setNumber(result);
			item.setPayTime(SystemUtils.getStr());
			item.setIssucc(2);
			item.setImgpath("");
			item.setSuccinfo("");
			item.setFailinfo("");
			
			new Thread(new UploadYundan(item, null,"add")).start();
			
		}
		startService();

	}
	private boolean openGPS() {
    	System.out.println("openGPS");
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS已开启", Toast.LENGTH_SHORT).show();

            return true;
        }else{
        	return false;
        }
    }

	// 显示数据到界面
	protected void setDataToView() {
		lvAdapter = new ListViewAdapter(this, dataMap);
		listView.setAdapter(lvAdapter);
	}

	// 删除操作
	private void deleteDanHao(int position1) {
		db = DBHelper.getInstance();
		String deleteData = dataMap.get(position1);
		String whereClause = "number = ?";
		String[] whereArgs = { deleteData };
		if (db.delete(Contants.table2, whereClause, whereArgs)) {
			dataMap.remove(position1);
		} else {
			Toast.makeText(this, "单号删除失败！", Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	protected void onResume() {

		// 获取本地表单数据
		getDataFromLocal();
		setDataToView();

		super.onResume();
	}

}
