package com.tropicana.ipingpang;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.tropicana.ipangpang.fragment.ContentFrament;
import com.tropicana.ipangpang.fragment.LeftMenuFragment;
import com.tropicana.ipingpang.application.MyApplication;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.location.MyLocationManager;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.bmob.im.BmobUserManager;

public class MainActivity extends SlidingFragmentActivity {
	private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
	private static final String FRAGMENT_CONTENT = "fragment_content";

	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener mListener;
	private boolean isAutoRotate=false;
	private MyLocationManager mLocationManager;
	public BmobUserManager userManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		
		WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		int menuWidth=3*width/5;
		
		setBehindContentView(R.layout.left_menu);  //侧边栏布局
		SlidingMenu slidingMenu=getSlidingMenu();   
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN); //左边触摸
		slidingMenu.setBehindOffset(menuWidth); //设置预留屏幕宽度
		
		initFragment();
		
		userManager = BmobUserManager.getInstance(this);
		mLocationManager=new MyLocationManager(this, userManager);

		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		Log.d("APP", "当前旋转状态"+isAutoRotate);
		setGravitySensor();  //重力感应
		if (isAutoRotate) {
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		}else {
			sensorManager.unregisterListener(mListener);
		}
		
	}

	private void initFragment() {
		android.support.v4.app.FragmentManager fm=getSupportFragmentManager();
		FragmentTransaction transaction=fm.beginTransaction(); //开启事务
		
		transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(),FRAGMENT_LEFT_MENU);  //用fragment替换layout
		transaction.replace(R.id.fl_content, new ContentFrament(),FRAGMENT_CONTENT);
		transaction.commit();  //提交事务
		
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

	/**
	 * 连续按两次返回键就退出
	 */
	private static long firstTime;
	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			MyApplication.getInstance().exit();
			//finish();
			super.onBackPressed();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",0).show();
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {  //界面不可见时取消监听重力感应
		sensorManager.unregisterListener(mListener);
		//mLocationManager.stop(); //暂停位置监听
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		//mLocationManager.start();				
		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		if (isAutoRotate) {
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		}else {
			sensorManager.unregisterListener(mListener);
		}
		super.onResume();
	}
	
}
