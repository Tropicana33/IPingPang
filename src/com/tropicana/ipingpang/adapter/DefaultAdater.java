package com.tropicana.ipingpang.adapter;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.base.impl.BaseVedioPager.ViewHolder;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class DefaultAdater<T> extends BaseAdapter {

	protected BitmapUtils utils;
	protected Context context;
	protected ArrayList<T> datas;
	
	public DefaultAdater(Context context,ArrayList<T> datas){
		this.context=context;
		this.datas=datas;
		utils=new BitmapUtils(context);
		utils.configDefaultLoadingImage(R.drawable.icon_default);
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		convertView=getContentView(position, convertView, parent);
		return convertView;
	}

	public abstract View getContentView(int position, View convertView, ViewGroup parent);

	
}
