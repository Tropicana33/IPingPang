package com.tropicana.ipingpang;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipangpang.view.ListViewForScrollView;
import com.tropicana.ipangpang.view.RefreshListView;
import com.tropicana.ipangpang.view.RefreshListView.RefreshListener;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bean.StarVideoListBean;
import com.tropicana.ipingpang.bean.StarVideoListBean.StarVideoInfo;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;
import com.tropicana.ipingpang.utils.LogUtils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������Ƶ�б�
 * 
 * @author Administrator
 *
 */
public class StarsVideoActivity extends BaseActivity implements OnItemClickListener {

	private String mStar_Url;
	private String mMoreUrl;
	private ListViewForScrollView lv_star_video_list;
	private View view;
	private ArrayList<StarVideoInfo> mStarVideoInfos;

	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	private ImageButton btn_star_info; // ��������չ������
	private LinearLayout rl_star_info_down, ll_star_des;
	private TextView tv_star_des; // ��������
	private boolean isDown = true;
	private String des;
	int startHeight = 0;
	int endHeight = 0;

	@Override
	public void initData() {
		view = View.inflate(getApplicationContext(), R.layout.star_video_list, null);
		lv_star_video_list = (ListViewForScrollView) view.findViewById(R.id.lv_star_video_list);
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.sv_star);
		mPullRefreshScrollView.setMode(Mode.PULL_FROM_START); // ֻ֧������ˢ��
		btn_star_info = (ImageButton) view.findViewById(R.id.btn_star_info_down);
		rl_star_info_down = (LinearLayout) view.findViewById(R.id.rl_star_info_down);
		tv_star_des = (TextView) view.findViewById(R.id.tv_star_des);
		ll_star_des = (LinearLayout) view.findViewById(R.id.ll_star_des);
		fl_base.addView(view);
		// ��������ˢ�½ӿ�
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
				Log.d("TOUCH", "ONREFRESH");
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		mScrollView.smoothScrollTo(0, 0);// �ֶ���scrollview�Ƶ���ǰ��

		mStarVideoInfos = new ArrayList<StarVideoInfo>();
		mStar_Url = GlobalContants.SERVER_URL + getIntent().getStringExtra("stars_url");

		String cache = CacheUtils.getCache(mStar_Url, this);
		if (!TextUtils.isEmpty(cache)) { // �жϻ������Ƿ�������
			//parseData(cache, false);
		}

		// lv_star_video_list.onRefreshStart(); //��һ��ˢ��
		changeView(STATE_LOADING);
		getDataFromServer();

		btn_close.setVisibility(View.INVISIBLE);

		lv_star_video_list.setOnItemClickListener(this);

