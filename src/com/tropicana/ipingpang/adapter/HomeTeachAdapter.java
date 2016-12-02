package com.tropicana.ipingpang.adapter;

import java.util.ArrayList;
import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.bean.HomeBean.MyTopInfo;
import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoName;
import com.tropicana.ipingpang.global.GlobalContants;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeTeachAdapter extends BaseAdapter {

	private BitmapUtils utils;
	private ArrayList<TeachVideoName> homeStarInfos;
	private Context mActivity;
	
	public HomeTeachAdapter(Context context,ArrayList<TeachVideoName> list){
		this.homeStarInfos=list;
		this.mActivity=context;
		utils=new BitmapUtils(mActivity);
		utils.configDefaultLoadingImage(R.drawable.icon_default);
		
	}
	
	@Override
	public int getCount() {
		return homeStarInfos.size();
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
		View view=View.inflate(mActivity, R.layout.live_content_item, null);
		ImageView iv=(ImageView) view.findViewById(R.id.iv_hot_image);
		TextView tv=(TextView) view.findViewById(R.id.tv_hot_spot_name);
		
		tv.setText(homeStarInfos.get(position).title);
		utils.display(iv, GlobalContants.SERVER_URL+homeStarInfos.get(position).imageurl);
		
		return view;
	}

}
