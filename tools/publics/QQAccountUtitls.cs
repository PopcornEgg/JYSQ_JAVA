using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Collections.Specialized;
using System.Net;
using System.Threading;
using System.IO;
using utitls;

public class QQUtitls
{
    public static List<QQAccount> lsQQAccount = new List<QQAccount>();
    public static List<QQAccountEx> lsQQAccountEx = new List<QQAccountEx>();
    public static List<QQFriend> lsQQFriend = new List<QQFriend>();

    public class QQAccount
    {
        public String qq;
        public String password;
        public String nickname;
        public String loginip;
        public int canuse = 0;
        public String phonenumber;
        public String owner;
        public String question1;
        public String question2;
        public String question3;
        public String answer1;
        public String answer2;
        public String answer3;
    }
    public class QQAccountEx
    {
        public String qq;
        public String state;
        public String locktime;
    }
    public class QQFriend
    {
        public String qq;
        public String qqfri;
        public String addtime;
        public String state;
    }

    static public void account_getlist()
    {
        try
        {
            String URL = Config.ROOT_URL + "account_getlist";
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            nvc.Add("order", "logintime");
            nvc.Add("desc", "DESC");
            nvc.Add("start", "0");
            nvc.Add("count", "0");
            nvc.Add("tag", "loginip");
            nvc.Add("tagval", "127.0.0.1");
            if (nvc != null)
            {
                string sttuas = Func.HttpPostData(URL, 10000, nvc);
                if (sttuas.Length > 10)
                {

                    MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                    for (int i = 0; i < jsons.GetListCount(); i++)
                    {
                        MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                        QQAccount info = new QQAccount();
                        info.qq = json["qq"].AsString();
                        info.password = Func.MyUrlDeCode(json["password"].AsString());
                        info.nickname = Func.MyUrlDeCode(json["nickname"].AsString());
                        info.loginip = Func.MyUrlDeCode(json["loginip"].AsString());
                        info.canuse = json["canuse"].AsInt();
                        info.phonenumber = json["phonenumber"].AsString();
                        info.owner = Func.MyUrlDeCode(json["owner"].AsString());
                        info.question1 = Func.MyUrlDeCode(json["question1"].AsString());
                        info.question2 = Func.MyUrlDeCode(json["question2"].AsString());
                        info.question3 = Func.MyUrlDeCode(json["question3"].AsString());
                        info.answer1 = Func.MyUrlDeCode(json["answer1"].AsString());
                        info.answer2 = Func.MyUrlDeCode(json["answer2"].AsString());
                        info.answer3 = Func.MyUrlDeCode(json["answer3"].AsString());
                        
                        lsQQAccount.Add(info);
                    }
                }
            }
        }
        catch (Exception)
        {
        }
    }

