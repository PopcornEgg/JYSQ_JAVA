package com.yxkj.jyb;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
//���-�Ÿ�
import com.tencent.android.tpush.XGPushManager;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tencent.stat.StatReportStrategy;
import com.yxkj.jyb.Utils.UserUtils.DataUtils;

public class Plugins {
	public static void Init(Context appContext)
	{
		Init_XG(appContext);
		Init_MTA(appContext);
	}
	
	private static void Init_XG(Context appContext )
	{
		//���-�Ÿ�
		// ����logcat���������debug������ʱ��ر�
		// XGPushConfig.enableDebug(appContext, true);
		// �����Ҫ֪��ע���Ƿ�ɹ�����ʹ��registerPush(getApplicationContext(), XGIOperateCallback)��callback�汾
		// �����Ҫ���˺ţ���ʹ��registerPush(getApplicationContext(),account)�汾
		// ����ɲο���ϸ�Ŀ���ָ��
		// ���ݵĲ���ΪApplicationContext
        //XGPushManager.registerPush(appContext);		
        if( appContext == null){
			XGPushManager.registerPush(appContext, new XGIOperateCallback() {
				@Override
				public void onSuccess(Object data, int flag) {
					Log.d("TPush", "ע��ɹ����豸tokenΪ��" + data);
				}

				@Override
				public void onFail(Object data, int errCode, String msg) {
					Log.d("TPush", "ע��ʧ�ܣ������룺" + errCode + ",������Ϣ��" + msg);
				}
			});
		}
	}
	
	
	/**
	 * ���ݲ�ͬ��ģʽ���������õĿ���״̬���ɸ���ʵ����������������ο���
	 * 
	 * @param isDebugMode
	 *            ���ݵ��Ի򷢲����������ö�Ӧ��MTA����
	 */
	private static void Init_MTA(Context appContext)
	{
		// ��ֹMTA��ӡ��־
		StatConfig.setDebugEnable(false);
		// ��������������Ƿ���MTA��appδ�����쳣�Ĳ���
		StatConfig.setAutoExceptionCaught(true);
		// ѡ��Ĭ�ϵ��ϱ�����
		StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		// δ������쳣�Զ��ϴ�
		StatConfig.setAutoExceptionCaught(true);
		
		 String appkey = "AX9BPXT4W91D";
		 // ��ʼ��������MTA
		 // ������SDK���밴���´����ʼ��MTA������appkeyΪ�涨�ĸ�ʽ��MTA����Ĵ��롣
		 // ������ͨ��app������ѡ���Ƿ����
		 try {
		   // ��������������Ϊ��com.tencent.stat.common.StatConstants.VERSION
		   StatService.startStatService(appContext, appkey,
		       com.tencent.stat.common.StatConstants.VERSION);
		 } catch (MtaSDkException e) {
			 GlobalUtility.Func.ShowToast( "MTA ����ʧ��");
		 }
	}
	
	public static void onResume(Context ctx) {
		StatService.onResume(ctx);
	}
	public static void onPause (Context ctx) {
		StatService.onPause(ctx);
	}

	public static void UnInit()
	{
		
	}
}
