using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using mshtml;
namespace QQZeoneMarketing.SpiderCommons
{
    //3.用户信息统计
    class UserInfo : Common
    {
        //进入个人信息页面
        public override void Enter()
        {
            bool error = true;
            try
            {
                foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("li"))
                {
                    string cname = he.GetAttribute("className");
                    if (cname == "menu_item_1")
                    {
                        error = false;
                        he.InvokeMember("click");
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                Log(e.Message);
            }


            //如果异常没有找到个人信息，进入下一个用户
            if (error)
                ChangeCommon("NewUser");

            completed = false;
            nextTime = DateTime.MaxValue;
        }
        public override void Tick()
        {
            if (Enable)
            {
                try
                {
                    //性别和年龄
                    mshtml.HTMLDocumentClass htmlDoc = webBrowser1.Document.DomDocument as mshtml.HTMLDocumentClass;
                    for (int i = 0; i < htmlDoc.frames.length; ++i)
                    {
                        object index = i;
                        mshtml.IHTMLWindow2 frameWindow = htmlDoc.frames.item(ref index) as mshtml.IHTMLWindow2;

                        IHTMLDocument2 frame = CodecentrixSample.CrossFrameIE.GetDocumentFromWindow(frameWindow);
                        mshtml.IHTMLElement sex = (mshtml.IHTMLElement)frame.all.item("sex", 0);
                        mshtml.IHTMLElement age = (mshtml.IHTMLElement)frame.all.item("age", 0);
                        mshtml.IHTMLElement birthday = (mshtml.IHTMLElement)frame.all.item("birthday", 0);
                        
                        if (sex != null)
                            spider.UIDB.sex = sex.outerText;
                        if (age != null)
                            spider.UIDB.age = Convert.ToInt32(age.outerText);
                        if (birthday != null)
                            spider.UIDB.birthday = birthday.outerText;
                    }

                    //说说、日志、照片
                    for (int i = 0; i < webBrowser1.Document.Window.Frames.Count; ++i)
                    {
                        HtmlDocument frame = webBrowser1.Document.Window.Frames[i].Document;
                        HtmlElement blog = frame.GetElementById("profile-blog-cnt");
                        HtmlElement photo = frame.GetElementById("profile-photo-cnt");
                        HtmlElement mood = frame.GetElementById("profile-mood-cnt");

                        if (blog != null)
                            spider.UIDB.blog = Convert.ToInt32(blog.OuterText);
                        if (photo != null)
                            spider.UIDB.photo = Convert.ToInt32(photo.OuterText);
                        if (mood != null)
                            spider.UIDB.mood = Convert.ToInt32(mood.OuterText);

                    }

                    ++spider.okcount;

                    //获取动态QQ好友信息
                    getFriend();
                    getNickName();
                    getLasTime();
                    spider.UIDB.zone_authority = 1;
                    spider.UIDB.used = 1;

                    ChangeCommon("ShuoShuo");
                    ////ChangeCommon("NewUser");
                }
                catch (Exception e)
                {
                    Log(e.Message);
                    ChangeCommon("NewUser");
                }
            }
        }

        //获取昵称
        private void getNickName()
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
                            spider.UIDB.nickname = span[j].OuterText;
                            return;
                        }
                    }
                }
            }
        }

        //获取最后活跃时间
        private void getLasTime()
        {
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
                            DateTime dnow = DateTime.Now;
                            str = str.Replace("月", "-");
                            str = str.Replace("日", "-");
                            str = str.Replace(" ", "");
                            str = str.Replace(":", "-");
                            str = String.Format("{0}-{1}-00", dnow.Year, str); 
                            spider.UIDB.logintime = str;
                            return;
                        }

                    }
                }
            }
        }
        //空间动态里的好友
        private void getFriend()
        {
            try
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
                                    {
                                        info += href + "\n";
                                        spider.AddQQ(href);
                                    }
                                }

                            }
                        }

                    }
                }
            }
            catch (Exception) { }
            
        }
    }
}
