using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Automation;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;

namespace QQClient
{
    class AutoFriend
    {
        ErrorType errorType;
        string errorDesc;

        public ErrorType _ErrorType { get { return errorType; } }
        public string _ErrorDesc { get { return errorDesc; } }

        void SetError(ErrorType type, string desc)
        {
            errorType = type;
            errorDesc = desc;
        }

        public bool FindFriend(AutomationElement appElement, string qq)
        {
            if (Search(appElement, qq))
            {
                //打开添加好友界面
                AutomationElement friend = FriendMainUI(appElement, qq);
                if (friend == null)
                {
                    SetError(ErrorType.NotFindUI, "没有找到好友面板");
                    return false;
                }

                Time time = new Time();
                time.Start(new TimeSpan(0, 1, 0));
                while (true)
                {
                    if (time.End())
                    {
                        SetError(ErrorType.TimeOut, "超时");
                        return false;
                    }

                    if (FriendTest0())
                    {
                        CloseFriendFind(friend);
                        return false;
                    }

                    if(FriendTest1(friend))
                        return false;

                    if (FriendTest2(friend,true))
                        break;

                    if (FriendTest3(friend, true))
                        break;
                    
                    if (FriendTest4(friend))
                        break;
                }

                return FriendFinish(friend);
            }

            return false;
        }

        //加友权限 0 无需验证 1 需要验证 2 禁止加友 -1超时（忽略）
        public int CheckFriend(AutomationElement appElement, string qq)
        {
            if (Search(appElement, qq))
            {
                //打开添加好友界面
                AutomationElement friend = FriendMainUI(appElement, qq);
                if (friend == null)
                {
                    SetError(ErrorType.NotFindUI, "没有找到好友面板");
                    return -1;
                }

                Time time = new Time();
                time.Start(new TimeSpan(0, 1, 0));
                while (true)
                {
                    if (time.End())
                    {
                        SetError(ErrorType.TimeOut, "超时");
                        return -1;
                    }

                    if (FriendTest1(friend))
                    {
                        CloseFriendFind(friend);
                        return 2;
                    }

                    if (FriendTest2(friend,false))
                    {
                        CloseFriendFind(friend);
                        return 1;
                    }

                    if (FriendTest3(friend,false))
                    {
                        CloseFriendFind(friend);
                        return 1;
                    }

                    if (FriendTest4(friend))
                    {
                        CloseFriendFind(friend);
                        return 0;
                    }
                }
            }

            return -1;
        }

