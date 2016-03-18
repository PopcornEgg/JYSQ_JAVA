using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using QQZeoneMarketing.SpiderCommons;
using System.Threading;
using System.Collections.Specialized;
using System.Net;
using utitls;

namespace QQZeoneMarketing
{
    public partial class Spider : UserControl
    {
        Common preCommon = null;
        Common curCommon = null;
        Dictionary<string, Common> commons = new Dictionary<string, Common>();
        Random random = new Random();


        Queue<string> qq = new Queue<string>();
        //所有QQ，bool 是否有权限
        Dictionary<string, bool> qqs = new Dictionary<string,bool>();

        object dblock = new object();
        List<UserInfoDB> dblist = new List<UserInfoDB>();

        UserInfoDB curUserInfoDB = null;
        public UserInfoDB UIDB
        {
            get
            {
                if (curUserInfoDB == null)
                    curUserInfoDB = new UserInfoDB();
                return curUserInfoDB; 
            }

            set
            {
                curUserInfoDB = value;
            }
        }

        int idx = 0;
        public int okcount = 0;
        public string QQ
        {
            get
            {
                idx %= qq.Count();

                string url = qq.Dequeue();
                UIDB.qq = url;

                return url;
            }
        }

        //爬到的QQ
        public void AddQQ(string url)
        {
            if (url.Contains("http://user.qzone.qq.com/"))
            {
                string nurl = url.Replace("http://user.qzone.qq.com/", "");
                string[] array = nurl.Split(new char[] { '/' });

                if (!qqs.ContainsKey(array[0]))
                {
                    qqs.Add(array[0], true);
                    qq.Enqueue(array[0]);
                    addUploadUserInfo(array[0]);
                }
            }
        }

        //一个用户获取结束
        public void UserGetOver() 
        {
            if (curUserInfoDB != null)
            {
                Log(curUserInfoDB.ToString());

                lock (dblock)
                {
                    dblist.Add(curUserInfoDB);
                    curUserInfoDB = null;
                }
            }
        }
        public void addUploadUserInfo(String qq)
        {
            lock (dblock)
            {
                UserInfoDB info = new UserInfoDB();
                info.qq = qq;
                info.pqq = curUserInfoDB != null ? curUserInfoDB.qq : "";
                dblist.Add(info);
            }
        }

        public Spider()
        {
            InitializeComponent();

            webBrowser1.DocumentCompleted += new WebBrowserDocumentCompletedEventHandler(webBrowser1_DocumentCompleted);

            RegisterCommonn(new Purview());
            RegisterCommonn(new NewUser());
            RegisterCommonn(new UserInfo());
            RegisterCommonn(new ShuoShuo());

            timer1.Enabled = false;
            logger.Items.Clear();
            LoadQQ();

            Thread uploader = new Thread(new ThreadStart(uploadUserInfo));
            uploader.Start();
        }

        //启动时从配置文件中读取上一次缓存未处理的QQ号码
        void LoadQQ()
        {
            try
            {
                string strs = File.ReadAllText("未处理.txt");
                string[] _qqs = strs.Split(new char[] { '\n', '\t' });
                for (int i = 0; i < _qqs.Length; ++i)
                {
                    if (_qqs[i].Length > 1 && !qqs.ContainsKey(_qqs[i]))
                    {
                        qq.Enqueue(_qqs[i]);
                        qqs.Add(_qqs[i], true);
                    }
                }
            }
            catch
            {

            }
           
        }
        void SaveQQ()
        {
            string fstr = "";
            string[] _qqs = qq.ToArray();
            for (int i = 0; i < _qqs.Length; ++i)
            {
                fstr += _qqs[i] + "\t\n";
            }

            File.WriteAllText("未处理.txt", fstr);
        }
        void SaveStayTimeQQ(String qq)
        {
            try
            {
                FileStream aFile = new FileStream("staytimeqq_Log.txt", FileMode.Append);
                StreamWriter sw = new StreamWriter(aFile);
                sw.WriteLine(qq);
                sw.Close();
            }
            catch{

            }
          
        }

