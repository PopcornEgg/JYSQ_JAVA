package com.yxkj.jyb;

import com.yxkj.jyb.Utils.QQUtils;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.version.VersionUpdateDlg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentPage4 extends Fragment{
	public static FragmentPage4 sFragmentPage4 = null;
	public static Context context;
	static TextView stextname;
	static TextView stextcoin;
	private ImageButton mAvatarBtn;
	
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		context = container.getContext();
		View view = inflater.inflate(R.layout.fragment_4, null);
		sFragmentPage4 = this;
		
		mAvatarBtn = (ImageButton)view.findViewById(R.id.avatarBtn);
		mAvatarBtn.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				OnClickHead();
			}
		});
		View rl_top = view.findViewById(R.id.rl_top);
		rl_top.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				OnClickHead();
			}
		});
		View rl_item = view.findViewById(R.id.rl_item2);
		rl_item.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				AboutApp.showSelf(context);
			}
		});
		View rl_mythreads = view.findViewById(R.id.rl_mythreads);
		rl_mythreads.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				MyThreads.show(context);
			}
		});
		View rl_msg = view.findViewById(R.id.rl_msg);
		rl_msg.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				MyNotification.show(context);
			}
		});
		view.findViewById(R.id.rl_qqgroup).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				QQUtils.joinQQGroup(context, 460625088);
			}
		});
		
		stextname = (TextView)view.findViewById(R.id.textname);
		stextcoin = (TextView)view.findViewById(R.id.textcoin);
		setInfo();
		
		RelativeLayout version = (RelativeLayout)view.findViewById(R.id.rl_version);
		
		version.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				LoadingBox.showBox( context, "检查更新中...");
				//VersionUpdateActivity.checkVersion(context, false);
				VersionUpdateDlg.checkVersion(context, false);
			}
		});
		
		return view;		
	}	
	public void OnClickHead()
	{
		if(UserUtils.DataUtils.isLogined())
		{
			Intent intent = new Intent(context, UserInfo.class);
			context.startActivity(intent);
		}
		else
		{
			UserLogin.showLogin(context,new CallBackInterface() {
	            @Override
	            public void exectueMethod(Object p) {
	            	if(UserUtils.DataUtils.isLogined()){
	            		stextname.setText(UserUtils.DataUtils.get("username"));
	            		stextcoin.setText("");
	            	}
	            }
	        });
		}
	}
	public void setInfo()
	{
		if(UserUtils.DataUtils.isLogined())
		{
			stextname.setText(UserUtils.DataUtils.get("username"));
			stextcoin.setText(UserUtils.DataUtils.get("realname"));
		}
		else
		{
			stextname.setText("未登录");
			stextcoin.setText("点击头像登录");
		}
		
		int sexid = 0;
		String _strgender = UserUtils.DataUtils.get("gender");
		if(!_strgender.isEmpty())
			sexid = Integer.parseInt(_strgender);
		mAvatarBtn.setImageResource(GlobalUtility.Func.getHeadIconBySex(sexid));
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Plugins.onResume(context);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		Plugins.onPause(context);
	}
}