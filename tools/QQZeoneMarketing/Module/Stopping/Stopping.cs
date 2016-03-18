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
    public partial class Stopping : UserControl
    {
        List<StoppingItem> items = new List<StoppingItem>();
        List<UserInfoDB> dblist = new List<UserInfoDB>();
        int curIdx = -1;
        Random random = new Random();
        public Stopping()
        {
            InitializeComponent();
#if DEBUG
            for (int i = 0; i < 1; ++i)
                    items.Add(new StoppingItem());
#else
            for (int i = 0; i < 2; ++i)
                    items.Add(new StoppingItem());
#endif
            downloadUserInfo();
        }

        private void btn_Start_Click(object sender, EventArgs e)
        {
            timer1.Enabled = !timer1.Enabled;
            btn_Start.Text = timer1.Enabled ? "停止" : "开始";
        }
        private UserInfoDB getNextUserInfo()
        {
            if (dblist.Count <= 0)
                return null;

            if (++curIdx > (dblist.Count - 1))
            {
                curIdx = 0;
            }

            return dblist[curIdx];
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            for (int i = 0; i < items.Count; ++i)
            {
                if (!items[i].Visible)
                {
                    UserInfoDB info = getNextUserInfo();
                    if (info != null)
                    {
                        items[i].Start(info.qq);
                    }
                }
            }
        }

        private void downloadUserInfo()
        {
            try{
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                //nvc.Add("order", "logintime");
                //nvc.Add("desc", "DESC");
                nvc.Add("start", "0");
                nvc.Add("count", "100");
                nvc.Add("tag", "used");
                nvc.Add("tagval", "0");
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(Config.GETLIST_ZONE_URL, 10000, nvc);
                    if (sttuas.Length > 10)
                    {
                        this.listBox1.Items.Clear();
                        MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                        for (int i = 0; i < jsons.GetListCount(); i++)
                        {
                            MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                            UserInfoDB info = new UserInfoDB();
                            info.qq         = json["qq"].AsString();
                            info.pqq = json["pqq"].AsString();
                            info.nickname = Func.MyUrlDeCode(json["nickname"].AsString());
                            info.age = json["age"].AsInt();
                            info.sex = Func.MyUrlDeCode(json["sex"].AsString());
	                        info.birthday   = Func.MyUrlDeCode(json["birthday"].AsString());
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

                            this.listBox1.Items.Add(info.qq);
                        }
           
                    }
                }
            }
           
            catch (Exception e)
            {
                LogFile.add(e.ToString(), "Stopping.cs:updateUserInfo", 1);
            }
        }
    }
}
