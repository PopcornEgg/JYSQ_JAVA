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
using utitls;
using mshtml;
using System.Collections;

namespace QQZeoneMarketing
{
    public partial class DianZanTest : UserControl
    {
        Queue<UserInfoDB> dblist = new Queue<UserInfoDB>();
        List<string> success = new List<string>(); //已经点赞了的QQ
        List<string> failed = new List<string>(); //已经点赞了的QQ

        string curQQ;
        bool seturl = false;
        bool completed = false;

        string pro = "点赞";

        public DianZanTest()
        {
            InitializeComponent();

            webBrowser1.DocumentCompleted += new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);
            downloadUserInfo();
        }

        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            if (webBrowser1.ReadyState != WebBrowserReadyState.Complete)
            {
                return;
            }

            if (seturl)
            {
                seturl = false;
                completed = true;
                
            }
        }

        private void button_Start_Click(object sender, EventArgs e)
        {
            timer1.Enabled = !timer1.Enabled;
            button_Start.Text = timer1.Enabled ? "结束" : "开始";

            if( timer1.Enabled )
                Zhan();
        }

        void Zhan()
        {
            curQQ = dblist.Dequeue().qq;

            webBrowser1.Navigate(string.Format("http://user.qzone.qq.com/{0}/311", curQQ));
            seturl = true;
            completed = false;
            pro = "点赞";
        }


        private void timer1_Tick(object sender, EventArgs e)
        {
            if (completed)
            {
                completed = false;
                if (pro == "点赞")
                    PraiseFriend();
                else if (pro == "验证赞")
                {
                    PraiseFriendFix();
                    Zhan();
                }

                label1.Text = pro;
                label2.Text = string.Format("成功：{0}  失败：{1}", success.Count, failed.Count);
            }


            if (pro == "点赞完成")
            {
                seturl = true;
                webBrowser1.Navigate(string.Format("http://user.qzone.qq.com/{0}/311", curQQ));
                pro = "验证赞";
            }
        }


        private void PraiseFriendFix()
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

                for (int i = 0; i < htmlDoc.frames.length; ++i)
                {
                    object index = i;
                    mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                    IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                    mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("A");
                    IEnumerator tagie = tagls.GetEnumerator();
                    while (tagie.MoveNext())
                    {
                        mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                        if (element.className == "qz_like_btn c_tx mr8")
                        {
                            string style = (string)element.style.cssText;

                            if (element.innerText != null && element.innerText.Contains("取消赞") && style != "display: none;")
                            {
                                element.click();
                                success.Add(curQQ);

                                richTextBox1.Text = richTextBox1.Text + "【成功】" + curQQ + "\n";
                                return;
                            }
                        }
                    }
                }
            }
            catch (Exception) { }

            failed.Add(curQQ);
            richTextBox1.Text = richTextBox1.Text + "【失败】" + curQQ + "\n";
        }

        private void PraiseFriend()
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

                for (int i = 0; i < htmlDoc.frames.length; ++i)
                {
                    object index = i;
                    mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                    IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                    mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("A");
                    IEnumerator tagie = tagls.GetEnumerator();
                    while (tagie.MoveNext())
                    {
                        mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                        if (element.className == "qz_like_btn c_tx mr8")
                        {
                            if (element.innerText != null && !element.innerText.Contains("取消赞"))
                            {
                                element.click();
                                pro = "点赞完成";
                                //richTextBox1.Text = richTextBox1.Text + "【成功】" + curQQ + "\n";
                                return;
                            }
                        }
                    }
                }
            }
            catch (Exception) { }

            //richTextBox1.Text = richTextBox1.Text + "【失败】" + curQQ + "\n";
            Zhan();//失败后直接进入下一个
        }
        private void downloadUserInfo()
        {
            try
            {
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
                            // info.nickname   = json["nickname"].AsString();
                            //info.age = json["age"].AsString();
                            //info.sex = json["sex"].AsString();
                            //info.birthday = json["birthday"].AsString();
                            //info.mood = json["mood"].AsString();
                            //info.photo = json["photo"].AsString();
                            //info.blog = json["blog"].AsString();
                            //info.time = json["time"].AsString();
                            //info.key = json["key"].AsString();
                            //info.heat = json["heat"].AsString();
                            //info.usedcount = json["usedcount"].AsInt();
                            dblist.Enqueue(info);
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
