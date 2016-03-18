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
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class UserRegister extends Activity{
	
	static Activity sActivity;
	static EditText seditname;
	static EditText seditpsw;
	static EditText seditpswre;
	static CallBackInterface scallBack = null;
	
	@SuppressWarnings("serial")
	public static final Map<String, String> mapMsg = new HashMap<String, String>(){
	{
		put("1","注册成功,已自动登录");
		put("0","未知错误");
		put("-1","用户名不合法");
		put("-2","包含要允许注册的词语");
		put("-3","用户名已经存在");
		put("-4","邮箱格式有误");
		put("-5","邮箱不允许注册");
		put("-6","该邮箱已经被注册");
	}};
	
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.user_register);
        
        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				UserRegister.sActivity.finish();
			}  
		});
		
		seditname = (EditText)findViewById(R.id.editname);
		seditname.setText("");
		seditpsw = (EditText)findViewById(R.id.editpsw);
		seditpsw.setText("");
		seditpswre = (EditText)findViewById(R.id.editpswre);
		seditpswre.setText("");
		
        Button button=(Button)findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				String strname = seditname.getText().toString();
				if(strname.isEmpty() || strname.length() < 6)
				{
					Toast.makeText(sActivity, "用户名不能少于6个字符", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String strpsw = seditpsw.getText().toString();
				if(strpsw.isEmpty() || strpsw.length() < 6)
				{
					Toast.makeText(sActivity, "密码不能少于6个字符", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String strpswre = seditpswre.getText().toString();
				if(!strpswre.equals(strpsw))
				{
					Toast.makeText(sActivity, "两次输入的密码不同", Toast.LENGTH_SHORT).show();
					return;
				}
				
				TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
				if(tm != null){
					asynPostRegister(strname, strpsw, tm.getDeviceId() + "@jyb.com");
				}
				else{
					Toast.makeText(sActivity, "当前设备不可用！", Toast.LENGTH_SHORT).show();
				}
			}  
		});   
    }  
	@TAInject
	private void asynPostRegister(String strname, String strpsw, String stremail)
	{
		LoadingBox.showBox( sActivity, "注册中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserRegisterUrl);
		params.put("username", strname);
		params.put("password", strpsw);
		params.put("email", stremail);
		params.put("did", "");//设备id
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
				            if(scallBack != null)
								scallBack.exectueMethod(null);
							sActivity.finish();
						}
						GlobalUtility.Func.ShowToast(mapMsg.get(id));
						
					} catch (JSONException e) {
						e.printStackTrace();
						GlobalUtility.Func.ShowToast(mapMsg.get("0"));
					}
				}
				else
					GlobalUtility.Func.ShowToast(mapMsg.get("0"));
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
	static public void showRegister(Context _c,CallBackInterface callBack){
		if(_c != null){
			scallBack = callBack;
			Intent intent = new Intent(_c, UserRegister.class);
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