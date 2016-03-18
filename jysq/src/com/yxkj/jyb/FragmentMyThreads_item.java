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
import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.UserUtils.DataUtils;
import com.yxkj.jyb.Utils.uiUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

public final class FragmentMyThreads_item extends Fragment {

	private List<ForumThreadItem> curListThreads = null;
    private PullToRefreshListView mPullListView = null;
    private Context mContext = null;
    private MyAdapter mMyAdapter = null;
    
    public static FragmentMyThreads_item newInstance() {
        FragmentMyThreads_item fragment = new FragmentMyThreads_item();
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
				ForumThreadItem item = curListThreads.get(pos);
				if(item != null)
				{
					QuestionView.show(mContext, item.tid, false);
				}
			}
			
			/*Intent intent=new Intent(parent.getContext(),Page_WebView.class);
			//intent.putExtra("curTid", item.tid);
			startActivity(intent);*/
		}
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	
    	mPullListView = new PullToRefreshListView(getActivity());
    	mPullListView.setMode(Mode.BOTH);
    	mPullListView.setBackgroundResource(R.color.font_color_gray_small2);
    //	mPullListView.setDividerPadding(1);
   // 	mPullListView.setDividerDrawable(new ColorDrawable(R.color.font_color_gray_small2));
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
				asynGetThreadList(10, 0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(curListThreads != null)
					asynGetThreadList( 10, curListThreads.size());
				else
					asynGetThreadList( 10, 0);
			}
		});
        
        ListView lv = mPullListView.getRefreshableView();
        mMyAdapter = new MyAdapter(mContext);
        lv.setAdapter(mMyAdapter);
        
        uiUtils.setEmptyView(mContext, DataUtils.getListEmptyText("您当前没有任何提问哦~~~"), lv);
        
    	updateListAdapter();
    	return mPullListView;
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(curListThreads == null)
    		return list;
        for( int i = 0; i < curListThreads.size(); ++i )
        {
        	ForumThreadItem item = curListThreads.get(i);
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("time",GlobalUtility.Func.timeStamp2Recently(item.dateline));
	        map.put("subject",item.subject);
	        map.put("img", "");
	        map.put("ansnum", item.replies + "回答");
	        map.put("credit", Integer.toString(item.credit));
	        list.add(map);
        }
         
        return list;
    }
    private void updateListAdapter(){
    	
    	curListThreads = ForumDataMgr.getMyThreads();
    	if(curListThreads == null || curListThreads.size() == 0){//拉取
    		asynGetThreadList(10,0);
    		return;
    	}
    	mMyAdapter.setData(curListThreads);
//    	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
//    			R.layout.mythread_item,
//    			new String[]{"img","time","subject","ansnum","credit"},
//    			new int[]{R.id.img, R.id.time, R.id.subject , R.id.ansnum , R.id.credit });
//    	mPullListView.setAdapter(listAdapter);
    }
    public LoadHandler mLoadHandler = new LoadHandler();
    class LoadHandler extends Handler  
    {  
        @Override  
        public void handleMessage(Message msg)  
        {  
            super.handleMessage(msg);  
            int what = msg.what;  
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
    public class MyAdapter extends BaseAdapter {

    	private Context context = null;
    	private List<ForumThreadItem> mListData = null;
    	public MyAdapter(Context _c) {
    		context = _c;
    	}
    	public void setData(List<ForumThreadItem> _d){
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
    		ForumThreadItem item = mListData.get(position);
    		if(item == null)
    			return null;
    		ViewHolder vh = null;
    		if(convertView == null )
    		{
    			convertView = LayoutInflater.from(context).inflate(R.layout.mythread_item,null);
    			if(convertView == null)
    				return null;
    			vh = new ViewHolder();
    			vh.img = (ImageView)convertView.findViewById(R.id.img);
    			vh.subname = (TextView)convertView.findViewById(R.id.subject);
    			vh.time = (TextView)convertView.findViewById(R.id.time);
    			vh.ansnum = (TextView)convertView.findViewById(R.id.ansnum);
    			vh.credit = (TextView)convertView.findViewById(R.id.credit);
    			convertView.setTag(vh);
    		}
    		else
    			vh = (ViewHolder)convertView.getTag();
    		
    		if(item.attachment == 0){
    			vh.img.setVisibility(View.GONE);
    			vh.subname.setVisibility(View.VISIBLE);
    			vh.subname.setText(Html.fromHtml(item.subject));
    		}
    		else if(item.attachment == 2){
    			vh.subname.setVisibility(View.GONE);
    			vh.img.setVisibility(View.VISIBLE);
    			
    			String url = item.subject + "/scaling";
    			ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
    			if(imginfo != null){
    				LayoutParams params = vh.img.getLayoutParams();  
    			    params.height= GlobalUtility.Func.dip2px(100.0f);  
    			    vh.img.setImageBitmap(imginfo.bitmap);
    			}
    			else{
    				vh.img.setImageResource(R.color.font_color_gray);
    				ImageDataMgr.sDownLoadMgr.addLoad(url, vh.img, 1, mLoadHandler);
    			}
    		}
    		
            vh.img.setTag(position);
            vh.ansnum.setText(item.replies + "回答");
            vh.time.setText(GlobalUtility.Func.timeStamp2Recently(item.dateline));
            vh.credit.setText(Integer.toString(item.credit));
            
            return convertView;
    	}
    	
    	public class ViewHolder
        {
        	public TextView subname;
        	public TextView time;
        	public TextView ansnum;
        	public TextView credit;
    		public ImageView img;
        }
    }

    @TAInject
    boolean isPulling = false;
	@SuppressLint("DefaultLocale") private void asynGetThreadList(int count, int start)
	{
		if(isPulling)
			return;
		isPulling = true;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserMyThreadUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
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
					String realname = UserUtils.DataUtils.get("realname");
					String gender = UserUtils.DataUtils.get("gender");
					int sex = 0;
					if(!realname.isEmpty())
						sex = Integer.parseInt(gender);
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
		                tdata.realname = realname;
		                tdata.gender = sex;
		                tdata.credit = json.getInt("credit");
		                tdata.adoptpid = json.getInt("adoptpid");
		                tdata.attachment = json.getInt("attachment");
		                ForumDataMgr.addMyThread(tdata);
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
