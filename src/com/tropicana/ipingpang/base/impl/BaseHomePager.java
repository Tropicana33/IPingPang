package com.tropicana.ipingpang.base.impl;

import java.io.Serializable;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipangpang.view.GridViewForScrollView;
import com.tropicana.ipangpang.view.ListViewForScrollView;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.StarsVideoActivity;
import com.tropicana.ipingpang.TeachVideoListActivity;
import com.tropicana.ipingpang.VideoDetailInfoActivity;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.adapter.HeadLineAdapter;
import com.tropicana.ipingpang.adapter.HomeStarAdapter;
import com.tropicana.ipingpang.adapter.HomeTeachAdapter;
import com.tropicana.ipingpang.adapter.HotSpotAdapter;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.bean.HomeBean;
import com.tropicana.ipingpang.bean.HomeBean.MyTopInfo;
import com.tropicana.ipingpang.bean.ShareInfo;
import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoName;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.Toast;


public class BaseHomePager extends BasePager {
	private View view;
	private ListViewForScrollView lv_headline;
	private GridViewForScrollView gv_hot_content;
	private GridViewForScrollView gv_star_content;
	private GridViewForScrollView gv_teach_content;
	private GridViewForScrollView gv_people_content;
	private String mURL;
	private HomeBean homeBean;
	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	private ArrayList<MyTopInfo> headtitles;
	private ArrayList<VideoInfo> hotSpots;
	private ArrayList<StarsInfo> homeStarInfos;
	private ArrayList<TeachVideoName> hometeachs;
	private ArrayList<VideoInfo> amateurVideos;
	private int videoIndex=0;
	
	private ShareInfo shareInfo;
	private String Title;
	private String TitleUrl;
	private String Text;
	private String ImageUrl;
	private String Url;
	private String Comment;	  
	private String VenueName;
	private String SiteUrl; 
	
	public BaseHomePager(Activity activity) {
		super(activity);
		mURL=GlobalContants.HOME_URL;
	}

	@Override
	public void initData() {
		tv_pager_title.setText("首页");		
		view=mRootView.inflate(mActivity, R.layout.live_layout, null);
		lv_headline=(ListViewForScrollView) view.findViewById(R.id.lv_headline);
		gv_hot_content=(GridViewForScrollView) view.findViewById(R.id.lv_live_content);
		gv_star_content=(GridViewForScrollView) view.findViewById(R.id.gv_stars_content);
		gv_people_content=(GridViewForScrollView) view.findViewById(R.id.gv_home_people_content);
		gv_teach_content=(GridViewForScrollView) view.findViewById(R.id.gv_home_teach_content);
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.sv_live);  
		mPullRefreshScrollView.setMode(Mode.PULL_FROM_START);  //只支持下拉刷新
		
