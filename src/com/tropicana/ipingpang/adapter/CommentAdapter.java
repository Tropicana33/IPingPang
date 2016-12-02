package com.tropicana.ipingpang.adapter;

import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.tropicana.ipingpang.R;
import com.tropicana.ipingpang.bmob.bean.Comment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseContentAdapter<Comment>{

	BitmapUtils bitmapUtils;
	
	public CommentAdapter(Context context, List<Comment> list) {
		super(context, list);
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.default_head);
	}

	
	
	
	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_item, null);
			viewHolder.userName = (TextView)convertView.findViewById(R.id.userName_comment);
			viewHolder.commentContent = (TextView)convertView.findViewById(R.id.content_comment);
			viewHolder.index = (TextView)convertView.findViewById(R.id.index_comment);
			viewHolder.userIcon=(ImageView) convertView.findViewById(R.id.iv_comment_icon);
			viewHolder.comment_date=(TextView) convertView.findViewById(R.id.tv_comment_date);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		final Comment comment = dataList.get(position);
		if(comment.getUser()!=null){
			viewHolder.userName.setText(comment.getUser().getNick());
			Log.d("TOUCH","NAME:"+comment.getUser().getUsername());
		}else{
			viewHolder.userName.setText("«Ú”—");
		}
		viewHolder.index.setText((position+1)+"¬•");
		viewHolder.commentContent.setText(comment.getCommentContent());
		//…Ë÷√Õ∑œÒ
		bitmapUtils.display(viewHolder.userIcon,comment.getUser().getAvatar());
		viewHolder.comment_date.setText(comment.getCreatedAt());
		return convertView;
	}

	public static class ViewHolder{
		public TextView userName;
		public TextView commentContent;
		public TextView index;
		public ImageView userIcon;
		public TextView comment_date;
	}
}
