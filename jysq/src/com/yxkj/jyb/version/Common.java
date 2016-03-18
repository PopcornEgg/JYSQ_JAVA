package com.yxkj.jyb.version;
/**
 * @author harvic
 * @date 2014-5-7
 * @address http://blog.csdn.net/harvic880925
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Common {
	public static final String SERVER_ADDRESS="http://bbs.jiyisq.cn/res/version/index.php";//�������°���ַ

	/**
	 * ����������Ͳ�ѯ���󣬷��ز鵽��StringBuilder��������
	 * 
	 * @param ArrayList
	 *            <NameValuePair> vps POST�����Ĳ�ֵ��
	 * @return StringBuilder builder ���ز鵽�Ľ��
	 * @throws Exception
	 */
	public static StringBuilder post_to_server(List<NameValuePair> vps) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpResponse response = null;
			// ����httpost.���ʱ��ط�������ַ
			HttpPost httpost = new HttpPost(SERVER_ADDRESS);
			StringBuilder builder = new StringBuilder();

			httpost.setEntity(new UrlEncodedFormEntity(vps, HTTP.UTF_8));
			response = httpclient.execute(httpost); // ִ��

			if (response.getEntity() != null) {
				// �����������JSONûд�ԣ�����ǻ���쳣����ִ�в���ȥ��
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));
				String s = reader.readLine();
				for (; s != null; s = reader.readLine()) {
					builder.append(s);
				}
			}
			return builder;

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("msg",e.getMessage());
			return null;
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();// �ر�����
				// �������ͷ����ӵķ���������
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("msg",e.getMessage());
			}
		}
	}
	
	/**
	 * ��ȡ�����汾��
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
        int verCode = -1;
        try {
        	//ע�⣺"com.example.try_downloadfile_progress"��ӦAndroidManifest.xml���package="����"����
            verCode = context.getPackageManager().getPackageInfo(
                    "com.yxkj.jyb", 0).versionCode;
        } catch (NameNotFoundException e) {
        	Log.e("msg",e.getMessage());
        }
        return verCode;
    }
   /**
    * ��ȡ�汾����
    * @param context
    * @return
    */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    "com.example.try_downloadfile_progress", 0).versionName;
        } catch (NameNotFoundException e) {
        	Log.e("msg",e.getMessage());
        }
        return verName;   
}
	
	
	
	
	
	
}