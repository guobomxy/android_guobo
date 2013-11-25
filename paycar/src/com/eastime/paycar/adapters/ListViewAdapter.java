package com.eastime.paycar.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eastime.paycar.R;
import com.eastime.paycar.adapters.viewholder.ListViewHolder;

import android.content.Context;
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
import android.widget.TextView;


public class ListViewAdapter extends BaseAdapter {

	private Context aCtx;
	
	private List<String> dataMap;
	private LayoutInflater aInFlater;
	
	
	public ListViewAdapter(Context ctx,List<String> dataMap){
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

		ListViewHolder viewHolder;
		
		if(convertView == null){
			viewHolder = new ListViewHolder();
			convertView = aInFlater.inflate(R.layout.data_saomiao, null);
			
			viewHolder.danhao = (TextView) convertView.findViewById(R.id.t1);
			
			convertView.setTag(viewHolder);
			
		}else{
			viewHolder = (ListViewHolder) convertView.getTag();
			

		}
		
			
			String danhaoNumber = dataMap.get(position);
			viewHolder.danhao.setText("单号：" + danhaoNumber.substring(danhaoNumber.indexOf(':')+1));
		
	
		return convertView;
	}

}
