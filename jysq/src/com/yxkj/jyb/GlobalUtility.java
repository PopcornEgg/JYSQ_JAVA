package com.yxkj.jyb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.yxkj.jyb.Utils.Debuger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class GlobalUtility {
	static public class Config
    {
        //社交系统root url
		public static String MobileRootUrl = "http://bbs.jiyisq.cn/upload/source/plugin/jysq/mobile.php?module=";
		static{
			if(Debuger.USE_LOCAL_PHPSERVER){
				MobileRootUrl = "http://192.168.31.243/qz/upload/source/plugin/jysq/mobile.php?module=";
			}
		};
		
		//版本号示例 添加&apiver=2 表示使用2号版本接口 
		//public static String ForumThreadListUrl = MobileRootUrl + "threadlist&apiver=2";
        //UCenter URL定义
        public static String UserRegisterUrl = MobileRootUrl + "register";
        public static String UserLoginUrl = MobileRootUrl + "login";
        public static String UserMyThreadUrl = MobileRootUrl + "mythread";
        public static String UserMyPostUrl = MobileRootUrl + "mypost";
        //public static String UserUpLoadAvatarUrl = MobileRootUrl + "uploadavatar&uid={0}&size={1}";
       // public static String UserDownLoadAvatarUrl = MobileRootUrl + "downloadavatar&uid={0}&size={1}";
        public static String UserInfoUrl = MobileRootUrl + "getuserinfo";
        public static String UserModifyUrl = MobileRootUrl + "usermodify";
        public static String UserMyNotificationUrl = MobileRootUrl + "mynotification";
        //论坛URL定义
        public static String ForumThreadListUrl = MobileRootUrl + "threadlist";
        public static String ForumNewThreadListUrl = MobileRootUrl + "newthreadlist";
        public static String ForumPostListUrl = MobileRootUrl + "postlist";
        public static String ForumPostNewThreadUrl = MobileRootUrl + "post_newthread";
        public static String ForumPostNewReplyUrl = MobileRootUrl + "post_newreply";
        public static String ForumSearchThreadUrl = MobileRootUrl + "searchthread";
        public static String ForumAdoptPostUrl = MobileRootUrl + "adoptpost";
        public static String ForumThreadOneUrl = MobileRootUrl + "threadone";
        //个人数据
        public static String ForumGetVideoStudyUrl = MobileRootUrl + "getvideostudy";
        public static String ForumSaveVideoStudyUrl = MobileRootUrl + "savevideostudy";
        //笔记本-科目数据
        //sys
        public static String BJB_SubjectDataUrl = MobileRootUrl + "bjb_subjectdata";
        public static String BJB_ThreadDataUrl = MobileRootUrl + "bjb_threaddata";
        public static String BJB_PostDataUrl = MobileRootUrl + "bjb_postdata";
        //my
        public static String BJB_MyThreadDataUrl = MobileRootUrl + "bjb_mythreaddata";
        public static String BJB_MyPostDataUrl = MobileRootUrl + "bjb_mypostdata";
        public static String BJB_PostMyPostDataUrl = MobileRootUrl + "bjb_postmypostdata";
        public static String BJB_PostMyGroupPostDataUrl = MobileRootUrl + "bjb_postmygrouppostdata";
        public static String BJB_DelMyPostDataUrl = MobileRootUrl + "bjb_delmypostdata";
        public static String BJB_DelMyGroupPostDataUrl = MobileRootUrl + "bjb_delmygrouppostdata";
        public static String BJB_UpdateMyPostDataUrl = MobileRootUrl + "bjb_updatemypostdata";
        public static String BJB_CreateMyCroupDataUrl = MobileRootUrl + "bjb_createmygroupdata";
        public static String BJB_CreateMyPostDataUrl = MobileRootUrl + "bjb_createmypostdata";
        //新的视频列表
        public static String GET_NEWVIDEOLIST = MobileRootUrl + "get_newvideolist";
        public static String ADD_NEWVIDEOCOUNT= MobileRootUrl + "add_newvideocount";
        
        //图片上传下载
        public static String  imagemyqcloudappid = "10003688";
        public static String  imagemyqcloudbucket = "5841314520";
        public static String ImageMainUrl = "http://"+imagemyqcloudbucket + "-" + imagemyqcloudappid + ".image.myqcloud.com/";
        public static String ImageSignUrl = MobileRootUrl + "imagesgin";
        
        public static Point screenSize = new Point();
        public static DisplayMetrics displayMetrics = null;
        public static String pkName = "";
        public static String baseAppPath = "";
        public static String baseSDPath = "";
    }
	static public class Func{
		static public void init(){
			//屏幕分辨率
			Config.displayMetrics = MainTabActivity.mMainTabActivity.getResources().getDisplayMetrics();
			//获得屏幕的宽和高    
			MainTabActivity.mMainTabActivity.getWindowManager().getDefaultDisplay().getSize(Config.screenSize);
			//包名
			Config.pkName = MainTabActivity.mMainTabActivity.getPackageName();
			Config.baseAppPath = "/data/data/" + Config.pkName + "/";
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				Config.baseSDPath = Environment.getExternalStorageDirectory() + "/Android/data/" + Config.pkName + "/";
		}
		public static String hexStr2Str(String hexStr) {  
		    String str = "0123456789abcdef";  
		    char[] hexs = hexStr.toCharArray();  
		    byte[] bytes = new byte[hexStr.length() / 2];  
		    int n;  
		    for (int i = 0; i < bytes.length; i++) {  
		        n = str.indexOf(hexs[2 * i]) * 16;  
		        n += str.indexOf(hexs[2 * i + 1]);  
		        bytes[i] = (byte) (n & 0xff);  
		    }  
		    return new String(bytes);  
		} 
		static public void showSoftInput(Context _c){
			InputMethodManager imm = (InputMethodManager) _c.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		}
		static public void showSoftInput(Context _c, View _v){
			_v.requestFocus();
			InputMethodManager imm = (InputMethodManager) _c.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.toggleSoftInputFromWindow(_v.getWindowToken(), InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		}
		static public void hideSoftInput(Context _c, View _v){
			InputMethodManager imm = (InputMethodManager) _c.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(_v.getWindowToken(), 0);
		}  
		static public void ShowToast(String _s){
			Toast.makeText(MainTabActivity.mMainTabActivity, _s, Toast.LENGTH_SHORT).show();
		}  
		//时间戳转换最近日期
		@SuppressLint("SimpleDateFormat") static public String timeStamp2Recently(long timeStamp){
			Date curdate = new Date(); 
			long newtimeStamp = curdate.getTime()/1000 - timeStamp;
			long days = newtimeStamp / (60*60*24);
			long hours = newtimeStamp / (60*60);
			long mins = newtimeStamp / (60);
			if(days > 0){
				if(days <= 10)
					return days + "天前";
				else{
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
					return fmt.format(new Date(timeStamp*1000));
				}
			}
			else if(hours > 0){
				return hours + "小时前";
			}
			else if(mins > 0){
				return mins + "分钟前";
			}
			else{
				if(newtimeStamp < 3)
					return "刚刚";
				else
					return newtimeStamp + "秒前";
			}
		}
		//时间戳转换成日期
		@SuppressLint("SimpleDateFormat") static public String timeStamp2Date(long timeStamp){
			return new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date(timeStamp * 1000));  
		}
		static public int getHeadIconBySex(int _s){
			int id = R.drawable.default_headicon;
			if(_s == 1){
				id = R.drawable.default_headicon_boy;
			}
			else if(_s == 2){
				id = R.drawable.default_headicon_gril;
			}
			return id;
		}  
		public static Bitmap drawableToBitmap(Drawable drawable) {
	        // 取 drawable 的长宽
	        int w = drawable.getIntrinsicWidth();
	        int h = drawable.getIntrinsicHeight();

	        // 取 drawable 的颜色格式
	        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
	                : Bitmap.Config.RGB_565;
	        // 建立对应 bitmap
	        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
	        // 建立对应 bitmap 的画布
	        Canvas canvas = new Canvas(bitmap);
	        drawable.setBounds(0, 0, w, h);
	        // 把 drawable 内容画到画布中
	        drawable.draw(canvas);
	        return bitmap;
	    }
		 static public Bitmap bytes2Bitmap(byte[] b) {
	         if (b.length != 0) {
	              return BitmapFactory.decodeByteArray(b, 0, b.length);
	         } else {
	              return null;
	         }
	     }
		private static final int ROTATION = 999;
		public static synchronized String nextUID(){
			int idx = new Random().nextInt(ROTATION);
		    return Long.toString(System.currentTimeMillis()) + Integer.toString(idx);
	    }
		
		public static int dip2px(float dpValue) {
			
			  final float scale = Config.displayMetrics.density;
			  return (int) (dpValue * scale + 0.5f);
		} 
		public static int px2dip(float pxValue){
			final float scale = Config.displayMetrics.density;
			return (int)(pxValue / scale +0.5f);
		}
		static public String getLocalData( String _name, String _key){
			if(_name.isEmpty())
				return "";
			SharedPreferences sp = MainTabActivity.mMainTabActivity.getSharedPreferences(_name,Context.MODE_PRIVATE);
			if(sp != null){
				return sp.getString(_key, "");
			}
			return null;
		}
		static public void saveLocalData(String _name, String _key, String _data){
			if(_name.isEmpty() || _key.isEmpty())
				return ;
			SharedPreferences sp = MainTabActivity.mMainTabActivity.getSharedPreferences(_name,Context.MODE_PRIVATE);
			Editor editor = sp.edit();  
			if(sp != null){
				editor.putString(_key, _data);  
				editor.commit();
			}
		}
		static public String second2Min(int _t){
			int min = _t / 60;
			int sec = _t % 60;
			return String.format("%02d:%02d",min,sec);
		}
		static public String second2Hour(int _t){
			int hour = _t / 3600;
			int min = _t % 3600 / 60;
			int sec = _t % 3600 % 60;
			return String.format("%02d:%02d:%02d",hour, min,sec);
		}
		public static List<Integer> getRandomNumbers(int count)
        {
            List<Integer> uesdIdx = new ArrayList<Integer>();
            for (int i = 0; i < count; i++)
                uesdIdx.add(i);
            return uesdIdx;
        }
	}
}
