package com.yxkj.jyb.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class uiUtils {
    
	public static void setEmptyView(Context _c, String info, ListView lv){
		TextView emptyView = new TextView(_c);  
	    emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));  
	    emptyView.setText(info);  
	    emptyView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
	    emptyView.setVisibility(View.GONE);  
	    ((ViewGroup)lv.getParent()).addView(emptyView);  
	    lv.setEmptyView(emptyView);
	}
}
