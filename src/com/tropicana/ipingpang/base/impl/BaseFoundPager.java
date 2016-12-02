package com.tropicana.ipingpang.base.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tropicana.ipingpang.NeighberActivity;
import com.tropicana.ipingpang.LoginActivity;
import com.tropicana.ipingpang.MyInfoActivity;
import com.tropicana.ipingpang.HistoryActivity;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.ShopActivity;
import com.tropicana.ipingpang.StoreActivity;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.base.BasePager;
import com.tropicana.ipingpang.bean.NewsMenuData;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.ImageLoadOptions;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import com.tropicana.ipingpang.utils.ShareSDKUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class BaseFoundPager extends BasePager implements OnItemClickListener,OnClickListener{

	private View view;
	private ListView lv_setting;
	private RelativeLayout rl_login;
	private ArrayList<String> mSettingStrings;
	private ArrayList<Integer> mIcons;
	private TextView tv_login_name,tv_login_description,tv_login_nick;
	private ImageView iv_login_head;
	public BmobUserManager userManager;
	public BmobChatManager manager;
	private ImageLoader imageLoader;
	private boolean isLogin=false;
	
	private static final int[] setting_icons={R.drawable.icon_shop,R.drawable.icon_neighber,
			R.drawable.icon_store,R.drawable.icon_history,R.drawable.icon_share};
	
	private static final String[] setting_names={"器材商店","附近球友","我的收藏","我的历史","分享应用"};
	
	
	public BaseFoundPager(Activity activity) {
		super(activity);

	}

	@Override
	public void initData() {
		tv_pager_title.setText("发现");
		view=mRootView.inflate(mActivity, R.layout.setting_pager, null);
		lv_setting=(ListView) view.findViewById(R.id.lv_setting_list);
		rl_login=(RelativeLayout) view.findViewById(R.id.rl_login);
		tv_login_name=(TextView) view.findViewById(R.id.tv_login_name);
		tv_login_description=(TextView) view.findViewById(R.id.tv_login_description);
		iv_login_head=(ImageView) view.findViewById(R.id.iv_login_head);
		tv_login_nick=(TextView) view.findViewById(R.id.tv_login_nick);
		fl_content.removeAllViews();
	    fl_content.addView(view);
	    
	    lv_setting.setAdapter(new SettingAdapter());
	    rl_login.setOnClickListener(this);
	    lv_setting.setOnItemClickListener(this);
	    ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mActivity));
	    refreshData();
	    
	}
	
	
	
	
	public class SettingAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return setting_names.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=View.inflate(mActivity, R.layout.setting_item, null);
			TextView tv=(TextView) view.findViewById(R.id.tv_setting);
			ImageView iv=(ImageView) view.findViewById(R.id.iv_setting_icon);
			iv.setImageResource(setting_icons[position]);	
			tv.setText(setting_names[position]);		
			return view;
			
			
		}

	}

	/**
	 * 更新信息
	 */
	private void refreshData() {
		userManager = BmobUserManager.getInstance(mActivity);  //初始化userManger
		isLogin=SharePreferenceUtils.getBoolean(mActivity, "is_login", false);
		Log.d("TOUCH", "是否登入："+isLogin);
		if (isLogin) {
			Log.d("TOUCH", "已经登入");
			User user = userManager.getCurrentUser(User.class);
			Log.d("TOUCH","account = "+user.getUsername()+",sex= "+user.getSex()+"avar="+user.getAvatar());
			updateUser(user);  //更新我的资料
		}else {
			Log.d("TOUCH", "没有登入");
			tv_login_name.setVisibility(View.VISIBLE);
			tv_login_description.setVisibility(View.VISIBLE);
			tv_login_nick.setVisibility(View.INVISIBLE);
			iv_login_head.setImageResource(R.drawable.default_head);
		}
		
	}
	
	//更新我的资料
		private void updateUser(User user) {
			// 更改
			refreshAvatar(user.getAvatar());
			tv_login_nick.setText(user.getNick());
			tv_login_description.setVisibility(View.INVISIBLE);	
			tv_login_name.setVisibility(View.INVISIBLE);
			tv_login_nick.setVisibility(View.VISIBLE);
		}

		/**
		 * 更新头像 refreshAvatar		
		 */
		private void refreshAvatar(String avatar) {
			if (avatar != null && !avatar.equals("")) {
				ImageLoader.getInstance().displayImage(avatar, iv_login_head,
						ImageLoadOptions.getOptions()); 
			} else {
				iv_login_head.setImageResource(R.drawable.default_head);
			}
		}
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		Log.d("SEND", "当前点击位置："+position);
		switch (position) {
		case 0:   //器材商店
			Intent intentShop=new Intent(mActivity, ShopActivity.class);
			mActivity.startActivity(intentShop);
			break;
		case 1:   //附近球友
			Intent intentGame=new Intent(mActivity, NeighberActivity.class);
			mActivity.startActivity(intentGame);
			break;
		case 2:  //我的收藏
			Intent intentStore=new Intent(mActivity, StoreActivity.class);
			mActivity.startActivity(intentStore);
			break;
		case 3:  //我的历史
			Intent intentHistory=new Intent(mActivity, HistoryActivity.class);
			mActivity.startActivity(intentHistory);
			break;
		case 4:  //分享应用
			ShareSDKUtils sdkUtils=new ShareSDKUtils(mActivity);
			sdkUtils.showShare();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_login:
			if (isLogin) {
				Intent intentInfo=new Intent(mActivity, MyInfoActivity.class);
				mActivity.startActivity(intentInfo);
			}else {
				Intent intentLogin=new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intentLogin);
			}
			
			break;

		default:
			break;
		}
	}

	
	
}
