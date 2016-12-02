package com.tropicana.ipingpang.location;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.tropicana.ipingpang.application.MyApplication;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.utils.LogUtils;
import android.content.Context;
import android.sax.StartElementListener;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

public class MyLocationManager {

	private String address;
	private Float latitude;
	private Float longitude;
	// 定位相关
	private LocationClient mLocClient;
	private Context context;
	private MyLocationListenner myLocationListenner;
	private BmobUserManager userManager;
	
	public MyLocationManager(Context context,BmobUserManager userManager) {
		this.context=context;
		this.userManager=userManager;		
		InitLocationClient();
	}

	private void InitLocationClient() {
		// 定位初始化
		mLocClient = new LocationClient(context);
		myLocationListenner= new MyLocationListenner();
		mLocClient.registerLocationListener(myLocationListenner);
		LocationClientOption option = new LocationClientOption();
		//option.setOpenGps(true);// 打开gps
		option.setIsNeedAddress(true);
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());// 获得当前时间
			sb.append("\nerror code : ");
			sb.append(location.getLocType());// 获得erro code得知定位现状
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());// 获得纬度
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());// 获得经度
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// 通过GPS定位
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 获得速度
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());// 获得当前地址
				sb.append(location.getDirection());// 获得方位
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 通过网络连接定位
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());// 获得当前地址
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());// 获得经营商？
			}
			LogUtils.d("APP", sb.toString());
			latitude = (float) location.getLatitude();
			longitude = (float) location.getLongitude();
			address = location.getAddrStr();
			if (BmobUser.getCurrentUser(MyApplication.getContext())!=null) {
				sendAddressToBmob(); // 传输定位信息到Bmob
			}
			

		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	// 传输定位信息到Bmob
	public void sendAddressToBmob() {
		User current = (User) userManager.getCurrentUser(User.class);
		User user = new User();
		user.setObjectId(current.getObjectId());
		user.setAddress(address);
		user.setLongitude(longitude);
		user.setLatitude(latitude);
		BmobGeoPoint point = new BmobGeoPoint(longitude, latitude);
		user.setLocation(point);
		user.update(context, new UpdateListener() {

			@Override
			public void onSuccess() {
				// ShowToast("定位上传成功");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// ShowToast("定位上传失败");
			}
		});
	}

	/**
	 * 停止位置监听
	 */
	public void stop(){
		mLocClient.stop();
	}
	public void start(){
		mLocClient.start();
	}
	
}
