package com.yxkj.jyb;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.tencent.qcload.playersdk.util.PlayerListener;
import com.tencent.qcload.playersdk.util.VideoInfo;
import com.tencent.qcload.playersdk.ui.UiChangeInterface;
import com.tencent.qcload.playersdk.ui.VideoRootFrame;

public class VideoPlayer extends Activity {

	public static Activity fActivity = null;
	public static List<VideoInfo> videos = null;
	static CallBackInterface scallBack = null;
	static int uid = 0;
	static int seekto = 0;
	
	VideoRootFrame player = null;
	boolean isseeked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);

        //��ȡҳ���еĲ������ؼ�	
        player=(VideoRootFrame) findViewById(R.id.qcloud_video_view);	
        player.play(videos);
       
        player.setListener(new PlayerListener(){
			@Override
			public void onError(Exception arg0) {
				//arg0.printStackTrace();
			}
			@Override
			public void onStateChanged(int arg0) {
//				if(arg0 == 4){
//					 if(seekto > 0)
//				        	player.seekTo(50);
//				}
				if(arg0 == 5){
					if(seekto > 0 && !isseeked){
						player.seekTo(seekto);
						isseeked = true;
				 	}
				}
			}
        });
        player.setToggleFullScreenHandler(new UiChangeInterface() {	
        	 @Override	
        	 public void OnChange() {	
	        	 if (player.isFullScreen()) {	
	        		 //������ȫ��ʱ����ҳ������Ϊ����״̬ 
	        		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	
	        	 } 
	        	 else 
	        	 {	
		        	 //��������ȫ��ʱ����ҳ������Ϊ����״̬����ʱ�������ؼ��������Ӧ����Ļ��
		        	 //��ʵ��ȫ�� 
		        	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
	        	 }	
        	 }	
    	 });	
    }
   
    @Override
    public void finish(){
    	super.finish();
    	
  	 	if(scallBack != null){
  	 		scallBack.exectueMethod(uid + "," + player.getCurrentTime() );
  	 	}
  	  
    	player.release();
    	scallBack = null;
    	seekto = 0;
    }
    
    static public void play(Context _c, int _uid, List<VideoInfo> _videos, int _seek, CallBackInterface _cb)
	{
    	uid = _uid;
    	videos = _videos;
    	scallBack = _cb;
    	seekto = _seek;
    	Intent intent = new Intent(_c, VideoPlayer.class);
        _c.startActivity(intent);
    }
}
