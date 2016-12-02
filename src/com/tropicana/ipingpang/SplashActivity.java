package com.tropicana.ipingpang;

import com.baidu.mapapi.SDKInitializer;
import com.wandoujia.ads.sdk.Ads;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import cn.bmob.v3.Bmob;

public class SplashActivity extends Activity {

	private boolean is_user_guide_before;
	/**
	 * SDK初始化建议放在启动页,bmob
	 */
	public static String APPID = "5a8cfeadb24939c3e3674066680065a5";
	public static String AD_ID="100035678";
	public static String AD_SK="7b73e308b925cff0008f3a5229a12c53";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		Bmob.initialize(getApplicationContext(),APPID); //初始化bmob
		//SDKInitializer.initialize(getApplicationContext()); //百度地图初始化
		/*try {
			Ads.init(SplashActivity.this, AD_ID, AD_SK);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		initView();
	}

	private void initView() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
				is_user_guide_before = sp.getBoolean("is_user_guide_before", false);				
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				

			}
		}, 2000);

	}

}
