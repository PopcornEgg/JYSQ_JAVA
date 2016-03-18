package com.yxkj.jyb;

import com.yxkj.jyb.BJBDataMgr.My;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class bjb_MyPostDataEdit_Act extends Activity{
	static CallBackInterface scallBack = null;
	static My.PostItem curPostItem = null;
	static My.ThreadItem curThreadItem = null;
	
	private Handler mHandler = null;
	private EditText mtitle ;
	private EditText mcontent ;
	private EditText mmem ;
	private Button mgroup;
	private String curSelectedTid = "";
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bjb_mypostdataedit_act);
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_MyPostDataEdit_Act.this.finish();
			}  
		});
		
		findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(curPostItem != null)
					asynUpdatePostData();
				else
					asynCreatePostData();
			}  
		});
		
		mgroup = (Button)findViewById(R.id.group);
		mgroup.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_MyChooseGroup_Dlg.show(bjb_MyPostDataEdit_Act.this, mHandler, 1);
			}  
		});
		
		curSelectedTid = curThreadItem.tid;
		mgroup.setText(curThreadItem.subname);
		
		mtitle = (EditText)findViewById(R.id.edtitle);
		mcontent = (EditText)findViewById(R.id.content);
		mmem = (EditText)findViewById(R.id.mem);
		
		if(curPostItem != null){
			String[] ps = curPostItem.message.split("\\[hr\\]");
			if(ps.length == 3){
				mtitle.setText(Html.fromHtml(ps[0]));
				mcontent.setText(Html.fromHtml(ps[1]));
				mmem.setText(Html.fromHtml(ps[2]));
			}
		}
		else{
			mtitle.setText("");
			mcontent.setText("");
			mmem.setText("");
		}
		
		mHandler = new Handler() {  
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1: {
						curSelectedTid = msg.obj.toString(); 
						mgroup.setText(My.getThreadItem(curSelectedTid).subname);
					}
				}
			}
		};
    }  
	private String getEditedText( ){
		StringBuilder msgs = new StringBuilder ( "" );
		msgs.append(Html.toHtml(mtitle.getText() )+ "[hr]");
		msgs.append(Html.toHtml(mcontent.getText()) + "[hr]");
		msgs.append(Html.toHtml(mmem.getText()));
//		msgs.append(mtitle.getText()+ "[hr]");
//		msgs.append(mcontent.getText() + "[hr]");
//		msgs.append(mmem.getText());
		return msgs.toString();
	}
	private void asynUpdatePostData()
	{
		if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("ÄúÉÐÎ´µÇÂ¼£¡");
			return;
		}
		
		curThreadItem = My.getThreadItem(curSelectedTid);
		if(curThreadItem == null){
			return;
		}
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_UpdateMyPostDataUrl);
		
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		String newmsgs = getEditedText();
		params.put("frompid", "0");
		params.put("msgs", newmsgs);
		params.put("pid", curPostItem.pid);
		params.put("oldtid",  curPostItem.tid);
		params.put("newtid",curSelectedTid);
		
		if(!curSelectedTid.equals(curPostItem.tid)){
			My.delPostItem(curPostItem.tid);
			curPostItem.tid = curSelectedTid;
			curPostItem.from = "0";
			curPostItem.message = newmsgs;
			curPostItem.dateline = System.currentTimeMillis();
			My.addPostItem(curPostItem);
		}
		else{
			curPostItem.tid = curSelectedTid;
			curPostItem.from = "0";
			curPostItem.message = newmsgs;
			curPostItem.dateline = System.currentTimeMillis();
		}
		
		LoadingBox.showBox( this, "ÐÞ¸ÄÖÐ...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("ÐÞ¸Ä³É¹¦");
					bjb_MyPostDataEdit_Act.this.finish();
				}
				else{
					GlobalUtility.Func.ShowToast("ÐÞ¸ÄÊ§°Ü");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	private void asynCreatePostData()
	{
		if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("ÄúÉÐÎ´µÇÂ¼£¡");
			return;
		}

		curThreadItem = My.getThreadItem(curSelectedTid);
		if(curThreadItem == null){
			return;
		}
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_CreateMyPostDataUrl);
		
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		String newmsgs = getEditedText();
		curPostItem = new My.PostItem();
		curPostItem.tid = curSelectedTid;
		curPostItem.pid = My.PostItem.getStrNextIdx();
		curPostItem.message = newmsgs;
		curPostItem.from = "0";
		curPostItem.dateline = System.currentTimeMillis();
		My.addPostItem(curPostItem);
		
		params.put("frompid", curPostItem.from);
		params.put("message", curPostItem.message);
		params.put("pid", curPostItem.pid);
		params.put("tid",  curPostItem.tid);
		params.put("subtype", Integer.toString(curThreadItem.subtype));
		params.put("subname", curThreadItem.subname);
		
		LoadingBox.showBox( this, "ÐÞ¸ÄÖÐ...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("ÐÞ¸Ä³É¹¦");
					bjb_MyPostDataEdit_Act.this.finish();
				}
				else{
					GlobalUtility.Func.ShowToast("ÐÞ¸ÄÊ§°Ü");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	static public void show(Context _c, My.ThreadItem ti, My.PostItem pi, CallBackInterface callBack){
		if(_c != null){
			curPostItem = pi;
			curThreadItem = ti;
			scallBack = callBack;
			Intent intent = new Intent(_c, bjb_MyPostDataEdit_Act.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		scallBack = null;
		curSelectedTid = "";
		curPostItem = null;
		curThreadItem = null;
	}
}