		btn_star_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO չ�����Ǽ����Ϣ
				LogUtils.d("APP", "������Ϣչ��");
				Down();
			}
		});
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, mStar_Url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(getApplicationContext(), "�����������磬��������ʧ��", Toast.LENGTH_SHORT).show();
				// lv_star_video_list.onRefreshComplete(false); //��������ˢ�¿ؼ�
				if (mStarVideoInfos.size() == 0) {
					changeView(STATE_ERROR);
				}
				error.printStackTrace();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				Log.d("SEND", "stars���ؽ����" + result);
				// int state=checkData(result);
				changeView(STATE_SUCCESS);
				CacheUtils.setCache(getApplicationContext(), mStar_Url, result);
				parseData(result, false);
				// lv_star_video_list.onRefreshComplete(true); //��������ˢ�¿ؼ�
			}
		});

	}

	private void getDataMoreFromServer() {
		if (mMoreUrl != null) {
			HttpUtils utils = new HttpUtils();
			utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException error, String msg) {
					Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT).show();

					error.printStackTrace();
					// lv_star_video_list.onRefreshComplete(false); //��������ˢ�¿ؼ�
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = responseInfo.result;
					// System.out.println("ҳǩ����ҳ���ؽ����"+result);
					parseData(result, true);
					// lv_star_video_list.onRefreshComplete(false); //��������ˢ�¿ؼ�

				}
			});
		} else {
			// lv_star_video_list.onRefreshComplete(false);
			Toast.makeText(getApplicationContext(), "û�и���������", Toast.LENGTH_SHORT).show();
		}
	}

	private void parseData(String result, boolean isMore) {
		Gson gson = new Gson();
		StarVideoListBean starVideoListBean = gson.fromJson(result, StarVideoListBean.class);

		
		
		LinearLayout.LayoutParams params=(LayoutParams) tv_star_des.getLayoutParams();
		params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		tv_star_des.setLayoutParams(params);
		
		if (!isMore) { // �����ظ���
			des = starVideoListBean.des;
			tv_star_des.setText(des);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub	
					isDown=true;
					btn_star_info.setImageResource(R.drawable.arrow_up);
					endHeight = tv_star_des.getMeasuredHeight();
					Log.d("APP", "endHeight��" + endHeight);
					/*// ��ʼ���ı��߶�
					LinearLayout.LayoutParams params = (LayoutParams) tv_star_des.getLayoutParams();
					params.height = 0;
					tv_star_des.setLayoutParams(params);*/
				}
			}, 100);
			mStarVideoInfos = starVideoListBean.video;
			int state = checkData(mStarVideoInfos);
			changeView(state); // û�����ݣ���ʾû���ݽ���
			if (mStarVideoInfos != null) {
				lv_star_video_list.setAdapter(new StarVideoAdapter());
				tv_title.setText(starVideoListBean.player);
			}
		} else {
			// ���ظ���
			ArrayList<StarVideoInfo> news = starVideoListBean.video;
			if (mStarVideoInfos != null) {
				mStarVideoInfos.addAll(news);
				new StarVideoAdapter().notifyDataSetChanged();
			}
		}

	}

	private class StarVideoAdapter extends BaseAdapter {

		private BitmapUtils utils;

		public StarVideoAdapter() {
			utils = new BitmapUtils(getApplicationContext());
			utils.configDefaultLoadingImage(R.drawable.icon_default);
		}

		@Override
		public int getCount() {
			return mStarVideoInfos.size();
		}

		@Override
		public StarVideoInfo getItem(int position) {
			return mStarVideoInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.video_item, null);
				holder = new ViewHolder();
				holder.videoTitle = (TextView) convertView.findViewById(R.id.tv_video_item_title);
				holder.videoDate = (TextView) convertView.findViewById(R.id.tv_video_item_date);
				holder.videoImage = (ImageView) convertView.findViewById(R.id.iv_video_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.videoTitle.setText(getItem(position).title);
			holder.videoDate.setText(getItem(position).date);
			utils.display(holder.videoImage, getItem(position).thumbnail);
			return convertView;
		}

	}

	public static class ViewHolder {
		public TextView videoTitle;
		public TextView videoDate;
		public ImageView videoImage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("SEND", "��ǰ���λ�ã�" + position);
		if (mStarVideoInfos != null) {
			StarVideoInfo videoInfo = mStarVideoInfos.get(position);
			// �����¼��������ݿ�
			MyHistoryDbUtils historyUtils = new MyHistoryDbUtils(this);
			historyUtils.InsertData(videoInfo.title, videoInfo.url, historyUtils.getCurrentTime());

			Intent intent = new Intent(StarsVideoActivity.this, VideoPlay.class);
			intent.putExtra("video_url", videoInfo.url);
			startActivity(intent);

		}
	}

	@Override // ����ʧ�����¼���
	public void reload() {
		LogUtils.d("APP", "���������");
		changeView(STATE_LOADING);
		new Handler().postDelayed(new Runnable() {
			public void run() { // ��ʱ1000��ȡ����
				getDataFromServer();
			}
		}, 1000);
	}

	@Override
	public void back() {
		finish();
	}

	// ����ˢ���첽����
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			Log.d("TOUCH", "��������");
			getDataFromServer(); // ����
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d("TOUCH", "�������");
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	/**
	 * �����õ������ı��ؼ��߶�
	 * 
	 * @return
	 */
	public int getTextDesHeight() {
		int width = tv_star_des.getMeasuredWidth(); // ���ڿ�Ȳ��ᷢ���仯 ��ȵ�ֵȡ����		
		LinearLayout.LayoutParams params=(LayoutParams) tv_star_des.getLayoutParams();
		params.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		tv_star_des.setLayoutParams(params);
		Log.d("APP","MEASURE1:"+tv_star_des.getMeasuredHeight() );
		// ����1 �����ؼ�mode ����2 ��С
		/*int widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, width); // mode+size
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.AT_MOST, 1000);// �ҵĸ߶�																						// �����1000
		// �������� �����һ����ȷ��ֵwidth, �߶������1000,��ʵ��Ϊ׼
		tv_star_des.measure(widthMeasureSpec, heightMeasureSpec); // ͨ���÷������²����ؼ�
		Log.d("APP", "�ı��߶ȣ�" + tv_star_des.getMeasuredHeight());*/
		//SystemClock.sleep(200);
		Log.d("APP","MEASURE2:"+tv_star_des.getMeasuredHeight() );
		return tv_star_des.getMeasuredHeight();
		
	}

	// չ��������
	private void Down() {
		int startHeight = 0;
		int targetHeight = 0;
		if (isDown) {
			startHeight = endHeight;
			targetHeight = 0;
			btn_star_info.setImageResource(R.drawable.arrow_down);
		} else {
			startHeight = 0;
			targetHeight = endHeight;
			btn_star_info.setImageResource(R.drawable.arrow_up);
		}
		isDown = !isDown;
		// ֵ����
		ValueAnimator animator = ValueAnimator.ofInt(startHeight, targetHeight);
		final LinearLayout.LayoutParams params = (LayoutParams) tv_star_des.getLayoutParams();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				int value = (Integer) animator.getAnimatedValue();// ���е�ǰʱ����һ��ֵ
				params.height = value;
				tv_star_des.setLayoutParams(params);// ˢ�½���
				//System.out.println(value);
			}
		});
		animator.setDuration(500);
		animator.start();
		// isDown = false;
	}
}
