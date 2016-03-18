
package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class bjb_MyCreateGroup_Act extends Activity{
	static Handler mHandler = null;
	static int mWhat = 1;
	private EditText msubname ;
	private Integer curSelectedIdx = 0;
	private List<Button> subbtns = new ArrayList<Button> ();
	private String realtid = "";
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.bjb_mycreategroup_act);
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				bjb_MyCreateGroup_Act.this.finish();
			}  
		});
		
		findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				asynUpdatePostData();
			}  
		});
		
		OnClickListener oncl = new OnClickListener(){
			public void onClick(View v) {
				updateBtnsState(Integer.parseInt(v.getTag().toString()), false);
			}  
		};
		
		subbtns.add((Button)findViewById(R.id.Button01));
		subbtns.add((Button)findViewById(R.id.Button02));
		subbtns.add((Button)findViewById(R.id.Button03));
		subbtns.add((Button)findViewById(R.id.Button04));
		subbtns.add((Button)findViewById(R.id.Button05));
		subbtns.add((Button)findViewById(R.id.Button06));
		subbtns.add((Button)findViewById(R.id.Button07));
		subbtns.add((Button)findViewById(R.id.Button08));
		subbtns.add((Button)findViewById(R.id.Button09));
		subbtns.add((Button)findViewById(R.id.Button10));
		for(int i=0;i<subbtns.size();i++){
			subbtns.get(i).setOnClickListener(oncl);
		}
		msubname = (EditText)findViewById(R.id.subname);
    }  
	private void updateBtnsState(int tag, boolean v){
		curSelectedIdx = tag;
		for(int k=0;k<subbtns.size();k++)
		{
			Button _btn = subbtns.get(k);
			_btn.setEnabled(tag == k ? v : true);
		}
	}
	private void asynUpdatePostData()
	{
		if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("您尚未登录！");
			return;
		}
		String _subname = msubname.getText().toString();
		if(_subname.isEmpty()){
			GlobalUtility.Func.ShowToast("请输入组名");
			return;
		}
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_CreateMyCroupDataUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		realtid = My.ThreadItem.getStrNextIdx("-1");
		params.put("tid", realtid);
		params.put("fromtid", "0");
		params.put("subtype", Integer.toString(curSelectedIdx));
		params.put("subname", _subname);
		
		My.ThreadItem ttiem = new My.ThreadItem();
		ttiem.subtype = curSelectedIdx;
    	ttiem.tid = realtid;
    	ttiem.subname = _subname;
    	ttiem.dateline = System.currentTimeMillis();
    	ttiem.count = 0;
    	ttiem.from = "0";
		My.addThreadItem(ttiem);
		
		LoadingBox.showBox( this, "创建中...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("创建成功");
					if(mHandler != null){
    					Message msg = new Message();
    					msg.what = mWhat;
    					msg.obj = realtid;
    					mHandler.sendMessage(msg);
    				}
					bjb_MyCreateGroup_Act.this.finish();
				}
				else{
					GlobalUtility.Func.ShowToast("创建失败");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	static public void show(Context _c, Handler _hl, int _w){
		if(_c != null){
			mHandler = _hl;
			mWhat = _w;
			Intent intent = new Intent(_c, bjb_MyCreateGroup_Act.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		mHandler = null;
		realtid = "";
	}
}