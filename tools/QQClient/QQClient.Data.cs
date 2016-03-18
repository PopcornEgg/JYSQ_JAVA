using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using utitls;


namespace QQClient
{
    public partial class QQClient
    {
        private void DownloadFriendQQ()
        {
            while (friends.Count == 0)
            {
                try
                {
                    PostData nvc = new PostData();
                    Func.putPassWord(nvc);
                    //nvc.Add("order", "heat");
                    //nvc.Add("desc", "DESC");
                    nvc.Add("fields", "qq");
                    nvc.Add("count", "250");
                    if (nvc != null)
                    {
                        string sttuas = Func.HttpPostData(Config.LOCKQQ_ZONE_URL, 10000, nvc);
                        if (!sttuas.Contains("_fail_"))
                        {
                            MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                            for (int i = 0; i < jsons.GetListCount(); i++)
                            {
                                MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                                friends.Enqueue(Func.MyUrlDeCode(json["qq"].AsString()));
                            }

                        }
                    }
                }
                catch (Exception e)
                {
                    Debug.OnException(e);
                }

                if (friends.Count == 0)
                {
                    System.Threading.Thread.Sleep(5000);
                    Debug.LogError("获取要加的QQ失败了，是不是完了？或者网络出什么问题啦！！");
                }
            }
        }
        //初始化数据
        void InitData()
        {
            InitializeAccount();
        }

        void InitializeAccount()
        {
            try
            {
                String URL = Config.ROOT_URL + "account_getlist";
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                nvc.Add("order", "logintime");
                nvc.Add("desc", "DESC");
                nvc.Add("start", "0");
                nvc.Add("count", "100");
                nvc.Add("tag", "loginip");
                string ip = Tools.GetIP();
                nvc.Add("tagval", ip);

                Debug.LogLine("本机IP：" + ip);
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(URL, 10000, nvc);
                    Debug.Log(sttuas);
                    if (sttuas.Length > 10)
                    {

                        MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                        for (int i = 0; i < jsons.GetListCount(); i++)
                        {
                            MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                            int canuse = json["canuse"].AsInt();
                            if (canuse == 1)
                            {
                                QQInfo info = new QQInfo();
                                info.qq = json["qq"].AsString();
                                info.pws = Func.MyUrlDeCode(json["password"].AsString());
                                info.nickname = Func.MyUrlDeCode(json["nickname"].AsString());

                                if (!qqInfo.ContainsKey(info.qq))
                                {
                                    qqinfo.Enqueue(info.qq);
                                    qqInfo.Add(info.qq, info);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }


            try
            {
                String URL = Config.ROOT_URL + "accountex_getlist";
                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                nvc.Add("start", "0");
                nvc.Add("count", "0");
                //nvc.Add("tag", "qq");
                //nvc.Add("tagval", "251197161");
                if (nvc != null)
                {
                    string sttuas = Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas.Length > 10)
                    {
                        MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                        for (int i = 0; i < jsons.GetListCount(); i++)
                        {
                            MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                            string qq = json["qq"].AsString();

                            QQInfo info;
                            if (qqInfo.TryGetValue(qq, out info))
                            {
                                info.state = Func.MyUrlDeCode(json["state"].AsString());
                                try
                                {
                                    info.locktime = DateTime.Now;
                                    info.locktime = DateTime.Parse(Func.MyUrlDeCode(json["locktime"].AsString()));
                                }
                                catch (Exception) { }
                            }
                        }
                    }
                }
            }
            catch (Exception)
            {
            }
        }
    }
}
