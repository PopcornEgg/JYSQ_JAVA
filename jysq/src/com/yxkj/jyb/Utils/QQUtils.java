package com.yxkj.jyb.Utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class QQUtils {

	/****************
	*
	* �������Ⱥ���̡�Ⱥ�ţ���ѧ����������(460625088) �� key Ϊ�� ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR
	* ���� joinQQGroup(ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR) ���ɷ�����Q�ͻ��������Ⱥ ��ѧ����������(460625088)
	*
	* @param key �ɹ������ɵ�key
	* @return ����true��ʾ������Q�ɹ�������fals��ʾ����ʧ��
	******************/
	//��ѧ����������(460625088) �� key Ϊ�� ExcMvTg8gLiOXMyCtYD0Cx-RTyJsZ5jR
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
	   // ��Flag�ɸ��ݾ����Ʒ��Ҫ�Զ��壬�����ã����ڼ�Ⱥ���水���أ�������Q�����棬�����ã������ػ᷵�ص������Ʒ����    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	    try {
	    	_c.startActivity(intent);
	        return true;
	    } catch (Exception e) {
	        // δ��װ��Q��װ�İ汾��֧��
	        return false;
	    }
	}
}
