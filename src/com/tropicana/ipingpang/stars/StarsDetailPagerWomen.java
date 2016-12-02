package com.tropicana.ipingpang.stars;

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
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.bean.StarsManBean;
import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.stars.StarsDetailPagerMan.manAdapter;
import com.tropicana.ipingpang.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StarsDetailPagerWomen extends BaseDetailPager implements OnItemClickListener{

	private String mUrl;
	private GridView gv_local_star_man;
	private StarsManBean starsManBean;
	private ArrayList<StarsInfo> mStarsList;
	
	
	public StarsDetailPagerWomen(Activity activity) {
		super(activity);
		mUrl=GlobalContants.STARS_WOMEN_URL;
	}
	
	
	

	@Override
	public void initData() {

		View view=View.inflate(mActivity, R.layout.stars_man_pager, null);
		gv_local_star_man=(GridView) view.findViewById(R.id.gv_local_star_man);
		
		fl_stars_pager_content.addView(view);
		changeView(STATE_LOADING);
		
		String cache=CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache);
		}
		getDataFromServer();

		gv_local_star_man.setOnItemClickListener(this);
		
	}
	


	@Override
	protected void getDataFromServer() {
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mActivity,"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
				if (mStarsList.size()==0) {
					changeView(STATE_ERROR);						
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					//Log.d("SEND","stars返回结果："+result);
					CacheUtils.setCache(mActivity, mUrl, result);
				parseData(result);
	
			}
		});
		
	}

	private void parseData(String result) {
		Gson gson=new Gson();
		starsManBean=gson.fromJson(result, StarsManBean.class);
		//Log.d("SEND","男球星信息解析："+starsManBean);

		mStarsList=starsManBean.stars;

			if(mStarsList!=null){
				changeView(STATE_SUCCESS);
				gv_local_star_man.setAdapter(new manAdapter());
			}else {
				changeView(STATE_EMPTY);
			}
		
		
	}




	public class manAdapter extends BaseAdapter{

		private BitmapUtils utils;
		
		public manAdapter(){
			utils=new BitmapUtils(mActivity);
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return mStarsList.size();
		}

		@Override
		public StarsInfo getItem(int position) {
			return mStarsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view=View.inflate(mActivity, R.layout.stars_man_item, null);
			TextView tv=(TextView) view.findViewById(R.id.tv_stars_man_item_name);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_stars_man_item_icon);
			
			//iv.setImageResource(getItem(position).image);
			utils.display(iv, GlobalContants.SERVER_URL+getItem(position).image);
			tv.setText(getItem(position).player);
			return view;
		}
		
	}
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		Log.d("SEND", "当前点击位置："+position);
		if (mStarsList!=null) {
			StarsInfo star=mStarsList.get(position);

			Intent intent=new Intent(mActivity, StarsVideoActivity.class);
			intent.putExtra("stars_url", star.url);
			mActivity.startActivity(intent);
		
	    }
	
	}
	
	@Override
	protected void reload() {
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable(){   
		    public void run() {   //延时1000再取数据
		    	getDataFromServer();
		    }   
		 }, 1000);   

	}

}
