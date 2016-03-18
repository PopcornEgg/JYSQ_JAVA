package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yxkj.jyb.TabsMgr.codesysdata;
import com.yxkj.jyb.TabsMgr.codesysdataTab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public final class CodeSystem_item_Fra extends Fragment {

	@SuppressWarnings("serial")
	static Map<String,String> name2key = new HashMap<String,String>(){{
		put("数字","post");
		put("字母","system");
	}};
	private codesysdataTab curcodesysdataTab = null;
    private ListView mListView = null;
    private Context mContext = null;
    private String mName = "";
    
    public static CodeSystem_item_Fra newInstance(String fname) {
        CodeSystem_item_Fra fragment = new CodeSystem_item_Fra();
        fragment.mName = fname;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	mListView = new ListView(getActivity());
    	mListView.setBackgroundResource(R.color.font_color_gray_small2);
    	
    	//点击
    	mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    		}
    	});
    	//长按
    	mListView.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				CodeSystem_editor_Act.show(mContext, position, mName);
				return true;
			}
    	});//注册
    	 
    	updateListAdapter();
    	return mListView;
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(curcodesysdataTab == null)
        	curcodesysdataTab = codesysdataTab.getItem_by_Name(mName);
        if(curcodesysdataTab == null)
        	return list;
        for( int i = 0; i < curcodesysdataTab.mItemList.size(); ++i )
        {
        	codesysdata item = curcodesysdataTab.mItemList.get(i);
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("cname",item.cname);
	        map.put("name",item.name);
	        list.add(map);
        }
         
        return list;
    }
    private void updateListAdapter(){
    	
    	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
    			R.layout.codesystem_item_fra,
    			new String[]{"cname","name"},
    			new int[]{R.id.cname,R.id.name});
    	mListView.setAdapter(listAdapter);
    }
    @Override
	public void onResume()
	{
		super.onResume();
		updateListAdapter();
	}
}
