package com.eastime.paycar.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eastime.paycar.R;
import com.eastime.paycar.adapters.viewholder.DataSelectItemHolder;
import com.eastime.paycar.adapters.viewholder.ListViewHolder;
import com.eastime.paycar.bean.YunDanItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;


public class SelectListViewAdapter extends BaseAdapter {

	private Context aCtx;
	
	private List<YunDanItem> dataMap;
	private LayoutInflater aInFlater;
	
	
	public SelectListViewAdapter(Context ctx,List<YunDanItem> dataMap){
		this.aCtx = ctx;
		this.aInFlater = LayoutInflater.from(aCtx);
		
		this.dataMap = dataMap;
		
	}
	
	
	@Override
	public int getCount() {
		return dataMap.size();
	}

	@Override
	public Object getItem(int position) {
		
		return dataMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DataSelectItemHolder viewHolder;
		
		if(convertView == null){
			viewHolder = new DataSelectItemHolder();
			convertView = aInFlater.inflate(R.layout.data_select, null);
			
			viewHolder.danhao_select = (TextView) convertView.findViewById(R.id.danhao_select);
			viewHolder.time_select = (TextView) convertView.findViewById(R.id.time_select);
			viewHolder.img_select = (TextView) convertView.findViewById(R.id.img_select);
						
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (DataSelectItemHolder) convertView.getTag();
			
		}
		
			
			String danhaoNumber = dataMap.get(position).getNumber();
			viewHolder.danhao_select.setText("单号：" + danhaoNumber.substring(danhaoNumber.indexOf(':')+1));
			
			String createtime = dataMap.get(position).getCreateTime().substring(0, 11);
			String payTime = null == dataMap.get(position).getPayTime()?"xxxx-xx-xx xx:xx:xx ":dataMap.get(position).getPayTime();
			viewHolder.time_select.setText(createtime+"至 "+payTime.substring(0, 11));
			
			int issucc = dataMap.get(position).getIssucc();
			int isupload = dataMap.get(position).getIsUpload(); //0  1 
			if(2 != issucc && 0 == isupload){
				//交车完成 未上传的 
				viewHolder.img_select.setText("未上传");
				viewHolder.img_select.setTextColor(Color.RED);
			}else if(2 != issucc && 1 == isupload){
				//交车完成并上传
				viewHolder.img_select.setText("已完成");
				viewHolder.img_select.setTextColor(Color.GREEN);
			}else{
				//交车未完成
				viewHolder.img_select.setText("未完成");
				viewHolder.img_select.setTextColor(Color.YELLOW);
			}
			
			
		return convertView;
	}

}
