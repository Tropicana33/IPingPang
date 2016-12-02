package com.tropicana.ipingpang.base.impl;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipangpang.view.RefreshListView;
import com.tropicana.ipangpang.view.RefreshListView.RefreshListener;
import com.tropicana.ipangpang.view.TopVideoPager;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.VideoDetailInfoActivity;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.adapter.DefaultAdater;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.bean.VideoMenuData;
import com.tropicana.ipingpang.bean.VideoMenuData.TopVideoInfo;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.bmob.bean.VideoBombBean;
import com.tropicana.ipingpang.db.HistoryInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import com.viewpagerindicator.CirclePageIndicator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseVedioPager extends BasePager 
implements OnItemClickListener,OnPageChangeListener,Serializable,OnTouchListener{

	private View view;
	private View headerView;
	private RefreshListView lv_video_list;
	private String mUrl;
	private String result;
	private String mMoreUrl;
	private TopVideoPager mTopViewPager;
	private CirclePageIndicator mIndicator; // 头条新闻位置指示器
	private TextView tv_top_video_title;
	private Handler mHandler;
	private int mCurrentPosition;
	private BaseVideoAdapter mAdapter;
	
	private VideoMenuData videoMenuData;
	private ArrayList<VideoInfo> videoInfos;
	private ArrayList<TopVideoInfo> topvideosList;
	
	private int index;
	
	
	public BaseVedioPager(Activity activity) {
		super(activity);
	
	}

	@Override
	public void initData() {
		tv_pager_title.setText("视频");

		view=View.inflate(mActivity, R.layout.video_list, null);
		lv_video_list=(RefreshListView) view.findViewById(R.id.lv_video_list);
		headerView=View.inflate(mActivity, R.layout.video_header, null);
		mTopViewPager=(TopVideoPager) headerView.findViewById(R.id.vp_top_video);
		mIndicator=(CirclePageIndicator) headerView.findViewById(R.id.indicator);
		tv_top_video_title=(TextView) headerView.findViewById(R.id.tv_top_video_title);
		
		lv_video_list.addHeaderView(headerView);
				
		fl_content.removeAllViews();
		fl_content.addView(view);
		//初始化视频页服务器url
		index=SharePreferenceUtils.getInt(mActivity, "video_index", 0);
		Log.d("APP", "视频页索引："+index);
		mUrl=GlobalContants.SERVER_URL+"/video"+index+".json";	
		
		videoInfos=new ArrayList<VideoMenuData.VideoInfo>();
		topvideosList=new ArrayList<TopVideoInfo>();
		changeView(STATE_LOADING);
		
		String cache=CacheUtils.getCache(GlobalContants.VIDEO_URL, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache,false);
		}
		//lv_video_list.onRefreshStart();  //进入页面就显示下拉刷新
		getDataFromServer();
		
		lv_video_list.setOnItemClickListener(this);
		lv_video_list.setOnfreshListener(new RefreshListener() {
			
			@Override
			public void onRefresh() {
				Log.d("SEND", "onRefresh");
				getDataFromServer();
			}

			@Override
			public void onLoadMore() {
				Log.d("SEND", "onLoadMore");				
				getDataMoreFromServer();
			}

			
		
		});
	
	}
	
	public class BaseVideoAdapter extends DefaultAdater<VideoInfo>{

		public BaseVideoAdapter(Context context, ArrayList<VideoInfo> datas) {
			super(context, datas);
		}

		@Override
		public View getContentView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView==null) {
				convertView=View.inflate(mActivity, R.layout.video_item, null);
				holder=new ViewHolder();
				holder.videoTitle=(TextView) convertView.findViewById(R.id.tv_video_item_title);
				holder.videoDate=(TextView) convertView.findViewById(R.id.tv_video_item_date);
				holder.videoImage=(ImageView) convertView.findViewById(R.id.iv_video_item);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			VideoInfo videoInfo=videoInfos.get(position);
			holder.videoTitle.setText(videoInfo.title);
			holder.videoDate.setText(videoInfo.date);
			utils.display(holder.videoImage,videoInfo.thumbnail);						
			return convertView;
		}	
	}
	
	public  class ViewHolder{
		public TextView videoTitle;
		public TextView videoDate;
		public ImageView videoImage;
	}
	
	
	private void getDataFromServer(){
		index=SharePreferenceUtils.getInt(mActivity, "video_index", 0);//刷新时重新获取视频索引
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mActivity,"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				lv_video_list.onRefreshComplete(false); //隐藏下拉刷新控件
				//rl_loading_error_pager.setVisibility(View.VISIBLE);
				if (videoInfos.size()==0 && topvideosList.size()==0) {
					changeView(STATE_ERROR);						
				}
					error.printStackTrace();
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					result = responseInfo.result;
					//Log.d("SEND","返回结果："+result);
					CacheUtils.setCache(mActivity, GlobalContants.VIDEO_URL, result);

				lv_video_list.onRefreshComplete(true); //隐藏下拉刷新控件
				parseData(result,false);
	
			}
		});
	}

	/**
	 * 从服务器加载更多
	 */
	private void getDataMoreFromServer() {
		index++;
		mMoreUrl=GlobalContants.SERVER_URL+"/video"+index+".json";
		LogUtils.d("APP", mMoreUrl);
		
			HttpUtils utils=new HttpUtils();
			utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException error, String msg) {
					Toast.makeText(mActivity,"没有更多数据了", Toast.LENGTH_SHORT).show();					
					error.printStackTrace();
					lv_video_list.onRefreshComplete(false); //隐藏下拉刷新控件
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result=responseInfo.result;
					//System.out.println("页签详情页返回结果："+result);
					parseData(result,true);
					lv_video_list.onRefreshComplete(false); //隐藏下拉刷新控件
					
				}
			});

		}
	
    /**
     * 解析数据
     * @param result 解析的内容
     * @param isMore 是否加载更多
     */
	private void parseData(String result,boolean isMore) {
		Gson gson=new Gson();
		videoMenuData=gson.fromJson(result, VideoMenuData.class);
		//changeView(STATE_SUCCESS);
		
		if (!isMore) {  //不加载更多
			topvideosList=videoMenuData.topvideos;
			videoInfos=videoMenuData.videos;
			int state=checkData(videoInfos);
			changeView(state);  //加载成功则隐藏errorpager
			Log.d("SEND","topvideo:"+ topvideosList);
			if(topvideosList!=null){
				mTopViewPager.setAdapter(new TopVideoAdapter());
				tv_top_video_title.setText(topvideosList.get(0).title);
				mIndicator.setViewPager(mTopViewPager);
				mIndicator.setSnap(true);  //支持快照显示
				mIndicator.setOnPageChangeListener(this);
				mIndicator.onPageSelected(0);  //让指示器重新定位到第一个位置
				mTopViewPager.setOnTouchListener(this); //ViewPager调用触摸事件

			}
			
			if(videoInfos!=null){
				mAdapter=new BaseVideoAdapter(mActivity, videoInfos);
				lv_video_list.setAdapter(new BaseVideoAdapter(mActivity, videoInfos));
			}
		}else { //加载更多
			ArrayList<VideoInfo> news=videoMenuData.videos;
			if (videoInfos!=null) {
				videoInfos.addAll(news);
				mAdapter.notifyDataSetChanged();
			}
		}

		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(android.os.Message msg) {
					Log.d("SEND", mTopViewPager.getCurrentItem() + "");
					mCurrentPosition = mTopViewPager.getCurrentItem();
					mCurrentPosition++;
					if (mCurrentPosition >= topvideosList.size()) {
						mCurrentPosition = 0;
					}

					mTopViewPager.setCurrentItem(mCurrentPosition);
					mHandler.sendEmptyMessageDelayed(0, 4000);
				};
			};

			mHandler.sendEmptyMessageDelayed(0, 4000);
	
		}
	}
	
	public class TopVideoAdapter extends PagerAdapter{

		private BitmapUtils bitmapUtils;

		public TopVideoAdapter(){
			bitmapUtils = new BitmapUtils(mActivity);
			bitmapUtils.configDefaultLoadingImage(R.drawable.icon_default);
			
		}
		
		@Override
		public int getCount() {
			return topvideosList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView=new ImageView(mActivity);
			imageView.setImageResource(R.drawable.icon_default);
			imageView.setScaleType(ScaleType.FIT_XY); //基于控件大小填充
			
			final TopVideoInfo topVideoInfo=topvideosList.get(position);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(mActivity,VideoDetailInfoActivity.class);
					Bundle bundle=new Bundle();
					bundle.putSerializable("video_info", (Serializable)topVideoInfo);
					intent.putExtras(bundle);
					SharePreferenceUtils.putBoolean(mActivity, "isTopVideo", true);
					mActivity.startActivity(intent);
				}
			});
			container.removeView(imageView);
			bitmapUtils.display(imageView, GlobalContants.SERVER_URL+topVideoInfo.thumbnail);//传递image对象，和url
			
			container.addView(imageView);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		//Log.d("SEND", "当前点击位置："+position);
		if (videoInfos!=null) {
			VideoInfo videoInfo=videoInfos.get(position);
			
			//浏览记录添加如数据库
			MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
			historyUtils.InsertData(videoInfo.title, videoInfo.url,historyUtils.getCurrentTime());

			SharePreferenceUtils.putBoolean(mActivity, "isTopVideo", false);
			Intent intent=new Intent(mActivity, VideoDetailInfoActivity.class);
			Bundle bundle=new Bundle();
			bundle.putSerializable("video_info", (Serializable)videoInfo);
			//Log.d("TOUCH", "当前点击位置："+position);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
		
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		tv_top_video_title.setText(topvideosList.get(arg0).title);
		
	}
	

	/**
	 * 处理viewpager滑动事件
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean onTouch(View v, MotionEvent event) {

	  /*  switch(event.getAction()) {
	    case MotionEvent.ACTION_MOVE:
	        mTopViewPager.requestDisallowInterceptTouchEvent(true);
	        break;
	    case MotionEvent.ACTION_UP:

	    case MotionEvent.ACTION_CANCEL:
	    	mTopViewPager.requestDisallowInterceptTouchEvent(false);
	        break;
	    }*/
    return false;
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









