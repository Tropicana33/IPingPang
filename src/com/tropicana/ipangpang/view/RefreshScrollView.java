package com.tropicana.ipangpang.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.tropicana.ipingpang.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * ����ˢ��
 * 
 * @author Administrator
 *
 */
public class RefreshScrollView extends ScrollView implements 
OnScrollListener{

	private static final int STATE_PULL_REFREASH = 0; // ����ˢ��
	private static final int STATE_RELEASE_REFREASH = 1; // �ɿ�ˢ��
	private static final int STATE_REFREASHiNG = 2; // ����ˢ��
	private int mCurrentState = STATE_PULL_REFREASH;

	private View mheaderView;
	private View mFooterView;

	private TextView tv_refresh_state;
	private TextView tv_refresh_time;
	private ImageView iv_refreash_array;
	private ProgressBar pb_refresh_progress;

	private RotateAnimation animationUp;
	private RotateAnimation animationDown;

	private int startY = -1;
	private int endY;
	private int dy;
	private int mHeaderViewHeight;
	private int mFooterViewHeight;
	
	private boolean isLoadMore=false;

	public RefreshScrollView(Context context) {
		super(context);
		initHeaderView();
		
	}

	public RefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		
	}

	public RefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		
	}

	/**
	 * ��ʼ��ͷ����
	 */
	private void initHeaderView() {

		mheaderView = View.inflate(getContext(), R.layout.refresh_header, null);
		//this.addHeaderView(mheaderView);
		tv_refresh_state = (TextView) findViewById(R.id.tv_refresh_state);
		tv_refresh_time = (TextView) findViewById(R.id.tv_refresh_time);
		iv_refreash_array = (ImageView) findViewById(R.id.iv_refreash_array);
		pb_refresh_progress = (ProgressBar) findViewById(R.id.pb_refresh_progress);

		initArrowAnim();
		mheaderView.measure(0, 0);
		mHeaderViewHeight = mheaderView.getMeasuredHeight();
		mheaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (startY == -1) { // ȷ��starty��Ч
				startY = (int) ev.getRawY();
			}

			if (mCurrentState == STATE_REFREASHiNG) {
				break;
			}

			endY = (int) ev.getRawY();
			dy = endY - startY; // �ƶ�ƫ����
			/*if (dy > 0 && getFirstVisiblePosition() == 0) { // ��ǰ��ʾ��һ��item
				int padding = dy / 2 - mHeaderViewHeight;
				//Log.d("SEND", "ƫ������"+padding);
				mheaderView.setPadding(0, padding, 0, 0);
				if (padding > 0 && mCurrentState != STATE_REFREASHiNG) {
					mCurrentState = STATE_RELEASE_REFREASH;
					refreshHeaderViewState();
				} else if (padding < 0 && padding > -mHeaderViewHeight) {
					mCurrentState = STATE_PULL_REFREASH;
					refreshHeaderViewState();
				}
				
			}*/

			break;
		case MotionEvent.ACTION_UP:
			startY = -1;
			if (mCurrentState == STATE_RELEASE_REFREASH) {
				mCurrentState = STATE_REFREASHiNG;
				mheaderView.setPadding(0, 0, 0, 0);
				refreshHeaderViewState();
			} else if (mCurrentState == STATE_PULL_REFREASH) {
				mheaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
			}
			break;
		default:
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void refreshHeaderViewState() {
		switch (mCurrentState) {
		case STATE_PULL_REFREASH:
			tv_refresh_state.setText("����ˢ��");
			iv_refreash_array.setVisibility(View.VISIBLE);
			pb_refresh_progress.setVisibility(View.INVISIBLE);
			iv_refreash_array.startAnimation(animationUp);
			break;
		case STATE_RELEASE_REFREASH:
			tv_refresh_state.setText("�ɿ�ˢ��");
			iv_refreash_array.setVisibility(View.VISIBLE);
			pb_refresh_progress.setVisibility(View.INVISIBLE);
			iv_refreash_array.startAnimation(animationDown);
			break;
		case STATE_REFREASHiNG:
			tv_refresh_state.setText("����ˢ��...");
			iv_refreash_array.clearAnimation(); // ���������������
			iv_refreash_array.setVisibility(View.INVISIBLE);
			pb_refresh_progress.setVisibility(View.VISIBLE);

			if (mListener != null) {
				mListener.onRefresh();
			}

			break;
		default:
			break;
		}

	}

	/**
	 * ��ʼ����ͷ����
	 */
	private void initArrowAnim() {
		// ��ͷ���ϵĶ���
		animationUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animationUp.setDuration(200);
		animationUp.setFillAfter(true);
		// ��ͷ���µĶ���
		animationDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animationDown.setDuration(200);
		animationDown.setFillAfter(true);

	}

	private RefreshListener mListener; // ����ˢ�¼���

	public void setOnfreshListener(RefreshListener listener) {
		mListener = listener;
	}

	/**
	 * ����ˢ�»ص��ӿ�
	 * 
	 * @author Administrator
	 *
	 */
	public interface RefreshListener {

		public void onRefresh(); // ����ˢ�»ص�����

		public void onLoadMore(); // ���ظ���ص�����
	}

	
	/**
	 * ��ʾ����ˢ��
	 */
	public void onRefreshStart(){
		mheaderView.setPadding(0,0, 0, 0);
		tv_refresh_state.setText("����ˢ��");
		iv_refreash_array.setVisibility(View.INVISIBLE);
		pb_refresh_progress.setVisibility(View.VISIBLE);
		mCurrentState = STATE_PULL_REFREASH;
	}
	
	
	
	/**
	 * ˢ�½�������������ˢ�¿ؼ�����ʼ����������
	 */

	public void onRefreshComplete(boolean needUpdateTime) {
		if (isLoadMore) {
			isLoadMore = false;
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		} else {
			mheaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
			tv_refresh_state.setText("����ˢ��");
			iv_refreash_array.setVisibility(View.VISIBLE);
			pb_refresh_progress.setVisibility(View.INVISIBLE);
			if (needUpdateTime) {
				tv_refresh_time.setText("���ˢ��ʱ�䣺" + getCurrentTime());
			}
			mCurrentState = STATE_PULL_REFREASH;
		}

	}

	/**
	 * �õ���ǰʱ��
	 */
	public String getCurrentTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	@Override
	public void onScroll(AbsListView view, int firstVisbleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override // ����״̬�����仯
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		/*if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			if (getLastVisiblePosition() == getCount() - 1 && !isLoadMore) {
				Log.d("SEND", "���ײ���");
				isLoadMore = true;
				mFooterView.setPadding(0, 0, 0, 0);
				setSelection(getCount());

				if (mListener != null) {
					mListener.onLoadMore();
				}
			}
		}
*/
	}

	
}
