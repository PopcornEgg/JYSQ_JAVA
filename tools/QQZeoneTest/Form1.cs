using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using mshtml;
using System.Collections;
using System.Collections.Specialized;
using System.Net;
using System.Threading;
using System.IO;

namespace QQZeoneTest
{
    public partial class Form1 : Form
    {
        Random random = new Random();

        public Form1()
        {
            InitializeComponent();

            webBrowser1.DocumentCompleted +=new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);
        }
        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            webBrowser1.Document.Window.Error +=new HtmlElementErrorEventHandler(Window_Error);
            //System.Console.Write(webBrowser1.DocumentText);
        }

        private void Window_Error(object sender, HtmlElementErrorEventArgs e)
        {
            e.Handled = true;
            this.Text = "出现了脚本错误" + e.Description;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {

            //webBrowser1.Url = new Uri("http://user.qzone.qq.com/" + listBox1.Items[random.Next(listBox1.Items.Count)].ToString());
            //this.Text = string.Format("正在访问第{0}个空间,时间间隔{1}秒 {2}", ++idx, timer1.Interval / 1000, webBrowser1.Url.ToString());
        }

        //进入个人页面
        private void button1_Click(object sender, EventArgs e)
        {
            foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("li"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "menu_item_1")
                {
                    he.InvokeMember("click");
                    break;
                }
            }
        }

        //进入说说页面
        private void button2_Click(object sender, EventArgs e)
        {
            foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("li"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "menu_item_311")
                {
                    he.InvokeMember("click");
                }
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("li"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "menu_item_4")
                {
                    he.InvokeMember("click");
                }
            }
        }


        //切换到账号输入
        private void button4_Click(object sender, EventArgs e)
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
                object index = 0;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection logins = (mshtml.IHTMLElementCollection)frame.all.tags("a");
                mshtml.IHTMLElement element = (mshtml.IHTMLElement)logins.item("switcher_plogin", 0);
                element.click();
            }
            catch (System.Exception _e)
            {
                Console.Write(_e.Message);
            }
        }

        //输入账号密码
        private void button5_Click(object sender, EventArgs e)
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
                object index = 0;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection logins = (mshtml.IHTMLElementCollection)frame.all.tags("input");

                mshtml.IHTMLInputElement element = (mshtml.IHTMLInputElement)logins.item("u", 0);
                element.value = "305723370";

                element = (mshtml.IHTMLInputElement)logins.item("p", 0);
                element.value = "cdtanji51520";

            }
            catch (System.Exception _e)
            {
                Console.Write(_e.Message);
            }
        }

        //登陆
        private void button6_Click(object sender, EventArgs e)
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
                object index = 0;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection logins = (mshtml.IHTMLElementCollection)frame.all.tags("input");
                mshtml.IHTMLElement element = (mshtml.IHTMLElement)logins.item("login_button", 0);
                element.click();
            }
            catch (System.Exception _e)
            {
                Console.Write(_e.Message);
            }
        }

        //获取【说说】的好友信息
        private void button7_Click(object sender, EventArgs e)
        {
            string info = null;
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);
                mshtml.IHTMLElementCollection divs = (mshtml.IHTMLElementCollection)frame.all.tags("div");
                IEnumerator div = divs.GetEnumerator();
                while (div.MoveNext())
                {
                    mshtml.IHTMLElement item = (mshtml.IHTMLElement)div.Current;

                    if (item.className == "ui_avatar" || item.className == "feed_like")
                    {
                        mshtml.IHTMLElementCollection childrenCol = (mshtml.IHTMLElementCollection)item.children;
                        IEnumerator cusers = childrenCol.GetEnumerator();
                        while (cusers.MoveNext())
                        {
                            mshtml.IHTMLElement celement = (mshtml.IHTMLElement)cusers.Current;
                            if (celement.tagName == "a" || celement.tagName == "A")
                            {
                                string href = (string)celement.getAttribute("href", 0);
                                if (href != null && href.Contains("http:"))
                                {
                                    info += href + "\n";
                                }
                            }
                        }
                    }
                }
            }
            MessageBox.Show(info);
        }

        //获取个人信息
        private void button8_Click(object sender, EventArgs e)
        {
            string _sex = "";
            string _age = "";
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);
                mshtml.IHTMLElement sex = (mshtml.IHTMLElement)frame.all.item("sex",0);
                mshtml.IHTMLElement age = (mshtml.IHTMLElement)frame.all.item("age",0);

                if (sex != null)
                    _sex = sex.outerText;
                
                if (age != null)
                    _age = age.outerText;
            }

            //for (int i = 0; i < webBrowser1.Document.Window.Frames.Count; ++i)
            //{
            //    HtmlElement sex = webBrowser1.Document.Window.Frames[i].Document.GetElementById("sex");
            //    if( sex != null )
            //        _sex = sex.OuterText;

            //    HtmlElement age = webBrowser1.Document.Window.Frames[i].Document.GetElementById("age");
            //    if (age != null)
            //        _age = age.OuterText;
            //}

            MessageBox.Show(string.Format("年龄：{0}\n性别：{1}", _age, _sex));
        }

        //空间动态里的好友
        private void button9_Click(object sender, EventArgs e)
        {
            string info = "";

            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                for (int j = 0; j < frame.frames.length; ++j)
                {
                    try
                    {
                        object rj = j;
                        mshtml.IHTMLWindow2 frameWindow2 = frame.frames.item(ref rj) as mshtml.IHTMLWindow2;

                        IHTMLDocument2 frame2 = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow2);
                        mshtml.IHTMLElementCollection userlist = (mshtml.IHTMLElementCollection)frame2.all.tags("div");
                        IEnumerator users = userlist.GetEnumerator();
                        while (users.MoveNext())
                        {
                            mshtml.IHTMLElement element = (mshtml.IHTMLElement)users.Current;
                            if (element.className == "user-list" || element.className == "ui-avatar")
                            {
                                mshtml.IHTMLElementCollection childrenCol = (mshtml.IHTMLElementCollection)element.children;
                                IEnumerator cusers = childrenCol.GetEnumerator();
                                while (cusers.MoveNext())
                                {
                                    mshtml.IHTMLElement celement = (mshtml.IHTMLElement)cusers.Current;
                                    string href = (string)celement.getAttribute("href", 0);
                                    if (href.Contains("http:"))
                                        info += href + "\n";
                                }

                            }
                        }
                    }
                    catch(Exception)
                    {
                    }
                }
            }
            MessageBox.Show(info);
        }

        private void button10_Click(object sender, EventArgs e)
        {
            try
            {
                for (int i = 0; i < webBrowser1.Document.Window.Frames.Count; ++i)
                {
                    HtmlDocument frame = webBrowser1.Document.Window.Frames[i].Document;
                    HtmlElement blog = frame.GetElementById("profile-blog-cnt");
                    HtmlElement photo = frame.GetElementById("profile-photo-cnt");
                    HtmlElement mood = frame.GetElementById("profile-mood-cnt");

                    if (blog != null && photo != null && mood != null)
                    {
                        MessageBox.Show(string.Format("日志：{0}\n照片：{1}\n说说：{2}", blog.OuterText, photo.OuterText, mood.OuterText));
                    }
                }
            }
            catch (Exception)
            { 
            }
        }

        //访问权限
        private void button11_Click(object sender, EventArgs e)
        {
            HtmlElementCollection body = webBrowser1.Document.GetElementsByTagName("body");

            bool privilege = true;
            for( int i = 0; i < body.Count; ++i )
            {
                if (body[i].GetAttribute("className") == "no_privilege proj_limit_v2")
                {
                    privilege = false;
                    break;
                }
            }

            MessageBox.Show(string.Format("访问权限：{0}", privilege ? "有" : "无"));
        }

        //获取最后活跃时间
        private void button12_Click(object sender, EventArgs e)
        {
            string info = null;
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                for (int j = 0; j < frame.frames.length; ++j)
                {
                    object rj = j;
                    mshtml.IHTMLWindow2 frameWindow2 = frame.frames.item(ref rj) as mshtml.IHTMLWindow2;

                    IHTMLDocument2 frame2 = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow2);
                    mshtml.IHTMLElementCollection userlist = (mshtml.IHTMLElementCollection)frame2.all.tags("span");
                    IEnumerator users = userlist.GetEnumerator();
                    while (users.MoveNext())
                    {
                        mshtml.IHTMLElement element = (mshtml.IHTMLElement)users.Current;
                        string str = element.innerText;
                        if (str != null && str.Contains("月") && str.Contains("日") && str.Contains(":"))
                        {
                            info = str;
                            break;
                        }
                        
                    }
                    if (info != null)
                        break;
                }
                if (info != null)
                    break;
            }
            MessageBox.Show(info);

        }

        //获取聊天内容（未完）
        private void button13_Click(object sender, EventArgs e)
        {
            string info = null;
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                for (int j = 0; j < frame.frames.length; ++j)
                {
                    object rj = j;
                    mshtml.IHTMLWindow2 frameWindow2 = frame.frames.item(ref rj) as mshtml.IHTMLWindow2;

                    IHTMLDocument2 frame2 = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow2);

                    mshtml.IHTMLElement host_home_feeds = (mshtml.IHTMLElement)frame2.all.item("host_home_feeds", 0);
                    if (host_home_feeds != null)
                    {
                        mshtml.IHTMLElementCollection children = (mshtml.IHTMLElementCollection)host_home_feeds.children;
                        IEnumerator child = children.GetEnumerator();
                        while (child.MoveNext())//li
                        {
                            mshtml.IHTMLElement element = (mshtml.IHTMLElement)child.Current;
                            if (element.className == "f-single f-s-s")
                            {
                                mshtml.IHTMLElementCollection children2 = (mshtml.IHTMLElementCollection)element.children;
                                IEnumerator child2 = children2.GetEnumerator();
                                while (child2.MoveNext())
                                {
                                    mshtml.IHTMLElement element2 = (mshtml.IHTMLElement)child2.Current;
                                    if (element2.className == "f-wrap")
                                    {
                                        //太长了，未完
                                    }
                                }
                            }

                        }
                    }
                    

                    //mshtml.IHTMLElementCollection userlist = (mshtml.IHTMLElementCollection)frame2.all.tags("div");
                    //IEnumerator users = userlist.GetEnumerator();
                    //while (users.MoveNext())
                    //{
                    //    mshtml.IHTMLElement element = (mshtml.IHTMLElement)users.Current;
                    //    if (element.className != null)
                    //        Console.WriteLine(element.className);

                    //    if (element.className == "comments-content" && element.innerText != null)
                    //    {
                    //        info += element.innerText + "|";
                    //        break;
                    //    }

                    //}
                }
            }
            MessageBox.Show(info);
        }

        //获取昵称
        private void button14_Click(object sender, EventArgs e)
        {
            HtmlElementCollection div = webBrowser1.Document.GetElementsByTagName("div");
            for (int i = 0; i < div.Count; ++i)
            {
                if (div[i].GetAttribute("className") == "head-detail-name")
                {
                    HtmlElementCollection span = div[i].GetElementsByTagName("span");
                    for (int j = 0; j < span.Count; ++j)
                    {
                        if (span[j].GetAttribute("className") == "user-name textoverflow")
                        {
                            MessageBox.Show(span[j].OuterText);
                        }
                    }
                }
            }
        }
      
        private void button15_Click(object sender, EventArgs e)
        {
            try
            {
                mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;

                for (int i = 0; i < htmlDoc.frames.length; ++i)
                {
                    object index = i;
                    mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                    IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                    mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("a");
                    IEnumerator tagie = tagls.GetEnumerator();
                    while (tagie.MoveNext())
                    {
                        mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                        if (element.className == "item qz_like_btn_v3")
                        {
                            if (element.innerText != null && !element.innerText.Contains("取消赞"))
                            {
                                element.click();
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception) { }

        }

        //获取我的QQ号
        private String getMyQQ()
        {
            foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("a"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "user-home")
                {
                    String home = he.GetAttribute("href");
                    home = home.Replace("http://user.qzone.qq.com/", "");
                    home = home.Replace("/main", "");
                    return home;
                }
            }
            return "";
        }
        //检测是否已评论
        private mshtml.IHTMLElement gerCommented()
        {
            String myqq = getMyQQ();
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("div");
                IEnumerator tagie = tagls.GetEnumerator();
                while (tagie.MoveNext())
                {
                    mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                    if (element.className == "mod-comments")
                    {
                        mshtml.IHTMLElementCollection childrenCol = (mshtml.IHTMLElementCollection)element.all;
                        IEnumerator cusers = childrenCol.GetEnumerator();
                        bool cangonext = true;
                        while (cusers.MoveNext())
                        {
                            mshtml.IHTMLElement celement = (mshtml.IHTMLElement)cusers.Current;
                            Console.WriteLine(celement.className);
                            if (celement.className == "comments-item bor3")
                            {
                                if ((String)celement.getAttribute("data-uin", 0) == myqq)
                                {
                                    cangonext = false;
                                    break;
                                }
                            }
                        }
                        //1.打开编辑框
                        if (cangonext)//自己没有评论
                        {
                            return element;
                        }
                    }
                }
            }
            return null;
        }

        //1.打开编辑框
        private mshtml.IHTMLElement commentStep1(mshtml.IHTMLElement commentTag)
        {
            String myqq = getMyQQ();
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("div");
                IEnumerator tagie = tagls.GetEnumerator();
                while (tagie.MoveNext())
                {
                    mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                    if (commentTag == element && element.className == "mod-comments")
                    {
                        mshtml.IHTMLElementCollection childrenCol = (mshtml.IHTMLElementCollection)element.all;
                        IEnumerator cusers = childrenCol.GetEnumerator();
                        while (cusers.MoveNext())
                        {
                            mshtml.IHTMLElement element1 = (mshtml.IHTMLElement)cusers.Current;
                            //if (element1.className == "textinput textinput-default bor2")
                            if (element1.className == "mod-commnets-poster feedClickCmd comment_default_inputentry")
                            {
                                element1.click();
                                return element;
                            }
                        }
                    }
                }
            }
            return null;
        }
        //2.编辑
        private mshtml.IHTMLElement commentStep2(mshtml.IHTMLElement commentTag)
        {
            if (commentTag == null)
                return null;
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("div");
                IEnumerator tagie = tagls.GetEnumerator();
                while (tagie.MoveNext())
                {
                    mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                    if (commentTag == element && element.className == "mod-comments")
                    {
                        mshtml.IHTMLElementCollection childrenCol = (mshtml.IHTMLElementCollection)element.all;
                        IEnumerator cusers = childrenCol.GetEnumerator();
                        Console.WriteLine("|||||||||||||||||||||||||||||||||||||||||||||");
                        while (cusers.MoveNext())
                        {
                            mshtml.IHTMLElement element2 = (mshtml.IHTMLElement)cusers.Current;
                            Console.WriteLine(element2.className);
                            if (element2.className == "textinput textarea c_tx2")
                            {
                                element2.innerText =  "哈哈:" + Environment.TickCount;
                                return element;
                            }
                        }
                        Console.WriteLine("$$$$$$$$$$$$$$$$$$$$$$");
                    }
                }
            }
            return null;
        }
        //3.提交
        private mshtml.IHTMLElement commentStep3(mshtml.IHTMLElement commentTag)
        {
            if (commentTag == null)
                return null;
            mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
            for (int i = 0; i < htmlDoc.frames.length; ++i)
            {
                object index = i;
                mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);

                mshtml.IHTMLElementCollection tagls = (mshtml.IHTMLElementCollection)frame.all.tags("a");
                IEnumerator tagie = tagls.GetEnumerator();
                while (tagie.MoveNext())
                {
                    try
                    {
                        mshtml.IHTMLElement element = (mshtml.IHTMLElement)tagie.Current;
                        if (element.className == "btn-post gb_bt  evt_click" &&
                            element.parentElement.parentElement.parentElement.parentElement.parentElement.parentElement == commentTag)
                        {
                            element.click();
                            return commentTag;
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine("*********************************************************");
                        Console.WriteLine(ex.ToString());
                        Console.WriteLine("*********************************************************");
                    }
                }
            }
            return null;
        }
        private void button16_Click(object sender, EventArgs e)
        {
            try
            {
                mshtml.IHTMLElement commentTag = gerCommented();
                if (commentTag != null)
                {
                    commentStep1(commentTag);
                    commentStep2(commentTag);
                  //  commentStep3(commentTag);
                }
            }
            catch (Exception ex) {
                Console.WriteLine("*********************************************************");
                Console.WriteLine(ex.ToString());
                Console.WriteLine("*********************************************************");
            }
        }
        private void button17_Click(object sender, EventArgs e)
        {
            QQUtitls.account_getlist();
        }

        private void button18_Click(object sender, EventArgs e)
        {
            QQUtitls.account_update();
        }

        private void button19_Click(object sender, EventArgs e)
        {
            QQUtitls.friend_getlist();
        }

        private void button20_Click(object sender, EventArgs e)
        {
            QQUtitls.friend_update();
        }

        private void button21_Click(object sender, EventArgs e)
        {
            QQUtitls.accountEx_getlist();
        }

        private void button22_Click(object sender, EventArgs e)
        {
            QQUtitls.accountEx_update();
        }

        private void button23_Click(object sender, EventArgs e)
        {
            Thread thread = new Thread(new ThreadStart(QQUtitls.test_updateqq_from_spider));
            thread.Start();
        }

        private void button24_Click(object sender, EventArgs e)
        {
            QQUtitls.common_get();
        }

        private void button25_Click(object sender, EventArgs e)
        {
            QQUtitls.common_update();
        }
    }
}
