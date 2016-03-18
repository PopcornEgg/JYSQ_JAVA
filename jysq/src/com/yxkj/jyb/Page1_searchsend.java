package com.yxkj.jyb;

import java.util.ArrayList;

import com.yxkj.jyb.ThreadsFilter.MarkItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;
import com.ta.annotation.TAInject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Page1_searchsend extends Activity{
	
	public static String sStrSearchfor = "";
	
	static Activity sActivity;
	static EditText sedit;
	boolean isPosting = false;
	private EditText mEditTextCredit;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.page1_searchsend);
        
        sActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.searchsend_back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Page1_searchsend.sActivity.finish();
			}  
		});
		
		sedit = (EditText)findViewById(R.id.searchsend_editText);
		sedit.setText(sStrSearchfor);
        GlobalUtility.Func.showSoftInput(this);
        Button button=(Button)findViewById(R.id.searchsend_ok);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!NetWorkStateDetector.isConnectingToInternet())
				{
					GlobalUtility.Func.ShowToast("当前无网络");
					return;
				}
				if(getCurFid().isEmpty())
				{
					GlobalUtility.Func.ShowToast("请选择科目");
					return;
				}
				if(!UserUtils.DataUtils.isLogined())
				{
					UserLogin.showLogin(sActivity,new CallBackInterface() {
			            @Override
			            public void exectueMethod(Object p) {
		            		asynPostTherad(sedit.getText().toString());
			            }
			        });
				}
				else{
					asynPostTherad(sedit.getText().toString());
				}
				GlobalUtility.Func.hideSoftInput(v.getContext(), sedit);
			}  
		});   
		mEditTextCredit = (EditText)findViewById(R.id.getcredit); 
		
		ImageButton carget = (ImageButton)findViewById(R.id.carget);
		carget.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				GlobalUtility.Func.hideSoftInput(sedit.getContext(), sedit);
				if(!UserUtils.DataUtils.isLogined())
				{
					UserLogin.showLogin(sActivity,new CallBackInterface() {
			            @Override
			            public void exectueMethod(Object p) {
			            	PhotoCropper.show(sActivity);
			            }
			        });
				}
				else
					PhotoCropper.show(sActivity);
			}
		});
		
		initView();
    }  
	@TAInject
	private void asynPostTherad(String str)
	{
		if(str.isEmpty())
		{
			GlobalUtility.Func.ShowToast("提交内容不能为空");
			return;
		}
		
		LoadingBox.showBox( sActivity, "提交中...");
          
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostNewThreadUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("fid", getCurFid());
		params.put("subject", str);
		params.put("message", "");
		params.put("typeid", "0");
		params.put("tag", "");
		String credit = mEditTextCredit.getText().toString();
		if(!credit.isEmpty())
			params.put("credit", credit);
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__"))
				{
					GlobalUtility.Func.ShowToast("提交成功");
					sedit.setText("");
					closeAllPreWindows();
				}
				else if(content.contains("__null__"))
				{
					String retstr = content.replace("\r\n", "");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				GlobalUtility.Func.ShowToast("提交失败");
				LoadingBox.hideBox();
	        }  
		});
	}
	
	ArrayList<View> sbtnTops = new ArrayList<View> ();
	ArrayList<View> sbtnBottoms = new ArrayList<View> ();
	
	private void initView( ){
		sbtnTops.clear();
		sbtnBottoms.clear();
		findViewById(R.id.gridLayout1).findViewsWithText(sbtnTops, "btnsTop", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		findViewById(R.id.gridLayout2).findViewsWithText(sbtnBottoms, "btnsBottom", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		for(int i=0;i<sbtnTops.size();i++)
		{
			Button _btn = (Button)sbtnTops.get(i);
			_btn.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					Button _b = (Button)v;
					updateBottom(_b.getText().toString());
					setTopState(v, false);
				}
			});
		}
		for(int i=0;i<sbtnBottoms.size();i++)
		{
			Button _btn = (Button)sbtnBottoms.get(i);
			_btn.setTag(i + 1);
			_btn.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					
					MarkItem mItem = ThreadsFilter.dicForwardMarks.get(getTopSelected());
					if(mItem != null)
					{	
						setBottomState(v, false);
					}
				}
			});
		}
		
		setTopState(sbtnTops.get(0), false);
		MarkItem mItem = ThreadsFilter.dicForwardMarks.get(getTopSelected());
		if(mItem != null)
		{
			for(int k=0;k<sbtnBottoms.size();k++)
			{
				Button _btn = (Button)sbtnBottoms.get(k);
				if((k + 1) < mItem.items.size())
				{
					MarkItem.MarkFids fids = mItem.items.get(k + 1);
					_btn.setVisibility(View.VISIBLE);
					_btn.setText(fids.name);
				}
				else
				{
					_btn.setVisibility(View.INVISIBLE);
				}
			}
		}
	}
	private void updateBottom(String kidnTop){
		MarkItem mItem = ThreadsFilter.dicForwardMarks.get(kidnTop);
		if(mItem != null)
		{
			for(int k=0;k<sbtnBottoms.size();k++)
			{
				Button _btn = (Button)sbtnBottoms.get(k);
				if((k + 1) < mItem.items.size())
				{
					MarkItem.MarkFids fids = mItem.items.get(k + 1);
					_btn.setVisibility(View.VISIBLE);
					_btn.setText(fids.name);
				}
				else
				{
					_btn.setVisibility(View.INVISIBLE);
				}
			}
			setBottomState(sbtnBottoms.get(0), false);
		}
	}
	private void setTopState(View tag, boolean v){
		for(int k=0;k<sbtnTops.size();k++)
		{
			View _v = sbtnTops.get(k);
			_v.setEnabled(_v == tag ? v : true);
		}
	}
	private void setBottomState(View tag, boolean v){
		for(int k=0;k<sbtnBottoms.size();k++)
		{
			View _v = sbtnBottoms.get(k);
			_v.setEnabled(_v == tag ? v : true);
		}
	}
	private String getTopSelected(){
		for(int k=0;k<sbtnTops.size();k++)
		{
			View _v = sbtnTops.get(k);
			if(!_v.isEnabled())
				return ((Button)_v).getText().toString();
		}
		return "小学";
	}
	private int getBottomSelected(){
		for(int k=0;k<sbtnBottoms.size();k++)
		{
			View _v = sbtnBottoms.get(k);
			if(!_v.isEnabled())
				return k + 1;
		}
		return -1;
	}
	private String getCurFid(){
		MarkItem mItem = ThreadsFilter.dicForwardMarks.get(getTopSelected());
		int tag = getBottomSelected();
		if(mItem != null && tag > 0)
		{	
			MarkItem.MarkFids fids = mItem.items.get(getBottomSelected());
			if(fids != null)
			{
				return fids.fids;
			}
		}
		return "";
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sActivity = null;
	}
	public static void closeAllPreWindows(){
		if(Page1_searchsend.sActivity != null)
			Page1_searchsend.sActivity.finish();
		if(Page1_searchresult.sActivity != null)
			Page1_searchresult.sActivity.finish();
		if(Page1_searchview.sActivity != null)
			Page1_searchview.sActivity.finish();
	} 
}