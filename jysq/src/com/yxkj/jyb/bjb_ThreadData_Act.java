package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.BJBDataMgr.Sys.ThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class bjb_ThreadData_Act extends Activity{
	
	private String curName = "";
	private List< ThreadItem > curItems = null;
	private ListView mListView = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bjb_threaddata_act);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_ThreadData_Act.this.finish();
			}  
		});
		mListView = (ListView)findViewById(R.id.list);
		mListView.setDivider(new ColorDrawable(R.color.font_color_gray));
		mListView.setDividerHeight(1);  
		//mListView.setBackgroundResource(R.color.font_color_gray);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    			if(position >=0 && position < curItems.size()){
    				ThreadItem item = curItems.get(position);
    				bjb_PostData_Act.show(bjb_ThreadData_Act.this,item.tid, item.name);
    			}
    		}
    	});
		
		Intent intent = this.getIntent();
		TextView title =(TextView)findViewById(R.id.title); 
		title.setText(intent.getStringExtra("title"));
		curName = intent.getStringExtra("name");
		
		updateListView();
    }  
	@TAInject
	private void asynGetSubjectData()
	{
		if(curName.isEmpty())
			return;
		
		LoadingBox.showBox( this, "加载中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_ThreadDataUrl);
		params.put("fid", curName);
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
							ThreadItem item = new ThreadItem();
							item.fid = json.getString("fid");
							item.tid = json.getString("tid");
							item.name = json.getString("name");
			                item.dateline = json.getLong("dateline");
			                item.replies = json.getInt("replies");
			                BJBDataMgr.Sys.addThreadItem(item);
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
    	
		curItems = BJBDataMgr.Sys.getThreadItems(curName);
		if(curItems == null || curItems.size() == 0){
    		asynGetSubjectData();
    		return;
    	}
    	List<String> namels = new ArrayList<String>();
    	for(int i=0;i<curItems.size();i++){
    		ThreadItem item = curItems.get(i);
    		namels.add(String.format("%s", item.name));
    	}
    	mListView.setAdapter(new ArrayAdapter<String>(bjb_ThreadData_Act.this, 
    			R.layout.fragmentpage_bjb_sysitem,namels));
    }
	static public void show(Context _c,String name ,String title){
		if(_c != null){
			Intent intent = new Intent(_c, bjb_ThreadData_Act.class);
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