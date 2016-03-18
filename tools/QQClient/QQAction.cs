using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Automation;

namespace QQClient
{
    public enum ErrorType
    {        
        ProtectedMode,  //处于保护模式，需要手动解封
        AddFriendSuspended, //添加好友被暂停使用	24小时后才允许添加好友
        NotFindUI,  //没有找到相关UI
        TimeOut,    //超时
        AlreadyFriend,  //已经是好友
        NotFindFriend,  //没有找到好友
        Exception,  //异常
        AnswerQuestion, //需要正确回答问题
        LoginException, //登录异常
        PasswordError, //登录密码错误
        SecurityCode,   //需要输入验证码
        UnknownError,   //未知错误
    }

    class QQAction
    {
        protected QQ qq;
        DateTime time;
        public DateTime Time { get { return time; } }
        public void Init(QQ qq, DateTime time) { this.qq = qq; this.time = time; }
        public virtual void Run() { }

        public static QQAction Create<T>(QQ qq, DateTime time) where T : QQAction,new()
        {
            QQAction action = new T();
            action.Init(qq, time);
            return action;
        }
    }

    //挂机时间
    class QAC_GuaJi : QQAction {}

    //查找好友
    class QAC_FindFriend : QQAction
    {
        public override void Run() 
        {
            try
            {
                string _qq = qq.Parent.GetFriend();
                if (_qq != null)
                {
                    AutoFriend autoFriend = new AutoFriend();
                    if (!autoFriend.FindFriend(qq.AppElement, _qq))
                    {
                        qq.OnFriendFindFail(_qq, autoFriend._ErrorDesc, autoFriend._ErrorType);
                        Debug.LogLine(autoFriend._ErrorDesc);
                    }
                    else
                    {
                        qq.OnFriendFindSuccess(_qq, "申请中");
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }
    }

    //统计好友数量
    class QAC_StatisticsFriendCount : QQAction
    {
        public override void Run()
        {
            AutomationElement appElement = AutoFriendEx.openMyFriendListUI(qq.AppElement);
            if (appElement != null)
            {
                List<String> qqs = AutoFriendEx.getMyFriendListQQs(appElement);
                Tools.closeNormalUI(qq.AppElement);
                updateQQAddFriendState(qq._QQInfo.qq, qqs);
            }
        }

        void updateQQAddFriendState(string myqq, List<String> qqs)
        {
            try
            {
                utitls.PostData nvc = new utitls.PostData();

                //更新加友状态
                foreach (string qqfri in qqs)
                {
                    Table.UpdateTable_friend(myqq, new string[,] { { "qqfri", qqfri }, { "state", "成为好友" } });
                    System.Threading.Thread.Sleep(100);
                }

                //更新好友数量
                Table.UpdateTable_accountex(myqq, new string[,] { { "fricount", qqs.Count.ToString() } });

                //更新日志
                Table.UpdateTable_accountex_log(myqq, new string[,] { { "fricount", qqs.Count.ToString() } });
            }
            catch (Exception)
            {
            }
        }
    }


    //更新加好友状态
    class QAC_AddFriendUpdate: QQAction
    {
        public override void Run()
        {
        }
    }
}
