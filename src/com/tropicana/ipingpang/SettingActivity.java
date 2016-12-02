package com.tropicana.ipingpang;

import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.utils.DataCleanUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends BaseActivity {

	private View view;
	private RelativeLayout rl_setting_auto_rotate;
	private ImageView iv_setting_auto_rotate;
	private RelativeLayout rl_setting_buffer_clean;
	private TextView tv_buffer_size;
	private RelativeLayout rl_setting_update;
	private RelativeLayout rl_setting_feedback;
	private RelativeLayout rl_setting_pull;
	private ImageView iv_setting_pull;
	private SensorManager sensorManager;
	private Sensor sensor; 
	private SensorEventListener mListener;
	private boolean isAutoRotate=false;
	private boolean isReceivePull=false;
	private String cacheSize;
	private DataCleanUtils dataCleanUtils;
	private ProgressDialog progress;
	
	
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			AlertDialog.Builder builder = new Builder(SettingActivity.this);
			builder.setMessage("当前版本号："+getVersion()+"  已是最新版本!");
			builder.setTitle("提示");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			progress.dismiss();
			builder.create().show();
			
		};	
	};

	@Override
	public void initData() {
		view=View.inflate(this, R.layout.activity_setting, null);
		rl_setting_auto_rotate=(RelativeLayout) view.findViewById(R.id.rl_setting_auto_rotate);
		iv_setting_auto_rotate=(ImageView) view.findViewById(R.id.iv_setting_auto_rotate);
		rl_setting_pull=(RelativeLayout) view.findViewById(R.id.rl_setting_pull);
		iv_setting_pull=(ImageView) view.findViewById(R.id.iv_setting_pull);
		rl_setting_buffer_clean=(RelativeLayout) view.findViewById(R.id.rl_setting_buffer_clean);
		tv_buffer_size=(TextView) view.findViewById(R.id.tv_buffer_size);
		rl_setting_update=(RelativeLayout) view.findViewById(R.id.rl_setting_update);
		rl_setting_feedback=(RelativeLayout) view.findViewById(R.id.rl_setting_feedback);
		fl_base.addView(view);
		btn_close.setVisibility(View.INVISIBLE);
		tv_title.setText("我的设置");
		
		
		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		Log.d("TOUCH", "当前旋转状态"+isAutoRotate);

		//屏幕自动旋转监听器
		rl_setting_auto_rotate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isAutoRotate) {
					iv_setting_auto_rotate.setImageResource(R.drawable.checked_box_1);
					SharePreferenceUtils.putBoolean(getApplicationContext(), "auto_rotate", false);
					sensorManager.unregisterListener(mListener);
				}else {
					iv_setting_auto_rotate.setImageResource(R.drawable.checked_box_2);
					SharePreferenceUtils.putBoolean(getApplicationContext(), "auto_rotate", true);
					sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				}
				isAutoRotate=!isAutoRotate;
			}
		});
		
		setGravitySensor();  //设置重力传感器
		
		if (isAutoRotate) {//判断屏幕是否自动旋转
			iv_setting_auto_rotate.setImageResource(R.drawable.checked_box_2);
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		}else {
			iv_setting_auto_rotate.setImageResource(R.drawable.checked_box_1);
			sensorManager.unregisterListener(mListener);
		}
		//接收推送消息
		isReceivePull=SharePreferenceUtils.getBoolean(getApplicationContext(), "is_receive_pull", false);
		if (isReceivePull) {
			iv_setting_pull.setImageResource(R.drawable.checked_box_2);
		}else {
			iv_setting_pull.setImageResource(R.drawable.checked_box_1);
		}
		rl_setting_pull.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isReceivePull=SharePreferenceUtils.getBoolean(getApplicationContext(), "is_receive_pull", false);
				if (isReceivePull) { //不接收推送消息
					SharePreferenceUtils.putBoolean(getApplicationContext(), "is_receive_pull", false);
					iv_setting_pull.setImageResource(R.drawable.checked_box_1);
					ShowToast("不接收推送消息");
				}else {  //接收推送消息
					SharePreferenceUtils.putBoolean(getApplicationContext(), "is_receive_pull", true);
					iv_setting_pull.setImageResource(R.drawable.checked_box_2);
					ShowToast("接收推送消息");
				}
				
			}
		});
		
		//获取缓存大小
		try {
			cacheSize=dataCleanUtils.getTotalCacheSize(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		tv_buffer_size.setText(cacheSize);
		
		//清除缓存
		rl_setting_buffer_clean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDeleteDialog();
			}
		});
		
		//版本检查更新
		rl_setting_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showUpdateDialog();
			}
		});
		
		//建议反馈
		rl_setting_feedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SettingActivity.this,FeedBackActivity.class);
				startActivity(intent);
			}
		});
	}

	
	/**
	 * 显示是否删除对话框
	 */
	private void showDeleteDialog() {
		 AlertDialog.Builder builder = new Builder(this);
		 builder.setMessage("确定要清除缓存吗？");
		 builder.setTitle("提示");
		 builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dataCleanUtils.cleanApplicationData(getApplicationContext());
				//获取缓存大小
				try {
					cacheSize=dataCleanUtils.getTotalCacheSize(getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
				tv_buffer_size.setText("0KB");
				Toast.makeText(getApplicationContext(), "清除所有数据", 0).show();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});	
		builder.create().show();
	}

	/**
	 * 显示是否删除对话框
	 */
	private void showUpdateDialog() {
		progress = new ProgressDialog(
				SettingActivity.this);
		progress.setMessage("检查中...");
		progress.setCanceledOnTouchOutside(false);
		progress.show(); 
		
		new Thread(){
			public void run() {
				mHandler.sendEmptyMessageDelayed(0, 1000);
			};
		}.start();
		 
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
	
	/**
	 * 得到当前版本号
	 * @return
	 */
	private String getVersion(){
		  PackageManager pm=this.getPackageManager();
		  try{
			  PackageInfo info=pm.getPackageInfo(getPackageName(), 0);
			  return info.versionName;
		  }catch(Exception e){
			  e.printStackTrace();
			  return "";
		  }
	  }
	
	
	@Override
	public void back() {
		finish();
	}
}
