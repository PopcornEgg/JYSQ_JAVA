using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using utitls;

namespace QQClient
{
    public partial class QQClient
    {
        bool active = false;
        Queue<QQ> qqs = new Queue<QQ>();
        Queue<string> qqinfo = new Queue<string>();
        Dictionary<string, QQInfo> qqInfo = new Dictionary<string, QQInfo>();
        Queue<string> friends = new Queue<string>();

        public void Start()
        {
            try
            {
                Debug.LogLine("【加载数据中】");
                active = true;
                InitData();
                Debug.LogLine("【准备环境中】");
                //AutoLogin.CloseQQProcess();
                Debug.LogLine("【奔跑吧！兄弟！！】");
                //Tools.OpenAllQQWindow();

                if (qqInfo.Count == 0)
                {
                    Debug.LogError("没有找到任何QQ！请检查IP什么是否配置正确");
                    return;
                }
                //进入循环
                while (active)
                {
                    while (qqs.Count < 3)
                    {
                        QQInfo info = NextQQ();
                        if (info == null)
                            break;

                        QQ qq = new QQ(this);
                        if (qq.Start(info))
                            qqs.Enqueue(qq);
                    }

                    QQ _qq = qqs.Dequeue();
                    qqs.Enqueue(_qq);

                    if (_qq.Tick())
                    {
                        _qq.Stop();

                        //加入下一个QQ
                        QQInfo info = NextQQ();
                        if (info != null)
                            _qq.Start(info);
                    }

                    System.Threading.Thread.Sleep(100);
                }
            }
            catch (Exception e)
            {
                Debug.LogError("启动QQ客户端失败了");
                Debug.OnException(e);
            }
        }

        public QQInfo NextQQ()
        {
            string qq = qqinfo.Dequeue();
            qqinfo.Enqueue(qq);

            QQInfo info;
            if (qqInfo.TryGetValue(qq, out info))
                return info;

            return null;
        }

        public string GetFriend()
        {
            if (friends.Count == 0)
                DownloadFriendQQ();

            return friends.Dequeue();
        }

        public void Stop()
        {
            active = false;
        }
    }
}
