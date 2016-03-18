package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public final class FragmentPage_jyf_item extends Fragment {
    private static final String KEY_CONTENT = "FragmentPage_jyf_item:Content";
    private static final Map<String,String> dicName2Fid = new HashMap<String,String>() {{
    	put(FragmentPage_jyf.tab_xtff,"96");
    	put(FragmentPage_jyf.tab_jylxl,"97");
    	put(FragmentPage_jyf.tab_sjyy,"98");
	}};
    
    private List<ForumThreadItem> curListThreads = null;
    private PullToRefreshListView mPullListView = null;
    private Context mContext = null;
    private String mCurKind = "";
    
    public static FragmentPage_jyf_item newInstance(String content) {
        FragmentPage_jyf_item fragment = new FragmentPage_jyf_item();
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

	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			int pos = position - 1;
			if( mCurKind.equals(FragmentPage_jyf.tab_xtff) ){
				if( pos == 0){
					Intent intent=new Intent(parent.getContext(),VideoStudyList.class);
					startActivity(intent);
				}
				else{
					pos--;
					if(curListThreads != null && curListThreads.size() > pos){
						JYFUbbView.show(mContext, curListThreads.get(pos));
					}
				}
			}
			else if( mCurKind.equals(FragmentPage_jyf.tab_jylxl) ){
				if( pos == 0){
					//CodeSystem_FraAct.show(parent.getContext());
				}
				else{
					pos--;
					if(curListThreads != null && curListThreads.size() > pos){
						JYFUbbView.show(mContext, curListThreads.get(pos));
					}
				}
			}
			else if( mCurKind.equals(FragmentPage_jyf.tab_sjyy) ){
				if(curListThreads != null && curListThreads.size() > pos){
					JYFUbbView.show(mContext, curListThreads.get(pos));
				}
			}
		}
	}
	
	private void asynGetStudyList(String getlist)
	{
		if(!UserUtils.DataUtils.isLogined() || !NetWorkStateDetector.isConnectingToInternet())
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumGetVideoStudyUrl);
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("getlist", getlist);
		
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				if(!content.contains("null\r\n")){
					try {
						JSONObject json = new JSONObject(content);
						for(Integer i=0;i<VideoStudyList.mDatas.size();i++){
							String _key = "data" + i;
							if(json.has(_key)){
								VideoStudyList.setStudyListStudyTime(i, json.getInt(_key), false);
							}
						}
						updateListAdapter();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			@Override  
	        public void onFailure(String error) {  
	        }  
		});
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	View view = inflater.inflate(R.layout.fragmentpage_jyf_item, null);
    	
    	//初始化视频学习列表
    	if(mCurKind.equals(FragmentPage_jyf.tab_xtff)){
    		String getlist = VideoStudyList.initStudyList();
      		if(!getlist.isEmpty())
      			asynGetStudyList(getlist);
    	}
  		
    	//LinearLayout ll = new LinearLayout(getActivity());
    	//ll.addView(inflater.inflate(R.layout.jyf_item_video, null));
    	
    	//XmlPullParser parser = resources.getXml(myResouce);
    	//AttributeSet attributes = Xml.getAttributeSet(parser);
    	//mPullListView = new PullToRefreshListView(getActivity());
    	mPullListView = (PullToRefreshListView)view.findViewById(R.id.pulllv);
    	mPullListView.setMode(Mode.BOTH);