        void RegisterCommonn(Common com)
        {
            com.spider = this;
            com.webBrowser1 = this.webBrowser1;
            commons.Add(com.GetType().Name, com);
        }

        public void ChangeCommon(string name)
        {
            Common com;
            if (commons.TryGetValue(name, out com))
            {
                curCommon = com;
            }
            else
            {
                MessageBox.Show("没有找到：" + name);
            }
        }

        //网页加载完成回调
        void webBrowser1_DocumentCompleted(object sender, WebBrowserDocumentCompletedEventArgs e)
        {
            webBrowser1.ScriptErrorsSuppressed = true;
            if (webBrowser1.ReadyState != WebBrowserReadyState.Complete) 
            {
                return;
            }
            if (curCommon != null)
                curCommon.DocumentCompleted();
        }

        public void Log(string str)
        {
            if (str == null)
                return;
            logger.Items.Add(str);
            logger.SetSelected(logger.Items.Count - 1, true);
        }

        private static int maxStayTime = 30 * 1000;
        public int curStayTime = 0;
        public virtual void checkStayTime()
        {
            //当前网页停留时间过长
            int st = Environment.TickCount - curStayTime;
            if (st > maxStayTime)
            {
                ChangeCommon("NewUser");
            }
        } 
        private void timer1_Tick(object sender, EventArgs e)
        {
            try
            {
                if (preCommon != curCommon)
                {
                    if (preCommon != null)
                        preCommon.Leave();

                    preCommon = curCommon;
                    curCommon.Enter();
                    Console.WriteLine(curCommon.GetType().Name);
                }

                if (curCommon != null)
                {
                    curCommon.Tick();
                }

                label1.Text = string.Format("{0}/{1}", okcount, qq.Count);

                checkStayTime();
            }
            catch (Exception ex)
            {
                LogFile.add(ex.ToString(), "Spider.cs:timer1_Tick", 1);
            }
           
        }


        private void btn_Start_Click(object sender, EventArgs e)
        {
            timer1.Enabled = !timer1.Enabled;
            btn_Start.Text = timer1.Enabled ? "停止" : "开始";
            preCommon = null;
            curCommon = null;
            ChangeCommon("NewUser");
        }

        private void btn_Saveqq_Click(object sender, EventArgs e)
        {
            SaveQQ();
        }

      
        // Note that Go is now an instance method
        private void uploadUserInfo() {
             while(true)
             {
                 System.Threading.Thread.Sleep(10 * 1000);

                 List<UserInfoDB> bufferls = new List<UserInfoDB>();
                 bufferls.Clear();
                 lock (dblock)
                 {
                         for (int i = 0; i < dblist.Count; i++)
                         {
                             bufferls.Add(dblist[i]);
                         }

                         dblist.Clear();
                     //int start = curUploadIdx;
                     //int count = start + oneTimeUpload > dblist.Count ? dblist.Count : start + oneTimeUpload;
                     //if (start < count && start >= 0 && start < dblist.Count)
                     //{
                     //    for (int i = start; i < count; i++)
                     //    {
                     //        UserInfoDB uinfo = dblist[i].clone();
                     //        bufferls.Add(uinfo);
                     //    }
                     //    curUploadIdx += count;
                     //}
                 }
                 foreach (UserInfoDB uinfo in bufferls)
                 {
                     bool isok = false;
                     try
                     {
                         PostData dicr = UserInfoDB.userInfo2PostData(uinfo);
                         if (dicr != null)
                         {
                             string sttuas = Func.HttpPostData(Config.INSERT_ZONE_URL, 10000, dicr);
                             if (sttuas.Contains("_succ_"))
                             {
                                 isok = true;
                             }
                         }
                        
                     }
                     catch (Exception ex)
                     {
                         LogFile.add(ex.ToString(), "Spider.cs:uploadUserInfo", 1);
                        // Log(ex.ToString());
                     }
                     if(!isok){
                         LogFile.add(uinfo.ToString() + "   上传失败!", "Spider.cs:uploadUserInfo", 1);
                     }
                 }
             }
        }
        
    }
}
