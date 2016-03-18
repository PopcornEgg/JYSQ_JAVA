package com.yxkj.jyb;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ModifyUserInfo extends Activity{
	private Activity mActivity;
	private EditText mEditNickName;
	private int mSexId = 0;
	static CallBackInterface scallBack = null;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.modifyuserinfo);
        
        mActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mActivity.finish();
			}  
		});
		
		String gender = UserUtils.DataUtils.get("gender");
		if(!gender.isEmpty())
			mSexId = Integer.parseInt(gender);
        RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup_sex);
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                //获取变更后的选中项的ID
                //int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)mActivity.findViewById(checkedId);
                //更新文本内容，以符合选中项
                //tv.setText("您的性别是：" + rb.getText());
                mSexId = Integer.parseInt(rb.getTag().toString());
            }
        });
        if(mSexId == 0)
        	group.check(R.id.radio2);
        else if(mSexId == 1)
	        group.check(R.id.radio0);
        else if(mSexId == 2)
	        group.check(R.id.radio1);
		
        mEditNickName = (EditText)findViewById(R.id.editname);
        mEditNickName.setText(UserUtils.DataUtils.get("realname"));
		
        Button button=(Button)findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				asynPostLogin( mEditNickName.getText().toString());
			}  
		});   
    }  
	
	@TAInject
	private void asynPostLogin(String nickname)
	{
		boolean isModify = false;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.UserModifyUrl);
		String curnickname = UserUtils.DataUtils.get("realname");
		if(!curnickname.isEmpty() ){
			if(!nickname.isEmpty() && !nickname.equals(curnickname)){
				params.put("realname", nickname);
				isModify = true;
			}
		}
		else{
			if(!nickname.isEmpty()){
				params.put("realname", nickname);
				isModify = true;
			}
		}
		String curgender = UserUtils.DataUtils.get("gender");
		String gender = Integer.toString(mSexId);
		if(!curgender.equals(gender)){
			params.put("gender", gender);
			isModify = true;
		}
		if(!isModify){
			GlobalUtility.Func.ShowToast("并未做任何修改");
			return;
		}
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("uid", UserUtils.DataUtils.get("uid"));
		LoadingBox.showBox( mActivity, "修改中...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				content.replace("\r\n", "");
				if(!content.contains("null")){
					try {
						JSONObject json = new JSONObject(content);
						if(json.has("realname"))
							UserUtils.DataUtils.set("realname", json.getString("realname"));
						if(json.has("gender"))
							UserUtils.DataUtils.set("gender",   json.getString("gender"));
			            if(scallBack != null)
							scallBack.exectueMethod(null);
			            FragmentPage4.sFragmentPage4.setInfo();
			            mActivity.finish();
						GlobalUtility.Func.ShowToast("修改成功");
					} catch (JSONException e) {
						GlobalUtility.Func.ShowToast(e.toString());
					}
				}
				else
					GlobalUtility.Func.ShowToast("修改失败");
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
				GlobalUtility.Func.ShowToast("修改失败");
	        }  
		});
	}
	static public void show(Context _c,CallBackInterface callBack){
		if(_c != null){
			scallBack = callBack;
			Intent intent = new Intent(_c, ModifyUserInfo.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		mActivity = null;
		scallBack = null;
	}
}