//    	mPullListView.setBackgroundResource(R.color.font_color_gray_small2);
//      // 设置下拉刷新文本  
//    	ILoadingLayout ilft = mPullListView.getLoadingLayoutProxy(false, true);
//    	ilft.setPullLabel("上拉刷新...");  
//    	ilft.setReleaseLabel("放开刷新...");  
//    	ilft.setRefreshingLabel( "正在加载...");  
//      // 设置上拉刷新文本  
//    	ILoadingLayout iltf = mPullListView.getLoadingLayoutProxy(true, false);
//    	iltf.setPullLabel("下拉刷新...");  
//    	iltf.setReleaseLabel( "放开刷新...");  
//    	iltf.setRefreshingLabel("正在加载...");  
    	
    	mPullListView.setOnItemClickListener(new OnItemClickListenerImpl());
        mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				asynGetThreadList(getCurFid(), 20, 0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String fid = getCurFid();
				if(curListThreads != null)
					asynGetThreadList(fid, 20, curListThreads.size());
				else
					asynGetThreadList(fid, 20, 0);
			}
		});

        ListView actualListView = mPullListView.getRefreshableView();
		actualListView.setDivider(mContext.getResources().getDrawable(R.color.font_color_gray_small));
		actualListView.setDividerHeight(1);
		actualListView.setCacheColorHint(R.color.font_color_gray_small);
		
    	updateListAdapter();
    	return view;
    }
    private void setEmptyStr(){
		ListView actualListView = mPullListView.getRefreshableView();
		uiUtils.setEmptyView(mContext, "网络好像出问题了哦~~~", actualListView);
	}
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mCurKind);
    }
    
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(curListThreads == null)
    		return list;
        for( int i = 0; i < curListThreads.size(); ++i )
        {
        	ForumThreadItem titem = curListThreads.get(i);
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("info",titem.subject);
	        map.put("img", R.drawable.abc_ic_menu_moreoverflow_normal_holo_light);
	        list.add(map);
        }
         
        return list;
    }
    private String getCurFid(){
    	String fid = "";
    	if(dicName2Fid.containsKey(mCurKind)){
    		fid = dicName2Fid.get(mCurKind);
    	}
    	return fid;
    }
    
    private void updateListAdapter(){
    	
    	String fid = getCurFid();
    	curListThreads = ForumDataMgr.GetThreads_By_Fid(fid);
    	if(curListThreads == null || curListThreads.size() == 0){//拉取
    		asynGetThreadList(fid,20,0);
    		return;
    	}

    	if(mCurKind.equals(FragmentPage_jyf.tab_xtff)){
    		JyfItemJyzs adapter = new JyfItemJyzs(mContext);
			mPullListView.setAdapter(adapter);
			adapter.setData(curListThreads);
    	}
    	else if( mCurKind.equals(FragmentPage_jyf.tab_jylxl) ){
    		JyfItemJyxl adapter = new JyfItemJyxl(mContext);
			mPullListView.setAdapter(adapter);
			adapter.setData(curListThreads);
		}
		else if( mCurKind.equals(FragmentPage_jyf.tab_sjyy) ){
			JyfItemSjxy adapter = new JyfItemSjxy(mContext);
			mPullListView.setAdapter(adapter);
			adapter.setData(curListThreads);
    	}
    }
    @TAInject
    int pullingIdx = 0;
	@SuppressLint("DefaultLocale") private void asynGetThreadList(String fids, int count, int start)
	{
		if(mCurKind.equals(FragmentPage_jyf.tab_xtff)){
			LoadingBox.showBox(mContext, "加载中...");
		}
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumThreadListUrl);
		params.put("fids", fids);
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		//params.put("authorex", "1");
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				mPullListView.onRefreshComplete();
				LoadingBox.hideBox();
				if(content.contains("null")){
					return;
				}
				try {
					JSONArray jsonArray = new JSONArray(content);
					for(int i=0;i<jsonArray.length();i++){
						JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			            //Toast.makeText(context, (String)json.getString("subject"), Toast.LENGTH_SHORT).show();
						ForumThreadItem tdata = new ForumThreadItem();
						tdata.tid = json.getString("tid");
		                tdata.fid = json.getString("fid");
		                tdata.author = json.getString("author");
		                tdata.authorid = json.getString("authorid");
		                tdata.subject = json.getString("subject");
		                tdata.dateline = json.getLong("dateline");
		                tdata.replies = json.getInt("replies");
		                tdata.realname = json.getString("realname");
		                tdata.gender = json.getInt("gender");
		                ForumDataMgr.AddThread(tdata);
					}
					updateListAdapter();
					setEmptyStr();
				} catch (JSONException e) {
					//e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				mPullListView.onRefreshComplete();
				LoadingBox.hideBox();
				setEmptyStr();
	        }  
		});
	}
	@Override
	public void onResume()
	{
		super.onResume();
		//初始化视频学习列表
		if(mCurKind.equals(FragmentPage_jyf.tab_xtff)){
			String fid = getCurFid();
	    	curListThreads = ForumDataMgr.GetThreads_By_Fid(fid);
	    	if(curListThreads == null || curListThreads.size() == 0){//拉取
	    		return;
	    	}
    		JyfItemJyzs adapter = new JyfItemJyzs(mContext);
			mPullListView.setAdapter(adapter);
			adapter.setData(curListThreads);
    	}
	}
}
