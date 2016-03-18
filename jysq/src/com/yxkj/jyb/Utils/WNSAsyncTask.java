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
 * ���ɸ���Ķ��󣬲�����execute����֮��  
 * ����ִ�е���onProExecute����  
 * ���ִ��doInBackgroup����  
 *  
 */  
public class WNSAsyncTask extends AsyncTask<Integer, Integer, String> {  
  
    private TextView textView;  
    private ProgressBar progressBar;  
    
    //����������
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
     * �����Integer������ӦAsyncTask�еĵ�һ������   
     * �����String����ֵ��ӦAsyncTask�ĵ���������  
     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
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
        	  
        	// ���ò������ַ��� ����httpRequest
    	    HttpEntity httpentity = new UrlEncodedFormEntity(mParams.params, mParams.encoding);
    	    request.setEntity(httpentity);
        	// ��ʼ����
            HttpResponse rsp = cli.execute(request);
            //ȡ������
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
            
            //����ʹ�õ�����
            content = out.toString().trim();
            if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){//�ɹ�
            	mWNSCode = HttpCommon.HttpStatus_SC_OK;
            }else{//ʧ��
                Header header = rsp.getFirstHeader(WnsService.KEY_HTTP_RESULT);
                if (header != null){
                	mWNSCode = header.getValue();//ʧ�ܴ���
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
     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
     */  
    @Override  
    protected void onPostExecute(String result) {  
        //textView.setText("�첽����ִ�н���" + result);  
        if(mWNSCode.equals(HttpCommon.HttpStatus_SC_OK)){
        	mHandler.onSuccess(result);
        }
        else{
        	mHandler.onFailure(mWNSCode);
        	Log.e("WNSAsyncTask:onPostExecute","������Ϣ:" + mWNSCode);
        }
    }  
  
  
    //�÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ��������  
    @Override  
    protected void onPreExecute() {  
        //textView.setText("��ʼִ���첽�߳�");  
    }  
  
  
    /**  
     * �����Intege������ӦAsyncTask�еĵڶ�������  
     * ��doInBackground�������У���ÿ�ε���publishProgress�������ᴥ��onProgressUpdateִ��  
     * onProgressUpdate����UI�߳���ִ�У����п��Զ�UI�ռ���в���  
     */  
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        //int vlaue = values[0];  
        //progressBar.setProgress(vlaue);  
    }  
}  
