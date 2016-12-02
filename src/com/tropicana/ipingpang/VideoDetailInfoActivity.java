package com.tropicana.ipingpang;

import java.util.ArrayList;
import java.util.List;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipangpang.view.ListViewForScrollView;
import com.tropicana.ipingpang.adapter.CommentAdapter;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bean.VideoMenuData.TopVideoInfo;
import com.tropicana.ipingpang.bean.VideoMenuData.VideoInfo;
import com.tropicana.ipingpang.bmob.bean.Comment;
import com.tropicana.ipingpang.bmob.bean.User;
import com.tropicana.ipingpang.bmob.bean.VideoBombBean;
import com.tropicana.ipingpang.db.MyStoreDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.SharePreferenceUtils;
import com.tropicana.ipingpang.utils.ShareSDKUtils;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoDetailInfoActivity extends BaseActivity{
	private View view;
	private TextView tv_video_info_title,tv_agree_num;
	private TextView tv_video_info_time;
	private ImageView iv_video_info_icon;
	private TextView tv_video_item_title;
	private RelativeLayout rl_video_detail_info;
	private ImageButton iv_video_storage;
	private ImageButton iv_video_share;
	private ImageButton iv_video_agree;
	private EditText et_comment;
	private Button btn_commit;
	private VideoInfo videoInfo;  //��Ƶ��Ϣ
	private TopVideoInfo topVideoInfo; //ͷ����Ϣ
	private ListViewForScrollView commentList;
	private CommentAdapter mAdapter;
	private List<Comment> comments = new ArrayList<Comment>();
	
	private MyStoreDbUtils storeUtils;
	private boolean isStore=false;
	
	private VideoBombBean videoBombBean;
	private PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	private TextView loadmore;
	
	private String commentEdit = "";
	private boolean isTopVideo=false;
	
	private String title,date,thumbnail;
	private String video_url;
	private ProgressBar progress;
	private int loveNums=0;
	@Override
	public void initData() {
		view=View.inflate(this, R.layout.activity_video_info, null);
		tv_video_info_title=(TextView) view.findViewById(R.id.tv_video_info_title);
		tv_video_info_time=(TextView) view.findViewById(R.id.tv_video_info_time);
		tv_agree_num=(TextView) view.findViewById(R.id.tv_agree_num);
		rl_video_detail_info=(RelativeLayout) view.findViewById(R.id.rl_video_detail_info);
		iv_video_info_icon=(ImageView) view.findViewById(R.id.iv_video_info_icon);
		tv_video_item_title=(TextView) view.findViewById(R.id.tv_video_item_title);
		iv_video_storage=(ImageButton) view.findViewById(R.id.iv_video_storage);
		iv_video_share=(ImageButton) view.findViewById(R.id.iv_video_share);
		iv_video_agree=(ImageButton) view.findViewById(R.id.iv_video_feedback);
		progress=(ProgressBar) view.findViewById(R.id.pb_loading);
		loadmore=(TextView) view.findViewById(R.id.loadmore);
		et_comment=(EditText) view.findViewById(R.id.et_comment);
		btn_commit=(Button) view.findViewById(R.id.btn_commit);
		commentList = (ListViewForScrollView) view.findViewById(R.id.comment_list);
		mPullRefreshScrollView=(PullToRefreshScrollView) view.findViewById(R.id.commit_scroll);
		mPullRefreshScrollView.setMode(Mode.PULL_FROM_START);
		
		fl_base.addView(view);
		tv_title.setText("��ƹ��");
		btn_close.setVisibility(View.INVISIBLE);
		
		//��������ˢ�½ӿ�
				mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
						new GetDataTask().execute();
						Log.d("TOUCH", "ONREFRESH");
					}
				});
				mScrollView = mPullRefreshScrollView.getRefreshableView();
		//�ײ�����������
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		isTopVideo=SharePreferenceUtils.getBoolean(getApplicationContext(), "isTopVideo", false);
		
		if (isTopVideo) {//ͷ������
			Intent intent = this.getIntent(); 
			topVideoInfo=(TopVideoInfo) intent.getSerializableExtra("video_info");
			title=topVideoInfo.title;
			date=topVideoInfo.date;
			thumbnail=GlobalContants.SERVER_URL+topVideoInfo.thumbnail;
			video_url=topVideoInfo.url;
		}else {//��Ƶ����
			Intent intent = this.getIntent(); 
			videoInfo=(VideoInfo) intent.getSerializableExtra("video_info");
			title=videoInfo.title;
			date=videoInfo.date;
			thumbnail=videoInfo.thumbnail;
			video_url=videoInfo.url;
		}
		
		tv_video_info_title.setText(title);
		tv_video_info_time.setText(date);
		tv_video_item_title.setText(title);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.icon_default);
		bitmapUtils.display(iv_video_info_icon,thumbnail);
		
		initComment();  //��ʼ������
		
		
		storeUtils=new MyStoreDbUtils(this); //��ʼ���ղؼ����ݿ�
		if (storeUtils.find(title)) {
			isStore=true;
			iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_2);
		}else {
			isStore=false;
			iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_1);
		}
		
		rl_video_detail_info.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {//�������Ƶ
				Intent intent=new Intent(VideoDetailInfoActivity.this, VideoPlay.class);
				intent.putExtra("video_url", video_url);
				startActivity(intent);
			}
		});
		iv_video_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {//����
				ShareSDKUtils sdkUtils=new ShareSDKUtils(getApplicationContext());
				sdkUtils.showShare();
			}
		});
		iv_video_storage.setOnClickListener(new OnClickListener() {
			
			@Override//�ղأ�ȡ���ղ�
			public void onClick(View arg0) {
				if (isStore) {
					Toast.makeText(getApplicationContext(), "��ȡ���ղ�", 0).show();
					iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_1);
					storeUtils.delete(title);
				}else {
					iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_2);
					Toast.makeText(getApplicationContext(), "�Ѽ����ղ�", 0).show();
					//�ղؼ�¼��������ݿ�		
					storeUtils.InsertData(title,video_url,
							date,thumbnail);
				}
				isStore=!isStore;			
			}
		});
		iv_video_agree.setOnClickListener(new OnClickListener() {
			
			@Override//���޹���
			public void onClick(View v) {
				iv_video_agree.setImageResource(R.drawable.userinfo_agree_check);
				VideoBombBean vBean=new VideoBombBean();
				loveNums=videoBombBean.getLove()+1;
				//loveNums=loveNums+1; 
				Log.d("APP", "���޳ɹ�"+loveNums);
				tv_agree_num.setText(loveNums+"");
				vBean.setLove(loveNums);
				vBean.update(getApplicationContext(),
						videoBombBean.getObjectId(), new UpdateListener() {
					@Override
					public void onSuccess() {
						Log.d("APP", "���޳ɹ�");
					}				
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
				
			}
		});
			
		
	}

	
	//���Ի�����
	private void initComment() {
		
		mAdapter=new CommentAdapter(VideoDetailInfoActivity.this, comments);
		if (commentList!=null) {
			commentList.setAdapter(mAdapter);
		}
		
		//setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//ShowToast(position+1+"¥");
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);
		
		btn_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onClickCommit();
			}
		});
		queryData();  //��bmob��ѯ����
	}


	
	private void queryData(){
		BmobQuery<VideoBombBean> query=new BmobQuery<VideoBombBean>();
		//query.get
		query.addWhereEqualTo("url",video_url);
		query.findObjects(this, new FindListener<VideoBombBean>() {

			@Override
			public void onSuccess(List<VideoBombBean> object) {
				//ShowToast("��ѯ�ɹ�:"+object.size());
				videoBombBean=object.get(0);
				Log.d("TOUCH", "��ѯ���"+videoBombBean);
				tv_agree_num.setText(videoBombBean.getLove()+"");//��ʼ�����޸���
				fetchComment();  //����Ȳ�ѯ�ɹ����ٻ�ȡ���ۣ�����͹�����������
			}
			@Override
			public void onError(int arg0, String arg1) {
				ShowToast("��ѯʧ��");
			}

		
		});
		
	}
	/**
	 * ��ȡ��������
	 */
	private void fetchComment() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(videoBombBean));
		query.include("user");
		query.order("-createdAt");  //������ʱ������
		//query.setLimit(GlobalContants.NUMBERS_PER_PAGE);
		query.findObjects(this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> data) {
				Log.d("TOUCH", "get comment success!" + data.size());
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					mAdapter.getDataList().addAll(data);
					mAdapter.notifyDataSetChanged();
					//ShowToast("�Ѽ�������������~");
					loadmore.setText("�Ѽ������");			
				} else {
					//ShowToast("���޸�������~");		
					loadmore.setText("���޸�������");	
				}
				progress.setVisibility(View.GONE); //���ؽ�����
			}

			@Override
			public void onError(int arg0, String arg1) {
				ShowToast("��ȡ����ʧ�ܡ���������~");
				//pageNum--;
			}
		});
	}

	/**
	 * ���������ύ���۲�����view
	 */
	private void onClickCommit() {
		User currentUser = BmobUser.getCurrentUser(this, User.class);
		if (currentUser != null) {// �ѵ�¼
			commentEdit = et_comment.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit)) {
				ShowToast("�������ݲ���Ϊ��!");
				return;
			}
			//��������
			publishComment(currentUser, commentEdit);
		} else {// δ��¼
			ShowToast("��������ǰ���ȵ�¼��");
			Intent intentLogin = new Intent();
			intentLogin.setClass(this, LoginActivity.class);
			startActivityForResult(intentLogin, GlobalContants.PUBLISH_COMMENT);
		}

	}
	
	/**
	 * ��������
	 * @param user
	 * @param content
	 */
	private void publishComment(User user, String content) {

		final Comment comment = new Comment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(this, new SaveListener() {

			@Override
			public void onSuccess() {
				
				//if (mAdapter.getDataList().size() < GlobalContants.NUMBERS_PER_PAGE) {
					mAdapter.getDataList().add(comment);
					mAdapter.notifyDataSetChanged();
					//��̬����listview�ĸ߶� item �ܲ��ֱ�����linearLayout
					//setListViewHeightBasedOnChildren(commentList);
				//}
				et_comment.setText("");
				hideSoftInput();  //���ؼ���

				// ������������Ƶ�󶨵�һ��
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				videoBombBean.setRelation(relation);
				Log.d("TOUCH", "relation�ɹ�");
				videoBombBean.update(VideoDetailInfoActivity.this, new UpdateListener() {

					@Override
					public void onSuccess() {
						Log.d("TOUCH", "�������۳ɹ�!");
						ShowToast("���۳ɹ�!");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Log.d("TOUCH","��������ʧ��!" + arg1);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("����ʧ�ܡ���������~");
			}
		});
	}

	
	/***
	 * ��̬����listview�ĸ߶� item �ܲ��ֱ�����linearLayout
	 * 
	 * @param listView
	 */
	/*public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ 15;
		listView.setLayoutParams(params);
	}
*/
	/**
	 * ���������
	 */	
	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
	}

	/**
	 * ��������ˢ��
	 * @author Administrator
	 *
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			
				Log.d("TOUCH", "���ڻ�ȡ����");				
				//comments=null;
				//fetchComment();  //���»�ȡ��������					
				//mAdapter.notifyDataSetChanged();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d("TOUCH", "��ȡ���");
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void back() {
		finish();
	}
}
