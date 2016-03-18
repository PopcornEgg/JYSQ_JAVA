package com.yxkj.jyb;

import com.yxkj.jyb.Utils.UserUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserInfo extends Activity{
	static Activity sActivity;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.user_info);
        
        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sActivity.finish();
			}  
		});
		Button btn_logout=(Button)findViewById(R.id.logout);
		btn_logout.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				clearUserData();
				GlobalUtility.Func.ShowToast("已退出");
				sActivity.finish();
			}  
		});
		
		findViewById(R.id.rl_2).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				ModifyUserInfo.show(sActivity, new CallBackInterface() {
		            @Override
		            public void exectueMethod(Object p) {
		            	setInfo();
		            }
	            });
			}  
		});
		findViewById(R.id.rl_3).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				ModifyUserInfo.show(sActivity, new CallBackInterface() {
		            @Override
		            public void exectueMethod(Object p) {
		            	setInfo();
		            }
	            });
			}  
		});
		
		setInfo();
    }  
	private void setInfo()
	{
		TextView username = (TextView)findViewById(R.id.username);
		username.setText(UserUtils.DataUtils.get("username"));
		String _sex = UserUtils.DataUtils.get("gender");
		if(_sex.equals("1"))
			_sex = "男";
		else if(_sex.equals("2"))
			_sex = "女";
		else
			_sex = "保密";
		TextView txtsex = (TextView)findViewById(R.id.sex);
		txtsex.setText(_sex);
		TextView txtnickName = (TextView)findViewById(R.id.nickname);
		txtnickName.setText(UserUtils.DataUtils.get("realname"));
		
		int sexid = 0;
		String _strgender = UserUtils.DataUtils.get("gender");
		if(!_strgender.isEmpty())
			sexid = Integer.parseInt(_strgender);
		ImageButton avatarBtn = (ImageButton)findViewById(R.id.avatarBtn);
		avatarBtn.setImageResource(GlobalUtility.Func.getHeadIconBySex(sexid));
		
		TextView credits = (TextView)findViewById(R.id.credits);
		credits.setText(UserUtils.DataUtils.get("credits"));
	}
	//登出清除用户数据
	static private void clearUserData(){
		UserUtils.DataUtils.clearData();
		BJBDataMgr.My.clear();
		if(FragmentPage4.sFragmentPage4 != null)
			FragmentPage4.sFragmentPage4.setInfo();
	}
}