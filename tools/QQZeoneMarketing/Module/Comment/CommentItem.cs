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
using utitls;

//自动说话第一版
namespace QQZeoneMarketing
{
    public partial class CommentItem : Form
    {
        public bool isCanDoNext = true;
        Comment father = null;
        int alltime;
        String cururl = "";
        String curQQ = "";
        mshtml.IHTMLElement commentTag = null;
        static int stayTime = 120 * 1000;
        int curstepidx = 0;
        bool candosteps = false;
        public CommentItem(Comment _f)
        {
            father = _f;
            InitializeComponent();
            webBrowser1.DocumentCompleted += new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);
            timer1.Enabled = false;
        }

        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            if (webBrowser1.ReadyState != WebBrowserReadyState.Complete)
            {
                return;
            }
            if (!candosteps)
            {
                curstepidx = 1;
                candosteps = true;
            }
        }

        public void Start(string qq)
        {
            this.Show();
            curstepidx = 0;
            candosteps = false;
            isCanDoNext = false;
            curQQ = qq;
            timer1.Enabled = true;
            alltime = Environment.TickCount + stayTime;
            cururl = string.Format("http://user.qzone.qq.com/{0}/main", qq);
            webBrowser1.Url = new Uri(cururl);
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            try
            {
//                 if (this.Visible)
//                     return;
                if (Environment.TickCount > alltime)
                {
                    if (!isCanDoNext)
                    {
                        if (curstepidx != 1000 && curstepidx != 999 )
                        {
                            LogFileEx.LogRow row = new LogFileEx.LogRow();
                            row.add(curQQ);
                            if (curstepidx == 3)
                                row.add("失败：发送按钮不管用");
                            else
                                row.add("失败：超时了");
                            LogFileEx.add("Comment.txt", row);
                            father.updateFailText(false);
                        }
                        isCanDoNext = true;
                        timer1.Enabled = false;
                    }
                }
                else
                {
                    if (curstepidx  == 1) // 回复
                    {
                        CommentFriend();
                    }
                    else if (curstepidx == 999)//停留10秒就隐藏了
                    {
                        //alltime = Environment.TickCount + 10000;
                        curstepidx = 1000;
                    }
                    if (curstepidx != 1000 && Environment.TickCount > alltime - 10 * 1000)//在110s的时候判断
                    {
                        bool iscucc = isCommentedSucc();
                        LogFileEx.LogRow row = new LogFileEx.LogRow();
                        row.add(curQQ);
                        row.add(iscucc ? "成功" : "提交失败");
                        LogFileEx.add("Comment.txt", row);
                        father.updateFailText(iscucc);
                        curstepidx = 999;
                    }
                    this.Text = cururl + " " + (alltime - Environment.TickCount).ToString();
                }
            }
            catch (Exception)
            {
            }
        }
       
        //检测是否评论成功
        private bool isCommentedSucc()
        {
            if (commentTag == null)
                return false;
            String myqq = Func.getMyQQ(webBrowser1);
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
                            mshtml.IHTMLElement element2 = (mshtml.IHTMLElement)cusers.Current;
                            if (element2.className == "comments-item bor3")
                            {
                                if (element2.innerHTML.Contains(myqq) )
                                {
                                    return true;
                                }
                            }
                            
                        }
                    }
                }
            }
            return false;
        }
        //获取可评论楼层
        private mshtml.IHTMLElement getCommented()
        {
            String myqq = Func.getMyQQ(webBrowser1);
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
        //获取编辑框已打开
        private bool hascommentOpened(mshtml.IHTMLElement commentTag)
        {
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
                            if (element1.className == "comment-box-wrap")
                            {
                                return true;
                            }
                        }
                        return false;
                    }
                }
            }
            return false;
        }
        //1.打开编辑框
        private bool commentStep1(mshtml.IHTMLElement commentTag)
        {
            if (hascommentOpened(commentTag))
                return true;
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
                            if (element1.className == "mod-commnets-poster feedClickCmd comment_default_inputentry")
                            {
                                element1.click();
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        //2.编辑
        private bool commentStep2(mshtml.IHTMLElement commentTag)
        {
            if (commentTag == null)
                return false;
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
                            mshtml.IHTMLElement element2 = (mshtml.IHTMLElement)cusers.Current;
                            if (element2.className == "textinput textarea c_tx2" || element2.className == "textinput textarea c_tx2 input_focus textinput_focus")
                            //if (element2.className == "textinput textarea c_tx2 input_focus textinput_focus")
                            {
                                element2.innerText = getReplyStr();
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
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
        //3.提交
        private bool commentStep3ex(mshtml.IHTMLElement commentTag)
        {
            if (commentTag == null)
                return false;
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
                            mshtml.IHTMLElement element2 = (mshtml.IHTMLElement)cusers.Current;
                            if (element2.className == "btn-post gb_bt  evt_click")
                            {
                                element2.click();
                                return true;

//                                 mshtml.IHTMLElementCollection element2Col = (mshtml.IHTMLElementCollection)element2.all;
//                                 IEnumerator element2Cols = element2Col.GetEnumerator();
//                                 while (element2Cols.MoveNext())
//                                 {
//                                     mshtml.IHTMLElement element3 = (mshtml.IHTMLElement)element2Cols.Current;
//                                     if (element3.className == "btn-post gb_bt  evt_click")
//                                     {
//                                         element3.click();
//                                         return commentTag;
//                                     }
//                                 }
                            }
                        }
                    }
                }
            }
            return false;
        }

        static string[] replyStrs = {
            "23333",
            "哈哈",
            "呵呵",
            "你好",
        }; 
        String getReplyStr()
        {
#if DEBUG
            return Environment.TickCount.ToString();
#else
            Random random = new Random();
            int idx = random.Next(0, replyStrs.Length);
            return replyStrs[idx];
#endif
        }
        //开始说话
        private void CommentFriend()
        {
            try
            {
                commentTag = getCommented();
                if (commentTag != null)
                {
                    if (commentStep1(commentTag))
                        if (commentStep2(commentTag))
                            if (commentStep3ex(commentTag))
                                curstepidx = 2;
                }
                else
                {
                    curstepidx = 999;
                    LogFileEx.LogRow row = new LogFileEx.LogRow();
                    row.add(curQQ);
                    row.add("失败：没有可评论的位置获取无法访问空间");
                    LogFileEx.add("Comment.txt", row);
                    father.updateFailText(false);
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
