package com.yxkj.jyb;

import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class OtherUserInfo extends Activity{

	private  Activity sActivity;
	private TextView username;
	private TextView sex;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.otheruser_info);
        
        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sActivity.finish();
				sActivity = null;
			}  
		});
		
		Intent intent = this.getIntent();
        if(intent.hasExtra("uid")){
        	String uid = intent.getStringExtra("uid");
        	if(!uid.isEmpty()){//发送服务器请求
        		if(!NetWorkStateDetector.isConnectingToInternet())
					return;
        		//asynGetUserInfo(uid);//具体功能还未实现
        	}
        }
        
        username = (TextView)findViewById(R.id.username);
        if(intent.hasExtra("realname")){
        	String realname = intent.getStringExtra("realname");
        	if(!realname.isEmpty())
        		username.setText(realname);
        	else if(intent.hasExtra("username")){
        		username.setText(intent.getStringExtra("username"));
        	}
        }
        else if(intent.hasExtra("username")){
    		username.setText(intent.getStringExtra("username"));
        }
        sex = (TextView)findViewById(R.id.sex);
        String _sex = "0";
        if(intent.hasExtra("gender")){
    		_sex = intent.getStringExtra("gender");
        }
		if(_sex.equals("1"))
			sex.setText("男");
		else if(_sex.equals("2"))
			sex.setText("女");
		else
			sex.setText("保密");
        
		ImageButton avatarBtn = (ImageButton)findViewById(R.id.avatarBtn);
		avatarBtn.setImageResource(GlobalUtility.Func.getHeadIconBySex(Integer.parseInt(_sex)));
    } 
	void setInfo(String username,String gender,String realname){
		
	}
	
	@TAInject
	private boolean isPulling = false;
	private void asynGetUserInfo(String uid)
	{
		if(isPulling)
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserInfoUrl);
		params.put("uid", uid);
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
					JSONObject json = new JSONObject(content);
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
	static public void show(Context _c, String uid, String username, String realname, int gender){
		Intent intent = new Intent(_c, OtherUserInfo.class);
		intent.putExtra("uid", uid);
		intent.putExtra("username", username);
		intent.putExtra("realname", realname);
		intent.putExtra("gender", Integer.toString(gender));
		_c.startActivity(intent);
	}
}