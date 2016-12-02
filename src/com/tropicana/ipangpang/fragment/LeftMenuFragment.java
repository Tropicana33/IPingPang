package com.tropicana.ipangpang.fragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.tropicana.ipingpang.AboutActivity;
import com.tropicana.ipingpang.FeedBackActivity;
import com.tropicana.ipingpang.LoginActivity;
import com.tropicana.ipingpang.MainActivity;
import com.tropicana.ipingpang.MyInfoActivity;
import com.tropicana.ipingpang.HistoryActivity;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.SettingActivity;
import com.tropicana.ipingpang.StoreActivity;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import com.tropicana.ipingpang.utils.ShareSDKUtils;

import android.R.menu;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 侧边栏内容
 * @author Administrator
 *
 */
public class LeftMenuFragment extends BaseFragment {

	String[] titles=new String[]{"我的账号","我的收藏","我的历史","分享应用",
			"关于我们","建议反馈","我的设置"};
	
	private int mCurrentPosition;
	private MenuAdapter mAdapter;
	private ListView lv_LeftList;
	private boolean isLogin;
	
	
	@Override
	public View initViews() {
		View view=View.inflate(mActivity,R.layout.fragment_left_menu, null);
		lv_LeftList=(ListView) view.findViewById(R.id.lv_leftMenu_list);
		return view;
	}

	@Override
	public void initData() {
		mAdapter=new MenuAdapter();
		lv_LeftList.setAdapter(new MenuAdapter());
		lv_LeftList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				mCurrentPosition=position;
				Log.d("SEND","当前点击位置："+ mCurrentPosition);
				mAdapter.notifyDataSetChanged();  //刷新adapter即listview
				switch (position) {
				case 0:   //我的账号
					isLogin=SharePreferenceUtils.getBoolean(mActivity, "is_login", false);
					if (isLogin) {
						Intent intentInfo=new Intent(mActivity, MyInfoActivity.class);
						mActivity.startActivity(intentInfo);
					}else {
						Intent intentLogin=new Intent(mActivity, LoginActivity.class);
						mActivity.startActivity(intentLogin);
					}					
					break;
				case 1:  //我的收藏
					Intent intentStore=new Intent(mActivity, StoreActivity.class);
					mActivity.startActivity(intentStore);
					break;
				case 2:  //我的历史
					Intent intentHistory=new Intent(mActivity, HistoryActivity.class);
					mActivity.startActivity(intentHistory);
					break;
				case 3:  //分享应用
					ShareSDKUtils sdkUtils=new ShareSDKUtils(mActivity);
					sdkUtils.showShare();
					break;
				case 4:  //关于爱乒乓
					Intent intentAbout=new Intent(mActivity, AboutActivity.class);
					mActivity.startActivity(intentAbout);
					break;
				case 5:  //建议反馈
					Intent intentFeedBack=new Intent(mActivity, FeedBackActivity.class);
					mActivity.startActivity(intentFeedBack);
					break;
				case 6:  //我的设置
					Intent intentSetting=new Intent(mActivity, SettingActivity.class);
					mActivity.startActivity(intentSetting);
					break;
				default:
					break;
				}
				
				toggleSlidingMenu();
				
			}
		});
	}
	
	/**
	 * 侧边栏状态切换
	 */
	protected void toggleSlidingMenu() {
		MainActivity mainUI=(MainActivity) mActivity;
		SlidingMenu slidingMenu=mainUI.getSlidingMenu();
		slidingMenu.toggle();//切换状态，隐藏，显示
	}


	public class MenuAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object getItem(int position) {
			return titles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=View.inflate(mActivity, R.layout.left_menu_list_item, null);
			TextView itemtitle=(TextView) view.findViewById(R.id.tv_left_item_name);
			itemtitle.setText(titles[position]);
			
			if(mCurrentPosition==position){
				itemtitle.setEnabled(true);
			}else{
				itemtitle.setEnabled(false);
			}
			
			return view;
		}
		
	}
	
}
