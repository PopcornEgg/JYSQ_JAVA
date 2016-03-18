package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.Debuger;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.uiUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.ta.annotation.TAInject;
//import com.handmark.pulltorefresh.samples.PullToRefreshListActivity;
//import com.handmark.pulltorefresh.samples.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

@SuppressLint("InflateParams") public class FragmentPage2 extends Fragment{

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

	private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private MyAdapterThread mAdapter;
	
	public static Context context;
	public static FragmentPage2 sFragmentPage2 = null;
	static String curFid = "";
	static List<ForumThreadItem> listCurThreads;
	
	public static String getCurSubName(String fid){
		if(curFid.length() <= 3)
			return "";
		return ThreadsFilter.getSubNameByFid(fid);
	};
	
	LayoutInflater mInflater = null;
	boolean isPulling = false;//是否正在拉取
	static Button titleName = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	

		mInflater = inflater;
		View view = inflater.inflate(R.layout.fragment_2, null);	
		context = container.getContext();
		sFragmentPage2 = this;

		mPullRefreshListView = (PullToRefreshListView)view.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);  
//		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
//			@Override
//			public void onLastItemVisible() {
//				if(listCurThreads != null)
//					asynGetThreadList(curFid, 10, listCurThreads.size());
//				else
//					asynGetThreadList(curFid, 10, 0);
//			}
//		});
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				asynGetNewThreadList(curFid, 10, 0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(listCurThreads != null)
					asynGetThreadList(curFid, 10, listCurThreads.size());
				else
					asynGetThreadList(curFid, 10, 0);
			}
		});
		
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		registerForContextMenu(actualListView);
		mAdapter = new MyAdapterThread(context);
		actualListView.setAdapter(mAdapter);
		
		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mStrings));
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListenerImpl());
		
		titleName = (Button)view.findViewById(R.id.titleName);
		titleName.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				//暂时屏蔽筛选
				//ThreadsFilter.showPopupWindow(context, v);
			}
		});
		ArrayList<String> pl = ThreadsFilter.getDefaultFids();
		if(pl.size() == 2)
		{
			curFid = pl.get(0);
			titleName.setText(pl.get(1));
			titleName.setText("回答");
		}
		view.findViewById(R.id.my).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				MyThreads.show(context);
			}
		});
		updateListView();
		return view;
	}
	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ForumThreadItem item = listCurThreads.get(position - 1);
			if(item != null){
				QuestionView.show(FragmentPage2.context, item.tid, false);
			}
		}
	}

	@TAInject
	@SuppressLint("DefaultLocale") private void asynGetThreadList(String fids, int count, int start)
	{
		if(isPulling)
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumThreadListUrl);
		params.put("fids", fids);
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		params.put("authorex", "y");
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				mPullRefreshListView.onRefreshComplete();
				LoadingBox.hideBox();
				isPulling = false;
				if(content.contains("null")){
					mAdapter.setData(listCurThreads);
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
		                tdata.credit = json.getInt("credit");
		                tdata.adoptpid = json.getInt("adoptpid");
		                tdata.attachment = json.getInt("attachment");
		                ForumDataMgr.AddThread(tdata);
					}
					updateListView();
					setEmptyStr();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
	            // 上传失败后要做到工作  
				//error.
				//Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
				LoadingBox.hideBox();
				isPulling = false;
				mPullRefreshListView.onRefreshComplete();
				setEmptyStr();
	        }  
		});
	}
	private void setEmptyStr(){
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		uiUtils.setEmptyView(context, "网络好像出问题了哦~~~", actualListView);
	}
	@SuppressLint("DefaultLocale") private void asynGetNewThreadList(String fids, int count, int start)
	{
		if(isPulling)
			return;
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumNewThreadListUrl);
		params.put("fids", fids);
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		params.put("authorex", "y");
		
		HttpUtils.post(FragmentPage2.this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				mPullRefreshListView.onRefreshComplete();
				LoadingBox.hideBox();
				isPulling = false;
				if(content.contains("null")){
					mAdapter.setData(listCurThreads);
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
		                tdata.credit = json.getInt("credit");
		                tdata.adoptpid = json.getInt("adoptpid");
		                tdata.attachment = json.getInt("attachment");
		                ForumDataMgr.AddThread(tdata);
					}
					updateListView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				if(Debuger.USE_WNS){
				}else{
				}
				LoadingBox.hideBox();
				isPulling = false;
				mPullRefreshListView.onRefreshComplete();
	        }  
		});
	}
	@SuppressWarnings("unchecked")
	private void updateListView()
	{
		listCurThreads = ForumDataMgr.GetThreads_By_Fids(curFid);
		if(listCurThreads == null || listCurThreads.size() <= 0)
		{
			LoadingBox.showBox( context, "加载中...");
			asynGetThreadList(curFid, 10, 0);	
			return;
		}
		Collections.sort(listCurThreads);
		mAdapter.setData(listCurThreads);
	}
	private String[] mStrings = { "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler", "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
			"Allgauer Emmentaler" };
	
	public void setFilter(String fids, String _name){
		curFid = fids;
		titleName.setText(_name);
		updateListView();
	}
	static public void updateThreadReply()
	{
		if(sFragmentPage2 != null)
			sFragmentPage2.mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Plugins.onResume(context);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		Plugins.onPause(context);
	}

	public LoadHandler mLoadHandler = new LoadHandler();
    class LoadHandler extends Handler  
    {  
        /** 
         * 接受子线程传递的消息机制 
         */  
        @Override  
        public void handleMessage(Message msg)  
        {  
            super.handleMessage(msg);  
            int what = msg.what;  
//            ListView actualListView = mPullRefreshListView.getRefreshableView();
//            
//            View _v = actualListView.getChildAt(0);
//            if(_v != null){
//                MyAdapterThread.ViewHolder vh = (MyAdapterThread.ViewHolder)_v.getTag();
//                if(vh != null){
//                	Toast.makeText(context, "MyAdapterThread.ViewHolder vh ok", Toast.LENGTH_SHORT).show();
//                }
//
//            }
            switch (what)  
            {                  
                case 1:  
                {  
                	DLItem dlitem = (DLItem)msg.obj;
                	if(dlitem != null){
                		String url = dlitem.url;
                		ImageView img = (ImageView)dlitem.tag;
                		ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
            			if(imginfo != null){
            				LayoutParams params = img.getLayoutParams();  
            			    params.height= GlobalUtility.Func.dip2px(100.0f);
            			    img.setImageBitmap(imginfo.bitmap);
            			}
            			else
            				Log.i("jiyibang_FragmentPage2:handleMessage", "下载成功;加载失败 : " + url);
                	}
                	else
                		Log.i("jiyibang_FragmentPage2:handleMessage", "DLItem == null");
                }  
            }  
        }  
    }  
}