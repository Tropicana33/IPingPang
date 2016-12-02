package com.tropicana.ipingpang;

import java.util.ArrayList;
import com.tropicana.ipingpang.base.BaseActivity;
import com.tropicana.ipingpang.bean.NewsWebBean.NewsWebInfo;
import com.tropicana.ipingpang.db.HistoryInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.ShareSDKUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryActivity extends BaseActivity implements OnItemClickListener{

	private View view;
	private ListView lv_history;
	private View myHeader;
	private TextView btn_clear;
	private ArrayList<HistoryInfo> historyInfos;
	private MyHistoryDbUtils mHistoryUtils;
	private HistoryAdapter mAdapter;
	
	
	@Override
	public void initData() {
		view=View.inflate(getApplicationContext(), R.layout.activity_history, null);
		lv_history=(ListView) view.findViewById(R.id.lv_history);
		myHeader=View.inflate(getApplicationContext(), R.layout.history_header, null);
		btn_clear=(TextView) myHeader.findViewById(R.id.tv_history_clear);
		lv_history.addFooterView(myHeader);  //给listview添加头布局
		fl_base.addView(view);
		tv_title.setText("我的浏览历史");
		btn_close.setVisibility(View.GONE); 
		
		//初始化数据库信息
		mHistoryUtils=new MyHistoryDbUtils(this);
		historyInfos=new ArrayList<HistoryInfo>();
		historyInfos=mHistoryUtils.findAll();   //查找全部历史记录
		
		mAdapter=new HistoryAdapter();
		if (historyInfos!=null) {
			Log.d("TOUCH", "有浏览记录");
			lv_history.setAdapter(mAdapter);
		}else {
			Log.d("TOUCH", "没有数据");
		}
		
		btn_clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("TOUCH", "情空历史记录");
				showDeleteDialog();
				
			}
		});
		
		lv_history.setOnItemClickListener(this);
		
		registerForContextMenu(lv_history);
		
	}

	/**
	 * 长按 显示菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.history_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * 菜单点击事件
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id;  //获取点击位置
		switch (item.getItemId()) {
		case R.id.item_history_delete:
			Log.d("TOUCH", "删除历史记录");
				mHistoryUtils.delete(historyInfos.get(position).title);   //在数据库中删除收藏
				historyInfos=mHistoryUtils.findAll();   //查找全部历史记录
				mAdapter.notifyDataSetChanged();  //更新列表数据
			return true;			
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	
	
	/**
	 * 显示是否删除对话框
	 */
	protected void showDeleteDialog() {
		 AlertDialog.Builder builder = new Builder(this);
		 builder.setMessage("确定删除历史记录吗？");
		 builder.setTitle("提示");
		 builder.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mHistoryUtils.deleteAll();
				fl_base.removeAllViews();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}

	public class HistoryAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return historyInfos.size();
		}

		@Override
		public HistoryInfo getItem(int position) {
			return historyInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view=View.inflate(getApplicationContext(), R.layout.history_item, null);
			TextView tv_title=(TextView) view.findViewById(R.id.tv_history_title);
			TextView tv_time=(TextView) view.findViewById(R.id.tv_history_time);
			tv_title.setText(historyInfos.get(position).title);
			tv_time.setText(historyInfos.get(position).time);
			return view;
		}
	}
	
	
	@Override
	public void back() {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		
		if (historyInfos!=null) {
			String mUrl=historyInfos.get(position).url;
			Log.d("TOUCH", "当前点击位置："+mUrl);
			Intent intent=new Intent(HistoryActivity.this, VideoPlay.class);
			intent.putExtra("video_url",mUrl);
			startActivity(intent);
		}
	}
	
}
