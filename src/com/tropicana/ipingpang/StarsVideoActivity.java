package com.tropicana.ipingpang;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipangpang.view.ListViewForScrollView;
import com.tropicana.ipangpang.view.RefreshListView;
import com.tropicana.ipangpang.view.RefreshListView.RefreshListener;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bean.StarVideoListBean;
import com.tropicana.ipingpang.bean.StarVideoListBean.StarVideoInfo;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.LogUtils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 球星视频列表
 * 
 * @author Administrator
 *
 */
public class StarsVideoActivity extends BaseActivity implements OnItemClickListener {

	private String mStar_Url;
	private String mMoreUrl;
	private ListViewForScrollView lv_star_video_list;
	private View view;
	private ArrayList<StarVideoInfo> mStarVideoInfos;

	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	private ImageButton btn_star_info; // 明星描述展开收起
	private LinearLayout rl_star_info_down, ll_star_des;
	private TextView tv_star_des; // 明星描述
	private boolean isDown = true;
	private String des;
	int startHeight = 0;
	int endHeight = 0;

	@Override
	public void initData() {
		view = View.inflate(getApplicationContext(), R.layout.star_video_list, null);
		lv_star_video_list = (ListViewForScrollView) view.findViewById(R.id.lv_star_video_list);
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.sv_star);
		mPullRefreshScrollView.setMode(Mode.PULL_FROM_START); // 只支持下拉刷新
		btn_star_info = (ImageButton) view.findViewById(R.id.btn_star_info_down);
		rl_star_info_down = (LinearLayout) view.findViewById(R.id.rl_star_info_down);
		tv_star_des = (TextView) view.findViewById(R.id.tv_star_des);
		ll_star_des = (LinearLayout) view.findViewById(R.id.ll_star_des);
		fl_base.addView(view);
		// 调用下拉刷新接口
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
				Log.d("TOUCH", "ONREFRESH");
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		mScrollView.smoothScrollTo(0, 0);// 手动将scrollview移到最前面

		mStarVideoInfos = new ArrayList<StarVideoInfo>();
		mStar_Url = GlobalContants.SERVER_URL + getIntent().getStringExtra("stars_url");

		String cache = CacheUtils.getCache(mStar_Url, this);
		if (!TextUtils.isEmpty(cache)) { // 判断缓存中是否有数据
			//parseData(cache, false);
		}

		// lv_star_video_list.onRefreshStart(); //第一次刷新
		changeView(STATE_LOADING);
		getDataFromServer();

		btn_close.setVisibility(View.INVISIBLE);

		lv_star_video_list.setOnItemClickListener(this);

