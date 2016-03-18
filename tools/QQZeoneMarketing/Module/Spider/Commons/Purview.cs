using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using utitls;

namespace QQZeoneMarketing.SpiderCommons
{

    //2.空间是否允许访问，否则进入下一个
    class Purview : Common
    {
        bool privilege = false;
        public override void Enter()
        {
            HtmlElementCollection body = webBrowser1.Document.GetElementsByTagName("body");
            privilege = true;
            for (int i = 0; i < body.Count; ++i)
            {
                if (body[i].GetAttribute("className") == "no_privilege proj_limit_v2")
                {
                    privilege = false;
                    break;
                }
            }

            if (!privilege)
            {
                String user_name = "";
                String user_sex = "";
                HtmlElementCollection user_infor = webBrowser1.Document.GetElementsByTagName("div");
                for (int i = 0; i < user_infor.Count; ++i)
                {
                    if (user_infor[i].GetAttribute("className") == "user_infor")
                    {
                        HtmlElementCollection childels = user_infor[i].All;
                        for (int j = 0; j < childels.Count; ++j)
                        {
                            if (childels[j].TagName == "p" || childels[j].TagName == "P")
                            {
                                if (childels[j].GetAttribute("className") == "user_name")
                                {
                                    user_name = childels[j].InnerText;
                                }
                                else
                                {
                                    if (childels[j].InnerText != null && childels[j].InnerText.Length > 0)
                                    {
                                        String[] p = childels[j].InnerText.Split(' ');
                                        if (p != null && p.Length > 0)
                                            user_sex = p[0];
                                    }
                                }
                            }
                        }
                    }
                }
                //存入数据库，调到下一个(只修改部分数据)
                PostData data = new PostData();
                data.Add("qq", spider.UIDB.qq);
                data.Add("used", 1);
                data.Add("nickname", user_name);
                data.Add("sex", user_sex);
                data.Add("zone_authority", 0);
                updateDBinfoThread.start(Config.UPDATE_ZONE_URL, data);
            }
            
        }
        public override void Tick()
        {
            ChangeCommon(privilege ? "UserInfo" : "NewUser");
        }
    }
}
