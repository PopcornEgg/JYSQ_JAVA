using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;
using System.Threading;
using System.Windows.Automation;
using System.IO;
using System.Windows.Forms;
using utitls;

namespace QQClient
{
    public sealed class Test
    {
        public static string[,] qqs = new string[,]
        {
#if DEBUG
            {"305723370","cdtanji51520", "心无杂念"},
            {"1553998504","wltanji51520", "记忆SQ"},
            {"2967034411","wltanji51520", "忆享科技"},
            {"2994338487","wltanji51520","指尖余溫╮"},
            {"1787842885","cdtanji51520","忆享天开"}
#else   
            //{"1809784335","wq123456",""},
            {"1809893851","wltanji51520","牵挂你的人是我"},
            {"1809562205","wq123456","只对你●有感觉"},
            {"1809942894","wltanji51520","等你那么久"},
            {"1810239085","wq123456","我们见过"},
            {"1810012524","wq123456","只对你有感觉"},
            {"1965287387","wq123454","1965287387"},
            {"2339748441","wltanji51520","2339748441"},
            {"1809939164","wq123456","红尘遇见你"},
            {"3308424680","cdtanji51520","爱的记忆"},
            {"1844230980","wltanji51520","好好学习"}
#endif
        };

        static Dictionary<string, AutomationElement> logined = new Dictionary<string, AutomationElement>();

        public static void TestLogin()
        {
            AutoLogin login = new AutoLogin();
            if (null == login.Login("1787842885", "wltanji51520", "忆享天开"))
                Debug.LogError(login._ErrorDesc);
            else
                Debug.Log("登录成功乐乐乐饿了");
        }
        public static void TestFindUI()
        {
            int count = 0;
            while(true)
            {
                if (qqlist.Count == 0)
                {
                    LoadQQ();
                    //System.Threading.Thread.Sleep(1000);

                    if (qqlist.Count == 0)
                    {
                        Debug.Log("好像莫得了");
                        System.Threading.Thread.Sleep(5000);
                        continue;
                    }
                }
                
                
                string qq = qqlist.Dequeue();
                Debug.Log(string.Format("查找【{0}】{1}", ++count, qq));
                AutomationElement appElement = AutoFriendEx.openSearchUI(qq);
                if (appElement != null)
                {
                    //关闭所有好友信息界面
                    while (true)
                    {
                        AutomationElement element = AutoFriendEx.checkQQInfoUI();
                        if (element == null)
                            break;
                        Tools.closeNormalUI(element);
                    }

                    Time time = new Time();
                    time.Start(new TimeSpan(0,0,2));
                    while (true)
                    {
                        if (time.End())
                            break;

                        //点击搜索按钮
                        Input.Click(appElement, 650, 120, false);
                        System.Threading.Thread.Sleep(1000);
                        //点击搜索到的QQ
                        Input.Click(appElement, 55, 290, false);

                        AutomationElement appElement2 = AutoFriendEx.checkQQInfoUI();
                        if (appElement2 != null)//资料界面已打开
                        {
                            Dictionary<String, String> dic = AutoFriendEx.getQQInfoFromUI(appElement2);
                            if (dic != null)//修改到服务器
                            {
                                if (dic.ContainsKey("qq"))
                                {
                                    if (dic["qq"] != qq)
                                    {
                                        Debug.LogLine("dic[\"qq\"] != qq  dic[\"qq\"]==" + dic["qq"] + "\tqq==" + qq);
                                        dic["qq"] = qq;
                                    }
                                }
                                else
                                {
                                    dic.Add("qq", qq);
                                }
                                updateQQ(dic);
                            }

                            break;
                        }
                    }
                }
                
                //开始下一个QQ号查询
                System.Threading.Thread.Sleep(1000);
            }

            //Debug.Log(string.Format("完了"));
        }

        public static void ChekcQQ()
        {
            LoadQQ();
            Debug.LogLine("【准备环境】");
            AutoLogin.CloseQQProcess();

            AutoLogin autoLogin = new AutoLogin();
            AutomationElement appElement = autoLogin.Login("305723370", "cdtanji51520", "心无杂念");
            if (appElement == null)
            {
                Debug.LogLine(autoLogin.Result);
                return;
            }

            AutoFriend autoFriend = new AutoFriend();

            while (qqlist.Count > 0)
            {
                string dqq = qqlist.Dequeue();
                int ret = autoFriend.CheckFriend(appElement, dqq);

                string result = "";
                if (ret == 0) result = "无需验证";
                else if (ret == 1) result = "需要验证";
                else if (ret == 2) result = "禁止加友";
                else if (ret == -1) result = autoFriend._ErrorDesc;

                Debug.LogLine("【" + result + "】 " + dqq);

                System.Threading.Thread.Sleep(100);
            }
        }

        public static void StartAllQQ()
        {
            try
            {
                Debug.LogLine("【准备环境】");
                string result = AutoLogin.CloseQQProcess();
                if (result != null)
                {
                    Debug.LogLine(result);
                    return;
                }

                AutoLogin autoLogin = new AutoLogin();
                int l = qqs.Length / 3;
                for (int i = 0; i < l; ++i)
                {
                    Debug.LogLine("【启动QQ】");
                    autoLogin.Login(qqs[i, 0], qqs[i, 1], qqs[i, 2]);
                }
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }

            Debug.LogLine("已启动完毕,按任意键退出...");
        }

