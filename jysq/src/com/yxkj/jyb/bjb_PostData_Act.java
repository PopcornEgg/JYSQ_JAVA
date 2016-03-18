package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.BJBDataMgr.My;
import com.yxkj.jyb.BJBDataMgr.Sys;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class bjb_PostData_Act extends Activity{
	
	private String curName = "";
	private String curTitle = "";
	private List< Sys.PostItem > curItems = null;
	private ListView mListView = null;
	private MyAdapter mMyAdapter = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bjb_postdata_act);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_PostData_Act.this.finish();
			}  
		});
		findViewById(R.id.getall).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Sys.ThreadItem item = Sys.getThreadItem(curName);
				if(item != null)
					asynStoreGroupPostData(FragmentPage_bjb_sysitem.getCurSubType(),item.tid,item.name);
			}  
		});
		mListView = (ListView)findViewById(R.id.list);
		//mListView.setBackgroundResource(R.color.font_color_gray);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    		}
    	});
		mMyAdapter = new MyAdapter(bjb_PostData_Act.this);
		mListView.setAdapter(mMyAdapter);
		
		Intent intent = this.getIntent();
		curName = intent.getStringExtra("name");
		curTitle = intent.getStringExtra("title");
		
		TextView title =(TextView)findViewById(R.id.title); 
		title.setText(curTitle);
		
		updateListView();
    }  
	@TAInject
	private void asynGetPostData()
	{
		if(curName.isEmpty())
			return;
		
		LoadingBox.showBox( this, "加载中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_PostDataUrl);
		params.put("tid", curName);
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(!content.contains("null\r\n")){
					try {
						JSONArray jsonArray = new JSONArray(content);
						for(int i=0;i<jsonArray.length();i++){
							JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
							Sys.PostItem item = new Sys.PostItem();
							item.tid = json.getString("tid");
							item.pid = json.getString("pid");
							item.subject = curTitle;//json.getString("subject");
							item.message = GlobalUtility.Func.hexStr2Str(json.getString("message"));
			                item.dateline = json.getLong("dateline");
			                item.first = json.getInt("first");
			                BJBDataMgr.Sys.addPostItem(item);
						}
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
	private void updateListView(){
    	
		curItems = BJBDataMgr.Sys.getPostItems(curName);
		if(curItems == null || curItems.size() == 0){
			asynGetPostData();
    		return;
    	}
    	List<String> namels = new ArrayList<String>();
    	for(int i=0;i<curItems.size();i++){
    		Sys.PostItem item = curItems.get(i);
    		namels.add(String.format("%s", item.message));
    	}
    	mMyAdapter.setData(curItems);
    	//mListView.setAdapter(new ArrayAdapter<String>(bjb_PostData_Act.this, 
    	//		android.R.layout.simple_expandable_list_item_1,namels));
    }
	public class MyAdapter extends BaseAdapter {

		private Context context = null;
		private List< Sys.PostItem > mTtems = null;

		public MyAdapter(Context _c) {
			context = _c;
		}
		public void setData(List<Sys.PostItem> _d){
			mTtems = _d;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mTtems == null)
				return 0;

			return mTtems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTtems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if( context == null || mTtems == null)
				return null;
			
			Sys.PostItem item = mTtems.get(position);
			if(item == null)
				return null;

			Holder ih = null;
			if(convertView == null ){
				
				convertView = LayoutInflater.from(context).inflate(R.layout.bjb_postdata_act_item,null);
				if(convertView == null)
					return null;

				ih = new Holder();
				ih.opfull = (ImageButton)convertView.findViewById(R.id.opfull);
				ih.opget = (Button)convertView.findViewById(R.id.opget);
				ih.fullinfo = (LinearLayout)convertView.findViewById(R.id.fullinfo);
				ih.title = (TextView)convertView.findViewById(R.id.title);
				ih.content = (TextView)convertView.findViewById(R.id.content);
				ih.mem = (TextView)convertView.findViewById(R.id.mem);
				
				ih.opfull.setTag(ih);
				ih.opfull.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						Holder ih = (Holder)v.getTag();
						opFullInfo(ih);
					} 
				});
				opFullInfo(ih);
				
				ih.opget.setTag(position);
				ih.opget.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						opGet( Integer.parseInt(v.getTag().toString()) );
					} 
				});
				
				convertView.setTag(ih);
			}
			else
				ih = (Holder)convertView.getTag();
			
			String[] ps = item.message.split("\\[hr\\]");
			if(ps.length == 3){
				ih.title.setText(Html.fromHtml(ps[0]));
				ih.content.setText(Html.fromHtml(ps[1]));
				ih.mem.setText(Html.fromHtml(ps[2]));
			}
			
	        return convertView;
		}
		private void  opFullInfo(Holder ih){
			
			if(ih.fullinfo.getVisibility() == View.INVISIBLE){
				ih.fullinfo.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				ih.fullinfo.setLayoutParams(llp);
				ih.opfull.setImageResource(R.drawable.btn_open1);
			}else{
				ih.fullinfo.setVisibility(View.INVISIBLE);
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(0,0);
				ih.fullinfo.setLayoutParams(llp);
				ih.opfull.setImageResource(R.drawable.btn_open2);
			}
		}
		private void  opGet(int tag){
			Sys.PostItem item = mTtems.get(tag);
			if(item == null)
				return ;
			asynStorePostData(FragmentPage_bjb_sysitem.getCurSubType(),
					item.tid,item.subject,item.pid,item.message);
		}
		
		class Holder
	    {
			public ImageButton opfull;
			public Button opget;
			public LinearLayout fullinfo;
	    	public TextView title;
	    	public TextView content;
	    	public TextView mem;
	    }
	}
	private void asynStorePostData(int _subtype, String _tid, String _subname, String _pid, String _msg)
	{
		if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("您尚未登录！");
			return;
		}
		
		if(!My.PostItem.checkNextIdx(_pid)){
			GlobalUtility.Func.ShowToast("已加入了我的知识库");
			return;
		}
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_PostMyPostDataUrl);
		
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		
		params.put("subtype", Integer.toString(_subtype));
		params.put("subname", _subname);
		params.put("message", _msg);
		
		String _realtid = My.ThreadItem.getStrNextIdx(_tid);
		params.put("tid", _realtid);
		params.put("fromtid", _tid);
		
		String _realpid = My.PostItem.getStrNextIdx();
		params.put("pid", _realpid);
		params.put("frompid", _pid);
		
		//怎强体验的代码
		if(My.getThreadItem(_realtid) == null){
			My.ThreadItem ttiem = new My.ThreadItem();
			ttiem.subtype = _subtype;
        	ttiem.tid = _realtid;
        	ttiem.subname = _subname;
        	ttiem.dateline = System.currentTimeMillis();
        	ttiem.count = 0;
        	ttiem.from = _tid;
			My.addThreadItem(ttiem);
		}
		My.PostItem pitem = new My.PostItem();
		pitem.tid = _realpid;
    	pitem.pid = _realpid;
    	pitem.message = _msg;
    	pitem.dateline = System.currentTimeMillis();
    	pitem.from = _pid;
		My.addPostItem(pitem);
		
		LoadingBox.showBox( this, "加载中...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("加入我的知识库成功");
				}
				else{
					GlobalUtility.Func.ShowToast("已加入了我的知识库");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	private void asynStoreGroupPostData(int _subtype, String _tid, String _subname )
	{
		if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("您尚未登录！");
			return;
		}
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_PostMyGroupPostDataUrl);
		
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		
		String _realtid = My.ThreadItem.getStrNextIdx(_tid);
		params.put("subtype", Integer.toString(_subtype));
		params.put("subname", _subname);
		params.put("tid", _realtid);
		params.put("fromtid", _tid);
		
		if(My.getThreadItem(_realtid) == null){
			My.ThreadItem ttiem = new My.ThreadItem();
			ttiem.subtype = _subtype;
        	ttiem.tid = _realtid;
        	ttiem.subname = _subname;
        	ttiem.dateline = System.currentTimeMillis();
        	ttiem.count = 0;
        	ttiem.from = _tid;
			My.addThreadItem(ttiem);
		}
		
		StringBuilder frompids = new StringBuilder ( "" );
		StringBuilder msgs = new StringBuilder ( "" );
		StringBuilder realpids = new StringBuilder ( "" );
		List< Sys.PostItem > items = Sys.getPostItems(_tid);
		int counter = 0;
		for(int i=0;i<items.size();i++){
			Sys.PostItem item = items.get(i);
			if(!My.PostItem.checkNextIdx(item.pid))
				continue;
			String _realpid = My.PostItem.getStrNextIdx();
			if(counter == 0){
				frompids.append(item.pid);
				msgs.append(item.message);
				realpids.append(_realpid);
			}
			else{
				frompids.append("|" + item.pid);
				msgs.append("#*" + item.message);
				realpids.append("|" + _realpid);
			}
			My.PostItem pitem = new My.PostItem();
			pitem.tid = _realtid;
	    	pitem.pid = _realpid;
	    	pitem.message = item.message;
	    	pitem.dateline = System.currentTimeMillis();
	    	pitem.from = item.pid;
			My.addPostItem(pitem);
			counter++;
		}
		params.put("frompids", frompids.toString());
		params.put("msgs", msgs.toString());
		params.put("pids", realpids.toString());
		
		if(counter == 0){
			GlobalUtility.Func.ShowToast("已加入了我的知识库");
			return;
		}
		
		LoadingBox.showBox( this, "加载中...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("全部加入我的知识库成功");
				}
				else{
					GlobalUtility.Func.ShowToast("已加入了我的知识库");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	static public void show(Context _c,String name ,String title){
		if(_c != null){
			Intent intent = new Intent(_c, bjb_PostData_Act.class);
			intent.putExtra("name", name);
			intent.putExtra("title", title);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
}