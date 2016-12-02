package com.tropicana.ipangpang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GridViewForScrollView extends GridView {

	
	public GridViewForScrollView(Context context) {
		super(context);
	}
	public GridViewForScrollView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	}
	public GridViewForScrollView(Context context, AttributeSet attrs,int defStyle) {
	        super(context, attrs, defStyle);
	}

	
	
	
	
	
	@Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
	
	
}