    static public void account_update()
    {
        try
        {
            String URL = Config.ROOT_URL + "account_update";
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            nvc.Add("qq", "251197161");
            nvc.Add("password", "mm21010");
            nvc.Add("nickname", "哈哈大饭店");
            nvc.Add("question1", "question1");
            nvc.Add("question3", "question3");
            nvc.Add("answer1", "answer1");
            nvc.Add("friendcount", "888");
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
    static public void accountEx_getlist()
    {
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
                        QQAccountEx info = new QQAccountEx();
                        info.qq = json["qq"].AsString();
                        //info.state = Func.MyUrlDeCode(json["state"].AsString());
                       // info.locktime = Func.MyUrlDeCode(json["locktime"].AsString());
                       
                        lsQQAccountEx.Add(info);
                    }
                }
            }
        }
        catch (Exception)
        {
        }
    }

    static public void accountEx_update()
    {
        try
        {
            String URL = Config.ROOT_URL + "accountex_update";
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            nvc.Add("qq", "251197161");
            nvc.Add("state", "非常好");
            nvc.Add("locktime", "一万年前");
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
    static public void friend_getlist()
    {
        try
        {
            String URL = Config.ROOT_URL + "friend_getlist";
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            //nvc.Add("order", "addtime");
            //nvc.Add("desc", "DESC");
            nvc.Add("start", "0");
            nvc.Add("count", "0");
            nvc.Add("tag", "qq");
            nvc.Add("tagval", "251197161");
            if (nvc != null)
            {
                string sttuas = Func.HttpPostData(URL, 10000, nvc);
                if (sttuas.Length > 10)
                {
                    MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                    for (int i = 0; i < jsons.GetListCount(); i++)
                    {
                        MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                        QQFriend info = new QQFriend();
                        info.qq = json["qq"].AsString();
                        info.qqfri = json["qqfri"].AsString();
                        info.addtime = Func.MyUrlDeCode(json["addtime"].AsString());
                        info.state = Func.MyUrlDeCode(json["state"].AsString());
                        lsQQFriend.Add(info);
                    }
                }
            }
        }
        catch (Exception)
        {
        }
    }
    static public void friend_update()
    {
        try
        {
            String URL = Config.ROOT_URL + "friend_update";
            PostData nvc = new PostData();
            Func.putPassWord(nvc);
            nvc.Add("qq", "251197161");
            nvc.Add("qqfri", "7777");
            nvc.Add("addtime", "2015-10-28 17:25:00");
            nvc.Add("state", "申请中");
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
    static public String test_updateqq_getqqs(Queue<string> qqs)
    {
        StringBuilder sb = new StringBuilder("");
        int maxCount = 100;
        int count = qqs.Count > maxCount ? maxCount : qqs.Count;
        for (int i = 0; i < maxCount; i++)
        {
            if(i==0)
                sb.Append(qqs.Dequeue());
            else
                sb.Append(","+qqs.Dequeue());
        }
        return sb.ToString();
    }
    static public void test_updateqq_from_spider()
    {
        try
        {
            //加载文件
            Queue<string> qqs = new Queue<string>();
            string strs = File.ReadAllText("未处理.txt");
            string[] _qqs = strs.Split(new char[] { '\n', '\t' });
            for (int i = 0; i < _qqs.Length; ++i)
            {
                if (_qqs[i].Length > 1 )
                {
                    qqs.Enqueue(_qqs[i]);
                }
            }

            String URL = Config.ROOT_URL + "test_updateqq";
            while (qqs.Count > 0)
            {
                String strqqs = test_updateqq_getqqs(qqs);

                PostData nvc = new PostData();
                Func.putPassWord(nvc);
                nvc.Add("qqs", strqqs);
                if (nvc != null)
                {
                    Console.WriteLine("\n剩余：【" + qqs.Count + "】");
                    string sttuas = Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas.Length > 10)
                    {
                        MyJson.JsonNode_Object json = MyJson.Parse(sttuas) as MyJson.JsonNode_Object;
                        String succ = json["succ"].AsString();
                        int succcount = succ.Split('[').Length - 1;
                        
                        String fail = json["fail"].AsString();
                        int failcount = fail.Split('[').Length - 1;

                        int all = succcount + failcount;

                        Console.WriteLine("插入成功：" + succcount + "/" + all + " " + succ);
                        Console.WriteLine("插入失败：" + failcount + "/" + all + " " + fail);
                    }
                }
                System.Threading.Thread.Sleep(100);
            }
           
        }
        catch (Exception)
        {
        }
    }

    static public void common_get()
    {
        try
        {
            SQLGetData nvc = new SQLGetData("zone");
            nvc.setInfo("", "", "0", "10");
            //nvc.addTag("qq", "904987377");
           // nvc.addTag("age", 23);
            nvc.addFeild("qq");
            nvc.addFeild("age");
            if (nvc != null)
            {
                string sttuas = Func.HttpPostData(SQLGetData.URL, 10000, nvc.getPostData());
                if (sttuas.Length > 10)
                {
                    MyJson.JsonNode_Array jsons = MyJson.Parse(sttuas) as MyJson.JsonNode_Array;
                    for (int i = 0; i < jsons.GetListCount(); i++)
                    {
                        MyJson.JsonNode_Object json = jsons.GetArrayItem(i) as MyJson.JsonNode_Object;
                        QQAccountEx info = new QQAccountEx();
                        String qq = Func.MyUrlDeCode(json["qq"].AsString());
                        String age = Func.MyUrlDeCode(json["age"].AsString());
                        //info.state = Func.MyUrlDeCode(json["state"].AsString());
                        //info.locktime = Func.MyUrlDeCode(json["locktime"].AsString());

                        lsQQAccountEx.Add(info);
                    }
                }
            }
        }
        catch (Exception)
        {
        }
    }

    static public void common_update()
    {
        try
        {
            SQLUpdateData nvc = new SQLUpdateData("zone");
            nvc.setInfo("1");
            nvc.addTag("qq", "904987377");
            // nvc.addTag("age", 23);
            //nvc.addFeild("qq");
            nvc.addFeild("age",888);
            if (nvc != null)
            {
                string sttuas = Func.HttpPostData(SQLUpdateData.URL, 10000, nvc.getPostData());
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

