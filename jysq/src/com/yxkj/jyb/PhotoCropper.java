package com.yxkj.jyb;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ThreadsFilter.MarkItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;
/**
 * Created with Android Studio.
 * User: ryan@xisue.com
 * Date: 10/3/14
 * Time: 11:44 AM
 * Desc: PhotoCropperActivity
 */
public class PhotoCropper extends Activity {

    public static final String TAG = "PhotoCropper";
    ImageView mImageView;
    private PhotoCropper sActivity;
	boolean isPosting = false;
	private EditText mEditTextCredit;
	ArrayList<View> sbtnTops = new ArrayList<View> ();
	ArrayList<View> sbtnBottoms = new ArrayList<View> ();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photocropper);
        sActivity = this;
        mImageView = (ImageView) findViewById(R.id.image);
        
        if(!NetWorkStateDetector.isConnectingToInternet())
		{
			GlobalUtility.Func.ShowToast("当前无网络");
			return;
		}
        if(MainTabActivity.sUploadManager == null && MainTabActivity.isAskUploadManager == false)
		{
        	MainTabActivity.mMainTabActivity.asynPostImageSign("1");
		}
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sActivity.finish();
			}  
		});
		
        Button button=(Button)findViewById(R.id.ok);
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
			            	Bitmap bm =GlobalUtility.Func.drawableToBitmap( mImageView.getDrawable());
			            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            	    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		            	    uplaod( baos.toByteArray());
			            }
			        });
				}
				else{
	            	Bitmap bm =GlobalUtility.Func.drawableToBitmap( mImageView.getDrawable());
	            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            	    uplaod( baos.toByteArray());
            	    
            	    /*String fileName = "/sdcard/test000.jpg";
            	    try {
            	    	mImageView.setDrawingCacheEnabled(true);
            	    	Bitmap tempBmp = Bitmap.createBitmap(mImageView.getDrawingCache());
            	    	mImageView.setDrawingCacheEnabled(false);
            	    	
            	    	FileOutputStream fos = new FileOutputStream(fileName);
            	    	if(fos != null){
            	    		tempBmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    						try {
								fos.flush();
								fos.close();
								GlobalUtility.Func.ShowToast(fileName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            	    	}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// 把数据写入文件
*/
				}
				GlobalUtility.Func.hideSoftInput(v.getContext(), mEditTextCredit);
			}  
		});   
		mEditTextCredit = (EditText)findViewById(R.id.getcredit); 
		initView();
		
		CameraCroperAct.show(this);
    }
    @TAInject
	private void asynPostTherad( String url )
	{
    	HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostNewThreadUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("fid", getCurFid());
		params.put("subject", url);
		params.put("message", "");
		params.put("typeid", "0");
		params.put("attachment", "2");
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
					sActivity.finish();
				}
				else if(content.contains("__null__"))
				{
					String retstr = content.replace("\r\n", "");
					String[] p = retstr.split(",");
					if(p.length == 2){
						if(p[1].equals("-1")){
							GlobalUtility.Func.ShowToast("提交失败:积分不足");
						}
						else if(p[1].equals("0")){
							GlobalUtility.Func.ShowToast("提交失败:未知错误");
						}
					}
				}
			}
			@Override  
	        public void onFailure(String error) {  
				GlobalUtility.Func.ShowToast("提交失败");
				LoadingBox.hideBox();
	        }  
		});
	}
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
    private void uplaod(byte[] datas){
    	LoadingBox.showBox(PhotoCropper.this, "提交中...");
    	ImageDataMgr.sUpLoadMgr.addLoad(datas, 1, mLoadHandler);
    }
    @Override
	public void onResume()
	{
		super.onResume();
		if(CameraCroperAct.sOutBitmap != null){
			mImageView.setImageBitmap(CameraCroperAct.sOutBitmap);
			CameraCroperAct.sOutBitmap = null;//用完记得设置
			//mImageView.setImageBitmap(CropHelper.decodeUriAsBitmapEx(this, mCropParams.uri,
	        //		GlobalUtility.Config.screenSize.x,(int)((double)(GlobalUtility.Config.screenSize.y) * 0.3f)));
		}
	}
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Page1_searchsend.closeAllPreWindows();
    }
    public LoadHandler mLoadHandler = new LoadHandler();
    class LoadHandler extends Handler  
    {  
        @Override  
        public void handleMessage(Message msg)  
        {  
            super.handleMessage(msg);  
            int what = msg.what;  
            switch (what)  
            {                  
                case 1:  {  
                	int tp = msg.arg1;
                	if(tp == 1){
                		asynPostTherad(GlobalUtility.Config.ImageMainUrl + msg.obj.toString());
                	}
                	else if(tp == 2){
                		//int per = msg.arg2;
                	}
                } 
            }  
        }  
    }  
    static public void show(Context _c){
    	if(_c != null){
			Intent intent = new Intent(_c, PhotoCropper.class);
			_c.startActivity(intent);
		}
    }
}
