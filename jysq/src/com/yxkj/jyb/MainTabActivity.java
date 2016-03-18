package com.yxkj.jyb;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.download.Downloader;
import com.tencent.download.Downloader.FileType;
import com.tencent.upload.UploadManager;
import com.yxkj.jyb.Utils.Debuger;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.version.VersionUpdateDlg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author yangyu
 *	功能描述：自定义TabHost
 */
public class MainTabActivity extends FragmentActivity{	
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	
	//定义一个布局
	private LayoutInflater layoutInflater;
		
	public class MainTabItem{
		public Class<?> fragment = null;
		public int imgres = 0;
		public String name = "";
		public MainTabItem(Class<?> _c, int _i, String _n){
			fragment = _c;
			imgres = _i;
			name = _n;
		}
	}
	//定义数组来存放Fragment界面
	private MainTabItem[] mainTabItems = new MainTabItem[]{
		new MainTabItem(FragmentPage1.class, R.drawable.tab_home_btn, "搜索"),
		new MainTabItem(FragmentPage2.class, R.drawable.tab_message_btn, "回答"),
		//new MainTabItem(FragmentPage_bjb.class, R.drawable.tab_bjb_btn, "笔记"),
		new MainTabItem(FragmentPage6.class, R.drawable.tab_video_btn, "视频"),
		new MainTabItem(FragmentPage_jyf.class, R.drawable.tab_selfinfo_btn, "记忆"),
		new MainTabItem(FragmentPage4.class, R.drawable.tab_square_btn, "发现"),
	};
	
	public static MainTabActivity mMainTabActivity = null;
	
	private Handler mHandler = null;
	
	public static boolean isAskUploadManager = false;
	public static UploadManager sUploadManager = null;
	public static Downloader sDownloader = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //showPopupWindow();
        
