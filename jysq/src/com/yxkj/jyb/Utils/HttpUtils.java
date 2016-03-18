package com.yxkj.jyb.Utils;

import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;
import com.yxkj.jyb.LoadingBox;

import android.app.Activity;
import android.util.Log;

public class HttpUtils {
	protected static final String TAG = "HttpUtils";
	static private WnsService mWNS = null;//WnsClientFactory.getThirdPartyWnsService();
	
	static public WnsService getWNS(){
		if(mWNS == null){
			mWNS = WnsClientFactory.getThirdPartyWnsService();
		}
		return mWNS;
	}
	static public void post( Activity _a, final HttpCommon.postParams _PI, final HttpCommon.HandlerInterface _hi){
		if(!NetWorkStateDetector.isConnectingToInternet()){
			if(_hi != null)
				_hi.onFailure("当前无网络~~~");
			return;
		}
		if(Debuger.USE_WNS){
			if(Debuger.USE_WNS_ASYNCTASK){
				WNSAsyncTask asyncTask = new WNSAsyncTask(); 
				if(!asyncTask.post(getWNS(), _PI, _hi)){
					Log.d(TAG, "WNSRunnable 参数可能为空 WNSCommon.postParams == null || WNSCommon.HandlerInterface == null");
				}
			}
			else{
				WNSRunnable runnable = new WNSRunnable(); 
				if(!runnable.post(_a, getWNS(), _PI, _hi)){
					Log.d(TAG, "WNSRunnable 参数可能为空 WNSCommon.postParams == null || WNSCommon.HandlerInterface == null");
				}
			}
		}else{
			TAAsyncTask asyncTask = new TAAsyncTask(); 
			asyncTask.post( _PI, _hi);
		}
	}
}