        public static void FindQQ()
        {

            LoadQQ();

            Debug.LogLine("【准备环境】");
            AutoLogin.CloseQQProcess();

            int l = qqs.Length / 2;
            for (int i = 0; i < 5; ++i)
            {
                for (int j = 0; j < l; ++j)
                {
                    try
                    {
                        string dqq = qqlist.Dequeue();

                        OnLoop(qqs[j, 0], qqs[j, 1], qqs[j, 2], dqq);
                    }
                    catch (Exception e)
                    {
                        Debug.OnException(e);
                    }
                }
            }

            Debug.LogLine("按任意键退出...");
        }

        public static void OnLoop(string mainqq, string pws, string nickname, string destqq)
        {
            Debug.LogLine("【登陆】 " + mainqq);

            AutoLogin autoLogin = new AutoLogin();
            AutomationElement appElement = autoLogin.Login(mainqq, pws, nickname);
            if (appElement == null)
            {
                Debug.LogLine(autoLogin.Result);
                return;
            }


            Debug.LogLine("【查找好友】" + destqq);
            AutoFriend autoFriend = new AutoFriend();
            if (!autoFriend.FindFriend(appElement, destqq))
            {
                Debug.LogLine("查找好友失败：" + autoFriend._ErrorDesc);
            }
            else
            {

                Debug.LogLine("【等待下一个】15分钟以后：");
                for (int i = 0; i < 15; ++i)
                {
                    System.Threading.Thread.Sleep(1000);

                    Debug.Log(string.Format("{0}% ", (int)((float)(15 - i) / 15.0f * 100)));
                }
            }

            Debug.LogLine("\n===================================================");
        }

        static Queue<string> qqlist = new Queue<string>();
        static void LoadQQ()
        {
                try
                {
                    PostData nvc = new PostData();
                    Func.putPassWord(nvc);
                    //nvc.Add("order", "logintime");
                    //nvc.Add("desc", "DESC");
                    nvc.Add("start", "0");
                    nvc.Add("count", "50");
                    nvc.Add("tag", "friend_authority");
                    nvc.Add("tagval", "-1");
                    nvc.Add("fields", "qq");
                    if (nvc != null)
                    {
                        string sttuas = Func.HttpPostData(Config.GETLISTFEILD_ZONE_URL, 10000, nvc);
                        if (sttuas.Length > 10)
                        {
                            MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                            for (int i = 0; i < jsons.GetListCount(); i++)
                            {
                                MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                                string qq = Func.MyUrlDeCode(json["qq"].AsString());
                                if (qq.Length > 1)
                                {
                                    qqlist.Enqueue(qq);
                                }
                            }

                        }
                    }
                }

                catch (Exception)
                {
                }
        }
    
        static void updateQQ(Dictionary<String, String> dic)
        {
            try
            {
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                foreach (string key in dic.Keys)
                {
                    nvc.Add(key, dic[key]);
                }
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(Config.UPDATE_ZONE_URL, 10000, nvc);
                    if (sttuas.Contains("_succ_"))
                    {
                    }
                }
            }

            catch (Exception)
            {
            }
        }

        public static void TestAddFriendStateUI()
        {/*
            //while (true)
            {
                AutomationElement appElement = AutoFriendEx.openMyFriendListUI("美丽如烟い");
                if (appElement != null)
                {
                    List<String> qqs = AutoFriendEx.getMyFriendListQQs(appElement);
                    Tools.closeNormalUI(appElement);
                    updateQQAddFriendState("2517172830", qqs);
                }
                //开始下一个QQ号查询
                System.Threading.Thread.Sleep(1000);
            }

            //Debug.Log(string.Format("完了"));*/
        }
        static void updateQQAddFriendState(string myqq, List<String> qqs)
        {
            try
            {
                PostData nvc = new PostData();

                //更新加友状态
                String URL = Config.ROOT_URL + "friend_update";
                foreach (string qqfri in qqs)
                {
                    nvc.Clear();
                    Func.putPassWord(nvc);
                    nvc.Add("qq", myqq);
                    nvc.Add("qqfri", qqfri);
                    nvc.Add("state", "成为好友");
                    if (nvc != null)
                    {
                        string sttuas = Func.HttpPostData(URL, 10000, nvc);
                        if (sttuas.Contains("_succ_"))
                        {
                        }
                    }
                    System.Threading.Thread.Sleep(100);
                }

                //更新好友数量
                URL = Config.ROOT_URL + "accountex_update";
                nvc.Clear();
                Func.putPassWord(nvc);
                nvc.Add("qq", myqq);
                nvc.Add("fricount", qqs.Count);
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas.Contains("_succ_"))
                    {

                    }
                }

                //更新日志
                URL = Config.ROOT_URL + "accountex_log_update";
                nvc.Clear();
                Func.putPassWord(nvc);
                nvc.Add("qq", myqq);
                nvc.Add("fricount", qqs.Count);
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas.Contains("_succ_"))
                    {

                    }
                }
            }
            catch (Exception)
            {
            }
        }
    }
    
}