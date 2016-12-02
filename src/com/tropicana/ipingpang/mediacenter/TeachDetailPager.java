package com.tropicana.ipingpang.mediacenter;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.StarsVideoActivity;
import com.tropicana.ipingpang.TeachVideoListActivity;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.bean.StarsManBean;
import com.tropicana.ipingpang.bean.MediaCenterTabBean.TabDetailInfo;
import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoName;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.stars.StarsDetailPagerMan.manAdapter;
import com.tropicana.ipingpang.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TeachDetailPager extends BaseDetailPager implements 
OnItemClickListener{

	private View view;
	private String mUrl;
	private ListView lv_list;
	private ArrayList<TeachVideoName> teachVideoNames;
	private TeachVideoBean teachVideoBean;
	private ArrayList<VideoInfo> videoInfos;
	
	public TeachDetailPager(Activity activity) {
		super(activity);
		mUrl=GlobalContants.TEACH_URL;
	}
	
	@Override
	public void initData() {
		view=mRootView.inflate(mActivity,R.layout.mediacenter_detail_pager, null);
		lv_list=(ListView) view.findViewById(R.id.lv_media_list1);
		fl_stars_pager_content.removeAllViews();
		fl_stars_pager_content.addView(view);
		
		String cache=CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache);
		}
		getDataFromServer();
		
		lv_list.setOnItemClickListener(this);
		/*lv_list.setOnfreshListener(new RefreshListener() {
			
			@Override
			public void onRefresh() {
				Log.d("SEND", "onRefresh");
				getDataFromServer();
			}

			@Override
			public void onLoadMore() {
				
			}
		
		});*/
	}

	@Override
	protected void getDataFromServer() {
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mActivity,"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
				//lv_list.onRefreshComplete(false); //隐藏下拉刷新控件
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					Log.d("SEND","Teach返回结果："+result);
					CacheUtils.setCache(mActivity, mUrl, result);
					
				parseData(result);
				//lv_list.onRefreshComplete(true); //隐藏下拉刷新控件
			}
		});
		
	}

	private void parseData(String result) {
		Gson gson=new Gson();
		teachVideoBean=gson.fromJson(result, TeachVideoBean.class);

		teachVideoNames=teachVideoBean.menu;

			if(teachVideoNames!=null){
				lv_list.setAdapter(new TeachAdapter());
			}
		
		
	}




	public class TeachAdapter extends BaseAdapter{
		
		private BitmapUtils utils;
		
		public TeachAdapter(){
			utils=new BitmapUtils(mActivity);
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return teachVideoNames.size();
		}

		@Override
		public TeachVideoName getItem(int position) {
			return teachVideoNames.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view=View.inflate(mActivity, R.layout.mediacenter_list_item, null);
			TextView tv=(TextView) view.findViewById(R.id.tv_teach_video_item_title);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_teach_image_item);
			utils.display(iv, GlobalContants.SERVER_URL+getItem(position).imageurl);
			tv.setText(teachVideoNames.get(position).title);
			return view;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		Log.d("SEND", "当前点击位置："+position);
		if (teachVideoNames!=null) {
			TeachVideoName teachVideoName=teachVideoNames.get(position);
			
			Intent intent=new Intent(mActivity, TeachVideoListActivity.class);
			
			intent.putExtra("teach_url",teachVideoName.url);
			mActivity.startActivity(intent);
	    }
	
	}
	
}






















