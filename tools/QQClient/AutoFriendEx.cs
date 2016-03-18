using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Automation;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;
using System.IO;

namespace QQClient
{
    class AutoFriendEx
    {
        #region 这块代码是取爬虫数据库 friend_authority=-1的QQ号，并且完善爬虫数据库QQ信息用的
        public class AddFriInfo
        {
            static AddFriInfo(){
                read();
            }
            public int state;
            public String desc;
            public String info;

            static Dictionary<String, AddFriInfo> dics = new Dictionary<string, AddFriInfo>();

            public static void add(String _info,String _desc,int _state){
                if (dics.ContainsKey(_info))
                    return;
                AddFriInfo afi = new AddFriInfo();
                afi.state = _state;
                afi.desc = _desc;
                afi.info = _info;
                dics.Add(_info, afi);

                String pathName = "addfriinfo.txt";
                FileStream aFile = new FileStream(pathName, FileMode.Create);
                StreamWriter sw = new StreamWriter(aFile);
                foreach (AddFriInfo _d in dics.Values)
                {
                    String _l = String.Format("{0}\t{1}\t{2}\n", _d.info, _d.desc, _d.state);
                    sw.Write(_l);
                }
                
                sw.Close();
            }
            public static void read()
            {
                String pathName = "addfriinfo.txt";
                if (File.Exists(pathName))
                {
                    FileStream aFile = new FileStream(pathName, FileMode.Open);
                    StreamReader sr = new StreamReader(aFile);
                    String strLine = sr.ReadLine();
                    //Read data in line by line 这个兄台看的懂吧~一行一行的读取
                    while(strLine != null)
                    {
                        String[] ps = strLine.Split('\t');
                        AddFriInfo afi = new AddFriInfo();
                        afi.state = Convert.ToInt32(ps[2]);
                        afi.desc = ps[1];
                        afi.info = ps[0];
                        dics.Add(afi.info, afi);

                        strLine = sr.ReadLine();
                    }
                    sr.Close();
                }
            }

        } 
        public static AutomationElement openSearchUI(string qq)
        {
            AutomationElement appElement = null;
            while (appElement == null)
            {
                try
                {
                    Condition[] condition = new Condition[2];
                    condition[0] = new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation");
                    condition[1] = new PropertyCondition(AutomationElement.NameProperty, "查找");

                    appElement = AutomationElement.RootElement.FindFirst(TreeScope.Children, new AndCondition(condition));
                }
                catch (Exception e)
                {
                    Debug.OnException(e);
                }

                if (appElement == null)
                {
                    Debug.Log("请打开查找好友界面");
                    System.Threading.Thread.Sleep(1000);
                }
            }
            Input.Clipboard(qq);
            Input.ShowFrame(appElement);
            Input.Click(appElement, 250, 100, true);
            Input.Ctrl_A();
            Input.Ctrl_V();

            return appElement;
        }
        public static AutomationElement checkQQInfoUI()
        {
            while (true)
            {
                try
                {
                    List<Condition> conditions = new List<Condition>();
                    conditions.Add(new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation"));
                    AutomationElementCollection items = AutomationElement.RootElement.FindAll(TreeScope.Children, new AndCondition(conditions.ToArray()));
                    for (int i = 0; i < items.Count; ++i)
                    {
                        AutomationElementCollection items2 = items[i].FindAll(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC363));
                        string key = "";
                        for (int j = 0; j < items2.Count; ++j)
                        {
                            key += items2[j].Current.Name + "|";
                        }

                        int count = 0;
                        if (key.Contains("资料")) ++count;
                        if (key.Contains("相册")) ++count;
                        if (key.Contains("动态")) ++count;
                        if (key.Contains("标签")) ++count;
                        if (key.Contains("账户")) ++count;
                        if (key.Contains("游戏")) ++count;
                        if (count >= 2)
                        {
                            return items[i];
                        }
                    }

                    return null;
                }
                catch (Exception e)
                {
                    Debug.OnException(e);
                    System.Threading.Thread.Sleep(500);
                }
            }
            
        }
//         public class QQInfo
//         {
        //             public String 
        //         }
        public static string getNumberInStr(String str)
        {
            String retstr = "";
            if (str != null && str.Length > 0)
            {
                for (int i = 0; i < str.Length; i++)
                {
                    if(str[i] >=48 && str[i] < 57)
                        retstr += str[i];
                }
            }
            return retstr;
        }
        public static Dictionary<String, String> getQQInfoFromUI(AutomationElement appElement)
        {
            try
            {
                Dictionary<String, String> dicInfo = new Dictionary<string, string>();
                AutomationElementCollection items = null;
                AutomationElement tagItme = null;
                List<Condition> conditions = new List<Condition>();

                //基本信息///////////////////////////////////
                items = appElement.FindAll(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354));
                for (int i = 0; i < items.Count; ++i)
                {
                    if (items[i].Current.Name == "帐号")//账号
                    {
                        LegacyIAccessiblePattern pattern =
                            (LegacyIAccessiblePattern)items[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                        if (pattern != null)
                        {
                            if (!dicInfo.ContainsKey("qq"))
                                dicInfo.Add("qq", pattern.Current.Value);
                        }
                    }
                    else if (items[i].Current.Name == "昵称")//昵称
                    {
                        LegacyIAccessiblePattern pattern =
                            (LegacyIAccessiblePattern)items[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                        if (pattern != null)
                        {
                            if (!dicInfo.ContainsKey("nickname"))
                                dicInfo.Add("nickname", pattern.Current.Value);
                        }
                    }
                }
                
                items = appElement.FindAll(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
                for (int i = 0; i < items.Count; ++i)
                {
                    //个人信息///////////////////////////////////
                    conditions.Clear();
                    conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "个      人："));
                    conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                    tagItme = items[i].FindFirst(TreeScope.Children, new AndCondition(conditions.ToArray()));
                    if (tagItme != null)//命中的话就
                    {
                        AutomationElementCollection items2 = items[i].FindAll(TreeScope.Children,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                        for (int j = 0; j < items2.Count; ++j)
                        {
                            string curName = items2[j].Current.Name;
                            //性别
                            if (curName.Contains("男")){
                                 if (!dicInfo.ContainsKey("sex"))
                                    dicInfo.Add("sex", "男");
                            }
                            else if (curName.Contains("女"))
                            {
                                if (!dicInfo.ContainsKey("sex"))
                                    dicInfo.Add("sex", "女");
                            }
                            //年龄
                            if (curName.Contains("岁"))
                            {
                                if (!dicInfo.ContainsKey("age"))
                                    dicInfo.Add("age", getNumberInStr(curName));
                            }

                            //出生，星座，属相
                            if (curName.Contains("年") ||　curName.Contains("月") || curName.Contains("日") || curName.Contains("属") || curName.Contains("座"))
                            {
                                if (!dicInfo.ContainsKey("birthday"))
                                    dicInfo.Add("birthday", curName);
                            }
                        }
                    }

                    //地址///////////////////////////////////
                    conditions.Clear();
                    conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "所 在 地："));
                    conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                    tagItme = items[i].FindFirst(TreeScope.Children, new AndCondition(conditions.ToArray()));
                    if (tagItme != null)//命中的话就
                    {
                        AutomationElementCollection items2 = items[i].FindAll(TreeScope.Children,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354));
                        for (int j = 0; j < items2.Count; ++j)
                        {
                            if (items2[j].Current.Name == "所在地")
                            {
                                LegacyIAccessiblePattern pattern =
                                    (LegacyIAccessiblePattern)items2[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                                if (pattern != null)
                                {
                                    if (!dicInfo.ContainsKey("address"))
                                        dicInfo.Add("address", pattern.Current.Value);
                                }
                            }
                        }
                    }

                    //Q龄///////////////////////////////////
                    conditions.Clear();
                    conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "Q       龄："));
                    conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                    tagItme = items[i].FindFirst(TreeScope.Children, new AndCondition(conditions.ToArray()));
                    if (tagItme != null)//命中的话就
                    {
                        AutomationElementCollection items2 = items[i].FindAll(TreeScope.Children,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354));
                        for (int j = 0; j < items2.Count; ++j)
                        {
                            if (items2[j].Current.Name == "Q龄")
                            {
                                LegacyIAccessiblePattern pattern =
                                    (LegacyIAccessiblePattern)items2[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                                if (pattern != null)
                                {
                                    if (!dicInfo.ContainsKey("qqage"))
                                        dicInfo.Add("qqage", getNumberInStr(pattern.Current.Value));
                                }
                            }
                        }
                    }
                }

