using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QQClient
{
    public partial class QQ
    {
        //下一次加好友时间
        DateTime NextAddFriendTime
        {
            set
            {
                qqinfo.addFriendTime = value;
                Table.UpdateTable_accountex(qqinfo.qq, new string[,] { { "next_add_friend_time", value.ToString() } });
            }

            get { return qqinfo.addFriendTime; }
        }

        //下一次刷新数据时间
        DateTime NextRefreshDataTime
        {
            set
            {
                qqinfo.refshDataTime = value;
                Table.UpdateTable_accountex(qqinfo.qq, new string[,] { { "next_refresh_data_time", value.ToString() } });
            }

            get { return qqinfo.refshDataTime; }
        }


        //好友添加失败
        //失败类型：需要输入验证码 查找受限 ...
        public void OnFriendFindFail(string qq, string errorType, ErrorType type)
        {
            switch(type)
            {
                    //需要输入验证码
                case ErrorType.SecurityCode:
                    NextAddFriendTime = NextAddFriendTime + new TimeSpan(12, 0, 0);
                    Debug.LogLine(string.Format("{0}需要输入验证码,12小时后再加好友", qqinfo.qq));
                    break;
                    //被暂停加好友
                case ErrorType.AddFriendSuspended:
                    NextAddFriendTime = NextAddFriendTime + new TimeSpan(24, 0, 0);
                    Debug.LogLine(string.Format("{0}需要输入验证码,24小时后再加好友", qqinfo.qq));
                    break;
                //如果已经是好友了，成功吧
                case ErrorType.AlreadyFriend:
                    OnFriendFindSuccess(qq,"已同意");
                    break;
                //需要正确回答问题，视为禁止加好友
                case ErrorType.AnswerQuestion:
                    OnFriendFindSuccess(qq, "禁止加好友");
                    break;
                default:
                    Debug.Log("添加好友失败：" + qq + " " + errorType);
                    break;
            }
        }

        //好友添加成功
        public void OnFriendFindSuccess(string qq, string state)
        {
            //30分钟后再添加
            NextAddFriendTime = NextAddFriendTime + new TimeSpan(0, 20, 0);

            //更新加好友状态
            Table.UpdateTable_friend(qqinfo.qq, new string[,] { { "qqfri", qq }, { "addtime", DateTime.Now.ToString() }, { "state", state } });
            //标记用户中心，该号码已经加了好友
            Table.UpdateTable_user(qq, new string[,] { { "lockip", "ok" } });
        }
    }
}
