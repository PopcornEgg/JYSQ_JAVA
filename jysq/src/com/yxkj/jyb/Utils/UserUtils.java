package com.yxkj.jyb.Utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ta.annotation.TAInject;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.yxkj.jyb.GlobalUtility;
import com.yxkj.jyb.LoadingBox;
import com.yxkj.jyb.MainTabActivity;
import com.yxkj.jyb.BJBDataMgr.My;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class UserUtils {
    static private Context mContext = null;
	static public void init(Context _c){
		mContext = _c;
	}
	static public class DataUtils{
		@SuppressWarnings("serial")
		static Map<String, String> checknull = new HashMap<String,String>(){{
			put("gender","0");
			put("credits","0");
		}};
		static Map<String, String> data = new HashMap<String,String>();
		static public void set(String _k, String _v)
		{
			data.put(_k, _v);
			MainTabActivity.saveUserData(_k, _v);
		}
		static public String get(String _k)
		{
			if(data.containsKey(_k))
				return data.get(_k);
			if(checknull.containsKey(_k))
				return checknull.get(_k);
			else
				return "";
		}
		static public String getNickName()
		{
			String _name = get("realname");
			if(_name.isEmpty())
				_name = get("username");
			return _name;
		}
		static public boolean isLogined(){
			return !get("username").isEmpty();
		}
		static public void initDataByJson(JSONObject json){
			try {
				set("uid", json.getString("uid"));
				set("username", json.getString("username"));
				set("email", json.getString("email"));
				set("password", json.getString("password"));
				set("realname", json.getString("realname"));
				set("gender", json.getString("gender"));
				set("credits", json.getString("credits"));
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			registerXGPush();
		}
		static public void clearData(){
			set("uid", "");
			set("username", "");
			set("email", "");
			set("password", "");
			set("realname", "");
			set("gender", "0");
			set("credits", "0");
		}
		static public String getListEmptyText(String txt){
			if(isLogined()){
				if(!NetWorkStateDetector.isConnectingToInternet()){
					txt = "网络好像出问题了哦~~~";
				}
			}else{
				txt = "您还尚未登录哦~~~";
			}
			return txt;
		}
		//登录的话，就重新注册XG
		static public void registerXGPush(){
			if(isLogined() && mContext != null){
				XGPushManager.registerPush(mContext, "xgpushuid_" + get("uid"),new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						Log.d("TPush", "注册成功，设备token为：" + data);
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
					}
				});
			}
		}
		/*
		static public void save(String _k, String _v){
			
			try{
			      String data = " This content will append to the end of the file";
			      String rootPath = Environment.getDataDirectory().toString();
			      File file =new File(rootPath + "/jysq-UserData.txt");
			      if(!file.exists()){
			       file.createNewFile();
			      }

			      GlobalUtility.Func.ShowToast(FragmentPage1.context, rootPath);
			      //true = append file
			      FileWriter fileWritter = new FileWriter(file.getName(),true);
			             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			             bufferWritter.write(data);
			             bufferWritter.close();

		         System.out.println("Done");
		     }catch(IOException e){
		      e.printStackTrace();
		     }
		}
		*/
	}
	
	public static BjbUtils bjbUtils = new BjbUtils();
	public static class BjbUtils {
		@TAInject
	    boolean isPulling = false;
		@SuppressLint("DefaultLocale") public void asynMyThreadData(final Activity _c, int count, int start)
		{
			if(isPulling)
				return;
			isPulling = true;
			//LoadingBox.showBox(mContext, "加载中...");
			HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_MyThreadDataUrl);
			params.put("uid", DataUtils.get("uid"));
			params.put("start", Integer.toString(start));
			params.put("count", Integer.toString(count));
			HttpUtils.post(_c,  params, new HttpCommon.HandlerInterface()
			{
				@Override
				public void onSuccess(String content)
				{
					isPulling = false;
					//LoadingBox.hideBox();
					if(content.contains("null")){
						return;
					}
					try {
						JSONArray jsonArray = new JSONArray(content);
						for(int i=0;i<jsonArray.length();i++){
							JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
					        My.ThreadItem ttiem = new My.ThreadItem();
							ttiem.subtype = json.getInt("subtype");
				        	ttiem.tid = json.getString("tid");
				        	ttiem.subname = json.getString("subname");
				        	ttiem.dateline = json.getLong("dateline");
				        	ttiem.count = json.getInt("count");
				        	ttiem.from = json.getString("from");
			                My.addThreadItem(ttiem);
			                asynGetSubjectData(_c, ttiem.tid);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override  
		        public void onFailure(String error) {  
					//LoadingBox.hideBox();
					isPulling = false;
		        }  
			});
		}
		private void asynGetSubjectData(Activity _c,String tid)
		{
			if(tid.isEmpty())
				return;
			
			HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_MyPostDataUrl);
			params.put("tid", tid);
			params.put("uid", DataUtils.get("uid"));
			HttpUtils.post(_c,  params, new HttpCommon.HandlerInterface()
			{
				@Override
				public void onSuccess(String content)
				{
					if(!content.contains("null\r\n")){
						try {
							JSONArray jsonArray = new JSONArray(content);
							for(int i=0;i<jsonArray.length();i++){
								JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
								My.PostItem pitem = new My.PostItem();
					        	pitem.tid = json.getString("tid");
					        	pitem.pid = json.getString("pid");
					        	pitem.message = GlobalUtility.Func.hexStr2Str(json.getString("message"));
					        	pitem.dateline = json.getLong("dateline");
					        	pitem.from = json.getString("from");
								My.addPostItem(pitem);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else{
						
					}
				}
				@Override  
		        public void onFailure(String error) {  
					LoadingBox.hideBox();
		        }  
			});
		}
	}
}
