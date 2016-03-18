using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.IO;
using QQZeoneMarketing.SpiderCommons;
using System.Threading;
using System.Net;
using utitls;

namespace QQZeoneMarketing.SpiderCommons
{
    class Common
    {
        protected DateTime nextTime = DateTime.MaxValue;
        protected bool completed = false;
        public Spider spider = null;
        public utitls.MyWebBrowser webBrowser1 = null;
        public void DocumentCompleted() { completed = true; }
        public virtual void Enter() { } //进入：初始化当前网页
        public virtual void Tick() { }  //循环：检查状态
        public virtual void Leave() { } //离开：进入下一个命令

        protected void ChangeCommon(string name) { spider.ChangeCommon(name); }
        protected void Log(string str) { spider.Log(str); }

        protected bool Enable
        {
            get
            {
                if (completed)
                {
                    completed = false;
                    nextTime = DateTime.Now + new TimeSpan(0, 0, 1);
                    return false;
                }
                else if (DateTime.Now > nextTime)
                {
                    nextTime = DateTime.MaxValue;
                    return true;
                }

                return false;
            }
        }
    }

    public class UserInfoDB
    {
        public string qq;           //QQ号
        public string pqq="";           //QQ号
        public string nickname = "";     //昵称
        public int age = 0;     //年龄
        public string sex = "";     //性别
        public string birthday = "";//生日
        public int mood = -1;    //说说水量
        public int photo = -1;   //相册数量
        public int blog = -1;    //日志数量
        public int heat = -1;    //热度 mood+photo+blog
        public int zone_authority = -1;    //访问空间权限
        public int friend_authority = -1;    //加好友权限
        public string logintime = "";    //最后更新时间
        public string label = "";     //关键字，平时聊天内容
        public int addfriend = 0;    //是否加好友
        public int used = 0;    //是否已爬过

        public override string ToString()
        {
            return string.Format("QQ：{0} 昵称：{1} 年龄：{2} 性别：{3} 生日：{4} 说说：{5} 照片：{6} 日志：{7} 最后时间：{8} 内容：{9}",
                qq, nickname, age, sex, birthday, mood, photo, blog, logintime, label);
        }
        public static PostData userInfo2PostData(UserInfoDB info)
        {
            if (info == null)
                return null;
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            nvc.Add("qq", info.qq);
            nvc.Add("pqq", info.pqq);
            nvc.Add("nickname", info.nickname);
            nvc.Add("age", info.age);
            nvc.Add("sex", info.sex);
            nvc.Add("birthday", info.birthday);
            nvc.Add("mood", info.mood);
            nvc.Add("photo", info.photo);
            nvc.Add("blog", info.blog);
            nvc.Add("heat", info.heat);
            nvc.Add("zone_authority", info.zone_authority);
            nvc.Add("friend_authority", info.friend_authority);
            nvc.Add("logintime", info.logintime);
            nvc.Add("label", info.label);
            nvc.Add("addfriend", info.addfriend);
            nvc.Add("used", info.used);
            return nvc;
        }
    }
}
