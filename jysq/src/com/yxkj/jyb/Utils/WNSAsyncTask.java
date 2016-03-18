package com.yxkj.jyb.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import com.tencent.wns.client.inte.WnsService;

import android.os.AsyncTask;  
import android.util.Log;
import android.widget.ProgressBar;  
import android.widget.TextView;  
  
/**  
 * 生成该类的对象，并调用execute方法之后  
 * 首先执行的是onProExecute方法  
 * 其次执行doInBackgroup方法  
 *  
 */  
public class WNSAsyncTask extends AsyncTask<Integer, Integer, String> {  
  
    private TextView textView;  
    private ProgressBar progressBar;  
    
    //必须三件套
    private HttpCommon.postParams mParams = null;
    private WnsService mWNS = null;
    private HttpCommon.HandlerInterface mHandler = null;
    private boolean mIsPost = true;
    private String  mWNSCode = "";
      
      
    public WNSAsyncTask(TextView textView, ProgressBar progressBar) {  
        super();  
        this.textView = textView;  
        this.progressBar = progressBar;  
    }  
    
    public WNSAsyncTask() {  
        super();
    }  
    
    public boolean post(final WnsService _wns, final HttpCommon.postParams _p, final HttpCommon.HandlerInterface _hi) {  
    	if(_wns == null || _p == null || _hi == null)
    		return false;
        this.mWNS = _wns;
        this.mParams = _p;
        this.mHandler = _hi;
        this.execute();
        return true;
    }  
    public boolean get(WnsService _wns, HttpCommon.postParams _p,HttpCommon.HandlerInterface _hi) {  
    	if(_wns == null || _p == null || _hi == null)
    		return false;
        this.mWNS = _wns;
        this.mParams = _p;
        this.mHandler = _hi;
        mIsPost = false;
        this.execute();
        return true;
    }  
  
  
    /**  
     * 这里的Integer参数对应AsyncTask中的第一个参数   
     * 这里的String返回值对应AsyncTask的第三个参数  
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
     */  
    @Override  
    protected String doInBackground(Integer... params) {  
      
        //    publishProgress(0);  
    	String content = "";
    	HttpClient cli = mWNS.getWnsHttpClient();
        HttpPost request = new HttpPost(URI.create(mParams.url));
        try
        {
//        	List<NameValuePair> params = new ArrayList<NameValuePair>();
//    	  	NameValuePair pair1 = new BasicNameValuePair("p1", "HttpClient_android_Post");
//    	  	params.add(pair1);
        	  
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
            content = out.toString().trim();
            if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){//成功
            	mWNSCode = HttpCommon.HttpStatus_SC_OK;
            }else{//失败
                Header header = rsp.getFirstHeader(WnsService.KEY_HTTP_RESULT);
                if (header != null){
                	mWNSCode = header.getValue();//失败代码
                }
            }
        }
        catch (ClientProtocolException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        return content;  
    }  
  
  
    /**  
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
     */  
    @Override  
    protected void onPostExecute(String result) {  
        //textView.setText("异步操作执行结束" + result);  
        if(mWNSCode.equals(HttpCommon.HttpStatus_SC_OK)){
        	mHandler.onSuccess(result);
        }
        else{
        	mHandler.onFailure(mWNSCode);
        	Log.e("WNSAsyncTask:onPostExecute","错误信息:" + mWNSCode);
        }
    }  
  
  
    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
    @Override  
    protected void onPreExecute() {  
        //textView.setText("开始执行异步线程");  
    }  
  
  
    /**  
     * 这里的Intege参数对应AsyncTask中的第二个参数  
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
     */  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        //int vlaue = values[0];  
        //progressBar.setProgress(vlaue);  
    }  
}  
