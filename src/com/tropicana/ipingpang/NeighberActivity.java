package com.tropicana.ipingpang;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.inner.GeoPoint;
import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.utils.DistanceUtils;
import com.tropicana.ipingpang.utils.LogUtils;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class NeighberActivity extends BaseActivity implements OnItemClickListener{

	private View view;
	private ListView lv_neighber;
	private User myUser;
	private ArrayList<User> neighber_users;
	private float longitude;
	private float latitude;
	private float myLongitude;
	private float myLatitude;
	private double distance;
	
	private GeoPoint point;
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_neighber, null);
		lv_neighber=(ListView) view.findViewById(R.id.lv_neighber);
		fl_base.addView(view);
		tv_title.setText("��������");
		changeView(STATE_LOADING);
		getLocationFromServer();
		
		
		
	}

	/**
	 * �ӷ�������ȡλ����Ϣ
	 */
	private void getLocationFromServer() {
		neighber_users=new ArrayList<User>();
		BmobQuery<User> query=new BmobQuery<User>();
		query.findObjects(this, new FindListener<User>() {
			
			@Override
			public void onSuccess(List<User> list) {
				changeView(STATE_SUCCESS);
				for (User user : list) {
					distance=getNeighberDistance(user);   //��ȡ�������˾���
					//LogUtils.d("APP", "���룺"+distance);
					if (user.getAddress()!=null && distance<30000) {
						neighber_users.add(user);
					}
				}
				if (neighber_users!=null) {
					lv_neighber.setAdapter(new NeighberAdapter());
					LogUtils.d("APP", "���˰�");
				}else {
					changeView(STATE_EMPTY);
					LogUtils.d("APP", "�տ���Ҳ");
				}
				
			}
			
			@Override
			public void onError(int code, String msg) {
				changeView(STATE_ERROR);			
				LogUtils.d("APP", "����������");
			}
		});
		
	}

	public class NeighberAdapter extends BaseAdapter{

		private BitmapUtils utils;
		
		public NeighberAdapter(){
			utils=new BitmapUtils(NeighberActivity.this);
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}
		
		@Override
		public int getCount() {
			return neighber_users.size();
		}

		@Override
		public Object getItem(int position) {
			return neighber_users.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView==null) {
				convertView=View.inflate(getApplicationContext(), R.layout.item_neighber, null);
				holder=new ViewHolder();
				holder.userNick=(TextView) convertView.findViewById(R.id.tv_neighber_nick);
				holder.userAddress=(TextView) convertView.findViewById(R.id.tv_neighber_address);
				holder.userImage=(ImageView) convertView.findViewById(R.id.iv_neighber_icon);
				holder.userDistance=(TextView) convertView.findViewById(R.id.tv_distance);
				convertView.setTag(holder);
			}else {
				holder=(ViewHolder) convertView.getTag();
			}
			User user=neighber_users.get(position);
			holder.userNick.setText(user.getNick());
			holder.userAddress.setText(user.getAddress());
			distance=getNeighberDistance(user);   //��ȡ�������˾���
			holder.userDistance.setText((int)distance+"������");
			utils.display(holder.userImage, user.getAvatar());
			return convertView;		
		}
	
	}
	
	public  class ViewHolder{
		public TextView userDistance;
		public TextView userAddress;
		public TextView userNick;
		public ImageView userImage;
		
	}
	
	/**
	 * ��ȡ�븽�����Ѿ���
	 */
	private float getNeighberDistance(User user) {
		User current = (User) userManager.getCurrentUser(User.class);
		
		if (current.getLocation()!=null && user.getLocation()!=null) {
			myLongitude=current.getLongitude();
			myLatitude=current.getLatitude();
			latitude=user.getLatitude();
			longitude=user.getLongitude();
			//LogUtils.d("APP", myLatitude+myLongitude+latitude+longitude+"");
			return (float) DistanceUtils.getDistance(myLatitude, myLongitude, latitude, longitude);
		}else {
			return 0f;
		}
				
	}

	@Override
	public void reload() {
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				getLocationFromServer();
			}
		}, 1000);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		//TODO �����������
		
	}
	
}
