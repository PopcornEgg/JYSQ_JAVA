using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using mshtml;

namespace QQZeoneMarketing.SpiderCommons
{
    class ShuoShuo : Common
    {
        public override void Enter()
        {
            foreach (HtmlElement he in webBrowser1.Document.GetElementsByTagName("li"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "menu_item_311")
                {
                    he.InvokeMember("click");
                    break;
                }
            }

            completed = false;
            nextTime = DateTime.MaxValue;
        }
        public override void Tick()
        {
            if (Enable)
            {
                try
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
                                            spider.AddQQ(href);
                                        }
                                    }
                                }
                            }
                        }
                    }


                    //string info = "";
                    //for (int i = 0; i < webBrowser1.Document.Window.Frames.Count; ++i)
                    //{
                    //    HtmlDocument frame = webBrowser1.Document.Window.Frames[i].Document;
                    //    HtmlElementCollection divs = frame.GetElementsByTagName("div");
                    //    for (int j = 0; j < divs.Count; ++j)
                    //    {
                    //        string className = divs[j].GetAttribute("className");
                    //        if (className == "ui_avatar")
                    //        {
                    //            HtmlElementCollection a = divs[j].GetElementsByTagName("a");
                    //            if (a != null && a.Count > 0)
                    //            {
                    //                string url = a[0].GetAttribute("href");
                    //                info += url + "\n";
                    //                spider.AddQQ(url);
                    //            }
                    //        }
                    //        else if (className == "feed_like")
                    //        {
                    //            HtmlElementCollection a = divs[j].GetElementsByTagName("a");
                    //            if (a != null && a.Count > 0)
                    //            {
                    //                for (int k = 0; k < a.Count; ++k)
                    //                {
                    //                    if (a[k].GetAttribute("className") == "c_tx")
                    //                    {
                    //                        string url = a[0].GetAttribute("href");
                    //                        info += url + "\n";
                    //                        spider.AddQQ(url);
                    //                    }
                    //                }
                    //            }
                    //        }
                    //    }
                    //}

                    spider.UserGetOver();
                    Log(info);
                    ChangeCommon("NewUser");
                }
                catch (Exception e)
                {
                    Log(e.Message);
                    ChangeCommon("NewUser");
                }
            }
        }
    }
}
