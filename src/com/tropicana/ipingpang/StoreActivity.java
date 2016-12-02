package com.tropicana.ipingpang;

import java.util.ArrayList;
import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.db.MyStoreDbUtils;
import com.tropicana.ipingpang.db.StoreInfo;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import com.tropicana.ipingpang.utils.ShareSDKUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.media.audiofx.BassBoost.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StoreActivity extends BaseActivity implements OnItemClickListener{

	private View view;
	private ListView lv_store;
	private ArrayList<StoreInfo> storeInfos;
	private MyStoreDbUtils mStoreUtils;
	private StoreAdapter mAdapter;
	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener mListener;
	private boolean isAutoRotate=false;
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_store, null);
		lv_store=(ListView) view.findViewById(R.id.lv_store);
		fl_base.addView(view);
		tv_title.setText("我的收藏");
		btn_close.setVisibility(View.GONE); 

		//setTheme(R.style.DarkTheme);
		
		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		Log.d("TOUCH", "当前旋转状态"+isAutoRotate);
		setGravitySensor();  //重力感应
		if (isAutoRotate) {
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		}else {
			sensorManager.unregisterListener(mListener);
		}
		
		//初始化数据库信息
		mStoreUtils=new MyStoreDbUtils(this);
		storeInfos=new ArrayList<StoreInfo>();
		storeInfos=mStoreUtils.findAll();   //查找全部历史记录
				
				mAdapter=new StoreAdapter();
				if (storeInfos!=null) {
					Log.d("TOUCH", "有浏览记录");
					lv_store.setAdapter(mAdapter);
				}
		lv_store.setOnItemClickListener(this);
		//lv_store.setOnItemLongClickListener(this);
		
		registerForContextMenu(lv_store);
		
	}

	/**
	 * 长按 显示菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.store_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * 菜单点击事件
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id;  //获取点击位置
		switch (item.getItemId()) {
		case R.id.item_store_delete:
			Log.d("TOUCH", "取消收藏");
				mStoreUtils.delete(storeInfos.get(position).title);   //在数据库中删除收藏
				storeInfos=mStoreUtils.findAll();   //查找全部历史记录
				mAdapter.notifyDataSetChanged();  //更新列表数据
			return true;
			case R.id.item_store_share:
				Log.d("TOUCH", "分享");	
				ShareSDKUtils sdkUtils=new ShareSDKUtils(this);
				sdkUtils.showShare();
			return true;
		default:
			break;

		}
		return super.onContextItemSelected(item);
	}

	
	public class StoreAdapter extends BaseAdapter{
		private BitmapUtils utils;
		
		public StoreAdapter(){
			utils=new BitmapUtils(getApplicationContext());
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return storeInfos.size();
		}

		@Override
		public StoreInfo getItem(int position) {
			return storeInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView==null) {
				convertView=View.inflate(getApplicationContext(), R.layout.video_item, null);
				holder=new ViewHolder();
				holder.videoTitle=(TextView) convertView.findViewById(R.id.tv_video_item_title);
				holder.videoDate=(TextView) convertView.findViewById(R.id.tv_video_item_date);
				holder.videoImage=(ImageView) convertView.findViewById(R.id.iv_video_item);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			
			holder.videoTitle.setText(getItem(position).title);
			holder.videoDate.setText(getItem(position).time);
			utils.display(holder.videoImage, getItem(position).thumbnail);	
			return convertView;
		}
	}
	
	public  class ViewHolder{
		public TextView videoTitle;
		public TextView videoDate;
		public ImageView videoImage;
	}
	
	@Override
	public void back() {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		if (storeInfos!=null) {
			String mUrl=storeInfos.get(position).url;
			Intent intent=new Intent(StoreActivity.this, VideoPlay.class);
			intent.putExtra("video_url",mUrl);
			startActivity(intent);
		}
	}

	private void setGravitySensor() {
		//重力感应
		sensorManager=(SensorManager) getSystemService(this.SENSOR_SERVICE);
		sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mListener=new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				float xValue=Math.abs(event.values[0]);
				float yValue=Math.abs(event.values[1]);
				if (isAutoRotate) {
					//Log.d("TOUCH", "能旋转");
					if (xValue>8) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}else if (yValue>8) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				}else {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					//Log.d("TOUCH", "不能旋转");
				}
				
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				
			}
		};
	}

	

	@Override
	protected void onPause() {  //界面不可见时取消监听重力感应
		sensorManager.unregisterListener(mListener);
		super.onPause();
	}
	
}