                //加友状态///////////////////////////////////
                conditions.Clear();
                conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "加为好友"));
                conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "按钮"));
                tagItme = appElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
                if (tagItme != null)
                {

                    while (true)
                    {
                        Input.ShowFrame(appElement);
                        Input.Click(appElement, 425, 175, false);

                        int state = CheckFriend();
                        if (state != -2)//资料界面已打开
                        {
                            if (!dicInfo.ContainsKey("friend_authority"))
                                dicInfo.Add("friend_authority", state.ToString());
                            closeAddFriendUI();
                            break;
                        }

                        System.Threading.Thread.Sleep(100);
                    }
                }

                return dicInfo;
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }

            return null;
        }
        //加友权限 0 无需验证 1 需要验证 2 禁止加友 -1超时（忽略）-2界面为生成
        static int CheckFriend()
        {
            //打开添加好友界面
            AutomationElement friend = checkAddFriendUI();
            if (friend == null)
            {
                //result = "没有找到好友面板";
                return -2;
            }

            Time time = new Time();
            time.Start(new TimeSpan(0, 1, 0));
            while (true)
            {
                if (time.End())
                {
                    return -1;
                }

                if (FriendTest1(friend))
                {
                    return 2;
                }

                if (FriendTest2(friend, false))
                {
                    return 1;
                }

                if (FriendTest3(friend, false))
                {
                    return 1;
                }

                if (FriendTest4(friend))
                {
                    return 0;
                }
            }
        }
        //添加好友界面
        static AutomationElement checkAddFriendUI()
        {
            AutomationElement appElement = null;
            List<Condition> conditions = new List<Condition>();
            conditions.Clear();
            conditions.Add(new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation"));
            conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC370));
            AutomationElementCollection item = AutomationElement.RootElement.FindAll(TreeScope.Children,new AndCondition(conditions.ToArray()));

            for (int i = 0; i < item.Count; ++i)
            {
                string name = item[i].Current.Name;
                if (name.Contains(" - 添加好友"))
                {
                    appElement = item[i];
                    break;
                }
            }
            if (appElement != null)
                Input.ShowFrame(appElement);
            return appElement;
        }
        //关闭好友界面
        static void closeAddFriendUI()
        {
            while (true)
            {
                AutomationElement appElement = null;
                List<Condition> conditions = new List<Condition>();
                conditions.Clear();
                conditions.Add(new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation"));
                conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC370));
                AutomationElementCollection item = AutomationElement.RootElement.FindAll(TreeScope.Children, new AndCondition(conditions.ToArray()));

                for (int i = 0; i < item.Count; ++i)
                {
                    string name = item[i].Current.Name;
                    if (name.Contains(" - 添加好友"))
                    {
                        appElement = item[i];
                        break;
                    }
                }

                if (appElement != null)
                {
                    Input.ShowFrame(appElement);
                    //关闭
                    conditions.Clear();
                    conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "关闭"));
                    conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                    conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "按钮"));
                    AutomationElement tagItme = appElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));

                    if (tagItme == null)
                    {
                        conditions.Clear();
                        conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "取消"));
                        conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                        conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "按钮"));
                        tagItme = appElement.FindFirst(TreeScope.Descendants, new AndCondition(
                            conditions.ToArray()
                            ));

                    }
                    
                    if (tagItme == null)
                    {
                        conditions.Clear();
                        conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "完成"));
                        conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                        conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "按钮"));
                        tagItme = appElement.FindFirst(TreeScope.Descendants, new AndCondition(
                            conditions.ToArray()
                            ));

                    }

                    if (tagItme != null)
                        Input.Click(tagItme, false);
                }
                else
                    break;
            }
        }

        //身份验证窗口
        //验证 第一种：需要输入正确答案的（放弃吧）
        static bool FriendTest1(AutomationElement appElement)
        {
            AutomationElementCollection item = appElement.FindAll(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
            for (int i = 0; i < item.Count; ++i)
            {
                Condition[] condition = new Condition[2];
                condition[0] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364);
                condition[1] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC355);
                AutomationElement element = item[i].FindFirst(TreeScope.Children, new OrCondition(condition));
                if (element != null)
                {
                    string key = element.Current.Name;
                    if (key == "对方需要你回答一下验证问题:" ||
                        key == "对方拒绝被添加" ||
                        key.Contains("已经是你的好友，不能重复添加"))
                    {
                        AutomationElementCollection item2 = item[i].FindAll(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                        for (int j = 0; j < item2.Count; ++j)
                        {
                            LegacyIAccessiblePattern pattern =
                                   (LegacyIAccessiblePattern)item2[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                            if (pattern != null && pattern.Current.ChildId == 2)
                            {
                                AddFriInfo.add(pattern.Current.Name, key, 2);
                                break;
                            }
                        }
                        return true;
                    }
                }
            }

            return false;
        }

        //验证 第二种：需要输入验证信息的
        static bool FriendTest2(AutomationElement appElement, bool next)
        {
            Condition[] condition = new Condition[2];
            condition[0] = new PropertyCondition(AutomationElement.NameProperty, "请输入验证信息:");
            condition[1] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354);
            AutomationElement element = appElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
            return element != null;
        }

        //验证 第三种：需要回答验证问题的
        static bool FriendTest3(AutomationElement appElement, bool next)
        {
            AutomationElement element = null;
            AutomationElementCollection item = appElement.FindAll(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
            for (int i = 0; i < item.Count; ++i)
            {
                AutomationElement tmp = item[i].FindFirst(TreeScope.Children,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                if (tmp != null)
                {
                    string name = tmp.Current.Name;
                    if (name.Contains("对方需要你回答") && name.Contains("个验证问题"))
                    {
                        AutomationElementCollection item2 = item[i].FindAll(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                        for (int j = 0; j < item2.Count; ++j)
                        {
                            LegacyIAccessiblePattern pattern =
                                   (LegacyIAccessiblePattern)item2[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                            if (pattern != null && pattern.Current.Name.Contains("问题") && !pattern.Current.Name.Contains("对方需要你回答"))
                            {
                                AddFriInfo.add(pattern.Current.Name, name, 1);
                            }
                        }

                        element = item[i];
                        break;
                    }
                }
            }

            return element != null;
        }
        
        //验证 第四种：不需要任何验证
        static bool FriendTest4(AutomationElement appElement)
        {
            return appElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "备注姓名:")) != null;
        }
#endregion

#region 这块代码是QQ好友数据库，并且更新加友状态用的
        public static AutomationElement openMyFriendListUI(AutomationElement appElement)
        {
            try
            {
                Input.ShowFrame(appElement);
                
                List<Condition> conditions = new List<Condition>();
                conditions.Clear();
                conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "我的好友"));
                conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC368));
                AutomationElement item = appElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));

                if (item != null)
                {
                    Input.Click_MouseRight(item, false);
                    while (true)
                    {
                        //检测菜单打开了没
                        conditions.Clear();
                        conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC35B));
                        conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "菜单项目"));
                        conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "好友管理器"));

                        AutomationElement menuItem = appElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
                        if (menuItem != null)
                        {
                            Input.Click(menuItem, false);
                            return checkMyFriendListUI();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }

            return null;
        }
        public static AutomationElement checkMyFriendListUI()
        {
            List<Condition> conditions = new List<Condition>();
            conditions.Clear();
            conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC370));
            conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "窗口"));
            conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "好友管理器"));
            conditions.Add(new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation"));
            AutomationElement appElement = null;
            while (appElement == null)
            {
                appElement = AutomationElement.RootElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
            }
            return appElement;
        }
        public static List<String> getMyFriendListQQs(AutomationElement appElement)
        {
            if( appElement == null)
                appElement = checkMyFriendListUI();
            Input.ShowFrame(appElement);
            
            List<Condition> conditions = new List<Condition>();
            //查找“全部好友”按钮
            conditions.Clear();
            conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC368));
            conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "树项目"));
            AutomationElementCollection items = appElement.FindAll(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
            for( int i = 0; i < items.Count; ++i)
            {
                if(items[i].Current.Name.Contains("全部好友")){
                    Input.Click(items[i], false);
                    break;
                }
            }

            List<String> qqs = new List<string>();
            conditions.Clear();
            conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC357));
            conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "列表项目"));
            conditions.Add(new PropertyCondition(AutomationElement.NameProperty, ""));
            AutomationElementCollection items2 = appElement.FindAll(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
            for (int i = 0; i < items2.Count; ++i)
            {
                LegacyIAccessiblePattern pattern =
                           (LegacyIAccessiblePattern)items2[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                if (pattern == null)
                    continue;

                String desc = pattern.Current.Description;
                if (desc.Contains("帐号") || desc.Contains("账号"))
                {
                    string qq = getNumberInStr(desc);
                    if (qq.Length > 3)
                    {
                        qqs.Add(qq);
                    }
                }
            }
            return qqs;
        }
#endregion
    }
}
