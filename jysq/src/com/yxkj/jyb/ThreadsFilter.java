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
	//������ұ�
    @SuppressWarnings("serial")
	public static final Map<String, MarkItem> dicForwardMarks = new HashMap<String, MarkItem>(){{
        
        put("Сѧ",new MarkItem("Сѧ",new ArrayList<MarkItem.MarkFids>(){{
             add(new MarkItem.MarkFids("��ѧ","42"));
             add(new MarkItem.MarkFids("����","2"));
             add(new MarkItem.MarkFids("Ӣ��","43"));
        }}));
        
        put("��һ",new MarkItem("��һ",new ArrayList<MarkItem.MarkFids>(){{
             add(new MarkItem.MarkFids("��ѧ","45"));
             add(new MarkItem.MarkFids("����","44"));
             add(new MarkItem.MarkFids("Ӣ��","46"));
             add(new MarkItem.MarkFids("����","47"));
             add(new MarkItem.MarkFids("����","48"));
             add(new MarkItem.MarkFids("��ʷ","49"));
             add(new MarkItem.MarkFids("����","50"));
        }}));
        
        put("����",new MarkItem("����",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("��ѧ","52"));
            add(new MarkItem.MarkFids("����","51"));
            add(new MarkItem.MarkFids("Ӣ��","53"));
            add(new MarkItem.MarkFids("����","54"));
            add(new MarkItem.MarkFids("����","55"));
            add(new MarkItem.MarkFids("����","56"));
            add(new MarkItem.MarkFids("��ʷ","57"));
            add(new MarkItem.MarkFids("����","58"));
        }}));
        
        put("����",new MarkItem("����",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("��ѧ","60"));
            add(new MarkItem.MarkFids("����","59"));
            add(new MarkItem.MarkFids("Ӣ��","61"));
            add(new MarkItem.MarkFids("����","62"));
            add(new MarkItem.MarkFids("��ѧ","63"));
            add(new MarkItem.MarkFids("����","64"));
            add(new MarkItem.MarkFids("����","65"));
            add(new MarkItem.MarkFids("��ʷ","66"));
            add(new MarkItem.MarkFids("����","67"));
        }}));
        
        put("��һ",new MarkItem("��һ",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("��ѧ","69"));
            add(new MarkItem.MarkFids("����","68"));
            add(new MarkItem.MarkFids("Ӣ��","70"));
            add(new MarkItem.MarkFids("����","71"));
            add(new MarkItem.MarkFids("��ѧ","72"));
            add(new MarkItem.MarkFids("����","73"));
            add(new MarkItem.MarkFids("����","74"));
            add(new MarkItem.MarkFids("��ʷ","75"));
            add(new MarkItem.MarkFids("����","76"));
        }}));
        
        put("�߶�",new MarkItem("�߶�",new ArrayList<MarkItem.MarkFids>(){{
              add(new MarkItem.MarkFids("��ѧ","78"));
              add(new MarkItem.MarkFids("����","77"));
              add(new MarkItem.MarkFids("Ӣ��","79"));
              add(new MarkItem.MarkFids("����","80"));
              add(new MarkItem.MarkFids("��ѧ","81"));
              add(new MarkItem.MarkFids("����","82"));
              add(new MarkItem.MarkFids("����","83"));
              add(new MarkItem.MarkFids("��ʷ","84"));
              add(new MarkItem.MarkFids("����","85"));
        }}));
        
        put("����",new MarkItem("����",new ArrayList<MarkItem.MarkFids>(){{
            add(new MarkItem.MarkFids("��ѧ","87"));
            add(new MarkItem.MarkFids("����","86"));
            add(new MarkItem.MarkFids("Ӣ��","88"));
            add(new MarkItem.MarkFids("����","89"));
            add(new MarkItem.MarkFids("��ѧ","90"));
            add(new MarkItem.MarkFids("����","91"));
            add(new MarkItem.MarkFids("����","92"));
            add(new MarkItem.MarkFids("��ʷ","93"));
            add(new MarkItem.MarkFids("����","94"));
        }}));
        
        //����ȫ��id
        List<MarkItem.MarkFids> alllist = new ArrayList<MarkItem.MarkFids>();
        for (Map.Entry<String, MarkItem> set : this.entrySet()) {  
        	String fids = "";
        	MarkItem checkItem = set.getValue();
            for(int i=0;i<checkItem.items.size();i++){
            	fids += checkItem.items.get(i).fids + ",";
            }
            fids = fids.substring(0,fids.length() - 2);
            checkItem.items.add(0, new MarkItem.MarkFids("ȫ��", fids)); 
            alllist.add(new MarkItem.MarkFids(set.getKey(), fids));
        } 
       //��������ȫ��
        String fids = "";
        MarkItem allitem = new MarkItem("ȫ��",alllist);
        for(int i=0;i<allitem.items.size();i++){
        	fids += allitem.items.get(i).fids + ",";
        }
        allitem.items.add(0, new MarkItem.MarkFids("ȫ��", fids.substring(0,fids.length() - 2))); 
        put("ȫ��", allitem);
    }};
    @SuppressWarnings("serial")
	public static final List<String> listIdx2Name = new ArrayList<String> () {{ 
        add("ȫ��");add("Сѧ");add("��һ");add("����");add("����");add("��һ");add("�߶�");add("����");
    }};
  //������ұ�
    @SuppressWarnings("serial")
	public static final Map<String, String> dicBackMarks = new HashMap<String, String>(){{
		for(Map.Entry<String, MarkItem> et : dicForwardMarks.entrySet()){
			MarkItem item = et.getValue();
			String key = et.getKey();
			if(!key.equals("ȫ��")){
				for(int i=0;i<item.items.size();i++){
					MarkItem.MarkFids mark = item.items.get(i);
					if(!mark.name.equals("ȫ��"))
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
		return dicForwardMarks.get("ȫ��").items.get(0).fids;
	};

	static ArrayList<View> sbtnTops = new ArrayList<View> ();
	static ArrayList<View> sbtnBottoms = new ArrayList<View> ();
	static String curkindTop = "ȫ��";
	static int curkindBottom = 0;
	static PopupWindow sPopupWindow = null;
	static public void showPopupWindow(Context _c, View view) {

        // һ���Զ���Ĳ��֣���Ϊ��ʾ������
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

        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
        // �Ҿ���������API��һ��bug
       //ʵ����һ��ColorDrawable��ɫΪ��͸��
    	ColorDrawable dw = new ColorDrawable(0xFF000000);
    	//����SelectPicPopupWindow��������ı���
    	popupWindow.setBackgroundDrawable(dw);
    	
    	popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
    	//popupWindow.showAsDropDown(view);
    	//�õ�mView����Ļ�е�����  
    	int [] pos = new int[2];  
    	view.getLocationOnScreen(pos);  
    	int offsetY = pos[1] + view.getHeight();  
    	int offsetX = 0;  
    	popupWindow.showAtLocation(view,Gravity.TOP|Gravity.CENTER_HORIZONTAL, offsetX, offsetY);
    	
    	MainTabActivity.setBackgroundAlpha(0.5f);
    	 //popWindow��ʧ��������
    	popupWindow.setOnDismissListener(new OnDismissListener() {
          @Override
          public void onDismiss() {
        	  sPopupWindow = null;
        	  MainTabActivity.setBackgroundAlpha(1.0f);
          }
        });
                  
    	/*
    	// ��ʾ��λ��Ϊ:��Ļ�Ŀ�ȵ�һ��-PopupWindow�ĸ߶ȵ�һ��
        WindowManager wm = (WindowManager) _c.getSystemService(Context.WINDOW_SERVICE);
        Point pt = new Point();
		wm.getDefaultDisplay().getSize(pt);
		int xPos = pt.x / 2;
        // ���úò���֮����show
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
						if(_name.equals("ȫ��ȫ��"))
							_name = "ȫ��";
						FragmentPage2.sFragmentPage2.setFilter(fids.fids, _name + " ��");
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
				if(_name.equals("ȫ��ȫ��"))
					_name = "ȫ��";
				outl.add(fids.fids);
				outl.add(_name + " ��");
			}
		}
		return outl;
	}
}