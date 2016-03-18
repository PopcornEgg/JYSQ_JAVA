using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Automation;


//1 个独立的QQ
namespace QQClient
{
    public class QQInfo
    {
        public string qq;
        public string pws;
        public string nickname;
        public string state;
        public bool allow_login = true;    //是否允许登陆
        public DateTime locktime;       //被锁时间
        public DateTime addFriendTime;  //添加好友时间
        public DateTime refshDataTime;  //刷新数据时间
    }



    public partial class QQ
    {
        QQInfo qqinfo;
        QQClient parent;
        public delegate string FriendDelegate(); 
        AutomationElement appElement;
        List<QQAction> actions = new List<QQAction>();

        public QQInfo _QQInfo { get { return qqinfo; } }
        public QQClient Parent { get { return parent; } }
        public AutomationElement AppElement { get { return appElement; } }
        public QQ(QQClient parent) { this.parent = parent; }

        public bool Start(QQInfo qq)
        {
            if (!qq.allow_login)
            {
                Debug.LogLine(string.Format("{0}被限制登录,原因：{1}", qq.qq, qq.state));
                return false;
            }

            qqinfo = qq;

            AutoLogin autoLogin = new AutoLogin();
            appElement = autoLogin.Login(qq.qq, qq.pws, qq.nickname);
            if (appElement == null)
            {
                OnLoginFail(qq.qq, autoLogin._ErrorType, autoLogin._ErrorDesc);
                return false;
            }

            //根据QQ情况分配执行命令
            DateTime endtime = DateTime.Now + new TimeSpan(1, 0, 0);
            
            //每个QQ必挂机1小时
            actions.Add(QQAction.Create<QAC_GuaJi>(this, endtime));

            //加好友
            if (qq.addFriendTime < endtime)
            {
                actions.Add(QQAction.Create<QAC_FindFriend>(this, qq.addFriendTime));
            }

            //统计数据上报
            if (qq.refshDataTime < endtime)
            {
                actions.Add(QQAction.Create<QAC_StatisticsFriendCount>(this, qq.refshDataTime));
            }

            return true;
        }

        //返回是否已经结束
        public bool Tick()
        {
            List<QQAction> remove = new List<QQAction>();
            foreach (QQAction action in actions)
            {
                if (action.Time < DateTime.Now)
                {
                    action.Run();
                    remove.Add(action);
                }
            }

            foreach (QQAction action in remove)
            {
                actions.Remove(action);
            }

            return !(actions.Count > 0);
        }

        public void Stop()
        {
            if (appElement == null)
                return;

            AutoLogin.LoginOut(appElement);
            appElement = null;

            actions.Clear();
        }
    }
}
