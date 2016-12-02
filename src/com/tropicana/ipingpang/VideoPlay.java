package com.tropicana.ipingpang;

import com.tropicana.ipingpang.utils.SharePreferenceUtils;

import android.R.integer;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.v3.listener.UpdateListener;


public class VideoPlay extends Activity implements OnClickListener{

	private WebView webView;
	private ProgressBar pb_load_value;
	private TextView tv_video_title;
	private ImageButton btn_back,btn_close;
	private LinearLayout rl_video_title;
	private String mUrl;
	private SensorManager sensorManager;
	private Sensor sensor;
	private SensorEventListener mListener;
	private boolean isAutoRotate=false;
	
	private boolean isUp=false;
		
	Handler mHandler=new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		//rl_video_title.setVisibility(View.INVISIBLE);
    	}
    };
	
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_play);
		
		setGravitySensor();
		initData();
    };
    
	public void initData(){		
		pb_load_value=(ProgressBar) findViewById(R.id.pb_load_value);
		webView=(WebView) findViewById(R.id.wv);
		tv_video_title=(TextView) findViewById(R.id.tv_video_title);
		btn_back=(ImageButton) findViewById(R.id.btn_back);
		btn_close=(ImageButton) findViewById(R.id.btn_close);
		btn_close.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		pb_load_value.setProgress(0);
		rl_video_title=(LinearLayout) findViewById(R.id.rl_video_title);
		mUrl = getIntent().getStringExtra("video_url");//��ȡ����url
		
		WebSettings settings = webView.getSettings();  
        settings.setJavaScriptEnabled(true);  //֧��javascript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);  //֧�ֲ��
        settings.setPluginState(PluginState.ON);  
        settings.setAllowFileAccess(true);  
        settings.setLoadWithOverviewMode(true);  
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ONLY); //���ӻ������
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
  
        webView.loadUrl(mUrl); //������Ƶurl
       webView.setOnTouchListener(new OnTouchListener() {		
		int startY=0;
		int endY=0;
    	   @Override
		public boolean onTouch(View view, MotionEvent event) {
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				/*if (isUp) {
					Down();
				}	*/
				startY=(int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:				
				break;
			case MotionEvent.ACTION_UP:
				//mHandler.sendEmptyMessageDelayed(0, 4000); //ʱ��4�����ر�����
				endY=(int) event.getY();
				int dy=endY-startY;
				if (dy>300 && isUp) {
					Down();
				}
				break;
			default:
				break;
			}
			return false;
		}
       });
       
        mHandler.sendEmptyMessageDelayed(0, 4000); //ʱ��4�����ر�����
        
        webView.setWebViewClient(new WebViewClient(){
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		super.onPageStarted(view, url, favicon);
        		tv_video_title.setText(webView.getTitle());
        		pb_load_value.setVisibility(View.VISIBLE);
        	}
        	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		pb_load_value.setVisibility(View.INVISIBLE);
        		tv_video_title.setText(webView.getTitle());
        		super.onPageFinished(view, url);
        	}

        	@Override//������ת���Ӷ��ڴ˷����лص�
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	   view.loadUrl(url);
        		Log.d("shouldOverrideUrlLoading", url);
    			// �����Զ���scheme
    			if (!url.startsWith("http")) {
    				Log.i("shouldOverrideUrlLoading", "�����Զ���scheme");
    				//Toast.makeText(getApplication(), "��Ҫ���ؿͻ����տ�", Toast.LENGTH_SHORT).show();
    				try {
    					// ���¹̶�д��
    					final Intent intent = new Intent(Intent.ACTION_VIEW,
    							Uri.parse(url));
    					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
    							| Intent.FLAG_ACTIVITY_SINGLE_TOP);
    					startActivity(intent);
    				} catch (Exception e) {
    					// ��ֹû�а�װ�����
    					e.printStackTrace();
    				}
    				return true;
    			}
    			return false;
           		
           	}
        	   
           });

		
        
        webView.setWebChromeClient(new WebChromeClient(){
        	
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		pb_load_value.setProgress(newProgress);
        	}
        	
        	@Override
        	public void onReceivedTitle(WebView view, String title) {
        		tv_video_title.setText(title);
        	}
        	
        });
       
	}
	


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) { 
           
			if (webView.canGoBack()) {
				webView.goBack(); //goBack()��ʾ����WebView����һҳ�� 
                return true; 
			} 
		}	
        return super.onKeyDown(keyCode, event);
	}
	
	  @Override  
	    public void onPause() {// �̳���Activity  
	        super.onPause();  
	        webView.onPause(); 
	        sensorManager.unregisterListener(mListener);
	    }  
	  
	    @Override  
	    public void onResume() {// �̳���Activity  
	        super.onResume();  
	        webView.onResume(); 
			sensorManager.registerListener(mListener, sensor,SensorManager.SENSOR_DELAY_NORMAL);			
	    }  
	
	    @Override
	    protected void onDestroy() {
	    	webView.destroy();
	    	super.onDestroy();
	    }

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_back:				
				finish();
				break;
			case R.id.btn_close:
				Up();				
				break;
			default:
				break;
			}
		}
    
		/**
		 * ��ʼ������������
		 */
		private void setGravitySensor() {
			//������Ӧ
			sensorManager=(SensorManager) getSystemService(this.SENSOR_SERVICE);
			sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mListener=new SensorEventListener() {				
				@Override
				public void onSensorChanged(SensorEvent event) {
					float xValue=event.values[0];
					float yValue=Math.abs(event.values[1]);
						if (xValue>8) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}else if (xValue<-8) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}else if (yValue>8) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);						
					}					
				}
					@Override
					public void onAccuracyChanged(Sensor sensor, int accuracy) {					
					}
				};
			
		}
		
		/**
		 * �����õ��������ؼ��߶�
		 * @return
		 */
		public int getTitleBarHeight(){
			int width = rl_video_title.getMeasuredWidth();  //  ���ڿ�Ȳ��ᷢ���仯  ��ȵ�ֵȡ����
			rl_video_title.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;//  �ø߶Ȱ�������

			//    ����1  �����ؼ�mode    ����2  ��С 
			int widthMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, width);  //  mode+size
			int heightMeasureSpec=MeasureSpec.makeMeasureSpec(MeasureSpec.AT_MOST, 1000);// �ҵĸ߶� �����1000
			// �������� �����һ����ȷ��ֵwidth, �߶������1000,��ʵ��Ϊ׼
			rl_video_title.measure(widthMeasureSpec, heightMeasureSpec); // ͨ���÷������²����ؼ� 
			
			return rl_video_title.getMeasuredHeight();
		}
		
		//���������
		private void Up(){
			int startHeight=getTitleBarHeight();
			int targetHeight=0;
			// ֵ����
			ValueAnimator animator=ValueAnimator.ofInt(startHeight,targetHeight);
			final LinearLayout.LayoutParams params=(LayoutParams) rl_video_title.getLayoutParams();
			animator.addUpdateListener(new AnimatorUpdateListener() {					
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					int value=(Integer) animator.getAnimatedValue();// ���е�ǰʱ����һ��ֵ
					params.height=value;
					rl_video_title.setLayoutParams(params);// ˢ�½���
					System.out.println(value);
				}
			});
			animator.setDuration(500);
			animator.start();
			isUp=true;
		}
		//չ��������
		private void Down(){
			int startHeight=0;
			int targetHeight=getTitleBarHeight();
			// ֵ����
			ValueAnimator animator=ValueAnimator.ofInt(startHeight,targetHeight);
			final LinearLayout.LayoutParams params=(LayoutParams) rl_video_title.getLayoutParams();
			animator.addUpdateListener(new AnimatorUpdateListener() {					
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					int value=(Integer) animator.getAnimatedValue();// ���е�ǰʱ����һ��ֵ
					params.height=value;
					rl_video_title.setLayoutParams(params);// ˢ�½���
					System.out.println(value);
				}
			});
			animator.setDuration(500);
			animator.start();
			isUp=false;
		}
}
