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
	private VideoInfo videoInfo;  //视频信息
	private TopVideoInfo topVideoInfo; //头条信息
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
		tv_title.setText("爱乒乓");
		btn_close.setVisibility(View.INVISIBLE);
		
		//调用下拉刷新接口
				mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
						new GetDataTask().execute();
						Log.d("TOUCH", "ONREFRESH");
					}
				});
				mScrollView = mPullRefreshScrollView.getRefreshableView();
		//底部评论栏配置
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		isTopVideo=SharePreferenceUtils.getBoolean(getApplicationContext(), "isTopVideo", false);
		
		if (isTopVideo) {//头条数据
			Intent intent = this.getIntent(); 
			topVideoInfo=(TopVideoInfo) intent.getSerializableExtra("video_info");
			title=topVideoInfo.title;
			date=topVideoInfo.date;
			thumbnail=GlobalContants.SERVER_URL+topVideoInfo.thumbnail;
			video_url=topVideoInfo.url;
		}else {//视频数据
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
		
		initComment();  //初始化评论
		
		
		storeUtils=new MyStoreDbUtils(this); //初始化收藏夹数据库
		if (storeUtils.find(title)) {
			isStore=true;
			iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_2);
		}else {
			isStore=false;
			iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_1);
		}
		
		rl_video_detail_info.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {//点击打开视频
				Intent intent=new Intent(VideoDetailInfoActivity.this, VideoPlay.class);
				intent.putExtra("video_url", video_url);
				startActivity(intent);
			}
		});
		iv_video_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {//分享
				ShareSDKUtils sdkUtils=new ShareSDKUtils(getApplicationContext());
				sdkUtils.showShare();
			}
		});
		iv_video_storage.setOnClickListener(new OnClickListener() {
			
			@Override//收藏，取消收藏
			public void onClick(View arg0) {
				if (isStore) {
					Toast.makeText(getApplicationContext(), "已取消收藏", 0).show();
					iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_1);
					storeUtils.delete(title);
				}else {
					iv_video_storage.setBackgroundResource(R.drawable.detail_tool_favorite_2);
					Toast.makeText(getApplicationContext(), "已加入收藏", 0).show();
					//收藏记录添加如数据库		
					storeUtils.InsertData(title,video_url,
							date,thumbnail);
				}
				isStore=!isStore;			
			}
		});
		iv_video_agree.setOnClickListener(new OnClickListener() {
			
			@Override//点赞功能
			public void onClick(View v) {
				iv_video_agree.setImageResource(R.drawable.userinfo_agree_check);
				VideoBombBean vBean=new VideoBombBean();
				loveNums=videoBombBean.getLove()+1;
				//loveNums=loveNums+1; 
				Log.d("APP", "点赞成功"+loveNums);
				tv_agree_num.setText(loveNums+"");
				vBean.setLove(loveNums);
				vBean.update(getApplicationContext(),
						videoBombBean.getObjectId(), new UpdateListener() {
					@Override
					public void onSuccess() {
						Log.d("APP", "点赞成功");
					}				
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
				
			}
		});
			
		
	}

	
	//初试化评论
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
				//ShowToast(position+1+"楼");
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
		queryData();  //从bmob查询数据
	}


	
	private void queryData(){
		BmobQuery<VideoBombBean> query=new BmobQuery<VideoBombBean>();
		//query.get
		query.addWhereEqualTo("url",video_url);
		query.findObjects(this, new FindListener<VideoBombBean>() {

			@Override
			public void onSuccess(List<VideoBombBean> object) {
				//ShowToast("查询成功:"+object.size());
				videoBombBean=object.get(0);
				Log.d("TOUCH", "查询结果"+videoBombBean);
				tv_agree_num.setText(videoBombBean.getLove()+"");//初始化点赞个数
				fetchComment();  //必须等查询成功后再获取评论，否则就关联不到评论
			}
			@Override
			public void onError(int arg0, String arg1) {
				ShowToast("查询失败");
			}

		
		});
		
	}
	/**
	 * 获取所有评论
	 */
	private void fetchComment() {
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereRelatedTo("relation", new BmobPointer(videoBombBean));
		query.include("user");
		query.order("-createdAt");  //按创建时间排序
		//query.setLimit(GlobalContants.NUMBERS_PER_PAGE);
		query.findObjects(this, new FindListener<Comment>() {

			@Override
			public void onSuccess(List<Comment> data) {
				Log.d("TOUCH", "get comment success!" + data.size());
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					mAdapter.getDataList().addAll(data);
					mAdapter.notifyDataSetChanged();
					//ShowToast("已加载完所有评论~");
					loadmore.setText("已加载完毕");			
				} else {
					//ShowToast("暂无更多评论~");		
					loadmore.setText("暂无更多评论");	
				}
				progress.setVisibility(View.GONE); //隐藏进度条
			}

			@Override
			public void onError(int arg0, String arg1) {
				ShowToast("获取评论失败。请检查网络~");
				//pageNum--;
			}
		});
	}

	/**
	 * 点击发表后，提交评论并更新view
	 */
	private void onClickCommit() {
		User currentUser = BmobUser.getCurrentUser(this, User.class);
		if (currentUser != null) {// 已登录
			commentEdit = et_comment.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit)) {
				ShowToast("评论内容不能为空!");
				return;
			}
			//发表评论
			publishComment(currentUser, commentEdit);
		} else {// 未登录
			ShowToast("发表评论前请先登录。");
			Intent intentLogin = new Intent();
			intentLogin.setClass(this, LoginActivity.class);
			startActivityForResult(intentLogin, GlobalContants.PUBLISH_COMMENT);
		}

	}
	
	/**
	 * 发表评论
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
					//动态设置listview的高度 item 总布局必须是linearLayout
					//setListViewHeightBasedOnChildren(commentList);
				//}
				et_comment.setText("");
				hideSoftInput();  //隐藏键盘

				// 将该评论与视频绑定到一起
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				videoBombBean.setRelation(relation);
				Log.d("TOUCH", "relation成功");
				videoBombBean.update(VideoDetailInfoActivity.this, new UpdateListener() {

					@Override
					public void onSuccess() {
						Log.d("TOUCH", "更新评论成功!");
						ShowToast("评论成功!");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Log.d("TOUCH","更新评论失败!" + arg1);
					}
				});
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("评论失败。请检查网络~");
			}
		});
	}

	
	/***
	 * 动态设置listview的高度 item 总布局必须是linearLayout
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
	 * 隐藏软键盘
	 */	
	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
	}

	/**
	 * 运行下拉刷新
	 * @author Administrator
	 *
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			
				Log.d("TOUCH", "正在获取评论");				
				//comments=null;
				//fetchComment();  //重新获取所有评论					
				//mAdapter.notifyDataSetChanged();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d("TOUCH", "获取完成");
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void back() {
		finish();
	}
}
