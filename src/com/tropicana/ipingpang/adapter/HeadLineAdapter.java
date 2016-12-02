package com.tropicana.ipingpang.adapter;

import java.util.ArrayList;

import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.bean.HomeBean.MyTopInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HeadLineAdapter extends DefaultAdater<MyTopInfo> {


	public HeadLineAdapter(Context context, ArrayList<MyTopInfo> datas) {		
		super(context, datas);
	}

	/**
	 * 改变头条icon状态
	 * @param type
	 * @param iv
	 */
	public void getIconState(int type, ImageView iv) {
		if (type==1) {  //新闻
			iv.setImageResource(R.drawable.ic_headline_news);
		}else { //视频
			iv.setImageResource(R.drawable.ic_headline_video);
		}
		
	}

	@Override
	public View getContentView(int position, View convertView, ViewGroup parent) {
		View view=View.inflate(context, R.layout.headline_item, null);
		TextView tv=(TextView) view.findViewById(R.id.tv_head_text);
		ImageView iv=(ImageView) view.findViewById(R.id.iv_headline_icon);
		int type=datas.get(position).type;   //获取数据类型，新闻还是视频
		getIconState(type,iv);			
		tv.setText(datas.get(position).title);		
		return view;
	}
}
