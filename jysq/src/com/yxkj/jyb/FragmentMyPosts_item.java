package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ForumDataMgr.ForumPostItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;
import com.yxkj.jyb.Utils.UserUtils.DataUtils;

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

public final class FragmentMyPosts_item extends Fragment {

	private List<ForumPostItem> curListPosts = null;
    private PullToRefreshListView mPullListView = null;
    private Context mContext = null;
    private MyAdapter mMyAdapter = null;
    
    public static FragmentMyPosts_item newInstance( ) {
        FragmentMyPosts_item fragment = new FragmentMyPosts_item();
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
			if(curListPosts != null && curListPosts.size() > pos ){
				QuestionView.show(mContext, curListPosts.get(pos).tid, false);
			}
		}
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mContext = inflater.getContext();
    	
    	mPullListView = new PullToRefreshListView(getActivity());
    	mPullListView.setMode(Mode.BOTH);
    	mPullListView.setBackgroundResource(R.color.font_color_gray_small2);
    	mPullListView.setDividerDrawable(new ColorDrawable(R.color.font_color_gray_small2));  
    	//mPullListView.setDividerDrawableHeight(1);
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
				if(curListPosts != null)
					asynGetThreadList( 10, curListPosts.size());
				else
					asynGetThreadList( 10, 0);
			}
		});
        
        ListView lv = mPullListView.getRefreshableView();
        mMyAdapter = new MyAdapter(mContext);
        lv.setAdapter(mMyAdapter);
        uiUtils.setEmptyView(mContext, DataUtils.getListEmptyText("您当前没有任何回答哦~~~"), lv);
        
    	updateListAdapter();
    	return mPullListView;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(curListPosts == null)
    		return list;
        for( int i = 0; i < curListPosts.size(); ++i )
        {
        	ForumPostItem item = curListPosts.get(i);
        	Map<String, Object> map = new HashMap<String, Object>();
	        map.put("time",GlobalUtility.Func.timeStamp2Recently(item.dateline));
	        map.put("message",Html.fromHtml(item.message));
	        map.put("img", "");
	        list.add(map);
        }
         
        return list;
    }
    private void updateListAdapter(){
    	
    	curListPosts = ForumDataMgr.getMyPosts();
    	if(curListPosts == null || curListPosts.size() == 0){//拉取
    		LoadingBox.showBox(mContext, "加载中...");
    		asynGetThreadList(10,0);
    		return;
    	}
    	mMyAdapter.setData(curListPosts);
//    	ListAdapter listAdapter = new SimpleAdapter(mContext,getData(), 
//    			R.layout.mypost_item,
//    			new String[]{"img","time","message"},
//    			new int[]{R.id.img, R.id.time, R.id.message });
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
    	private List<ForumPostItem> mListData = null;
    	public MyAdapter(Context _c) {
    		context = _c;
    	}
    	public void setData(List<ForumPostItem> _d){
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
    		ForumPostItem item = mListData.get(position);
    		if(item == null)
    			return null;
    		ViewHolder vh = null;
    		if(convertView == null )
    		{
    			convertView = LayoutInflater.from(context).inflate(R.layout.mypost_item,null);
    			if(convertView == null)
    				return null;
    			vh = new ViewHolder();
    			vh.img = (ImageView)convertView.findViewById(R.id.img);
    			vh.time = (TextView)convertView.findViewById(R.id.time);
    			vh.message = (TextView)convertView.findViewById(R.id.message);
    			convertView.setTag(vh);
    		}
    		else
    			vh = (ViewHolder)convertView.getTag();
    		
    		if(item.attachment == 0){
    			vh.img.setVisibility(View.GONE);
    			//LayoutParams params = vh.img.getLayoutParams();
    		   // params.height=1;  
    			vh.message.setVisibility(View.VISIBLE);
    			vh.message.setText(Html.fromHtml(item.message));
    		}
    		else if(item.attachment == 2){
    			//vh.message.setText("");
    			vh.message.setVisibility(View.GONE);
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
            vh.time.setText(GlobalUtility.Func.timeStamp2Recently(item.dateline));
            return convertView;
    	}
    	
    	public class ViewHolder
        {
        	public TextView time;
        	public TextView message;
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
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserMyPostUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isPulling = false;
				LoadingBox.hideBox();
				mPullListView.onRefreshComplete();
				if(content.contains("null")){
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
						ForumPostItem pitem = new ForumPostItem();
						pitem.tid = json.getString("tid");
						pitem.pid = json.getString("pid");
						pitem.author = json.getString("author");
						pitem.authorid = json.getString("authorid");
						pitem.subject = json.getString("subject");
						pitem.message = GlobalUtility.Func.hexStr2Str(json.getString("message"));
						pitem.dateline = json.getInt("dateline");
						pitem.realname = realname;
						pitem.gender = sex;
						pitem.attachment = json.getInt("attachment");
		                ForumDataMgr.addMyPost(pitem);
					}
					updateListAdapter();
				} catch (JSONException e) {
					e.printStackTrace();
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