		//调用下拉刷新接口
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
				Log.d("TOUCH", "ONREFRESH");
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		fl_content.removeAllViews();
	    fl_content.addView(view);	
	    changeView(STATE_LOADING);
	    headtitles=new ArrayList<MyTopInfo>(); //头条信息
		hotSpots=new ArrayList<VideoInfo>();  //热门推荐信息
	    String cache=CacheUtils.getCache(mURL, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache);
		}
	    getDataFromServer();  //从服务器获取数据	    
	    //头条点击事件
	    lv_headline.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Log.d("SEND", "当前点击位置："+position);
				if (headtitles!=null) {
					MyTopInfo topInfo=headtitles.get(position);
					//浏览记录添加如数据库
					MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
					historyUtils.InsertData(topInfo.title, topInfo.url,historyUtils.getCurrentTime());
					
					Intent intent=new Intent(mActivity, VideoPlay.class);
					intent.putExtra("video_url", topInfo.url);
					mActivity.startActivity(intent);
				
				}
				
			}
	    	
		});
	    
	  //热门推荐点击
	    gv_hot_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Log.d("SEND", "当前点击位置："+position);
				if (hotSpots!=null) {
					VideoInfo hotSpotInfo=hotSpots.get(position);
					//浏览记录添加如数据库
					MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
					historyUtils.InsertData(hotSpotInfo.title, hotSpotInfo.url,historyUtils.getCurrentTime());
					
					SharePreferenceUtils.putBoolean(mActivity, "isTopVideo", false);
					Intent intent=new Intent(mActivity, VideoDetailInfoActivity.class);
					Bundle bundle=new Bundle();
					bundle.putSerializable("video_info", (Serializable)hotSpotInfo);
					intent.putExtras(bundle);
					mActivity.startActivity(intent);
				
				}				
			}	    	
		});
	  //闪耀明星点击
	    gv_star_content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				StarsInfo star=homeStarInfos.get(position);
				Intent intent=new Intent(mActivity, StarsVideoActivity.class);
				intent.putExtra("stars_url", star.url);
				mActivity.startActivity(intent);
			}
		});
	   
	    //全民乒乓点击
	    gv_people_content.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				VideoInfo videoInfo=amateurVideos.get(position);
				//浏览记录添加如数据库
				MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
				historyUtils.InsertData(videoInfo.title, videoInfo.url,historyUtils.getCurrentTime());
				
				SharePreferenceUtils.putBoolean(mActivity, "isTopVideo", false);
				Intent intent=new Intent(mActivity, VideoDetailInfoActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("video_info", (Serializable)videoInfo);
				intent.putExtras(bundle);
				mActivity.startActivity(intent);
			}
		});
	    //视频教学点击
	    gv_teach_content.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				if (hometeachs!=null) {
					TeachVideoName teachVideoName=hometeachs.get(position);
					
					Intent intent=new Intent(mActivity, TeachVideoListActivity.class);
					
					intent.putExtra("teach_url",teachVideoName.url);
					mActivity.startActivity(intent);
			    }
			}
		});
	    
	    
	}
	
	//下拉刷新异步进程
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {			
				Log.d("TOUCH", "正在载入");
				getDataFromServer();  //载入			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d("TOUCH", "载入完成");
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	
	private void getDataFromServer(){
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mURL, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mActivity,"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
				if (hotSpots.size()==0 && headtitles.size()==0) {
					changeView(STATE_ERROR);						
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {			
					String result = responseInfo.result;	
					//LogUtils.d("APP","我的头条数据："+result);
					CacheUtils.setCache(mActivity,mURL, result);
				    parseData(result);
	
			}
		});
	}

	private void parseData(String result) {
		Gson gson=new Gson();
	    homeBean=gson.fromJson(result, HomeBean.class);	    
	    LogUtils.d("APP","我的头条数据："+homeBean);
		headtitles=homeBean.mytop;
		hotSpots=homeBean.hotspots;
		homeStarInfos=homeBean.homestars;
		amateurVideos=homeBean.amateurs;
		hometeachs=homeBean.hometeachs;
		videoIndex=homeBean.extend; //视频页面初始json索引
		shareInfo=homeBean.shareinfo;
		//LogUtils.d("APP", shareInfo.toString());
		initShareInfo();
		
		SharePreferenceUtils.putInt(mActivity, "video_index", videoIndex);
		
		if (homeBean!=null) {
			changeView(STATE_SUCCESS);
		}
		
		if (headtitles!=null) {  //我的头条适配器
			lv_headline.setAdapter(new HeadLineAdapter(mActivity,headtitles));
		}
		
		if (hotSpots!=null) {  //热门推荐适配器
			gv_hot_content.setAdapter(new HotSpotAdapter(mActivity,hotSpots));
		}
		
		if (homeStarInfos!=null) { //闪耀明星适配器
			gv_star_content.setAdapter(new HomeStarAdapter(mActivity,homeStarInfos));
		}
		
		if (amateurVideos!=null) { //业余视频
			gv_people_content.setAdapter(new HotSpotAdapter(mActivity,amateurVideos));
		}
		if (hometeachs!=null) {  //教学视频推荐
			gv_teach_content.setAdapter(new HomeTeachAdapter(mActivity, hometeachs));
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
		
	//存入sharepreference
	private void initShareInfo(){
		Title=shareInfo.Title;
		TitleUrl=shareInfo.TitleUrl;
		Text=shareInfo.Text;
		ImageUrl=shareInfo.ImageUrl;
		Url=shareInfo.Url;
		Comment=shareInfo.Comment;	  
		VenueName=shareInfo.VenueName;
		SiteUrl=shareInfo.SiteUrl; 
		SharePreferenceUtils.putString(mActivity, "Title", Title);
		SharePreferenceUtils.putString(mActivity, "TitleUrl", TitleUrl);
		SharePreferenceUtils.putString(mActivity, "Text", Text);
		SharePreferenceUtils.putString(mActivity, "ImageUrl", ImageUrl);
		SharePreferenceUtils.putString(mActivity, "Url", Url);
		SharePreferenceUtils.putString(mActivity, "Comment", Comment);
		SharePreferenceUtils.putString(mActivity, "VenueName", VenueName);
		SharePreferenceUtils.putString(mActivity, "SiteUrl", SiteUrl);
		LogUtils.d("APP", Title+TitleUrl+Text);
	}
	
	
	
}
	
	
























