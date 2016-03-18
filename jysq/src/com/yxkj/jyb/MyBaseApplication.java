package com.yxkj.jyb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.tencent.base.util.ProcessUtils;
import com.tencent.base.Global;
import com.tencent.wns.client.inte.WnsClientFactory;
import com.tencent.wns.client.inte.WnsService;
import com.tencent.wns.client.inte.WnsService.GlobalListener;
import com.tencent.wns.client.log.WnsClientLog;
import com.yxkj.jyb.Utils.Debuger;

/**
 * 业务需要实现自己的{@code android.app.Application}
 * 
 * @author chadguo
 * 
 */
public class MyBaseApplication extends Application
{
    private final static String TAG = "BaseApplication";
    private int mActivityVisibleCount = 0;
    private boolean mIgnoreActivityVisibleCountChange = false;

    private WnsService wns;

    GlobalListener listenerImpl = new GlobalListener()
    {

        @Override
        public void onPrintLog(int level, String tag, String msg, Throwable tr)
        {
            // 打开以下注释进程测试，以此回调的目的是应用可以输出日志到自己的持久化
            // switch (level)
            // {
            // case Log.DEBUG:
            // Log.d(tag, msg, tr);
            // break;
            // case Log.ASSERT:
            // case Log.ERROR:
            // Log.e(tag, msg, tr);
            // break;
            // case Log.INFO:
            // Log.i(tag, msg, tr);
            // break;
            // case Log.VERBOSE:
            // Log.v(tag, msg, tr);
            // break;
            // case Log.WARN:
            // Log.w(tag, msg, tr);
            // break;
            // default:
            // Log.v(tag, msg, tr);
            //
            // }
        }

		@Override
		public void showDialog(String title, final String content) {
			//Activity act=MainTabActivity.mMainTabActivity.getInstance().getCurrentActiveActivity();
			if(MainTabActivity.mMainTabActivity != null && !MainTabActivity.mMainTabActivity.isFinishing()){
				MainTabActivity.mMainTabActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//DialogUtils.makeWarningDialog(getApplicationContext(), content).show();
					}
				});
			}
		}

    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        // 初始化WNS全局参数
        Global.init(this, listenerImpl);

        wns = WnsClientFactory.getThirdPartyWnsService();
        // 判断是否主进程
        boolean isMain = ProcessUtils.isMainProcess(this);

        // important-只能够在主进程执行
        if (isMain)
        {
            // 初始化app的身份信息，不必在Application中调用，但是必须在使用WnsService接口之前调用
            int your_appid = 202230;// your appid
            String your_appversion = Integer.toString(com.yxkj.jyb.version.Common.getVerCode(this));
            String your_channelid = "jyb";
            boolean isQuickVerification = false; //是否开启快速验证模式
            int whichDns = 0; //设置默认dns环境，无特殊说明请保持为0。
            wns.initWnsWithAppInfo(your_appid, your_appversion, your_channelid, isQuickVerification, whichDns);

            // 设置测试服务器,正式版本可以删除这段代码**********开始
            if(Debuger.USE_WNS_DEBUG){
                 wns.setDebugIp(Debuger.WNS_DEBUG_IP, Debuger.WNS_DEBUG_IP_PORT);
            }
            // 设置测试服务器,正式版本也可以删除这段代码**********结束

            // 启动wns服务
            wns.startWnsService();

            // important-
            // 监控应用是否处于前台，应用需要将前后台变化状态通知到WNS,WnsService.setBackgroundMode(boolean)
            // 为适配低版本平台，前后台变化状态切换放到BaseActivity中

            /*registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
            {

                @Override
                public void onActivityStarted(Activity activity)
                {
                    updateActivityVisibleCount(true, mIgnoreActivityVisibleCountChange);
                    mIgnoreActivityVisibleCountChange = false;

                }

                @Override
                public void onActivityStopped(Activity activity)
                {
                    mIgnoreActivityVisibleCountChange = isActivityConfigChanging(activity);
                    updateActivityVisibleCount(false, mIgnoreActivityVisibleCountChange);
                }

                @Override
                public void onActivityDestroyed(Activity activity)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onActivityPaused(Activity activity)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onActivityResumed(Activity activity)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState)
                {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState)
                {
                    // TODO Auto-generated method stub

                }

            });*/
        }
    }

    private void updateActivityVisibleCount(boolean increase, boolean ignore)
    {
        if (increase)
        {
            final int prev = mActivityVisibleCount;
            mActivityVisibleCount++;
            if (prev == 0 && !ignore)
            {
                dispatchApplicationEnterForeground();
            }
        }
        else
        {
            mActivityVisibleCount--;
            if (mActivityVisibleCount == 0 && !ignore)
            {
                dispatchApplicationEnterBackground();
            }
        }
    }

    private void dispatchApplicationEnterForeground()
    {
        WnsClientLog.w(TAG, "dispatchApplicationEnterForeground");
        wns.setBackgroundMode(false);

    }

    private void dispatchApplicationEnterBackground()
    {
        WnsClientLog.w(TAG, "dispatchApplicationEnterBackground");
        wns.setBackgroundMode(true);
    }

    @SuppressLint("NewApi")
    private boolean isActivityConfigChanging(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            return activity.isChangingConfigurations();
        }
        return activity.getChangingConfigurations() != 0;
    }

    public void activityStarted(Activity activity)
    {
        updateActivityVisibleCount(true, mIgnoreActivityVisibleCountChange);
        mIgnoreActivityVisibleCountChange = false;

    }

    public void activityStopped(Activity activity)
    {
        mIgnoreActivityVisibleCountChange = isActivityConfigChanging(activity);
        updateActivityVisibleCount(false, mIgnoreActivityVisibleCountChange);
    }

}
