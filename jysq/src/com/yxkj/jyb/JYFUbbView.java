package com.yxkj.jyb;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxkj.jyb.ForumDataMgr.ForumPostItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.ta.annotation.TAInject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class JYFUbbView extends Activity
{
	public static ForumThreadItem curForumThreadItem = null;
	private Activity mActivity;
	boolean isPulling = false;//是否正在拉取
	private List<ForumPostItem> listForumPostItem = null;
	private WebView mInfo = null;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.jyfubbview);
        mActivity = this;
		
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mActivity.finish();
			}  
		});
		mInfo = (WebView)findViewById(R.id.info);	
		mInfo.getSettings().setDefaultTextEncodingName("UTF -8");
		mInfo.loadDataWithBaseURL("","<div style=\"text-align: center;\"><b style=\"line-height: 1.5;\"><font size=\"4\"><font color=\"#808080\">加载中...</font></font></b></div>", 
				"text/html", "UTF-8","");
        updateListView();
    }  
    @TAInject
	private void asynGetPostList(String tid)
	{
		if(isPulling)
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostListUrl);
		params.put("tid", tid);
		params.put("count", "0");
		params.put("start", "0");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isPulling = false;
				
				if(content.contains("null/r/n")){
					return;
				}
				try {
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
						pitem.realname = json.getString("realname");
						pitem.gender = json.getInt("gender");
						ForumDataMgr.addPost(pitem);
					}
					updateListView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				isPulling = false;
	        }  
		});
	}
	

	private void updateListView(){
		if(curForumThreadItem == null)
			return;
		listForumPostItem = ForumDataMgr.getPosts(curForumThreadItem.tid);
		if( listForumPostItem == null || listForumPostItem.size() == 0 ){
			asynGetPostList(curForumThreadItem.tid);
			return;
		}
		ForumPostItem item = listForumPostItem.get(0);
        //mInfo.loadDataWithBaseURL("", Html.fromHtml(item.message).toString(), "text/html", "UTF-8", "");
		
        mInfo.loadDataWithBaseURL("", getHeadTitle() + item.message, "text/html", "UTF-8", "");
	}
	private String getHeadTitle(){
		//return String.format("<div align=\"center\"><b><font size=\"5\">%s</font></b></div><div align=\"center\"><font color=\"rgb(154, 154, 154)\"><font face=\"SimSun,\" size=\"3\">%s&nbsp;&nbsp;作者：%s</font></font><br></div>", 
		//		curForumThreadItem.subject,curForumThreadItem.author,curForumThreadItem.author);
		return String.format("<div align=\"center\"><b><font size=\"5\">%s</font></b></div></br>", curForumThreadItem.subject);
	}
	public static void show(Context _c, ForumThreadItem item){
		if(_c != null){
			curForumThreadItem = item;
			Intent intent = new Intent(_c, JYFUbbView.class);
			_c.startActivity(intent);
		}
	}
}