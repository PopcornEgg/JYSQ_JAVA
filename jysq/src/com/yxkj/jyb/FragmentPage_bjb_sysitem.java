package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yxkj.jyb.Utils.Debuger;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public final class FragmentPage_bjb_sysitem extends Fragment {
	
	public static final String tab_Chinese   = "语文";
	public static final String tab_Math      = "数学";
	public static final String tab_English   = "英语";
	public static final String tab_Physics   = "物理";
	public static final String tab_Chemistry = "化学";
	public static final String tab_Biology   = "生物";
	public static final String tab_Politics  = "政治";
	public static final String tab_History   = "历史";
	public static final String tab_Geography = "地理";
	
	@SuppressWarnings({ "serial" })
	private static final Map<String,String> subjectName2Fid = new HashMap<String,String>() {{
		if(Debuger.USE_LOCAL_PHPSERVER){
			put(tab_Chinese,"116");
		}else{
			put(tab_Chinese,"103");
		}
    	put(tab_Math,"104");
    	put(tab_English,"105");
    	put(tab_Physics,"106");
    	put(tab_Chemistry,"107");
    	put(tab_Biology,"111");
    	put(tab_Politics,"108");
    	put(tab_History,"109");
    	put(tab_Geography,"110");
	}};
	public static int curSubType = 0;
	
	private static final String[] subjectNameList = new String[] { 
		tab_Chinese, tab_Math, tab_English,
		tab_Physics,tab_Chemistry,tab_Biology,
		tab_Politics,tab_History,tab_Geography,
	};
	private static final int[] subjectIconList = new int[] { 
		R.drawable.ico_yuwen,
		R.drawable.ico_shuxue,
		R.drawable.ico_yingyu,
		R.drawable.ico_wuli,
		R.drawable.ico_huaxue,
		R.drawable.ico_shengwu,
		R.drawable.ico_zhengzhi,
		R.drawable.ico_lishi,
		R.drawable.ico_dili,
	};
	public static String getSubjectName(int idx){
		if(idx >= 0 && idx < subjectNameList.length)
			return subjectNameList[idx];
		return "";
	} 
	public static String getSubjectFid_ByName(String _name){
		if(subjectName2Fid.containsKey(_name))
			return subjectName2Fid.get(_name);
		return "";
	} 
	public static String getSubjectFid_ByIdx(int idx){
		if(idx >= 0 && idx < subjectNameList.length){
			String _name = subjectNameList[idx];
			if(subjectName2Fid.containsKey(_name))
				return subjectName2Fid.get(_name);
		}
		return "";
	} 
	public static int getCurSubType(){
		return curSubType;
	}
	public static int getIconBySubType(int _st){
		if(_st >=0 && _st < subjectIconList.length )
			return subjectIconList[_st];
		return -1;
	} 
		
    private static final String KEY_CONTENT = "FragmentPage_bjb_sysitem:Content";
    private Context mContext = null;
    private String mCurKind = "";
   
    
    public static FragmentPage_bjb_sysitem newInstance(String content) {
        FragmentPage_bjb_sysitem fragment = new FragmentPage_bjb_sysitem();
        fragment.mCurKind = content;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
        	mCurKind = savedInstanceState.getString(KEY_CONTENT);
        }        
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	View view = inflater.inflate(R.layout.fragmentpage_bjb_syspage, null);
    	OnClickListener oncl = new OnClickListener(){
    		@Override
    		public void onClick(View v){
    			curSubType = Integer.parseInt(v.getTag().toString()) ;
     			bjb_SubjectData_Act.show(mContext, subjectNameList[curSubType]);
    		}
    	};
    	view.findViewById(R.id.Button01).setOnClickListener(oncl);
    	view.findViewById(R.id.Button02).setOnClickListener(oncl);
    	view.findViewById(R.id.Button03).setOnClickListener(oncl);
    	view.findViewById(R.id.Button04).setOnClickListener(oncl);
    	view.findViewById(R.id.Button05).setOnClickListener(oncl);
    	view.findViewById(R.id.Button06).setOnClickListener(oncl);
    	view.findViewById(R.id.Button07).setOnClickListener(oncl);
    	view.findViewById(R.id.Button08).setOnClickListener(oncl);
    	view.findViewById(R.id.Button09).setOnClickListener(oncl);
    	return view;
    	
//        ListView listView = new ListView(mContext);
//        listView.setDivider(new ColorDrawable(R.color.font_color_gray));  
//        listView.setDividerHeight(1);
//        //listView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.fragmentpage_bjb_sysitem_img,Arrays.asList(subjectNameList)));
//        ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
//    			R.layout.fragmentpage_bjb_sysitem_img,
//    			new String[]{"icon", "subname"},
//    			new int[]{R.id.icon, R.id.subname});
//        listView.setAdapter(listAdapter);
//    	
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//    		@Override
//    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
//    			curSubType = position;
//    			bjb_SubjectData_Act.show(mContext, subjectNameList[position]);
//    		}
//    	});
//        
//    	return listView;
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for( int i = 0; i < subjectNameList.length; ++i )
        {
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("subname",subjectNameList[i]);
	        map.put("icon",subjectIconList[i]);
	        list.add(map);
        }
         
        return list;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mCurKind);
    }
  
	@Override
	public void onResume()
	{
		super.onResume();
	}
}
