package com.yxkj.jyb.Utils;

import org.apache.http.NameValuePair;

import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.AsyncHttpResponseHandler;
import com.ta.util.http.RequestParams;

public class TAAsyncTask  {  
	private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
	public boolean post(final HttpCommon.postParams _PI, final HttpCommon.HandlerInterface _hi){
		
		if(_PI == null || _hi == null)
    		return false;
		
		RequestParams params = new RequestParams();
		for(int i=0;i<_PI.params.size();i++){
			NameValuePair p = _PI.params.get(i);
			params.put(p.getName(), p.getValue());
		}
		
		asyncHttpClient.post(_PI.url, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content)
			{
				super.onSuccess(content);
				_hi.onSuccess(content);
			}
			@Override  
	        public void onFailure(Throwable error) {  
				_hi.onFailure(error.toString());
	        }  
		});
		return true;
	}
}  
