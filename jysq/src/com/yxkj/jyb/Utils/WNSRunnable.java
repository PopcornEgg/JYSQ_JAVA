package com.yxkj.jyb.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.tencent.wns.client.inte.WnsAsyncHttpRequest;
import com.tencent.wns.client.inte.WnsAsyncHttpResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TextView;

import com.tencent.wns.client.data.Option;
import com.tencent.wns.client.data.WnsError;
import com.tencent.wns.client.inte.IWnsCallback.WnsBindCallback;
import com.tencent.wns.client.inte.IWnsCallback.WnsRegPushCallback;
import com.tencent.wns.client.inte.IWnsCallback.WnsTransferCallback;
import com.tencent.wns.client.inte.IWnsCallback.WnsUnbindCallback;
import com.tencent.wns.client.inte.IWnsResult.IWnsBindResult;
import com.tencent.wns.client.inte.IWnsResult.IWnsRegPushResult;
import com.tencent.wns.client.inte.IWnsResult.IWnsTransferResult;
import com.tencent.wns.client.inte.IWnsResult.IWnsUnbindResult;
import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;
import com.tencent.wns.client.inte.WnsService.WnsSDKStatus;
import com.tencent.wns.client.inte.WnsService.WnsSDKStatusListener;
import com.yxkj.jyb.FragmentPage2;
import com.yxkj.jyb.GlobalUtility;
import com.yxkj.jyb.LoadingBox;

public class WNSRunnable {

	private Activity mActivity = null;
    //必须三件套
    private HttpCommon.postParams mParams = null;
    private WnsService mWNS = null;
    private HttpCommon.HandlerInterface mHandler = null;
    
    private boolean mIsPost = true;
	
	public boolean post(Activity _a, WnsService _wns, final HttpCommon.postParams _p, final HttpCommon.HandlerInterface _hi){
		
		if(_a == null || _wns == null || _p == null || _hi == null)
    		return false;
		this.mActivity = _a;
		this.mWNS = _wns;
	    this.mParams = _p;
	    this.mHandler = _hi;
		Runnable run = new Runnable(){
            @Override
            public void run()
            {
                HttpClient cli = mWNS.getWnsHttpClient();
                HttpPost request = new HttpPost(URI.create(mParams.url));
                try
                {
                	// 设置参数及字符集 请求httpRequest
            	    HttpEntity httpentity = new UrlEncodedFormEntity(mParams.params, mParams.encoding);
            	    request.setEntity(httpentity);
                	// 开始请求
                    HttpResponse rsp = cli.execute(request);
                    //取回数据
                    HttpEntity entity = rsp.getEntity();
                    InputStream in = entity.getContent();
                    byte[] buff = new byte[1024];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int len = -1;
                    while ((len = in.read(buff)) != -1){
                        out.write(buff, 0, len);
                    }
                    in.close();
                    // final String content = EntityUtils.toString(rsp.getEntity()).trim();// out.toString().trim();
                    
                    //真正使用的数据
                    final String content = out.toString().trim();
                    if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){//成功
                    	onSuccess(content);
                    }else{//失败
                        Header header = rsp.getFirstHeader(WnsService.KEY_HTTP_RESULT);
                        if (header != null){
                        	onFailure(header.getValue());
                        }
                    }
                }
                catch (ClientProtocolException e){
                    e.printStackTrace();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(run).start();
        return true;
	}
	
	private void onSuccess(final String content){
		mActivity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
            	mHandler.onSuccess(content);
            }
        });
	}
	private void onFailure(final String error){
		mActivity.runOnUiThread(new Runnable(){
            @Override
            public void run(){
            	mHandler.onFailure(error);
            }
        });
	}
}
