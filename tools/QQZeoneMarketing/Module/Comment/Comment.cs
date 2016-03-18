using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using QQZeoneMarketing.SpiderCommons;
using System.Collections.Specialized;
using System.Threading;
using System.Net;
using utitls;

namespace QQZeoneMarketing
{
    public partial class Comment : UserControl
    {
        CommentItem commentItem = null;
        List<UserInfoDB> dblist = new List<UserInfoDB>();
        int curIdx = -1;
        Random random = new Random();
        public Comment()
        {
            InitializeComponent();

            commentItem = new CommentItem(this);
            label3.Text = "服务器URL：" + Config.ROOT_URL.Replace("?module=", "");
            downloadUserInfo();
            updateCurText("");
            webBrowser1.DocumentCompleted += new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);
        }

        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            if (webBrowser1.ReadyState != WebBrowserReadyState.Complete)
            {
                return;
            }

            label4.Text = "我的QQ：" + Func.getMyQQ(webBrowser1);
        }

        private void btn_Start_Click(object sender, EventArgs e)
        {
            timer1.Enabled = !timer1.Enabled;
            btn_Start.Text = timer1.Enabled ? "停止" : "开始";
            curIdx = -1;
        }
        private UserInfoDB getNextUserInfo()
        {
            if (dblist.Count <= 0)
                return null;

            if (++curIdx > (dblist.Count - 1))
            {
                return null;
            }

            return dblist[curIdx];
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (commentItem.isCanDoNext)
            {
                UserInfoDB info = getNextUserInfo();
                if (info != null)
                {
                    commentItem.Start(info.qq);
                    info.used = 1;
                    updateCurText(info.qq);
                }
                else
                    label5.Text = "已发送到最后一个用户！！！";
            }
        }

        private void downloadUserInfo()
        {
            try
            {
#if DEBUG
                String[] qqs = { 
                   //"1532931245", 
                   //"657565846", 
                   "2517172830", 
                   //"305723370",
                   //"947087352",
                };
                for (int i = 0; i < qqs.Length; i++)
                {
                    UserInfoDB info = new UserInfoDB();
                    info.qq = qqs[i];
                    dblist.Add(info);
                }
#else
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                nvc.Add("order", "time");
                nvc.Add("desc", "DESC");
                nvc.Add("start", "0");
                nvc.Add("count", "0");
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(Config.GETLIST_ZONE_URL, 10000, nvc);
                    if (sttuas.Length > 10)
                    {
                        MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                        for (int i = 0; i < jsons.GetListCount(); i++)
                        {
                            MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                            UserInfoDB info = new UserInfoDB();
                            info.qq = json["qq"].AsString();
                            info.pqq = json["pqq"].AsString();
                            info.nickname = Func.MyUrlDeCode(json["nickname"].AsString());
                            info.age = json["age"].AsInt();
                            info.sex = Func.MyUrlDeCode(json["sex"].AsString());
                            info.birthday = Func.MyUrlDeCode(json["birthday"].AsString());
                            info.mood = json["mood"].AsInt();
                            info.photo = json["photo"].AsInt();
                            info.blog = json["blog"].AsInt();
                            info.heat = json["heat"].AsInt();
                            info.zone_authority = json["zone_authority"].AsInt();
                            info.friend_authority = json["friend_authority"].AsInt();
                            info.logintime = Func.MyUrlDeCode(json["logintime"].AsString());
                            info.label = Func.MyUrlDeCode(json["label"].AsString());
                            info.addfriend = json["addfriend"].AsInt();
                            info.used = json["used"].AsInt();
                            dblist.Add(info);
                        }

                    }
                }
#endif
            }
            catch (Exception e)
            {
                LogFile.add(e.ToString(), "Stopping.cs:updateUserInfo", 1);
            }
        }

        public void updateCurText(String qq)
        {
            label1.Text = String.Format("当前 {0}/{1}\nQQ:{2}", curIdx + 1, dblist.Count, qq);
        }
        int fCount = 0;
        public void updateFailText(bool _b)
        {
            if (!_b)
            {
                fCount++;
            }
            label2.Text = String.Format("失败 {0}/{1}", fCount, dblist.Count);
        }
    }
}
