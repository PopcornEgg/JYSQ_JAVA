package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.BJBDataMgr.My;
import com.yxkj.jyb.BJBDataMgr.My.ThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public final class FragmentPage_bjb_myitem extends Fragment {
	
    private static final String KEY_CONTENT = "FragmentPage_bjb_myitem:Content";
    private Context mContext = null;
    private String mCurKind = "";
    private ListView mListView = null;
    List< My.ThreadItem > mDataList = new ArrayList< My.ThreadItem >();
    //private MyAdapter mMyAdapter = null;
    
    public static FragmentPage_bjb_myitem newInstance(String content) {
        FragmentPage_bjb_myitem fragment = new FragmentPage_bjb_myitem();
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
    	View view = inflater.inflate(R.layout.fragmentpage_bjb_mypage, null);
    	
    	//mMyAdapter = new MyAdapter(mContext);
    	mListView = (ListView)view.findViewById(R.id.lv);
    	mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    			if(position < mDataList.size()){
    				ThreadItem item = mDataList.get(position);
        			bjb_MyPostData_Act.show(mContext, item.tid, item.subtype, item.subname);
    			}
    		}
    	});
    	//mListView.setAdapter(mMyAdapter);
    	if(com.yxkj.jyb.Utils.UserUtils.DataUtils.isLogined()){
    		uiUtils.setEmptyView(mContext, "还未添加任何笔记", mListView);
    	}else{
    		uiUtils.setEmptyView(mContext, "请先登陆", mListView);
    	}
    	
    	updateListView();
    	return view;
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(mDataList == null)
        	return list;
        for( int i = 0; i < mDataList.size(); ++i )
        {
        	My.ThreadItem item = mDataList.get(i);
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("subname",item.subname);
	        map.put("icon",FragmentPage_bjb_sysitem.getIconBySubType(item.subtype));
	        map.put("count",item.getCount().toString());
	        list.add(map);
        }
         
        return list;
    }
    private void updateListView(){
    	mDataList = My.getThreadItems();
    	if(mDataList == null || mDataList.size() == 0){
    		asynMyThreadData(0, 0);
    		return;
    	}
    	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
    			R.layout.fragmentpage_bjb_myitem_img,
    			new String[]{"icon", "subname", "count"},
    			new int[]{R.id.icon, R.id.subname, R.id.count});
    	mListView.setAdapter(listAdapter);
    	
    	//mMyAdapter.setData(mDataList);
    }
    @TAInject
    boolean isPulling = false;
	@SuppressLint("DefaultLocale") private void asynMyThreadData(int count, int start)
	{
		if(isPulling)
			return;
		isPulling = true;
		//LoadingBox.showBox(mContext, "加载中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_MyThreadDataUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isPulling = false;
				//LoadingBox.hideBox();
				if(content.contains("null")){
					return;
				}
				try {
					JSONArray jsonArray = new JSONArray(content);
					for(int i=0;i<jsonArray.length();i++){
						JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				        My.ThreadItem ttiem = new My.ThreadItem();
						ttiem.subtype = json.getInt("subtype");
			        	ttiem.tid = json.getString("tid");
			        	ttiem.subname = json.getString("subname");
			        	ttiem.dateline = json.getLong("dateline");
			        	ttiem.count = json.getInt("count");
			        	ttiem.from = json.getString("from");
		                My.addThreadItem(ttiem);
		                asynGetSubjectData(ttiem.tid);
					}
					updateListView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				//LoadingBox.hideBox();
				isPulling = false;
	        }  
		});
	}
	private void asynGetSubjectData(String tid)
	{
		if(tid.isEmpty())
			return;
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_MyPostDataUrl);
		params.put("tid", tid);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				if(!content.contains("null\r\n")){
					try {
						JSONArray jsonArray = new JSONArray(content);
						for(int i=0;i<jsonArray.length();i++){
							JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
							My.PostItem pitem = new My.PostItem();
				        	pitem.tid = json.getString("tid");
				        	pitem.pid = json.getString("pid");
				        	pitem.message = GlobalUtility.Func.hexStr2Str(json.getString("message"));
				        	pitem.dateline = json.getLong("dateline");
				        	pitem.from = json.getString("from");
							My.addPostItem(pitem);
						}
						if(jsonArray.length() > 0)
							updateListView();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else{
					
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
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
		mDataList = My.getThreadItems();
    	if(mDataList != null){
        	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
        			R.layout.fragmentpage_bjb_myitem_img,
        			new String[]{"icon", "subname", "count"},
        			new int[]{R.id.icon, R.id.subname, R.id.count});
        	mListView.setAdapter(listAdapter);
    	}
	}
	
	public class MyAdapter extends BaseAdapter {

		private Context mContext = null;
		private List<My.ThreadItem> mListData = null;
		public MyAdapter(Context _c) {
			mContext = _c;
		}
		public void setData(List<My.ThreadItem> _d){
			mListData = _d;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mListData == null)
				return 0;
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if( mContext == null || mListData == null)
				return null;
			My.ThreadItem item = mListData.get(position);
			if(item == null)
				return null;
			Holder vh = null;
			if(convertView == null )
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.fragmentpage_bjb_myitem,null);
				if(convertView == null)
					return null;
				vh = new Holder();
				vh.unget = (ImageButton)convertView.findViewById(R.id.unget);
				vh.unget.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						My.ThreadItem item = mListData.get(Integer.parseInt( v.getTag().toString()));
						
					}  
				});
				vh.subname = (TextView)convertView.findViewById(R.id.subname);
				convertView.setTag(vh);
			}
			else
				vh = (Holder)convertView.getTag();
	        
	        vh.subname.setText(item.subname);
	        vh.unget.setTag(position);
	        
	        return convertView;
		}
		
		public class Holder
	    {
			public TextView subname;
	    	public ImageButton unget;
	    }
	}
}
