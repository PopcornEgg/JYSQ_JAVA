package com.yxkj.jyb;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
//插件-信鸽
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
		//插件-信鸽
		// 开启logcat输出，方便debug，发布时请关闭
		// XGPushConfig.enableDebug(appContext, true);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
        //XGPushManager.registerPush(appContext);		
        if( appContext == null){
			XGPushManager.registerPush(appContext, new XGIOperateCallback() {
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
	
	
	/**
	 * 根据不同的模式，建议设置的开关状态，可根据实际情况调整，仅供参考。
	 * 
	 * @param isDebugMode
	 *            根据调试或发布条件，配置对应的MTA配置
	 */
	private static void Init_MTA(Context appContext)
	{
		// 禁止MTA打印日志
		StatConfig.setDebugEnable(false);
		// 根据情况，决定是否开启MTA对app未处理异常的捕获
		StatConfig.setAutoExceptionCaught(true);
		// 选择默认的上报策略
		StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
		// 未处理的异常自动上传
		StatConfig.setAutoExceptionCaught(true);
		
		 String appkey = "AX9BPXT4W91D";
		 // 初始化并启动MTA
		 // 第三方SDK必须按以下代码初始化MTA，其中appkey为规定的格式或MTA分配的代码。
		 // 其它普通的app可自行选择是否调用
		 try {
		   // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
		   StatService.startStatService(appContext, appkey,
		       com.tencent.stat.common.StatConstants.VERSION);
		 } catch (MtaSDkException e) {
			 GlobalUtility.Func.ShowToast( "MTA 启动失败");
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
