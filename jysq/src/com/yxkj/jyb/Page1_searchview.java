package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.ta.annotation.TAInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Page1_searchview extends Activity{
	static Activity sActivity;
	static EditText sedit;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.page1_searchview);
        
        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.searchview_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				GlobalUtility.Func.hideSoftInput(sActivity, sedit);
				sActivity.finish();
			}  
		});
		
		sedit = (EditText)findViewById(R.id.searchview_editText);
		sedit.setText("");
        GlobalUtility.Func.showSoftInput(this);
        Button button=(Button)findViewById(R.id.searchview_ok);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!NetWorkStateDetector.isConnectingToInternet())
				{
					GlobalUtility.Func.ShowToast("当前无网络");
					return;
				}
				String str = sedit.getText().toString();
				if(str.isEmpty())
				{
					Toast.makeText(sActivity, "搜索不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				asynGetSearchThreads(ThreadsFilter.getAllFids(), str , 10);
		        GlobalUtility.Func.hideSoftInput(sActivity, sedit);
			}  
		});   
    }  
	@TAInject
	private void asynGetSearchThreads(String fid, String tag, int count)
	{
		LoadingBox.showBox( sActivity, "搜索中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumSearchThreadUrl);
		params.put("fid", fid);
		params.put("tag", tag);
		params.put("count", Integer.toString(count));
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				try {
					List<ForumThreadItem> listThreads = new ArrayList<ForumThreadItem>();
					if(!content.contains("null\r\n")){
						JSONArray jsonArray = null;
						jsonArray = new JSONArray(content);
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
			                listThreads.add(tdata);
						}
					}
					if(listThreads.size() > 0 )
						Collections.sort(listThreads);
					enterSearchResult(listThreads);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
	            // 上传失败后要做到工作  
				//error.
			//	Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
				LoadingBox.hideBox();
	        }  
		});
	}
	private void enterSearchResult(List<ForumThreadItem> l){
		Page1_searchresult.listThreads= l; 
		Page1_searchresult.sStrSearchfor= sedit.getText().toString();
		Intent intent=new Intent(FragmentPage1.context,Page1_searchresult.class);
		startActivity(intent);
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sActivity = null;
	}
}