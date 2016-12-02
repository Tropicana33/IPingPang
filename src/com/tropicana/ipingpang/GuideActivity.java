package com.tropicana.ipingpang;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GuideActivity extends Activity {

	private ViewPager vp_guide;
	private Button btn_start;
	private List<ImageView> images;
	private CirclePageIndicator mIndicator; //头条新闻位置指示器

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		initView();
		
	}
	
	private void initView() {
		vp_guide=(ViewPager) findViewById(R.id.vp_guide);
		btn_start=(Button) findViewById(R.id.btn_start);
		mIndicator=(CirclePageIndicator) findViewById(R.id.indicator_guide);
		
		btn_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
				sp.edit().putBoolean("is_user_guide_before", true).commit();
				Intent intent=new Intent(GuideActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
		initData();
		
		vp_guide.setAdapter(new GuideAdapter());
		mIndicator.setViewPager(vp_guide);
		mIndicator.setSnap(true);  //支持快照显示
		mIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
		mIndicator.onPageSelected(0);  //让指示器重新定位到第一个位置
		
		//vp_guide.setOnPageChangeListener(new MyOnPageChangeListener());
		
		
		
	}
	
	
	private void initData() {
		int[] imageIds=new int[]{
				R.drawable.guide_1,R.drawable.guide_2,R.drawable.guide_3
		};
		images=new ArrayList<ImageView>();
		for (int i = 0; i < imageIds.length; i++) {
			ImageView image=new ImageView(this);
			image.setBackgroundResource(imageIds[i]);
			images.add(image);

		}
		
		
	}


	//viewpaper适配器
	 private class GuideAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		 
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(images.get(position));
			return images.get(position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((ImageView)object);
		}
		
	 }
	 
	 //滑动事件监听
	 private class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
			if (position==images.size()-1) {
				btn_start.setVisibility(View.VISIBLE);
			}else {
				btn_start.setVisibility(View.GONE);
			}
			
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
			
		}

	 }
}











