package com.yxkj.jyb;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import com.ta.annotation.TAInject;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class UserLogin extends Activity{
	static Activity sActivity;
	static EditText seditname;
	static EditText seditpsw;
	static CallBackInterface scallBack = null;
	
	@SuppressWarnings("serial")
	public static final Map<String, String> mapLoginMsg = new HashMap<String, String>(){
	{
		put("1","登录成功");
		put("0","未知错误");
		put("-1","用户不存在");
		put("-2","密码错误");
	}};
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.user_login);
        
        sActivity = this;
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sActivity.finish();
			}  
		});
		
		findViewById(R.id.register).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				UserRegister.showRegister(sActivity, new CallBackInterface() {
		            @Override
		            public void exectueMethod(Object p) {
		            	if(scallBack != null)
							scallBack.exectueMethod(p);
		            	sActivity.finish();
		            }
		        });
			}  
		});
		
		seditname = (EditText)findViewById(R.id.editname);
		seditname.setText("");
		seditpsw = (EditText)findViewById(R.id.editpsw);
		seditpsw.setText("");
		
        findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String strname = seditname.getText().toString();
				if(strname.isEmpty())
				{
					GlobalUtility.Func.ShowToast("用户名不能为空");
					return;
				}
				String strpsw = seditpsw.getText().toString();
				if(strpsw.isEmpty())
				{
					GlobalUtility.Func.ShowToast("密码不能为空");
					return;
				}
				asynPostLogin( strname , strpsw);
			}  
		});   
    }  
	
	@TAInject
	private void asynPostLogin(String strname, String strpsw)
	{
		LoadingBox.showBox( sActivity, "登录中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserLoginUrl);
		params.put("username", strname);
		params.put("password", strpsw);
		params.put("did", "");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(!content.contains("null\r\n")){
					try {
						JSONObject json = new JSONObject(content);
						String id = json.getString("id");
						if(id.equals("1")){
							UserUtils.DataUtils.initDataByJson(json);
							UserUtils.bjbUtils.asynMyThreadData(sActivity, 0, 0);
				            if(scallBack != null)
								scallBack.exectueMethod(null);
							sActivity.finish();
						}
						GlobalUtility.Func.ShowToast(mapLoginMsg.get(id));
						
					} catch (JSONException e) {
						e.printStackTrace();
						GlobalUtility.Func.ShowToast(mapLoginMsg.get("0"));
					}
				}
				else
					GlobalUtility.Func.ShowToast(mapLoginMsg.get("0"));
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	static public void showLogin(Context _c,CallBackInterface callBack){
		if(_c != null){
			scallBack = callBack;
			Intent intent = new Intent(_c, UserLogin.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sActivity = null;
		scallBack = null;
	}
}