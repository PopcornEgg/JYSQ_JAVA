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

//自动访问第一版
//支持各个页面随机点击访问，随机停留时间
namespace QQZeoneMarketing
{
    public partial class StoppingItem : Form
    {
        List<string> urls = new List<string>();
        DateTime alltime;
        DateTime nexttime;
        Random random = new Random();
        string cururl;

        String curQQ = "";
        HashSet<String> praisedMap = new HashSet<String>();
        HashSet<String> CommentMap = new HashSet<String>();
        public StoppingItem()
        {
            InitializeComponent();
            webBrowser1.DocumentCompleted += new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);
        }
        
        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            if (webBrowser1.ReadyState != WebBrowserReadyState.Complete)
            {
                return;
            }

            if (!praisedMap.Contains(curQQ))
            {
                PraiseFriend();
            }
            if (!CommentMap.Contains(curQQ))
            {
                CommentFriend();
            }
        }

        public void Start(string qq)
        {
            this.Show();

            curQQ = qq;
            timer1.Enabled = true;

            alltime = DateTime.Now + new TimeSpan(0, 0, random.Next(30,60));
            nexttime = DateTime.Now + new TimeSpan(0, 0, random.Next(2, 5));

            urls.Clear();
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/main", qq));//主页
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/4", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/334", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/311", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/1", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/305", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/main?mode=gfp_timeline", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}", qq));//
            urls.Add(string.Format("http://user.qzone.qq.com/{0}/2", qq));//

            cururl = string.Format("http://user.qzone.qq.com/{0}", qq);
            webBrowser1.Url = new Uri(cururl);
            this.Text = cururl + " " + (alltime - DateTime.Now).ToString();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            try
            {
                if (DateTime.Now > nexttime)
                {
                    nexttime = DateTime.Now + new TimeSpan(0, 0, random.Next(5, 20));
                    cururl = urls[random.Next(urls.Count)];
                    webBrowser1.Url = new Uri(cururl);
                }
                else if (DateTime.Now > alltime)
                {
                    this.Hide();
                }

                this.Text = cururl + " " + (alltime - DateTime.Now).ToString();
            } catch(Exception)
            {
            }
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
                                praisedMap.Add(curQQ);
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
                                element2.innerText = "哈哈:" + Environment.TickCount;
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
                            CommentMap.Add(curQQ);
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
        private void CommentFriend()
        {
            try
            {
                mshtml.IHTMLElement commentTag = gerCommented();
                if (commentTag != null)
                {
                    commentStep1(commentTag);
                    commentStep2(commentTag);
                    commentStep3(commentTag);
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
}