        void CloseFriendFind(AutomationElement friend)
        {
           AutomationElementCollection items = friend.FindAll(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
           for (int i = 0; i < items.Count; ++i)
           {
               LegacyIAccessiblePattern pattern = 
                   (LegacyIAccessiblePattern)items[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
               if (pattern != null)
               {
                   if (pattern.Current.Description == "关闭")
                   {
                       Input.ShowFrame(items[i]);
                       Input.Click(items[i], false);
                       return;
                   }
               }
           }
        }

        //搜索
        bool Search(AutomationElement appElement, string qq)
        {
            try
            {
                //检查是否已经有输入 
                AutomationElementCollection item = appElement.FindAll(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
                for (int i = 0; i < item.Count; ++i)
                {
                    AutomationElement child = item[i].FindFirst(TreeScope.Children,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354));
                    if (child != null)
                    {
                        if (child.Current.Name.Contains("搜索") &&
                            child.Current.Name.Contains("联系人") &&
                            child.Current.Name.Contains("讨论组") &&
                            child.Current.Name.Contains("群") &&
                            child.Current.Name.Contains("企业"))
                        {
                            child = item[i].FindFirst(TreeScope.Children,
                                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                            if (child != null)
                            {
                                Input.ShowFrame(appElement);
                                Input.Click(child, false);
                            }
                        }
                    }
                }

                AutomationElement element = appElement.FindFirst(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.NameProperty, "搜索：联系人、讨论组、群、企业"));
                if (element == null)
                {
                    SetError(ErrorType.NotFindUI, "没有找到搜索框");
                    return false;
                }

                Input.ShowFrame(appElement);
                Input.Click(element, false);

                Input.Clipboard(qq);
                Input.Ctrl_V();

                //进入查找等待
                {
                    Time time = new Time();
                    time.Start(new TimeSpan(0, 1, 0));
                    element = null;
                    int pos = 0;
                    while (element == null)
                    {
                        if (time.End())
                        {
                            SetError(ErrorType.TimeOut, "超时");
                            return false;
                        }

                        //是否是好友了
                        ++pos;
                        if (pos % 2 == 0)
                        {
                            item = appElement.FindAll(TreeScope.Descendants,
                                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
                            for (int i = 0; i < item.Count; ++i)
                            {
                                AutomationElementCollection childs = item[i].FindAll(TreeScope.Descendants,
                                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC357));
                                if (childs.Count == 2)
                                {
                                    string sss = "";
                                    for (int j = 0; j < childs.Count; ++j)
                                    {
                                        sss += childs[j].Current.Name + " ";
                                    }

                                    if (sss.Contains("好友") && sss.Contains(qq))
                                    {
                                        //已经是好友了
                                        SetError(ErrorType.AlreadyFriend, "已经是好友");
                                        return false;
                                    }
                                }
                            }
                        }

                        //等待查询结果
                        element = appElement.FindFirst(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.NameProperty, "无本地搜索结果..."));

                        System.Threading.Thread.Sleep(500);
                    }

                    //有查早结果了，双击查找到的对象
                    element = null;
                    item = appElement.FindAll(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));

                    Condition[] condition = new Condition[2];
                    condition[0] = new PropertyCondition(AutomationElement.NameProperty, "其他联系人");
                    condition[1] = new PropertyCondition(AutomationElement.NameProperty, "精确查找");
                    for (int i = 0; i < item.Count; ++i)
                    {
                        if (item[i].FindFirst(TreeScope.Children, new OrCondition(condition)) != null)
                        {
                            element = item[i];
                            break;
                        }
                    }
                    if (element == null)
                    {
                        SetError(ErrorType.NotFindFriend, "没有找到好友");
                        return false;
                    }

                    item = element.FindAll(TreeScope.Children,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC357));

                    element = null;
                    for (int i = 0; i < item.Count; ++i)
                    {
                        string name = item[i].Current.Name;
                        if (name.Contains(qq))
                        {
                            element = item[i];
                            break;
                        }
                    }

                    if (element == null)
                    {
                        SetError(ErrorType.NotFindFriend, "没有找到好友");
                        return false;
                    }

                    //再次确认是群还是好友
                    Input.MoveRight(element);
                    System.Threading.Thread.Sleep(300);
                    item = element.FindAll(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                    for (int i = 0; i < item.Count; ++i)
                    {
                       LegacyIAccessiblePattern pattern = 
                           (LegacyIAccessiblePattern)item[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                       if (pattern != null)
                       {
                           if (pattern.Current.Description == "加为好友")
                           {
                               //点击右边的添加按钮
                               Input.ClickRight(element, false);
                               return true;
                           }
                       }
                    }

                    SetError(ErrorType.NotFindFriend, "没有找到好友");
                    return false;
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);

                SetError(ErrorType.Exception, e.Message);
                return false;
            }
        }

        //添加好友界面
        AutomationElement FriendMainUI(AutomationElement appElement, string qq)
        {
            AutomationElementCollection item = AutomationElement.RootElement.FindAll(TreeScope.Children,
                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC370));

            appElement = null;
            for (int i = 0; i < item.Count; ++i)
            {
                string name = item[i].Current.Name;
                if (name.Contains(" - 添加好友"))
                {
                    appElement = item[i];
                    break;
                }
            }

            Input.ShowFrame(appElement);
            return appElement;
        }

