package com.yxkj.jyb.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class HttpCommon {

	public static final String HttpStatus_SC_OK = Integer.toString(HttpStatus.SC_OK);
	
	public interface HandlerInterface {
		public void onSuccess(final String content);
        public void onFailure(final String error);
	}
	
	static public class postParams{
		public String url;
		public String encoding = HTTP.UTF_8;
		public List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		public postParams(String _url){
			this.url = _url;
		}
		public postParams(String _url, String _encoding){
			this.url = _url;
			this.encoding = _encoding;
		}
		
		public void put(String n, String p){
			NameValuePair nvp = new BasicNameValuePair(n, p);
			params.add(nvp);
		}
		public void put(String n, Integer p){
			NameValuePair nvp = new BasicNameValuePair(n, p.toString());
			params.add(nvp);
		}
		public void put(String n, Long p){
			NameValuePair nvp = new BasicNameValuePair(n, p.toString());
			params.add(nvp);
		}
	}
}
