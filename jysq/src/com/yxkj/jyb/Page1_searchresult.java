package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Page1_searchresult extends Activity{
	public static List<ForumThreadItem> listThreads = null;
	public static String sStrSearchfor = "";
	private SimpleAdapter adapter;
	private PullToRefreshListView mPullRefreshListView;
	static Activity sActivity;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.page1_searchresult);
        
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.searchresult_lv);
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListenerImpl());

        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.searchresult_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Page1_searchresult.sActivity.finish();
			}  
		});
        Button btn_search=(Button)findViewById(R.id.searchresult_search);
        btn_search.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Page1_searchresult.sActivity.finish();
			}  
		});  
        Button btn_send=(Button)findViewById(R.id.searchresult_send);
        btn_send.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Page1_searchsend.sStrSearchfor = sStrSearchfor;
				Intent intent = new Intent(Page1_searchresult.sActivity,Page1_searchsend.class);
				startActivity(intent);
			}  
		});  
        
        updateListView();
    }  
	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ForumThreadItem item = listThreads.get(position - 1);
			if(item != null){
				QuestionView.show(FragmentPage1.context, item.tid, false);
			}
		}
	}
	private void updateListView()
	{
		View searchresult_null = findViewById(R.id.searchresult_null); 
		if(listThreads == null || listThreads.size() <= 0)
			searchresult_null.setVisibility(View.VISIBLE);
		else
			searchresult_null.setVisibility(View.INVISIBLE);
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=0;i<listThreads.size();i++){
			ForumThreadItem item = listThreads.get(i);
			
			Map<String, Object> map = new HashMap<String, Object>();
    		map.put("question", item.subject);
    		map.put("ansnum", item.replies + "»Ø´ð");		
    		list.add(map);
		}
		ListView actualListView = mPullRefreshListView.getRefreshableView();
//		adapter = new SimpleAdapter(this, list, R.layout.fragmentpage2_item,
//				new String[]{"time","question","img","head","name","ansnum"},
//				new int[]{R.id.time,R.id.question,R.id.img,R.id.head,R.id.name,R.id.ansnum});
		adapter = new SimpleAdapter(this, list, R.layout.page1_searchresult_item,
				new String[]{"question","img","ansnum"},
				new int[]{R.id.question,R.id.img,R.id.ansnum});	
		actualListView.setAdapter(adapter);
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sActivity = null;
	}
}