        //身份验证窗口
        bool FriendTest0()
        {
            while (true)
            {
                try
                {
                    Condition[] condition = new Condition[2];
                    condition[0] = new PropertyCondition(AutomationElement.ClassNameProperty, "TXGuiFoundation");
                    condition[1] = new PropertyCondition(AutomationElement.NameProperty, "身份验证");
                    AutomationElement appElement = AutomationElement.RootElement.FindFirst(TreeScope.Children, new AndCondition(condition));
                    if (appElement != null)
                    {
                        SetError(ErrorType.SecurityCode, "身份验证");

                        condition[0] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350);
                        condition[1] = new PropertyCondition(AutomationElement.NameProperty, "取消");

                        appElement = appElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
                        if (appElement == null)
                        {
                            Debug.LogError("异常：找到了身份验证窗口,但是没有找到关闭按钮~!");
                            return true;
                        }

                        Input.ShowFrame(appElement);
                        Input.Click(appElement, true);

                        return true;
                    }

                    return false;
                }
                catch (Exception) { }
            }
        }
        //验证 第一种：需要输入正确答案的（放弃吧）
        bool FriendTest1(AutomationElement appElement)
        {
            AutomationElementCollection item = appElement.FindAll(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
            for (int i = 0; i < item.Count; ++i)
            {
                AutomationElement element = item[i].FindFirst(TreeScope.Children,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                if (element != null)
                {
                    if (element.Current.Name == "对方需要你回答一下验证问题:")
                    {
                        element = appElement.FindFirst(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.NameProperty, "关闭"));
                        if (element != null)
                        {
                            Input.ShowFrame(appElement);
                            Input.Click(element, false);
                        }

                        //关闭UI
                        SetError(ErrorType.AnswerQuestion, "需要正确回答问题");
                        return true;
                    }
                }
            }

            return false;
        }

        //验证 第二种：需要输入验证信息的
        bool FriendTest2(AutomationElement appElement, bool next)
        {
            Condition[] condition = new Condition[2];
            condition[0] = new PropertyCondition(AutomationElement.NameProperty, "请输入验证信息:");
            condition[1] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354);
            AutomationElement element = appElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
            if (element == null)
                return false;

            if( next )
            {
                Input.ShowFrame(appElement);
                Input.Click(element, false);
                Input.KeybdClick(Input.VK_BACK);
                Input.KeybdClick(Input.VK_BACK);

                Input.Clipboard("你好！~美女");
                Input.Ctrl_V();

                element = appElement.FindFirst(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.NameProperty, "下一步"));
                if (element == null)
                {
                    SetError(ErrorType.UnknownError, "下一步异常");
                    return false;
                }

                Input.ShowFrame(appElement);
                Input.Click(element, false);
            }
            return true;
        }

        //验证 第三种：需要回答验证问题的
        bool FriendTest3(AutomationElement appElement, bool next)
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
                        element = item[i];
                        break;
                    }
                }
            }

            //输入回答内容
            if (element == null)
                return false;

            if (next)
            {
                item = element.FindAll(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));

                for (int i = 0; i < item.Count; ++i)
                {
                    AutomationElement edit = item[i].FindFirst(TreeScope.Children,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC354));
                    if (edit != null)
                    {
                        Input.ShowFrame(appElement);
                        Input.Click(edit, false);

                        Input.Clipboard("爱你");
                        Input.Ctrl_V();

                        System.Threading.Thread.Sleep(500);
                    }
                }

                element = appElement.FindFirst(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.NameProperty, "下一步"));
                Input.ShowFrame(appElement);
                Input.Click(element, false);
            }
            return true;
        }
        
        //验证 第四种：不需要任何验证
        bool FriendTest4(AutomationElement appElement)
        {
            return appElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "备注姓名:")) != null;
        }

        //结束加好友
        bool FriendFinish(AutomationElement appElement)
        {
            AutomationElement element = appElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "下一步"));
            if (null == element)
            {
                SetError(ErrorType.UnknownError, "下一步异常");
                return false;
            }

            Time time = new Time();
            time.Start(new TimeSpan(0, 1, 0));
            AutomationElement next = null;
            while (next == null)
            {
                if (time.End())
                {
                    SetError(ErrorType.TimeOut, "超时");
                    return false;
                }

                System.Threading.Thread.Sleep(500);
                next = appElement.FindFirst(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.NameProperty, "备注姓名:"));
            }
            Input.ShowFrame(appElement);
            Input.Click(element, false);

            time = new Time();
            time.Start(new TimeSpan(0, 1, 0));
            element = null;
            while (element == null)
            {
                if (time.End())
                {
                    SetError(ErrorType.TimeOut, "超时");
                    return false;
                }
                System.Threading.Thread.Sleep(500);

                //检查是否被暂停使用
                AutomationElementCollection item = appElement.FindAll(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                for (int i = 0; i < item.Count; ++i)
                {
                    string name = item[i].Current.Name;
                    if (name.Contains("抱歉") ||
                        name.Contains("由于你操作过于频繁或账户存在不安全因素") ||
                        name.Contains("添加好友功能暂被停止使用"))
                    {
                        SetError(ErrorType.AddFriendSuspended, "被限制加好友");

                        element = appElement.FindFirst(TreeScope.Descendants,
                            new PropertyCondition(AutomationElement.NameProperty, "取消"));
                        if( element != null )
                        {
                            Input.ShowFrame(appElement);
                            Input.Click(element, false);
                        }
                        return false;
                    }
                }


                if (element == null)
                {
                    //检查完成
                    element = appElement.FindFirst(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.NameProperty, "完成"));
                }
            }

            Input.ShowFrame(appElement);
            Input.Click(element, false);

            return true;
        }
    }
}
