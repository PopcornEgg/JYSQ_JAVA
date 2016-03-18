package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class ThreadsFilter {
	static public class MarkItem
	{
		static public class MarkFids
	    {
	        public String name;
	        public String fids = "";
	        public MarkFids(String _n,String _l)
	        {
	            name = _n;
	            fids = _l;
	        }
	        public MarkFids() { }
	    }
	    public String name;
	    public List<MarkFids> items = new ArrayList<MarkFids>();
	    public MarkItem(String _n, List<MarkFids> _l)
	    {
	        name = _n;
	        items = _l;
	    }
	}
	//正向查找表
    @SuppressWarnings("serial")
	public static final Map<String, MarkItem> dicForwardMarks = new HashMap<String, MarkItem>(){{
        
        put("小学",new MarkItem("小学",new ArrayList<MarkItem.MarkFids>(){{
             add(new MarkItem.MarkFids("数学","42"));
             add(new MarkItem.MarkFids("语文","2"));
             add(new MarkItem.MarkFids("英语","43"));
        }}));
        
        put("初一",new MarkItem("初一",new ArrayList<MarkItem.MarkFids>(){{
             add(new MarkItem.MarkFids("数学","45"));
             add(new MarkItem.MarkFids("语文","44"));
             add(new MarkItem.MarkFids("英语","46"));
             add(new MarkItem.MarkFids("生物","47"));
             add(new MarkItem.MarkFids("政治","48"));
             add(new MarkItem.MarkFids("历史","49"));
             add(new MarkItem.MarkFids("地理","50"));
        }}));
        
        put("初二",new MarkItem("初二",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("数学","52"));
            add(new MarkItem.MarkFids("语文","51"));
            add(new MarkItem.MarkFids("英语","53"));
            add(new MarkItem.MarkFids("物理","54"));
            add(new MarkItem.MarkFids("生物","55"));
            add(new MarkItem.MarkFids("政治","56"));
            add(new MarkItem.MarkFids("历史","57"));
            add(new MarkItem.MarkFids("地理","58"));
        }}));
        
        put("初三",new MarkItem("初三",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("数学","60"));
            add(new MarkItem.MarkFids("语文","59"));
            add(new MarkItem.MarkFids("英语","61"));
            add(new MarkItem.MarkFids("物理","62"));
            add(new MarkItem.MarkFids("化学","63"));
            add(new MarkItem.MarkFids("生物","64"));
            add(new MarkItem.MarkFids("政治","65"));
            add(new MarkItem.MarkFids("历史","66"));
            add(new MarkItem.MarkFids("地理","67"));
        }}));
        
        put("高一",new MarkItem("高一",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("数学","69"));
            add(new MarkItem.MarkFids("语文","68"));
            add(new MarkItem.MarkFids("英语","70"));
            add(new MarkItem.MarkFids("物理","71"));
            add(new MarkItem.MarkFids("化学","72"));
            add(new MarkItem.MarkFids("生物","73"));
            add(new MarkItem.MarkFids("政治","74"));
            add(new MarkItem.MarkFids("历史","75"));
            add(new MarkItem.MarkFids("地理","76"));
        }}));
        
        put("高二",new MarkItem("高二",new ArrayList<MarkItem.MarkFids>(){{
              add(new MarkItem.MarkFids("数学","78"));
              add(new MarkItem.MarkFids("语文","77"));
              add(new MarkItem.MarkFids("英语","79"));
              add(new MarkItem.MarkFids("物理","80"));
              add(new MarkItem.MarkFids("化学","81"));
              add(new MarkItem.MarkFids("生物","82"));
              add(new MarkItem.MarkFids("政治","83"));
              add(new MarkItem.MarkFids("历史","84"));
              add(new MarkItem.MarkFids("地理","85"));
        }}));
        
        put("高三",new MarkItem("高三",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("数学","87"));
            add(new MarkItem.MarkFids("语文","86"));
            add(new MarkItem.MarkFids("英语","88"));
            add(new MarkItem.MarkFids("物理","89"));
            add(new MarkItem.MarkFids("化学","90"));
            add(new MarkItem.MarkFids("生物","91"));
            add(new MarkItem.MarkFids("政治","92"));
            add(new MarkItem.MarkFids("历史","93"));
            add(new MarkItem.MarkFids("地理","94"));
        }}));
        
        //生成全部id
        List<MarkItem.MarkFids> alllist = new ArrayList<MarkItem.MarkFids>();
        for (Map.Entry<String, MarkItem> set : this.entrySet()) {  
        	String fids = "";
        	MarkItem checkItem = set.getValue();
            for(int i=0;i<checkItem.items.size();i++){
            	fids += checkItem.items.get(i).fids + ",";
            }
            fids = fids.substring(0,fids.length() - 2);
            checkItem.items.add(0, new MarkItem.MarkFids("全部", fids)); 
            alllist.add(new MarkItem.MarkFids(set.getKey(), fids));
        } 
       //生成所有全部
        String fids = "";
        MarkItem allitem = new MarkItem("全部",alllist);
        for(int i=0;i<allitem.items.size();i++){
        	fids += allitem.items.get(i).fids + ",";
        }
        allitem.items.add(0, new MarkItem.MarkFids("全部", fids.substring(0,fids.length() - 2))); 
        put("全部", allitem);
    }};
    @SuppressWarnings("serial")
	public static final List<String> listIdx2Name = new ArrayList<String> () {{ 
        add("全部");add("小学");add("初一");add("初二");add("初三");add("高一");add("高二");add("高三");
    }};
  //正向查找表
    @SuppressWarnings("serial")
	public static final Map<String, String> dicBackMarks = new HashMap<String, String>(){{
		for(Map.Entry<String, MarkItem> et : dicForwardMarks.entrySet()){
			MarkItem item = et.getValue();
			String key = et.getKey();
			if(!key.equals("全部")){
				for(int i=0;i<item.items.size();i++){
					MarkItem.MarkFids mark = item.items.get(i);
					if(!mark.name.equals("全部"))
						this.put( mark.fids, key + mark.name );
				}
			}
		}
	}};
	
	public static String getSubNameByFid(String fid){
		if(dicBackMarks.containsKey(fid))
			return dicBackMarks.get(fid);
		return "";
	};
	
	public static String getAllFids( ){
		return dicForwardMarks.get("全部").items.get(0).fids;
	};

	static ArrayList<View> sbtnTops = new ArrayList<View> ();
	static ArrayList<View> sbtnBottoms = new ArrayList<View> ();
	static String curkindTop = "全部";
	static int curkindBottom = 0;
	static PopupWindow sPopupWindow = null;
	static public void showPopupWindow(Context _c, View view) {

        // 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(_c).inflate(
                R.layout.threadsfilter, null);
		initView(contentView);
		
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        sPopupWindow = popupWindow;
        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
       //实例化一个ColorDrawable颜色为半透明
    	ColorDrawable dw = new ColorDrawable(0xFF000000);
    	//设置SelectPicPopupWindow弹出窗体的背景
    	popupWindow.setBackgroundDrawable(dw);
    	
    	popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
    	//popupWindow.showAsDropDown(view);
    	//得到mView在屏幕中的坐标  
    	int [] pos = new int[2];  
    	view.getLocationOnScreen(pos);  
    	int offsetY = pos[1] + view.getHeight();  
    	int offsetX = 0;  
    	popupWindow.showAtLocation(view,Gravity.TOP|Gravity.CENTER_HORIZONTAL, offsetX, offsetY);
    	
    	MainTabActivity.setBackgroundAlpha(0.5f);
    	 //popWindow消失监听方法
    	popupWindow.setOnDismissListener(new OnDismissListener() {
          @Override
          public void onDismiss() {
        	  sPopupWindow = null;
        	  MainTabActivity.setBackgroundAlpha(1.0f);
          }
        });
                  
    	/*
    	// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
        WindowManager wm = (WindowManager) _c.getSystemService(Context.WINDOW_SERVICE);
        Point pt = new Point();
		wm.getDefaultDisplay().getSize(pt);
		int xPos = pt.x / 2;
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view, xPos, 0);
		*/
    }
	static public void initView(View layout){
		sbtnTops.clear();
		sbtnBottoms.clear();
		layout.findViewsWithText(sbtnTops, "btnsTop", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		layout.findViewsWithText(sbtnBottoms, "btnsBottom", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		for(int i=0;i<sbtnTops.size();i++)
		{
			Button _btn = (Button)sbtnTops.get(i);
			_btn.setTag(i);
			_btn.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int tag = Integer.parseInt(v.getTag().toString());
					curkindTop = listIdx2Name.get(tag);
					updateBottom(tag, true);
					setTopState(tag, false);
				}
			});
		}
		for(int i=0;i<listIdx2Name.size();i++){
			if(curkindTop.equals(listIdx2Name.get(i)))
			{
				setTopState(i, false);
				updateBottom(i, false);
				break;
			}
		}
		for(int i=0;i<sbtnBottoms.size();i++)
		{
			Button _btn = (Button)sbtnBottoms.get(i);
			_btn.setTag(i);
			_btn.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					
					MarkItem mItem = dicForwardMarks.get(curkindTop);
					if(mItem != null)
					{	
						curkindBottom = Integer.parseInt(v.getTag().toString());
						setBottomState(curkindBottom, false);
					}
				}
			});
		}
		
		Button ok=(Button)layout.findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				MarkItem mItem = dicForwardMarks.get(curkindTop);
				if(mItem != null)
				{	
					MarkItem.MarkFids fids = mItem.items.get(curkindBottom);
					if(fids != null)
					{
						String _name = mItem.name + fids.name;
						if(_name.equals("全部全部"))
							_name = "全部";
						FragmentPage2.sFragmentPage2.setFilter(fids.fids, _name + " ▼");
						GlobalUtility.Func.ShowToast(_name);
						hideBox();
					}
				}
			}
		});
	}
	static public void updateBottom(int tag, boolean isreset){
		MarkItem mItem = dicForwardMarks.get(curkindTop);
		if(mItem != null)
		{
			for(int k=0;k<sbtnBottoms.size();k++)
			{
				Button _btn = (Button)sbtnBottoms.get(k);
				if(k < mItem.items.size())
				{
					MarkItem.MarkFids fids = mItem.items.get(k);
					_btn.setVisibility(View.VISIBLE);
					_btn.setText(fids.name);
				}
				else
				{
					_btn.setVisibility(View.INVISIBLE);
				}
			}
			if(isreset){
				curkindBottom = 0;
			}
			setBottomState(curkindBottom, false);
		}
	}
	static public void setTopState(int tag, boolean v){
		for(int k=0;k<sbtnTops.size();k++)
		{
			Button _btn = (Button)sbtnTops.get(k);
			_btn.setEnabled(tag == k ? v : true);
		}
	}
	static public void setBottomState(int tag, boolean v){
		for(int k=0;k<sbtnBottoms.size();k++)
		{
			Button _btn = (Button)sbtnBottoms.get(k);
			_btn.setEnabled(tag == k ? v : true);
		}
	}
	static public void hideBox(){
		if(sPopupWindow != null){
			sPopupWindow.dismiss();
			sPopupWindow = null;
		}
	}
	static public  ArrayList<String> getDefaultFids()
	{
		ArrayList<String> outl = new ArrayList<String>(); 
		MarkItem mItem = dicForwardMarks.get(curkindTop);
		if(mItem != null)
		{	
			MarkItem.MarkFids fids = mItem.items.get(curkindBottom);
			if(fids != null)
			{
				String _name = mItem.name + fids.name;
				if(_name.equals("全部全部"))
					_name = "全部";
				outl.add(fids.fids);
				outl.add(_name + " ▼");
			}
		}
		return outl;
	}
}