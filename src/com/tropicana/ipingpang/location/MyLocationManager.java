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
	// ��λ���
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
		// ��λ��ʼ��
		mLocClient = new LocationClient(context);
		myLocationListenner= new MyLocationListenner();
		mLocClient.registerLocationListener(myLocationListenner);
		LocationClientOption option = new LocationClientOption();
		//option.setOpenGps(true);// ��gps
		option.setIsNeedAddress(true);
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());// ��õ�ǰʱ��
			sb.append("\nerror code : ");
			sb.append(location.getLocType());// ���erro code��֪��λ��״
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());// ���γ��
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());// ��þ���
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// ͨ��GPS��λ
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// ����ٶ�
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());// ��õ�ǰ��ַ
				sb.append(location.getDirection());// ��÷�λ
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// ͨ���������Ӷ�λ
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());// ��õ�ǰ��ַ
				// ��Ӫ����Ϣ
				sb.append("\noperationers : ");
				sb.append(location.getOperators());// ��þ�Ӫ�̣�
			}
			LogUtils.d("APP", sb.toString());
			latitude = (float) location.getLatitude();
			longitude = (float) location.getLongitude();
			address = location.getAddrStr();
			if (BmobUser.getCurrentUser(MyApplication.getContext())!=null) {
				sendAddressToBmob(); // ���䶨λ��Ϣ��Bmob
			}
			

		}

		public void onReceivePoi(BDLocation poiLocation) {

		}
	}

	// ���䶨λ��Ϣ��Bmob
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
				// ShowToast("��λ�ϴ��ɹ�");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// ShowToast("��λ�ϴ�ʧ��");
			}
		});
	}

	/**
	 * ֹͣλ�ü���
	 */
	public void stop(){
		mLocClient.stop();
	}
	public void start(){
		mLocClient.start();
	}
	
}
