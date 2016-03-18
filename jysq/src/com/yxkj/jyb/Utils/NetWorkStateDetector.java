package com.yxkj.jyb.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkStateDetector {
	static private Context sContext = null;

	static public void init(Context context){
	    sContext = context;
	}
	
	public static boolean isConnectingToInternet(){
		if(sContext == null)
			return false;
	    ConnectivityManager connectivity = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivity != null)
	    {
	    	NetworkInfo[] info = connectivity.getAllNetworkInfo();
	    	if (info != null){
	    		for (int i = 0; i < info.length; i++){
	    			 if (info[i].getState() == NetworkInfo.State.CONNECTED){
	                      return true;
	                 }
	    		}
	    	}
	    }
	    return false;
	}
}
