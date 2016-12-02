package com.tropicana.ipingpang;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bean.GameCenterBean;
import com.tropicana.ipingpang.bean.GameCenterBean.GameInfo;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
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

public class ShopActivity extends BaseActivity implements OnItemClickListener{

	private View view;
	private ListView lv_games;
	private GameCenterBean gameCenterBean;
	private ArrayList<GameInfo> games;
	private String mUrl;
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_shop, null);
		lv_games=(ListView) view.findViewById(R.id.lv_shop_list);
		fl_base.addView(view); 
		tv_title.setText("器材商店");
		mUrl=GlobalContants.SERVER_URL+"/setting/shop/shop.json";
		changeView(STATE_LOADING);
		String cache=CacheUtils.getCache(mUrl, this);
		if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
			parseData(cache);
		}
		getDataFromServer();

		btn_back.setOnClickListener(this);
		btn_close.setVisibility(View.INVISIBLE);
		lv_games.setOnItemClickListener(this);
	}


	private void getDataFromServer() {
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getApplicationContext(),"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
				if (games.size()!=0) {
					changeView(STATE_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					Log.d("SEND","game返回结果："+result);
					CacheUtils.setCache(getApplicationContext(), mUrl, result);
				parseData(result);
	
			}
		});
		
	}

	private void parseData(String result) {
		Gson gson=new Gson();
		gameCenterBean=gson.fromJson(result, GameCenterBean.class);
		Log.d("SEND","游戏列表："+gameCenterBean);

		games=gameCenterBean.games;

			if(games!=null){
				changeView(STATE_SUCCESS);
				lv_games.setAdapter(new GameAdapter());
			}
		
		
	}




	public class GameAdapter extends BaseAdapter{

		private BitmapUtils utils;
		
		public GameAdapter(){
			utils=new BitmapUtils(getApplicationContext());
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return games.size();
		}

		@Override
		public GameInfo getItem(int position) {
			return games.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView==null) {
				convertView=View.inflate(getApplicationContext(), R.layout.games_item, null);
				holder=new ViewHolder();
				holder.gameTitle=(TextView) convertView.findViewById(R.id.tv_games_item_title);
				holder.gameImage=(ImageView) convertView.findViewById(R.id.iv_game_icon);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			
			holder.gameTitle.setText(getItem(position).title);
			utils.display(holder.gameImage, GlobalContants.SERVER_URL+getItem(position).thumbnail);
			return convertView;
			
		}
		
	}
	

	public static class ViewHolder{
		public TextView gameTitle;
		public ImageView gameImage;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		Log.d("SEND", "当前点击位置："+position);
		if (games!=null) {
			GameInfo gameInfo=games.get(position);
		
			Intent intent=new Intent(this, VideoPlay.class);
			intent.putExtra("video_url", gameInfo.url);
			this.startActivity(intent);
		
	    }
	
	}
	
	@Override
	public void reload() {
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				getDataFromServer();
			}
		}, 1000);
	}
	@Override
	public void back() {
		finish();
	}
}
