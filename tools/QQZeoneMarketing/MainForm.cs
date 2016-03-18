using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using utitls;
using QQZeoneMarketing.SpiderCommons;

namespace QQZeoneMarketing
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();

            this.Text = "PHP：" + Config.ROOT_URL;
        }

        private void btn_Sprder_Click(object sender, EventArgs e)
        {
            flowLayoutPanel1.Visible = false;
            this.Width = 1024;
            this.Height = 768;

            Spider spider = new Spider();
            spider.Dock = DockStyle.Fill;

            panel1.Controls.Clear();
            panel1.Controls.Add(spider);

            this.Text = "爬虫";

        }

        private void btn_LiuHen_Click(object sender, EventArgs e)
        {
            flowLayoutPanel1.Visible = false;
            this.Width = 1024;
            this.Height = 768;

            Stopping stopping = new Stopping();
            stopping.Dock = DockStyle.Fill;

            panel1.Controls.Clear();
            panel1.Controls.Add(stopping);

            this.Text = "留痕";
        }

        private void button1_Click(object sender, EventArgs e)
        {
            flowLayoutPanel1.Visible = false;
            this.Width = 1024;
            this.Height = 768;

            DianZanTest stopping = new DianZanTest();
            stopping.Dock = DockStyle.Fill;

            panel1.Controls.Clear();
            panel1.Controls.Add(stopping);

            this.Text = button1.Text;
        }

        private void button2_Click(object sender, EventArgs e)
        {
            flowLayoutPanel1.Visible = false;
            this.Width = 1024;
            this.Height = 768;

            Comment c = new Comment();
            c.Dock = DockStyle.Fill;

            panel1.Controls.Clear();
            panel1.Controls.Add(c);

            this.Text = "说话";
        }

        private void btn_QQClient_Click(object sender, EventArgs e)
        {
            flowLayoutPanel1.Visible = false;
            this.Width = 500;
            this.Height = 800;

            QQClientUI c = new QQClientUI();
            c.Dock = DockStyle.Fill;

            panel1.Controls.Clear();
            panel1.Controls.Add(c);

            this.Text = "QQ客户端";
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            utitls.LogFileEx.stop();
            utitls.LogFile.stop();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            try
            {
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                //nvc.Add("order", "heat");
                //nvc.Add("desc", "DESC");
                nvc.Add("fields", "qq,nickname");
                nvc.Add("count", "10");
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(Config.LOCKQQ_ZONE_URL, 10000, nvc);
                    if (!sttuas.Contains("_fail_"))
                    {
                        MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                        for (int i = 0; i < jsons.GetListCount(); i++)
                        {
                            MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                            UserInfoDB info = new UserInfoDB();
                            //每一个参数必须 Func.MyUrlDeCode();
                            info.qq = Func.MyUrlDeCode(json["qq"].AsString());
                            //info.pqq = json["pqq"].AsString();
                            info.nickname = Func.MyUrlDeCode(json["nickname"].AsString());
//                             info.age = json["age"].AsInt();
//                             info.sex = Func.MyUrlDeCode(json["sex"].AsString());
//                             info.birthday = Func.MyUrlDeCode(json["birthday"].AsString());
//                             info.mood = json["mood"].AsInt();
//                             info.photo = json["photo"].AsInt();
//                             info.blog = json["blog"].AsInt();
//                             info.heat = json["heat"].AsInt();
//                             info.zone_authority = json["zone_authority"].AsInt();
//                             info.friend_authority = json["friend_authority"].AsInt();
//                             info.logintime = Func.MyUrlDeCode(json["logintime"].AsString());
//                             info.label = Func.MyUrlDeCode(json["label"].AsString());
//                             info.addfriend = json["addfriend"].AsInt();
//                             info.used = json["used"].AsInt();
                        }

                    }
                }
            }

            catch (Exception )
            {
            }
        }

        private void button4_Click(object sender, EventArgs e)
        {
            try
            {
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                nvc.Add("qq", "1002027357");
                //nvc.Add("lockip", "ok");
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(Config.UNLOCKEQQ_ZONE_URL, 10000, nvc);
                    if (sttuas != null)
                    {
                        if (sttuas.Contains("_succ_"))
                        {
                        }
                    }
                }
            }

            catch (Exception)
            {
            }
        }

        private void flowLayoutPanel1_Paint(object sender, PaintEventArgs e)
        {

        }
    }
}
