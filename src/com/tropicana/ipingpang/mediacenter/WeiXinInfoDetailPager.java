package com.tropicana.ipingpang.mediacenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tropicana.ipangpang.view.RefreshExpendListView;
import com.tropicana.ipangpang.view.RefreshExpendListView.RefreshListener;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.VideoPlay;
import com.tropicana.ipingpang.base.BaseDetailPager;
import com.tropicana.ipingpang.bean.NewsMenuData;
import com.tropicana.ipingpang.bean.NewsMenuData.NewsChildInfo;
import com.tropicana.ipingpang.db.MyHistoryDbUtils;
import com.tropicana.ipingpang.global.GlobalContants;
import com.tropicana.ipingpang.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class WeiXinInfoDetailPager extends BaseDetailPager implements OnChildClickListener{
    private View view;
    private RefreshExpendListView expandableListView;
    private String mUrl;
    private NewsMenuData newsMenuData;
    private ArrayList<NewsChildInfo> newsChildInfos;
    private Map<Integer, ArrayList<NewsChildInfo>> children;
    
    public WeiXinInfoDetailPager(Activity activity) {
        super(activity);
        mUrl=GlobalContants.WEIXIN_URL;
    }
    
    @Override
    public void initData() {
        view=mRootView.inflate(mActivity, R.layout.weixin_info, null);
        expandableListView=(RefreshExpendListView) view.findViewById(R.id.elv_weixin_info);
        fl_stars_pager_content.removeAllViews();
        fl_stars_pager_content.addView(view);
        
        expandableListView.setGroupIndicator(null);  //取消向下箭头
       String cache=CacheUtils.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {  //判断缓存中是否有数据
            parseData(cache);
        }
        getDataFromServer();
       
        expandableListView.setOnfreshListener(new RefreshListener() {
			
			@Override
			public void onRefresh() {
				Log.d("SEND", "onRefresh");
				getDataFromServer();
			}
			@Override
			public void onLoadMore() {
				
			}
		});
    }
    
    
    /**
     * 获取数据
     */
    @Override
    protected void getDataFromServer(){
        HttpUtils utils=new HttpUtils();
        utils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity,"请检查您的网络，网络连接失败", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                expandableListView.onRefreshComplete(false);
            }
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {            
                    String result = responseInfo.result;    
                    //System.out.println("技术文章数据："+result);
                    CacheUtils.setCache(mActivity,mUrl, result);
                    parseData(result);
                    expandableListView.onRefreshComplete(true); //隐藏下拉刷新控件
            }
        });
    }
    private void parseData(String result) {
        Gson gson=new Gson();
        newsMenuData=gson.fromJson(result,NewsMenuData.class);
        //System.out.println("技术文章数据："+newsMenuData);

            if(newsMenuData!=null){
                VideoAdapter mAdapter=new VideoAdapter();
                expandableListView.setAdapter(mAdapter);
                expandableListView.setOnChildClickListener(this);
                for(int i = 0; i < mAdapter.getGroupCount(); i++){   
                    expandableListView.expandGroup(i);                            
                 }  
            }
        
        
    }
    
    
    private class VideoAdapter extends BaseExpandableListAdapter{
        private ArrayList<String> groupNames;
        private ArrayList<String> titles;
        
        public VideoAdapter(){
            children=new HashMap<Integer, ArrayList<NewsChildInfo>>();
            groupNames=new ArrayList<String>();
            titles=new ArrayList<String>();
        }
        
        @Override
        public Object getChild(int groupPositon, int childPosition) {
            
            return null;
        }
        @Override
        public long getChildId(int groupPositon, int childPosition) {
            
            return childPosition;
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            
            return newsMenuData.data.get(groupPosition).news.size();
        }
        @Override
        public Object getGroup(int groupPosition) {
            
            return null;
        }
        @Override
        public int getGroupCount() {
            
            return  newsMenuData.data.size();
        }
        @Override
        public long getGroupId(int groupPosition) {
            
            return 0;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpended, 
                View convertView, ViewGroup parent) {
            TextView tv_date;
            if (convertView==null) {
                convertView=View.inflate(mActivity, R.layout.weixin_item, null);
            }
            
            tv_date=(TextView) convertView.findViewById(R.id.tv_date);
                for (int i = 0; i < getGroupCount(); i++) {
                    String groupName=newsMenuData.data.get(i).date;
                    groupNames.add(groupName);
                }
                tv_date.setText(groupNames.get(groupPosition));
            return convertView;
        }
       
        @Override
        public View getChildView(int groupPosition, int childPositon, 
                boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView==null) {
                convertView=View.inflate(mActivity, R.layout.weixin_child_item, null);
                holder=new ViewHolder();
                //holder.videoTitle=(TextView) convertView.findViewById(R.id.tv_video_item_title);
                holder.tv_source=(TextView) convertView.findViewById(R.id.tv_source);
				holder.tv_name=(TextView) convertView.findViewById(R.id.tv_weixin_name);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder) convertView.getTag();
            }
            
            //ArrayList<NewsChildInfo> results=newsMenuData.data.get(groupPosition).news;
            newsChildInfos=newsMenuData.data.get(groupPosition).news;
            children.put(groupPosition,newsChildInfos);  //将对象信息传入哈希表，方便调用
            
            holder.tv_name.setText(newsChildInfos.get(childPositon).title);
           holder.tv_source.setText(newsChildInfos.get(childPositon).source);
            return convertView;
        }
        
        @Override
        public boolean hasStableIds() {
            
            return false;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            
            return true;
        }
        
    }
    
    
    public static class ViewHolder{
        public TextView videoTitle;
        public TextView tv_name;
		public TextView tv_source;
    }
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, 
            int groupPosition, int childPosition, long id) {
        
        NewsChildInfo childInfo=children.get(groupPosition).get(childPosition);
      //浏览记录添加如数据库
		MyHistoryDbUtils historyUtils=new MyHistoryDbUtils(mActivity);		
		historyUtils.InsertData(childInfo.title, childInfo.url,historyUtils.getCurrentTime());
        Intent intent=new Intent(mActivity,VideoPlay.class);
        intent.putExtra("video_url", childInfo.url);
        mActivity.startActivity(intent);
        return false;
    }
    
    
}
