package com.tropicana.ipingpang.base.impl;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.bean.MediaCenterTabBean;
import com.tropicana.ipingpang.bean.MediaCenterTabBean.TabDetailInfo;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.mediacenter.NewsWebDetailPager;
import com.tropicana.ipingpang.mediacenter.PingpangChannelDetailPager;
import com.tropicana.ipingpang.mediacenter.PingpangWebDetailPager;
import com.tropicana.ipingpang.mediacenter.TeachDetailPager;
import com.tropicana.ipingpang.mediacenter.WeiXinInfoDetailPager;
import com.tropicana.ipingpang.mediacenter.WeiboDetailPager;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.viewpagerindicator.TabPageIndicator;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseMediaCenterPager extends BasePager implements OnPageChangeListener{

private static final String[] CONTENT = new String[] {"΢����Ѷ","��ѧ��Ƶ",
		"ƹ����վ","ƹ��Ƶ��","������վ", "ƹ��΢��"};
	
	private TabPageIndicator mIndicator;
	private ViewPager mViewPager;
	private TechAdapter mAdapter;
	
	private MediaCenterTabBean mediaCenterTabBean;
	
	private View view;
	private ArrayList<BaseDetailPager> mDetailPagers;
	private ArrayList<TabDetailInfo> mTabInfos;
	private String mUrl;
	
	public BaseMediaCenterPager(Activity activity) {
		super(activity);
		mUrl=GlobalContants.MEDIACENTER_URL;
	}

	@Override
	public void initData() {
		tv_pager_title.setText("ý������");
		
		mDetailPagers=new ArrayList<BaseDetailPager>();
		mTabInfos=new ArrayList<TabDetailInfo>();
		
		view=mRootView.inflate(mActivity, R.layout.stars_pager, null);
		mIndicator=(TabPageIndicator) view.findViewById(R.id.vpi_stars_indicator);
		mViewPager=(ViewPager) view.findViewById(R.id.vp_stars_pager);
		
		fl_content.removeAllViews();
		fl_content.addView(view);
		
		getDataFromServer();
		
		//��ʼ��������ҳ��
		    mDetailPagers.add(new WeiXinInfoDetailPager(mActivity));
			mDetailPagers.add(new TeachDetailPager(mActivity));
			mDetailPagers.add(new PingpangWebDetailPager(mActivity));
			mDetailPagers.add(new PingpangChannelDetailPager(mActivity));
			mDetailPagers.add(new NewsWebDetailPager(mActivity));
			mDetailPagers.add(new WeiboDetailPager(mActivity));

		
		/*String cache=CacheUtils.getCache(GlobalContants.MEDIACENTER_URL, mActivity);
		if (!TextUtils.isEmpty(cache)) {  //�жϻ������Ƿ�������
			//parseData(cache);
		}*/
		
		
		mAdapter=new TechAdapter();
		mViewPager.setAdapter(mAdapter);
		//��manifest�ж�����ʽ
		mIndicator.setViewPager(mViewPager);  //��ָ���viewpager��������
		mIndicator.setOnPageChangeListener(this);
		

	}

	private void getDataFromServer() {
		HttpUtils utils=new HttpUtils();
		utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mActivity,"�����������磬��������ʧ��", Toast.LENGTH_SHORT).show();
				error.printStackTrace();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

					String result = responseInfo.result;
					Log.d("SEND","MediaCenter���ؽ����"+result);
					CacheUtils.setCache(mActivity, GlobalContants.MEDIACENTER_URL, result);
				
				    parseData(result);
	
			}
		});
		
	}

	private void parseData(String result) {
		Gson gson=new Gson();
		mediaCenterTabBean=gson.fromJson(result, MediaCenterTabBean.class);
		Log.d("SEND","ҳǩ����ҳ������"+mediaCenterTabBean);

		mTabInfos=mediaCenterTabBean.menu;

			/*if(mTabInfos!=null){
				//��ʼ��������ҳ��
				for (int i = 0; i < CONTENT.length; i++) {
					TechDetailPager pager=new TechDetailPager(mActivity);
					mDetailPagers.add(pager);
				}	
				mAdapter=new TechAdapter();
				mViewPager.setAdapter(mAdapter);
				
				//��manifest�ж�����ʽ
				mIndicator.setViewPager(mViewPager);  //��ָ���viewpager��������
				mIndicator.setOnPageChangeListener(this);

			}*/
		
		
	}

	public class TechAdapter extends PagerAdapter{

		 @Override
	        public CharSequence getPageTitle(int position) {
	            return CONTENT[position];
	        }

		@Override
		public int getCount() {
			return CONTENT.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BaseDetailPager starsPager=mDetailPagers.get(position);
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
