package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ForumDataMgr.ForumNotificationItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public final class FragmentMyNotification_item extends Fragment {

	@SuppressWarnings("serial")
	static Map<String,String> name2key = new HashMap<String,String>(){{
		put("提问","post");
		put("系统","system");
	}};
	private List<ForumNotificationItem> curListThreads = null;
    private PullToRefreshListView mPullListView = null;
    private Context mContext = null;
    private String mName = "";
    private MyAdapter mMyAdapter = null;
    
    public static FragmentMyNotification_item newInstance(String fname) {
        FragmentMyNotification_item fragment = new FragmentMyNotification_item();
        fragment.mName = fname;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			
			int pos = position - 1;
			if(curListThreads != null && curListThreads.size() > pos){
				ForumNotificationItem item = curListThreads.get(pos);
				if(item != null){
					QuestionView.show(mContext, item.from_id, false);
				}
			}
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	
    	mPullListView = new PullToRefreshListView(getActivity());
    	mPullListView.setMode(Mode.BOTH);
    	mPullListView.setBackgroundResource(R.color.font_color_gray_small2);
    	//mPullListView.setBackgroundColor(R.color.font_color_gray_small2)
      // 设置下拉刷新文本  
    	ILoadingLayout ilft = mPullListView.getLoadingLayoutProxy(false, true);
    	ilft.setPullLabel("上拉刷新...");  
    	ilft.setReleaseLabel("放开刷新...");  
    	ilft.setRefreshingLabel( "正在加载...");  
      // 设置上拉刷新文本  
    	ILoadingLayout iltf = mPullListView.getLoadingLayoutProxy(true, false);
    	iltf.setPullLabel("下拉刷新...");  
    	iltf.setReleaseLabel( "放开刷新...");  
    	iltf.setRefreshingLabel("正在加载...");  
    	
    	mPullListView.setOnItemClickListener(new OnItemClickListenerImpl());
        mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				asynPost_GetList("0",10, 0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(curListThreads != null)
					asynPost_GetList( "-1", 10, curListThreads.size());
				else
					asynPost_GetList( "-1", 10, 0);
			}
		});
        
        ListView lv = mPullListView.getRefreshableView();
        mMyAdapter = new MyAdapter(mContext);
        lv.setAdapter(mMyAdapter);
        //uiUtils.setEmptyView(mContext, "网络好像出问题了哦~~~", lv);
        
    	updateListAdapter();
    	return mPullListView;
    }
    private void updateListAdapter(){
    	
    	curListThreads = ForumDataMgr.getMyNotifications_by_type(name2key.get(mName));
    	if(curListThreads == null || curListThreads.size() == 0){//拉取
    		asynPost_GetList("1",10,0);
    		return;
    	}
    	mMyAdapter.setData(curListThreads);
//    	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
//    			R.layout.fragmentmynotification_item,
//    			new String[]{"note","time"},
//    			new int[]{R.id.note,R.id.time});
//    	mPullListView.setAdapter(listAdapter);
    }
    public class MyAdapter extends BaseAdapter {

    	private Context context = null;
    	private List<ForumNotificationItem> mListData = null;
    	public MyAdapter(Context _c) {
    		context = _c;
    	}
    	public void setData(List<ForumNotificationItem> _d){
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
    		if( context == null || mListData == null)
    			return null;
    		ForumNotificationItem item = mListData.get(position);
    		if(item == null)
    			return null;
    		ViewHolder vh = null;
    		if(convertView == null )
    		{
    			convertView = LayoutInflater.from(context).inflate(R.layout.fragmentmynotification_item,null);
    			if(convertView == null)
    				return null;
    			vh = new ViewHolder();
    			vh.subname = (TextView)convertView.findViewById(R.id.note);
    			vh.time = (TextView)convertView.findViewById(R.id.time);
    			convertView.setTag(vh);
    		}
    		else
    			vh = (ViewHolder)convertView.getTag();
    		
    		vh.subname.setText(Html.fromHtml(item.note));
            vh.time.setText(GlobalUtility.Func.timeStamp2Recently(item.dateline));
            return convertView;
    	}
    	
    	public class ViewHolder
        {
        	public TextView subname;
        	public TextView time;
        }
    }
    @TAInject
    boolean isPulling = false;
	@SuppressLint("DefaultLocale") private void asynPost_GetList( String isnew, int count, int start)
	{
		if(isPulling)
			return;
		isPulling = true;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserMyNotificationUrl);
	    params.put("op", "get");
	    params.put("type", name2key.get(mName));
	    params.put("isnew", isnew);
	    params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				mPullListView.onRefreshComplete();
				if(content.contains("null")){
					isPulling = false;
					return;
				}
				try {
					JSONArray jsonArray = new JSONArray(content);
					for(int i=0;i<jsonArray.length();i++){
						JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
						
						ForumNotificationItem item = new ForumNotificationItem();
						item.id = json.getString("id");
		                item.uid = json.getString("uid");
		                item.type = json.getString("type");
		                item.isnew = json.getInt("isnew");
		                item.author = json.getString("author");
		                item.authorid = json.getString("authorid");
		                item.note = GlobalUtility.Func.hexStr2Str(json.getString("note"));
		                item.dateline = json.getLong("dateline");
		                item.from_id = json.getString("from_id");
		                item.from_idtype = json.getString("from_idtype");
		                
		                ForumDataMgr.addMyNotification(item);
					}
					updateListAdapter();
					isPulling = false;
				} catch (JSONException e) {
					e.printStackTrace();
					mPullListView.onRefreshComplete();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				mPullListView.onRefreshComplete();
				LoadingBox.hideBox();
				isPulling = false;
	        }  
		});
	}
}