		btn_star_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 展开球星简介信息
				LogUtils.d("APP", "球星信息展开");
				Down();
			}
		});
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, mStar_Url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getApplicationContext(), "请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				// lv_star_video_list.onRefreshComplete(false); //隐藏下拉刷新控件
				if (mStarVideoInfos.size() == 0) {
					changeView(STATE_ERROR);
				}
				error.printStackTrace();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				Log.d("SEND", "stars返回结果：" + result);
				// int state=checkData(result);
				changeView(STATE_SUCCESS);
				CacheUtils.setCache(getApplicationContext(), mStar_Url, result);
				parseData(result, false);
				// lv_star_video_list.onRefreshComplete(true); //隐藏下拉刷新控件
			}
		});

	}

	private void getDataMoreFromServer() {
		if (mMoreUrl != null) {
			HttpUtils utils = new HttpUtils();
			utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException error, String msg) {
					Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();

					error.printStackTrace();
					// lv_star_video_list.onRefreshComplete(false); //隐藏下拉刷新控件
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					// System.out.println("页签详情页返回结果："+result);
					parseData(result, true);
					// lv_star_video_list.onRefreshComplete(false); //隐藏下拉刷新控件

				}
			});
		} else {
			// lv_star_video_list.onRefreshComplete(false);
			Toast.makeText(getApplicationContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
		}
	}

	private void parseData(String result, boolean isMore) {
		Gson gson = new Gson();
		StarVideoListBean starVideoListBean = gson.fromJson(result, StarVideoListBean.class);

		
		
		LinearLayout.LayoutParams params=(LayoutParams) tv_star_des.getLayoutParams();
		params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		tv_star_des.setLayoutParams(params);
		
		if (!isMore) { // 不加载更多
			des = starVideoListBean.des;
			tv_star_des.setText(des);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub	
					isDown=true;
					btn_star_info.setImageResource(R.drawable.arrow_up);
					endHeight = tv_star_des.getMeasuredHeight();
					Log.d("APP", "endHeight：" + endHeight);
					/*// 初始化文本高度
					LinearLayout.LayoutParams params = (LayoutParams) tv_star_des.getLayoutParams();
					params.height = 0;
					tv_star_des.setLayoutParams(params);*/
				}
			}, 100);
			mStarVideoInfos = starVideoListBean.video;
			int state = checkData(mStarVideoInfos);
			changeView(state); // 没有内容，显示没内容界面
			if (mStarVideoInfos != null) {
				lv_star_video_list.setAdapter(new StarVideoAdapter());
				tv_title.setText(starVideoListBean.player);
			}
		} else {
			// 加载更多
			ArrayList<StarVideoInfo> news = starVideoListBean.video;
			if (mStarVideoInfos != null) {
				mStarVideoInfos.addAll(news);
				new StarVideoAdapter().notifyDataSetChanged();
			}
		}

	}

	private class StarVideoAdapter extends BaseAdapter {

		private BitmapUtils utils;

		public StarVideoAdapter() {
			utils = new BitmapUtils(getApplicationContext());
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}

		@Override
		public int getCount() {
			return mStarVideoInfos.size();
		}

		@Override
		public StarVideoInfo getItem(int position) {
			return mStarVideoInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.video_item, null);
				holder = new ViewHolder();
				holder.videoTitle = (TextView) convertView.findViewById(R.id.tv_video_item_title);
				holder.videoDate = (TextView) convertView.findViewById(R.id.tv_video_item_date);
				holder.videoImage = (ImageView) convertView.findViewById(R.id.iv_video_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.videoTitle.setText(getItem(position).title);
			holder.videoDate.setText(getItem(position).date);
			utils.display(holder.videoImage, getItem(position).thumbnail);
			return convertView;
		}

	}

	public static class ViewHolder {
		public TextView videoTitle;
		public TextView videoDate;
		public ImageView videoImage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("SEND", "当前点击位置：" + position);
		if (mStarVideoInfos != null) {
			StarVideoInfo videoInfo = mStarVideoInfos.get(position);
			// 浏览记录添加如数据库
			MyHistoryDbUtils historyUtils = new MyHistoryDbUtils(this);
			historyUtils.InsertData(videoInfo.title, videoInfo.url, historyUtils.getCurrentTime());

			Intent intent = new Intent(StarsVideoActivity.this, VideoPlay.class);
			intent.putExtra("video_url", videoInfo.url);
			startActivity(intent);

		}
	}

	@Override // 加载失败重新加载
	public void reload() {
		LogUtils.d("APP", "点击重试了");
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable() {
			public void run() { // 延时1000再取数据
				getDataFromServer();
			}
		}, 1000);
	}

	@Override
	public void back() {
		finish();
	}

	// 下拉刷新异步进程
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			Log.d("TOUCH", "正在载入");
			getDataFromServer(); // 载入
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d("TOUCH", "载入完成");
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	/**
	 * 测量得到描述文本控件高度
	 * 
	 * @return
	 */
	public int getTextDesHeight() {
		int width = tv_star_des.getMeasuredWidth(); // 由于宽度不会发生变化 宽度的值取出来		
		LinearLayout.LayoutParams params=(LayoutParams) tv_star_des.getLayoutParams();
		params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		tv_star_des.setLayoutParams(params);
		Log.d("APP","MEASURE1:"+tv_star_des.getMeasuredHeight() );
		// 参数1 测量控件mode 参数2 大小
		/*int widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, width); // mode+size
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.AT_MOST, 1000);// 我的高度																						// 最大是1000
		// 测量规则 宽度是一个精确的值width, 高度最大是1000,以实际为准
		tv_star_des.measure(widthMeasureSpec, heightMeasureSpec); // 通过该方法重新测量控件
		Log.d("APP", "文本高度：" + tv_star_des.getMeasuredHeight());*/
		//SystemClock.sleep(200);
		Log.d("APP","MEASURE2:"+tv_star_des.getMeasuredHeight() );
		return tv_star_des.getMeasuredHeight();
		
	}

	// 展开标题栏
	private void Down() {
		int startHeight = 0;
		int targetHeight = 0;
		if (isDown) {
			startHeight = endHeight;
			targetHeight = 0;
			btn_star_info.setImageResource(R.drawable.arrow_down);
		} else {
			startHeight = 0;
			targetHeight = endHeight;
			btn_star_info.setImageResource(R.drawable.arrow_up);
		}
		isDown = !isDown;
		// 值动画
		ValueAnimator animator = ValueAnimator.ofInt(startHeight, targetHeight);
		final LinearLayout.LayoutParams params = (LayoutParams) tv_star_des.getLayoutParams();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				int value = (Integer) animator.getAnimatedValue();// 运行当前时间点的一个值
				params.height = value;
				tv_star_des.setLayoutParams(params);// 刷新界面
				//System.out.println(value);
			}
		});
		animator.setDuration(500);
		animator.start();
		// isDown = false;
	}
}
