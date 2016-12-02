package com.tropicana.ipingpang.mediacenter;

import java.util.ArrayList;

import com.a.a.a.of;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.bean.PingpangWebBean;
import com.tropicana.ipingpang.bean.PingpangWebBean.PingpangWebMenu;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import android.app.Activity;
import android.content.Intent;
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

public class PingpangChannelDetailPager extends BaseDetailPager implements OnItemClickListener{

	private View view;
	private ListView lv_menu_list;
	private String mUrl;
	private PingpangWebBean pingpangWebBean;
	private ArrayList<PingpangWebMenu> menus;

	public PingpangChannelDetailPager(Activity activity) {
		super(activity);
		mUrl=GlobalContants.SERVER_URL+"/MediaCenter/PingpangChannel/my.json";
		Log.d("SEND", "pingpangChannel"+mUrl);
	}
	
	
	
	@Override
	public void initData() {
		view=mRootView.inflate(mActivity,R.layout.mediacenter_detail_pager, null);
		lv_menu_list=(ListView) view.findViewById(R.id.lv_media_list1);
		
		fl_stars_pager_content.removeAllViews();
		fl_stars_pager_content.addView(view);
		
		String cache=CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache);
		}
		getDataFromServer();
		
		lv_menu_list.setOnItemClickListener(this);
		/*lv_menu_list.setOnfreshListener(new RefreshListener() {
			
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
				//lv_menu_list.onRefreshComplete(false); //隐藏下拉刷新控件
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					Log.d("SEND","Teach返回结果："+result);
					CacheUtils.setCache(mActivity, mUrl, result);
					
				parseData(result);
				//lv_menu_list.onRefreshComplete(true); //隐藏下拉刷新控件
			}
		});
		
	}

	private void parseData(String result) {
		Gson gson=new Gson();
		pingpangWebBean=gson.fromJson(result, PingpangWebBean.class);

		menus=pingpangWebBean.menu;

			if(menus!=null){
				lv_menu_list.setAdapter(new PingpangWebAdapter());
			}
		
		
	}




	public class PingpangWebAdapter extends BaseAdapter{
		
		private BitmapUtils utils;
		
		public PingpangWebAdapter(){
			utils=new BitmapUtils(mActivity);
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return menus.size();
		}

		@Override
		public PingpangWebMenu getItem(int position) {
			return menus.get(position);
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
			tv.setText(menus.get(position).title);
			return view;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		Log.d("SEND", "当前点击位置："+position);
	if (menus!=null) {
			PingpangWebMenu pingpangWebMenu=menus.get(position);
			//浏览记录添加如数据库
			MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
			historyUtils.InsertData(pingpangWebMenu.title, pingpangWebMenu.url,historyUtils.getCurrentTime());
			Intent intent=new Intent(mActivity, VideoPlay.class);
			intent.putExtra("video_url",pingpangWebMenu.url);
			mActivity.startActivity(intent);
	    }
	
	}

}
