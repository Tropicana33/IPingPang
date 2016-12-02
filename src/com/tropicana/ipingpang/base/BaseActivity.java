package com.tropicana.ipingpang.base;

import java.util.List;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.utils.LogUtils;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

public abstract class BaseActivity extends Activity implements OnClickListener{

	public static final int STATE_UNKOWN = 0;
	public static final int STATE_LOADING = 1;
	public static final int STATE_ERROR = 2;
	public static final int STATE_EMPTY = 3;
	public static final int STATE_SUCCESS = 4;
	public int state = STATE_UNKOWN;
	
	public LinearLayout rl_title;
	public TextView tv_title;
	public ImageButton btn_back;
	public ImageButton btn_close;
	public FrameLayout fl_base;
	public RelativeLayout rl_loading_empty;
	public RelativeLayout rl_loading_error;
	public Button btn_reload;
	public RelativeLayout rl_loading;
	
	public BmobUserManager userManager;
	public BmobChatManager manager;
	public int mScreenWidth;
	public int mScreenHeight;
	public SensorManager sensorManager;
	public Sensor sensor;
	public SensorEventListener mListener;
	public boolean isAutoRotate;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);
		initView();
		initGravitySensor();
		initData();
		//changeView(state);
	}
	
	public void initView(){
		rl_title=(LinearLayout) findViewById(R.id.rl_title);
		tv_title=(TextView) findViewById(R.id.tv_video_title_base);
		btn_back=(ImageButton) findViewById(R.id.btn_back_base);
		btn_close=(ImageButton) findViewById(R.id.btn_close_base);
		rl_loading=(RelativeLayout) findViewById(R.id.rl_loading);
		rl_loading_empty=(RelativeLayout) findViewById(R.id.rl_loading_empty);
		rl_loading_error=(RelativeLayout) findViewById(R.id.rl_loading_error);
		btn_reload=(Button) findViewById(R.id.btn_reload);
		fl_base=(FrameLayout) findViewById(R.id.fl_base_content);
		btn_back.setOnClickListener(this);
		btn_close.setOnClickListener(this);
		btn_reload.setOnClickListener(this);
		btn_reload.setClickable(true);
		//btn_close.setVisibility(View.INVISIBLE);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	/**
	 * 子类重写，初始化布局
	 */
	public abstract void initData();
	
	public void initGravitySensor() {
		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		
		//重力感应
				sensorManager=(SensorManager) getSystemService(this.SENSOR_SERVICE);
				sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				if (isAutoRotate) {
					sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
				}else {
					sensorManager.unregisterListener(mListener);
				}
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
					public void onAccuracyChanged(Sensor arg0, int arg1) {
						
					}
				};
		}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_base:
			back();
			break;
		case R.id.btn_close_base:
			close();
			break;
		case R.id.btn_reload:
			reload();
			break;
		default:
			break;
		}
		
	}

	public void reload() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		isAutoRotate=SharePreferenceUtils.getBoolean(this, "auto_rotate",false);
		if (isAutoRotate) {
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);
		}else {
			sensorManager.unregisterListener(mListener);
		}
	}
	
	public void close() {
			
	}

	public void back() {
			
	}
	

	
	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {			
				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								0);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
			
		}
	}
	
	/**
	 * 改变显示状态
	 */
	public void changeView(int state){
		if (state==STATE_LOADING || state==STATE_UNKOWN) {
			rl_loading.setVisibility(View.VISIBLE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.GONE);			
		}else if (state==STATE_ERROR) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.VISIBLE);	
			//btn_reload.setClickable(true);
		}else if (state==STATE_EMPTY) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.VISIBLE);
			rl_loading_error.setVisibility(View.GONE);	
		}else if (state==STATE_SUCCESS) {
			rl_loading.setVisibility(View.GONE);
			rl_loading_empty.setVisibility(View.GONE);
			rl_loading_error.setVisibility(View.GONE);	
		}
	}
	
	/**校验数据 */
	public int checkData(List datas) {
		if(datas==null){
			return STATE_ERROR;//  请求服务器失败
		}else{
			if(datas.size()==0){
				return STATE_EMPTY;
			}else{
				return STATE_SUCCESS;
			}
		}
		
	}
	
}