        setContentView(R.layout.main_tab_layout);
        mMainTabActivity = this;
        Plugins.Init(this);
        initOther();
        initView();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
    }
	 
	AlertDialog myDialog;
    private void showPopupWindow()
    {
    	myDialog = new AlertDialog.Builder(this).create();  
        myDialog.show();  
        myDialog.getWindow().setContentView(R.layout.guide_page);
        
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
        params.width = getWindow().getAttributes().width;
        params.height = getWindow().getAttributes().height;
        myDialog.getWindow().setAttributes(params);
        
        
        myDialog.getWindow()  
            .findViewById(R.id.button1)  
            .setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                myDialog.dismiss();  
            }  
        });  
    }

	private void initView(){
		//实例化布局对象
		layoutInflater = LayoutInflater.from(this);
				
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);	
		
		//得到fragment的个数
		int count = mainTabItems.length;	
				
		for(int i = 0; i < count; i++){	
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mainTabItems[i].name).setIndicator(getTabItemView(i));
			//将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, mainTabItems[i].fragment, null);
			//设置Tab按钮的背景
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
	}
				
	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
	
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mainTabItems[index].imgres);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);		
		textView.setText(mainTabItems[index].name);
	
		return view;
	}
	private void initOther()
	{
		//初始化工具
		GlobalUtility.Func.init();
		
		//初始化用户工具集
		UserUtils.init(this);

        //初始化网络信息
        NetWorkStateDetector.init(this);
        
        //初始化FileTools
        FileTools.init(this);
        
        //初始化万象优图
        asynPostImageSign("1");
        init_img_qcloud_download();
        
        //是否自动登录
		if(NetWorkStateDetector.isConnectingToInternet())
			autoUserLogin();
		
		VersionUpdateDlg.checkVersion(this, true);
		//VersionUpdateActivity.checkVersion(mMainTabActivity, true);
	}
	public void init_img_qcloud_download(){
		 // 初始化用户信息
        Downloader.authorize(GlobalUtility.Config.imagemyqcloudappid,  "0"); 
        // 实例化下载管理类
        sDownloader = new Downloader(this, FileType.Photo, null);
        // 设置最大并发数
        sDownloader.setMaxConcurrent(5);
        // 启动断点续传功能
        sDownloader.enableHTTPRange(true);
        // 启动长连接功能
        sDownloader.enableKeepAlive(true);
	}
	public void init_img_qcloud_upload(String sign){
		/**
		  * 构造方法
		  * @param persistenceId  持久化ID，每个UploadManager需设置一个唯一的ID用于持久
		  *                       化保存未完成任务列表，以便应用退出重进后能继续进行上传；传
		  *                       入为Null，则不会进行持久化保存
		  *                       Secret ID: AKIDKmhA1dyCUFXu8TfTmN3MgbILVJD4gsXH
								  Secret Key: WKjtcXzrPJog3m2eef3BIIxuUfQaxF2H

		  */
	    // 1.注册签名 
        UploadManager.authorize(GlobalUtility.Config.imagemyqcloudappid, "0", sign);
        //2.实例化上传管理类
        sUploadManager = new UploadManager(this,null);
	}
	public void asynPostImageSign(String type)
	{
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ImageSignUrl);
		params.put("signtype", type);
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isAskUploadManager = true;
				if(!content.contains("null")){
					try {
						JSONObject json = new JSONObject(content);
						String sign = json.getString("sign");
					//	GlobalUtility.Func.ShowToast( "sign:" + sign);
						init_img_qcloud_upload(sign);
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(mMainTabActivity, e.toString(), Toast.LENGTH_LONG).show();
					}
//					String sign = content.replace("\r\n", "");
//					sign = sign.substring(1);
//					String appid= "10006010";
//					GlobalUtility.Func.ShowToast( "sign:" + sign);
//					init_img_qcloud(appid,sign);
				}
			}
			@Override  
	        public void onFailure(String error) {  
				isAskUploadManager = true;
	        }  
		});
	}
	
	private void autoUserLogin(){
		if(UserUtils.DataUtils.isLogined())
			return;
		SharedPreferences sp = getSharedPreferences("jysq-UserData",Context.MODE_PRIVATE);
		String uid = sp.getString("uid", "");
		String username = sp.getString("username", "");
		String psw = sp.getString("password", "");
		if(uid.isEmpty() || username.isEmpty() || psw.isEmpty())
			return;
		asynPostLogin(username, psw);
	}
	private void asynPostLogin(String strname, String strpsw)
	{
		HttpCommon.postParams _PI = new HttpCommon.postParams(GlobalUtility.Config.UserLoginUrl);
		_PI.put("username", strname);
		_PI.put("password", strpsw);
		_PI.put("did", "");
		
		HttpUtils.post(this, _PI, new HttpCommon.HandlerInterface(){
		//WNSHttpMgr.get().postEx(_PI, new HttpCommon.HandlerInterface(){
			@Override
			public void onSuccess(String content){
				if(!content.contains("null\r\n")){
					try {
						JSONObject json = new JSONObject(content);
						String id = json.getString("id");
						if(id.equals("1")){
							UserUtils.DataUtils.initDataByJson(json);
							UserUtils.bjbUtils.asynMyThreadData(MainTabActivity.this, 0, 0);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public void onFailure(String error){
				if(Debuger.USE_WNS){
				}else{
				}
			}
		});
	}
	
	 // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            int durationTime = 2000;
            Toast toast = Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT);
            toast.setDuration(durationTime);
            toast.show();
            mHandler.sendEmptyMessageDelayed(0, durationTime);// 利用handler延迟发送更改状态信息
        } else {
            finish();
            System.exit(0);
        }
    }
    
	public static void saveUserData( String _k, String _v ){
		if(_k.isEmpty() || mMainTabActivity == null)
			return;
		SharedPreferences sp = mMainTabActivity.getSharedPreferences("jysq-UserData",Context.MODE_PRIVATE);
		Editor editor = sp.edit();  
		editor.putString(_k, _v);  
		editor.commit();  
	}
	public static void setBackgroundAlpha( float bgAlpha ){
		if(mMainTabActivity != null){
			WindowManager.LayoutParams lp = mMainTabActivity.getWindow().getAttributes();  
			lp.alpha = bgAlpha; //0.0-1.0  
	        mMainTabActivity.getWindow().setAttributes(lp);  
		}
	}
}
