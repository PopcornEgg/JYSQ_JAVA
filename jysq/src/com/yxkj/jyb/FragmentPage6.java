package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.ta.annotation.TAInject;
import com.tencent.qcload.playersdk.util.VideoInfo;
//import com.handmark.pulltorefresh.samples.PullToRefreshListActivity;
//import com.handmark.pulltorefresh.samples.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("InflateParams") public class FragmentPage6 extends Fragment{

	private PullToRefreshListView mPullRefreshListView;
	private MyAdapter mAdapter;
	private List<NewVideoDataMgr.Item> listCurItems;
	boolean isPulling = false;//是否正在拉取
	public static Context sContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	

		sContext = container.getContext();
		View view = inflater.inflate(R.layout.fragment_6, null);	
		mPullRefreshListView = (PullToRefreshListView)view.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);  
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				asynGetList(10, 0);
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(listCurItems != null)
					asynGetList( 10, listCurItems.size());
				else
					asynGetList( 10, 0);
			}
		});
		
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		mAdapter = new MyAdapter(sContext);
		actualListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListenerImpl());
		
		updateListView();
		
		view.findViewById(R.id.help).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Help_Act.show(sContext, 0);
			}  
		});
			
		return view;
	}
	private void setEmptyStr(){
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		uiUtils.setEmptyView(sContext, "网络好像出问题了哦~~~", actualListView);
	}
	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			NewVideoDataMgr.Item item = listCurItems.get(position - 1);
			if(item != null)
			{
				List<VideoInfo> videos = new ArrayList<VideoInfo>();
				VideoInfo v1=new VideoInfo(); 
		        v1.description="标清"; 
		        v1.type=VideoInfo.VideoType.MP4; 
		        v1.url = "http://8731.vod.myqcloud.com/8731_" + item.url + ".f20.mp4";
		        //v1.url="http://4500.vod.myqcloud.com/4500_d754e448e74c11e4ad9e37e079c2b389.f20.mp4?vkey=693D66AF23164CA4741745A2FE9675DCC4493BF10CF724CBE3769CB237121DAB55F3D494AC2C6DB7&ocid=12345"; 
		        videos.add(v1); 
		        
		        VideoInfo v2=new VideoInfo(); 
		        v2.description="手机"; 
		        v2.type=VideoInfo.VideoType.MP4; 
		        v2.url = "http://8731.vod.myqcloud.com/8731_" + item.url + ".f10.mp4"; 
		        videos.add(v2); 
		        
		        VideoInfo v3=new VideoInfo(); 
		        v3.description="高清"; 
		        v3.type=VideoInfo.VideoType.MP4; 
		        v3.url = "http://8731.vod.myqcloud.com/8731_" + item.url + ".f0.mp4"; 
		        videos.add(v3); 
		        
		        String myuid = UserUtils.DataUtils.get("uid");
		        int uid = 0;
		        if(!myuid.isEmpty())
		        	uid = Integer.parseInt(UserUtils.DataUtils.get("uid"));
		        VideoPlayer.play(sContext, uid , videos, 0, new CallBackInterface() {
		            @Override
		            public void exectueMethod(Object p) {
		            	String[] params = p.toString().split(",");
		            	if(params.length == 2){
		            		//int studyTime = Integer.parseInt(params[1].toString()) ;
		            		//int uid = Integer.parseInt(params[0].toString()) ;
		            		//setStudyListStudyTime(uid, studyTime, true);
		            	}
		            }
		        });
		        
		        asynAddCount(item.id);
			}
		}
	}

	@TAInject
	@SuppressLint("DefaultLocale") private void asynGetList( int count, int start)
	{
		if(isPulling)
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.GET_NEWVIDEOLIST);
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				mPullRefreshListView.onRefreshComplete();
				LoadingBox.hideBox();
				isPulling = false;
				if(content.contains("null")){
					mAdapter.setData(listCurItems);
					return;
				}
				try {
					JSONArray jsonArray = new JSONArray(content);
					for(int i=0;i<jsonArray.length();i++){
						JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
						NewVideoDataMgr.Item tdata = new NewVideoDataMgr.Item();
						tdata.id = json.getString("id");
		                tdata.title = json.getString("title");
		                tdata.type = json.getInt("type");
		                tdata.playcount = json.getInt("playcount");
		                tdata.price = json.getInt("price");
		                tdata.img = json.getString("img");
		                tdata.url = json.getString("url");
		                tdata.dateline = json.getLong("dateline");
		                NewVideoDataMgr.addItem(tdata);
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
	@SuppressLint("DefaultLocale") private void asynAddCount( String id){
		NewVideoDataMgr.addPlayCount(id, 1);
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ADD_NEWVIDEOCOUNT);
		params.put("id", id);
		HttpUtils.post(this.getActivity(),  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
			}
			@Override  
	        public void onFailure(String error) {  
	        }  
		});
	}
	@SuppressWarnings("unchecked")
	private void updateListView()
	{
		listCurItems = NewVideoDataMgr.getItems();
		if(listCurItems == null || listCurItems.size() <= 0)
		{
			LoadingBox.showBox( sContext, "加载中...");
			asynGetList(10, 0);	
			return;
		}
		Collections.sort(listCurItems);
		mAdapter.setData(listCurItems);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onResume()
	{
		super.onResume();
		Plugins.onResume(sContext);
		
		listCurItems = NewVideoDataMgr.getItems();
		if(listCurItems == null || listCurItems.size() <= 0){
			return;
		}
		Collections.sort(listCurItems);
		mAdapter.setData(listCurItems);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		Plugins.onPause(sContext);
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
    	private List<NewVideoDataMgr.Item> mListData = null;
    	public MyAdapter(Context _c) {
    		context = _c;
    	}
    	public void setData(List<NewVideoDataMgr.Item> _d){
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
    		NewVideoDataMgr.Item item = mListData.get(position);
    		if(item == null)
    			return null;
    		ViewHolder vh = null;
    		if(convertView == null )
    		{
    			convertView = LayoutInflater.from(context).inflate(R.layout.newvideolist_item,null);
    			if(convertView == null)
    				return null;
    			vh = new ViewHolder();
    			vh.img = (ImageView)convertView.findViewById(R.id.img);
    			vh.title = (TextView)convertView.findViewById(R.id.question);
    			vh.playcount = (TextView)convertView.findViewById(R.id.ansnum);
    			vh.price = (TextView)convertView.findViewById(R.id.price);
    			vh.time = (TextView)convertView.findViewById(R.id.time);
    			vh.type = (TextView)convertView.findViewById(R.id.type);
    			convertView.setTag(vh);
    		}
    		else
    			vh = (ViewHolder)convertView.getTag();
    	
			vh.title.setText(Html.fromHtml(item.title));
			vh.playcount.setText(item.playcount + "次播放 ");
	        //vh.time.setText(GlobalUtility.Func.timeStamp2Recently(item.dateline));
	        vh.time.setText("");
	        //vh.price.setText(Integer.toBinaryString(item.price));
	        vh.price.setText("");
	        vh.type.setText(FragmentPage_bjb_sysitem.getSubjectName(item.type));
	            
			String url = item.img + "/scaling";
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
            vh.img.setTag(position);
            
            return convertView;
    	}
    	
    	public class ViewHolder
        {
        	public TextView title;
        	public TextView time;
        	public TextView playcount;
        	public TextView price;
        	public TextView type;
    		public ImageView img;
        }
    }

}