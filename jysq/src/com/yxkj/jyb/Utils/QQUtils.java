package com.yxkj.jyb.Utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class QQUtils {

	/****************
	*
	* 发起添加群流程。群号：中学生记忆联盟(460625088) 的 key 为： ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR
	* 调用 joinQQGroup(ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR) 即可发起手Q客户端申请加群 中学生记忆联盟(460625088)
	*
	* @param key 由官网生成的key
	* @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	******************/
	//中学生记忆联盟(460625088) 的 key 为： ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR
	@SuppressWarnings("serial")
	static private Map<Integer, String> qqGroupKey = new HashMap<Integer, String>(){{
		put(460625088, "ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR");
	}};
	static public boolean joinQQGroup(Context _c, int qq) {
		
		if(!qqGroupKey.containsKey(qq))
			return false;
		String key = qqGroupKey.get(qq);
	    Intent intent = new Intent();
	    intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
	   // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	    try {
	    	_c.startActivity(intent);
	        return true;
	    } catch (Exception e) {
	        // 未安装手Q或安装的版本不支持
	        return false;
	    }
	}
}
