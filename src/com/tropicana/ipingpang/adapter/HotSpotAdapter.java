package com.tropicana.ipingpang.adapter;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.bean.HomeBean.MyTopInfo;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HotSpotAdapter extends BaseAdapter {

	private BitmapUtils utils;
	private ArrayList<VideoInfo> hotSpots;
	private Context mActivity;
	
	public HotSpotAdapter(Context context,ArrayList<VideoInfo> list){		
		this.hotSpots=list;
		this.mActivity=context;
		utils=new BitmapUtils(mActivity);
		utils.configDefaultLoadingImage(R.drawable.icon_default);
	}

	@Override
	public int getCount() {
		return hotSpots.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView==null) {
			convertView=View.inflate(mActivity, R.layout.live_content_item, null);
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.iv_hot_image);
			holder.tv=(TextView) convertView.findViewById(R.id.tv_hot_spot_name);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.tv.setText(hotSpots.get(position).title);
		utils.display(holder.image,hotSpots.get(position).thumbnail);
		
		return convertView;
	}

	public class ViewHolder{
		public ImageView image;
		public TextView tv;
	}
	
}
