using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QQZeoneMarketing.SpiderCommons
{
    //1.访问一个新的用户
    class NewUser : Common
    {
        bool seturl = false;
        public override void Enter()
        {
            try
            {
                if (webBrowser1.IsBusy)
                    seturl = false;
                else
                {
                    seturl = true;

                    completed = false;
                    nextTime = DateTime.MaxValue;
                    spider.curStayTime = Environment.TickCount;
                    webBrowser1.Navigate("http://user.qzone.qq.com/" + spider.QQ + "/1");
                }
            }
            catch (Exception e)
            {
                System.Windows.Forms.MessageBox.Show(e.Message);
            }
        }
        public override void Tick()
        {
            if (!seturl)
            {
                if (!webBrowser1.IsBusy)
                {
                    seturl = true;
                   
                    completed = false;
                    nextTime = DateTime.MaxValue;
                    spider.curStayTime = Environment.TickCount;
                    //webBrowser1.Url = new Uri("http://user.qzone.qq.com/" + spider.QQ + "/1");
                    webBrowser1.Navigate("http://user.qzone.qq.com/" + spider.QQ + "/1");
                }
            }
            else if (Enable)
                ChangeCommon("Purview");
        }
    }
}
