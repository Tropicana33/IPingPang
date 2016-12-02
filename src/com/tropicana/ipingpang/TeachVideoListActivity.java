package com.tropicana.ipingpang;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.base.impl.BaseVedioPager.ViewHolder;
import com.tropicana.ipingpang.bean.TeachVideoBean;
import com.tropicana.ipingpang.bean.StarsManBean.StarsInfo;
import com.tropicana.ipingpang.bean.TeachVideoBean.TeachVideoInfo;
import com.tropicana.ipingpang.bean.TeachVideoDetailInfo;
import com.tropicana.ipingpang.bean.VideoMenuData;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.mediacenter.TeachDetailPager.TeachAdapter;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.LogUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TeachVideoListActivity extends BaseActivity implements OnItemClickListener{

	/**
	 * 教学视频具体列表
	 */
	private ListView lv_list;
	private View view;
	private ArrayList<TeachVideoDetailInfo> videoInfos;
	private String mUrl;
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.mediacenter_detail_pager, null);
		lv_list=(ListView) view.findViewById(R.id.lv_media_list1);
		fl_base.addView(view);
		videoInfos=new ArrayList<TeachVideoDetailInfo>();
		lv_list.setAdapter(new VideoAdapter());
		tv_title.setText("教学视频");
		btn_close.setVisibility(View.INVISIBLE);
		lv_list.setOnItemClickListener(this);
		mUrl=GlobalContants.SERVER_URL+getIntent().getStringExtra("teach_url");//获取播放url
		LogUtils.d("APP", mUrl);
		getDataFromServer();
		/*lv_list.setOnfreshListener(new RefreshListener() {
			
			@Override
			public void onRefresh() {
				Log.d("SEND", "onRefresh");
				lv_list.onRefreshComplete(false);
			}

			@Override
			public void onLoadMore() {
				
			}
		
		});*/
	}
	
	private void getDataFromServer() {
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getApplicationContext(),"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
				//lv_list.onRefreshComplete(false); //隐藏下拉刷新控件
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					Log.d("APP","Teach返回结果："+result);
					CacheUtils.setCache(getApplicationContext(), mUrl, result);
					
				parseData(result);
				//lv_list.onRefreshComplete(true); //隐藏下拉刷新控件
			}
		});
		
	}

	private void parseData(String result) {		
		try {
			JSONObject object=new JSONObject(result);
			JSONArray jsonArray=object.getJSONArray("videos");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject videoObject=jsonArray.getJSONObject(i);
				String title=videoObject.getString("title");
				String url=videoObject.getString("url");
				String thumbnail=videoObject.getString("thumbnail");
				TeachVideoDetailInfo info=new TeachVideoDetailInfo(title, url, thumbnail);
				videoInfos.add(info);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (videoInfos!=null) {
			lv_list.setAdapter(new VideoAdapter());
		}
	}

	
	private class VideoAdapter extends BaseAdapter{

		private BitmapUtils utils;
		
		public VideoAdapter(){
			utils=new BitmapUtils(TeachVideoListActivity.this);
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return videoInfos.size();
		}

		@Override
		public TeachVideoDetailInfo getItem(int position) {
			return videoInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder ;			
			if (convertView==null) {
				convertView=View.inflate(getApplicationContext(), R.layout.video_item, null);
				holder=new ViewHolder();				
				holder.title=(TextView) convertView.findViewById(R.id.tv_video_item_title); 
				holder.date=(TextView) convertView.findViewById(R.id.tv_video_item_date);
				holder.image=(ImageView) convertView.findViewById(R.id.iv_video_item);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			
			holder.title.setText(videoInfos.get(position).getTitle());
			utils.display(holder.image, videoInfos.get(position).getThumbnail());
			return convertView;
		}
		
	}

	public class ViewHolder{
		public TextView title;
		public TextView date;
		public ImageView image;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		if (videoInfos!=null) {
			TeachVideoDetailInfo videoInfo=videoInfos.get(position);
			//浏览记录添加如数据库
			MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(this);		
			historyUtils.InsertData(videoInfo.title, videoInfo.url,historyUtils.getCurrentTime());
			
			Intent intent=new Intent(this, VideoPlay.class);
			intent.putExtra("video_url", videoInfo.url);
			startActivity(intent);
		
	    }
	}

	
	@Override
	public void back() {
		finish();
	}
	
}
