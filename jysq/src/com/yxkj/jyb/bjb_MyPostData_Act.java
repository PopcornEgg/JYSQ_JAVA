package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.BJBDataMgr.My;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class bjb_MyPostData_Act extends Activity{
	
	private List< My.PostItem > curItems = null;
	private ListView mListView = null;
	private MyAdapter mMyAdapter = null;
	private String subtid = "";
	private int subtype;
	private String subname = "";
	private Handler mHandler = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bjb_mypostdata_act);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_MyPostData_Act.this.finish();
			}  
		});
		findViewById(R.id.menu).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_MyPostData_PopMenu.show(bjb_MyPostData_Act.this, v, subtid, mHandler);
			}  
		});
		
		mListView = (ListView)findViewById(R.id.list);
		//mListView.setBackgroundResource(R.color.font_color_gray);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    		}
    	});
		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
             public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
            	 menu.add(0, 0, 0, "编辑");
            	 menu.add(0, 1, 0, "删除"); 
             } 
        }); 
		
		mMyAdapter = new MyAdapter(bjb_MyPostData_Act.this);
		mListView.setAdapter(mMyAdapter);
		uiUtils.setEmptyView(bjb_MyPostData_Act.this, "该分组下还没有内容哦~~~", mListView);
		
		Intent intent = this.getIntent();
		subtid = intent.getStringExtra("subtid");
		subtype = intent.getIntExtra("subtype", 0);
		subname = intent.getStringExtra("subname");
		
		TextView title =(TextView)findViewById(R.id.title); 
		title.setText(subname);
		
		mHandler = new Handler() {  
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1: {
						My.ThreadItem item = My.getThreadItem(msg.obj.toString());
						if(item != null)
							asynDelGroup(item.tid);
						break;
					}
					case 2: {
	                	updateListView();
						break;
					}
				}
			}
		};
		updateListView();
    }  
	@Override
	// 长按菜单响应函数 
    public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0:{// 编辑操作
				My.PostItem pitem = curItems.get(info.position);
				bjb_MyPostDataEdit_Act.show(bjb_MyPostData_Act.this, My.getThreadItem(pitem.tid), pitem, null);
				break;
			}
			case 1:{// 删除操作
				My.PostItem pitem = curItems.get(info.position);
				asynDelPost(pitem.tid, pitem.pid);
				break;
			}
			case 2:
				// 删除ALL操作
				break;
			default:
				break;
		}
		return super.onContextItemSelected(item);
    }
	@TAInject
	private void asynGetSubjectData()
	{
		if(subtid.isEmpty())
			return;
		
		LoadingBox.showBox( this, "加载中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_MyPostDataUrl);
		params.put("tid", subtid);
		params.put("uid", UserUtils.DataUtils.get("uid"));
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
	private void updateListView(){
    	
		curItems = My.getPostItems(subtid);
		if(curItems == null || curItems.size() == 0){
    		asynGetSubjectData();
    		return;
    	}
//    	List<String> namels = new ArrayList<String>();
//    	for(int i=0;i<curItems.size();i++){
//    		My.PostItem item = curItems.get(i);
//    		namels.add(String.format("%s", item.message));
//    	}
    	mMyAdapter.setData(curItems);
    	//mListView.setAdapter(new ArrayAdapter<String>(bjb_PostData_Act.this, 
    	//		android.R.layout.simple_expandable_list_item_1,namels));
    }
	public class MyAdapter extends BaseAdapter {

		private Context context = null;
		private List< My.PostItem > mTtems = null;

		public MyAdapter(Context _c) {
			context = _c;
		}
		public void setData(List<My.PostItem> _d){
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
			
			My.PostItem item = mTtems.get(position);
			if(item == null)
				return null;

			Holder ih = null;
			if(convertView == null ){
				
				convertView = LayoutInflater.from(context).inflate(R.layout.bjb_mypostdata_act_item,null);
				if(convertView == null)
					return null;

				ih = new Holder();
				ih.position = position;
				ih.opfull = (ImageButton)convertView.findViewById(R.id.opfull);
				ih.fullinfo = (LinearLayout)convertView.findViewById(R.id.fullinfo);
				ih.tvs[0] = (TextView)convertView.findViewById(R.id.title);
				ih.tvs[1] = (TextView)convertView.findViewById(R.id.content);
				ih.tvs[2] = (TextView)convertView.findViewById(R.id.mem);
				ih.ivs[0] = (ImageView)convertView.findViewById(R.id.ivtitle);
				ih.ivs[1] = (ImageView)convertView.findViewById(R.id.ivmem);
				ih.ivs[2] = (ImageView)convertView.findViewById(R.id.ivcontent);
				ih.fulls[0] = convertView.findViewById(R.id.full_1);
				ih.fulls[1] = convertView.findViewById(R.id.full_2);
				
				ih.opfull.setTag(ih);
				ih.opfull.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						Holder ih = (Holder)v.getTag();
						opFullInfo(ih);
					} 
				});
				opFullInfo(ih);
				
				convertView.setTag(ih);
			}
			else
				ih = (Holder)convertView.getTag();
			
			String[] ps = item.message.split("\\[hr\\]");
			for(int i=0;i<ih.tvs.length;i++){
				if(i < ps.length){
					setImageViewPost(ps[i], ih.tvs[i], ih.ivs[i]);
				}
			}
			if(ps.length == 2){
				ih.fulls[1].setVisibility(View.INVISIBLE);
				LayoutParams params = ih.fulls[1].getLayoutParams();
			    params.height=1;
			    ih.fulls[1].setLayoutParams(params);
			}
			else if(ps.length == 3){
				ih.fulls[1].setVisibility(View.VISIBLE);
				LayoutParams params = ih.fulls[1].getLayoutParams();
			    params.height=LayoutParams.WRAP_CONTENT;  
			    ih.fulls[1].setLayoutParams(params);
			}
	        return convertView;
		}
		private void setImageViewPost(String txt, TextView tv, ImageView iv){
	    	
			if(txt.contains("jyb_fid_")){
				tv.setText("");
				iv.setVisibility(View.VISIBLE);
				String url = txt + "/scaling";
				ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
				if(imginfo != null){
					LayoutParams params = iv.getLayoutParams();  
					params.height= GlobalUtility.Func.dip2px(80.0f); 
					iv.setImageBitmap(imginfo.bitmap);
				}
				else{
					iv.setImageResource(R.color.font_color_gray);
					ImageDataMgr.sDownLoadMgr.addLoad(url, iv, 2, mHandler);
				}
			}else{
				tv.setText(Html.fromHtml(txt));
				LayoutParams params = iv.getLayoutParams();
			    params.height=1;  
				iv.setVisibility(View.INVISIBLE);
			}
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
		
		public class Holder
	    {
			public int position = 0;
			public ImageButton opfull;
			public LinearLayout fullinfo;
			public View[] fulls = new View[2];
			public TextView[]  tvs = new TextView[3];
			public ImageView[] ivs = new ImageView[3];
	    }
	}
	private void asynDelGroup(String tid)
	{
		if(tid.isEmpty())
			return;
		
		LoadingBox.showBox( this, "删除中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_DelMyGroupPostDataUrl);
		params.put("tid", tid);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("username", UserUtils.DataUtils.get("username"));
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("删除成功");
				}
				else{
					GlobalUtility.Func.ShowToast("删除失败");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
		My.delThreadItem(tid);
		bjb_MyPostData_Act.this.finish();
	}
	private void asynDelPost(String tid, String pid )
	{
		if( pid.isEmpty())
			return;
		
		LoadingBox.showBox( this, "删除中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_DelMyPostDataUrl);
		params.put("pid", pid);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("username", UserUtils.DataUtils.get("username"));
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("删除成功");
				}
				else{
					GlobalUtility.Func.ShowToast("删除失败");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
		
		if(My.delPostItem( pid)){
			bjb_MyPostData_Act.this.finish();
		}
		else{
			curItems = My.getPostItems(tid);
			if(curItems != null){
				List<String> namels = new ArrayList<String>();
		    	for(int i=0;i<curItems.size();i++){
		    		My.PostItem item = curItems.get(i);
		    		namels.add(String.format("%s", item.message));
		    	}
		    	mMyAdapter.setData(curItems);
	    	}
		}
	}
	static public void show(Context _c,String subtid ,int subtype, String subname){
		if(_c != null){
			Intent intent = new Intent(_c, bjb_MyPostData_Act.class);
			intent.putExtra("subtid", subtid);
			intent.putExtra("subtype", subtype);
			intent.putExtra("subname", subname);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		curItems = My.getPostItems(subtid);
    	mMyAdapter.setData(curItems);
    	//mListView.setAdapter(new ArrayAdapter<String>(bjb_PostData_Act.this, 
    	//		android.R.layout.simple_expandable_list_item_1,namels));
	}
}