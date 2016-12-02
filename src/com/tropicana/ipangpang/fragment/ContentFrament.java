package com.tropicana.ipangpang.fragment;

import java.util.ArrayList;

import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.base.impl.BaseMediaCenterPager;
import com.tropicana.ipingpang.base.impl.BaseHomePager;
import com.tropicana.ipingpang.base.impl.BaseStarsPager;
import com.tropicana.ipingpang.base.impl.BaseFoundPager;
import com.tropicana.ipingpang.base.impl.BaseVedioPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 主页内容
 * 
 * @author Administrator
 *
 */
public class ContentFrament extends BaseFragment {

	private RadioGroup rg_group;
	private ViewPager mViewPager;
	private ArrayList<BasePager> mViewPagerList;
	private RadioButton rb_home;
	private RadioButton rb_setting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View initViews() {
		View view = View.inflate(mActivity,R.layout.fragment_content, null);
		mViewPager=(ViewPager) view.findViewById(R.id.bottom_view_tab);
		rg_group=(RadioGroup) view.findViewById(R.id.rg_group);
		rb_home=(RadioButton) view.findViewById(R.id.rb_home);
		rb_setting=(RadioButton) view.findViewById(R.id.rb_setting);
		return view;
	}

	@Override
	public void initData() {
		rg_group.check(R.id.rb_home);//默认选择第I个
		mViewPagerList = new ArrayList<BasePager>();

		// 初始化5个子页面
		mViewPagerList.add(new BaseHomePager(mActivity));
		mViewPagerList.add(new BaseVedioPager(mActivity));
		mViewPagerList.add(new BaseMediaCenterPager(mActivity));
		mViewPagerList.add(new BaseStarsPager(mActivity));
		mViewPagerList.add(new BaseFoundPager(mActivity));

		mViewPagerList.get(0).initData();//初始化第一页数据
		
		mViewPager.setAdapter(new ContentAdapter());

		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					mViewPager.setCurrentItem(0, false);
					break;
				case R.id.rb_vedio:
					mViewPager.setCurrentItem(1, false);
					break;
				case R.id.rb_news:
					mViewPager.setCurrentItem(2, false);
					break;
				case R.id.rb_data:
					mViewPager.setCurrentItem(3, false);
					break;
				case R.id.rb_setting:
					mViewPager.setCurrentItem(4, false);
					break;
				default:
					break;
				}

			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				mViewPagerList.get(position).initData();  //页面切换时初始化数据
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

	}

	private class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mViewPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePager pager = mViewPagerList.get(position);
			container.addView(pager.mRootView);			
			return pager.mRootView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {			
			return POSITION_NONE;
		}
	}

	@Override
	public void onResume() {		
		super.onResume();
		Log.d("TOUCH", "viewpager数据更新");
		
	}
	
	
	
}
