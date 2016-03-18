package com.yxkj.jyb.version;
/**
 * @author harvic
 * @date 2014-5-7
 * @address http://blog.csdn.net/harvic880925
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yxkj.jyb.GlobalUtility;
import com.yxkj.jyb.LoadingBox;
import com.yxkj.jyb.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;

public class VersionUpdateDlg  {
	static long m_newVerCode; //���°�İ汾��
	static String m_newVerName; //���°�İ汾��
	static String m_appNameStr; //���ص�����Ҫ�����APP��������
	static Context mContext;
	static Boolean mAuto;
	static String mVersionInfo;
	static String mApk;
	static int mForce = 0;	//ǿ�Ƹ���
	static AlertDialog sDialog;
	static Boolean mStopThread;
	static Handler m_mainHandler;
	static WebView mWebView;
	static ProgressBar mProgressBar;
	static Button btnUpdate;
	static Button btnBack;

	static private void initVariable(View layout)
	{
		m_appNameStr = "com.yxkj.jysqk12(cache).apk";
		m_mainHandler = new Handler();
		
		mProgressBar = (ProgressBar)layout.findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.GONE);
		
		WebView mWebView = (WebView)layout.findViewById(R.id.versionInfo);
		mWebView.loadUrl(mVersionInfo);
		
		btnUpdate = (Button)layout.findViewById(R.id.update);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//GlobalUtility.Func.ShowToast(mApk);
				downFile(mApk);  //��ʼ����
			}
		});
		
		btnBack = (Button)layout.findViewById(R.id.version_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mStopThread = true;
				sDialog.dismiss();
			}
		});
		if( mForce == 1 )
			btnBack.setVisibility(View.GONE);
	}

	public static void checkVersion(Context context, boolean auto)
	{
		mContext = context;
		mAuto = auto;
		new checkNewestVersionAsyncTask().execute();
	}
	
	static class checkNewestVersionAsyncTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params) {
			if(postCheckNewestVersionCommand2Server())
			{
				 int vercode = Common.getVerCode(mContext); // �õ�ǰ���һ��д�ķ���  
		         if (m_newVerCode > vercode) {  
		             return true;
		         } else {
		             return false;
		         }
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			LoadingBox.hideBox();
			// TODO Auto-generated method stub
			if (result) {//��������°汾
				doNewVersionUpdate(); // �����°汾  
			}else {
				if(!mAuto)
					GlobalUtility.Func.ShowToast("�Ѿ������°汾");
			}
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	}
	
	static private void doNewVersionUpdate() {  
		
        Builder bl =  new Builder(mContext);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.version_update,null);
		initVariable(layout);
        bl.setView(layout);
	   	bl.setInverseBackgroundForced(true);
	   	bl.setCancelable(false);
	   	sDialog = bl.show();
	}  
	static public String getApplication_MetaData(String metaName)
	{
		//��applicationӦ��<meta-data>Ԫ�ء�
		ApplicationInfo appInfo;
		try {
			appInfo = mContext.getPackageManager()
					.getApplicationInfo(mContext.getPackageName(),PackageManager.GET_META_DATA);
			
			return appInfo.metaData.getString(metaName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "debug";
	}
	/**
	 * �ӷ�������ȡ��ǰ���°汾�ţ�����ɹ�����TURE�����ʧ�ܣ�����FALSE
	 * @return
	 */
	private static Boolean postCheckNewestVersionCommand2Server()
	{
		StringBuilder builder = new StringBuilder();
		try {
			// ����POST������{name:value} ������
			List<NameValuePair> vps = new ArrayList<NameValuePair>();
			// ����������post������
			vps.add(new BasicNameValuePair("action", "checkNewestVersion"));
			vps.add(new BasicNameValuePair("channel", getApplication_MetaData("InstallChannel")));
			
			builder = Common.post_to_server(vps);
			//Log.e("msg", builder.toString());
			JSONObject json = new JSONObject(builder.toString());
			if (json.getInt("id") == 1) {
				m_newVerName = json.getString("verName");
				m_newVerCode = json.getLong("verCode");
				mApk = json.getString("apk");
				mVersionInfo = json.getString("verInfo");
				mForce = json.getInt("force");
				return true;
			}
	
			return false;
		} catch (Exception e) {
			Log.e("msg",e.getStackTrace().toString());
			m_newVerName="";
			m_newVerCode=-1;
			return false;
		}
	}
		
	static private void downFile(final String url)
	{
		mStopThread = false;
		mProgressBar.setVisibility(View.VISIBLE);
		btnUpdate.setVisibility(View.GONE);
		btnBack.setVisibility(View.GONE);
	    new Thread() {  
	        public void run() {  
	            HttpClient client = new DefaultHttpClient();  
	            HttpGet get = new HttpGet(url);  
	            HttpResponse response;  
	            try {  
	                response = client.execute(get);  
	                HttpEntity entity = response.getEntity();  
	                long length = entity.getContentLength();  
	                
	                mProgressBar.setMax((int)length);
	                
	                InputStream is = entity.getContent();  
	                FileOutputStream fileOutputStream = null;  
	                if (is != null) {  
	                    File file = new File(  
	                            Environment.getExternalStorageDirectory(),  
	                            m_appNameStr);  
	                    fileOutputStream = new FileOutputStream(file);  
	                    byte[] buf = new byte[1024];  
	                    int ch = -1;  
	                    int count = 0;  
	                    while ((ch = is.read(buf)) != -1) {  
	                        fileOutputStream.write(buf, 0, ch);  
	                        count += ch;  
	                        if (length > 0) {  
	                        	mProgressBar.setProgress(count);
	                        }  
	                        
	                        if( mStopThread )
	                        	break;
	                    }  
	                }  
	                fileOutputStream.flush();  
	                if (fileOutputStream != null) {  
	                    fileOutputStream.close();  
	                }  
	                
	                if(!mStopThread)
	                	down();  //����HANDER�Ѿ���������ˣ����԰�װ��
	                
	            } catch (ClientProtocolException e) {  
	                e.printStackTrace();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }.start();  
	}
	/**
	 * ����HANDER�Ѿ���������ˣ����԰�װ��
	 */
	static private void down() {
	    m_mainHandler.post(new Runnable() {
	        public void run() {
	        	mProgressBar.setVisibility(View.GONE);
	            update();
	        }
	    });
	}
	/**
	 * ��װ����
	 */
    static void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), m_appNameStr)),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        sDialog.dismiss();
    }
}
