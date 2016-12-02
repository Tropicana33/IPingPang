package com.tropicana.ipingpang.base.impl;

import java.util.ArrayList;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.stars.StarsDetailPagerMan;
import com.tropicana.ipingpang.stars.StarsDetailPagerWomen;
import com.viewpagerindicator.TabPageIndicator;
import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class BaseStarsPager extends BasePager implements OnPageChangeListener{

	private static final String[] CONTENT = new String[] { "男明星", "女明星"};
	
	private TabPageIndicator mIndicator;
	private ViewPager mViewPager;
	private StarsMenuAdapter mAdapter;
	private View view;
	private ArrayList<BaseDetailPager> mStarsDetailPagers;
	
	public BaseStarsPager(Activity activity) {
		super(activity);

	}

	@Override
	public void initData() {
		tv_pager_title.setText("明星");
		mStarsDetailPagers=new ArrayList<BaseDetailPager>();
		
		view=mRootView.inflate(mActivity, R.layout.stars_pager, null);
		mIndicator=(TabPageIndicator) view.findViewById(R.id.vpi_stars_indicator);
		mViewPager=(ViewPager) view.findViewById(R.id.vp_stars_pager);
		
		fl_content.removeAllViews();
		fl_content.addView(view);
		
		//初始化两个子页面
			mStarsDetailPagers.add(new StarsDetailPagerMan(mActivity));
			mStarsDetailPagers.add(new StarsDetailPagerWomen(mActivity));

			//mStarsDetailPagers.get(0).initData();//初始化第一个页面
		mAdapter=new StarsMenuAdapter();
		mViewPager.setAdapter(mAdapter);
		
		//在manifest中定义样式
		mIndicator.setViewPager(mViewPager);  //将指针和viewpager关联起来
		mIndicator.setOnPageChangeListener(this);
	}
	
	
	
	public class StarsMenuAdapter extends PagerAdapter{

		 @Override
	        public CharSequence getPageTitle(int position) {
	            return CONTENT[position];
	        }

		
		@Override
		public int getCount() {
			return mStarsDetailPagers.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BaseDetailPager starsPager=mStarsDetailPagers.get(position);
			container.addView(starsPager.mRootView);
			starsPager.initData();
			return starsPager.mRootView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}



	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		
	}


	
	
	
}









