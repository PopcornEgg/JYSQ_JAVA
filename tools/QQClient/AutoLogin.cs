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
    class AutoLogin
    {
        string QQScLauncher = @"C:\Program Files (x86)\Tencent\QQ\Bin\QQScLauncher.exe";

        public AutoLogin()
        {
            string[] paths = new string[]
            {
                @"E:\Program Files (x86)\Tencent\QQ\Bin\QQScLauncher.exe",
                @"C:\Program Files (x86)\Tencent\QQ\Bin\QQScLauncher.exe"
            };

            for (int i = 0; i < paths.Length; ++i)
            {
                if (System.IO.File.Exists(paths[i]))
                {
                    QQScLauncher = paths[i];
                    break;
                }
            }
        }

        string errorDesc;
        ErrorType errorType;
        void SetError(ErrorType type, string desc) { errorType = type; errorDesc = desc; }
        public ErrorType _ErrorType { get { return errorType; } }
        public string _ErrorDesc { get { return errorDesc; } }

        string result;
        public string Result { get { return result; } }
        private AutomationElement StartProcess()
        {
            try
            {
                bool startLauncher = false;
                Time time = new Time();
                time.Start(new TimeSpan(0, 1, 0));
                AutomationElement appElement = null;
                while (appElement == null)
                {
                    if (time.End())
                    {
                        SetError(ErrorType.LoginException, "启动登录器超时");
                        return null;
                    }

                    try
                    {
                        AutomationElementCollection items = AutomationElement.RootElement.FindAll(TreeScope.Children,
                            new PropertyCondition(AutomationElement.NameProperty, "QQ"));

                        for (int ii = 0; ii < items.Count; ++ii)
                        {
                            AutomationElementCollection childs = items[ii].FindAll(TreeScope.Descendants,
                                new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));

                            string names = "";
                            for (int j = 0; j < childs.Count; ++j)
                            {
                                names += childs[j].Current.Name + "|";
                            }

                            if ((names.Contains("登   录") || names.Contains("安  全  登  录")) && !names.Contains("取   消"))
                            {
                                appElement = items[ii];
                                break;
                            }
                        }

                        if (appElement == null && !startLauncher )
                        {
                            startLauncher = true;
                            Process.Start(QQScLauncher);
                        }
                    }
                    catch (Exception e)
                    {
                        Debug.OnException(e);
                    }

                    if( appElement != null )
                        System.Threading.Thread.Sleep(1000);
                }

                return appElement;
            }
            catch (Exception e)
            {
                Debug.OnException(e);

                result = "启动登录器失败";
                return null;
            }
        }

        public AutomationElement Login(string qq, string pws, string nickname)
        {
            AutomationElement appElement = Login(qq,pws);
            if (appElement == null)
                return null;

            Time time = new Time(new TimeSpan(0, 3, 0));
           
            System.Threading.Thread.Sleep(500);
            //错误检查和等待登录成功
            while (true)
            {
                if( time.End())
                {
                    SetError(ErrorType.LoginException, "在等待登录时超时");
                    return null;
                }

                if (ErrorCheck_Password(appElement))
                {
                    SetError(ErrorType.PasswordError, "密码错误");
                    return null;
                }

                if (ErrorCheck_SecurityCode(appElement))
                {
                    SetError(ErrorType.SecurityCode, "需要输入验证码");
                    return null;
                }

                System.Threading.Thread.Sleep(500);

                AutomationElement retElement = WaitLogin(qq, pws, nickname);
                if (retElement != null)
                    return retElement;
            }
        }

        //重复登录（不用了，直接获UI返回就可以了）
        //bool ErrorCheck_Password(AutomationElement appElement)
        //{ 
        //}

        //密码错误
        bool ErrorCheck_Password(AutomationElement appElement)
        {
            try
            {
                AutomationElementCollection items = appElement.FindAll(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC355));
                for (int i = 0; i < items.Count; ++i)
                {
                    if (items[i].Current.Name.Contains("您输入的密码不正确"))
                    {
                        // 关闭提示页面
                        Condition[] condition = new Condition[2];
                        condition[0] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350);
                        condition[1] = new PropertyCondition(AutomationElement.NameProperty, "取消");
                        AutomationElement sure = appElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
                        if (sure != null)
                            Input.Click(sure, true);
                        else
                            Debug.LogError("怪了！居然没找到“确定”按钮");
                        
                        return true;
                    }
                }
            }
            catch (Exception)
            {
            }
            return false;
        }

        //需要输入验证码
        bool ErrorCheck_SecurityCode(AutomationElement appElement)
        {
            try
            {
                AutomationElementCollection items = appElement.FindAll(TreeScope.Descendants,
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC364));
                for (int i = 0; i < items.Count; ++i)
                {
                    if (items[i].Current.Name.Contains("为了您的帐号安全，本次登录需输验证码。"))
                    {
                        // 关闭提示页面
                        Condition[] condition = new Condition[2];
                        condition[0] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350);
                        condition[1] = new PropertyCondition(AutomationElement.NameProperty, "取消");
                        AutomationElement sure = appElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
                        if (sure != null)
                            Input.Click(sure, true);
                        else
                            Debug.LogError("怪了！居然没找到“取消”按钮");

                        return true;
                    }
                }
            }
            catch (Exception)
            {
            }
            return false;
        }

        AutomationElement Login(string qq, string pws)
        {
            try
            {
                Debug.LogLine("【登陆QQ】" + qq);

                AutomationElement rootElement = StartProcess();
                if (rootElement == null)
                    return null;

                Condition[] condition = new Condition[2];
                condition[0] = new PropertyCondition(AutomationElement.NameProperty, "QQ号码");
                condition[1] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC353);

                Time time = new Time();
                time.Start(new TimeSpan(0, 1, 0));
                AutomationElement appElement = null;
                while (appElement == null)
                {
                    if (time.End())
                    {
                        SetError(ErrorType.LoginException,"验证登录器超时");
                        return null;
                    }

                    appElement = rootElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
                    System.Threading.Thread.Sleep(500);
                }

                //置顶
                Input.ShowFrame(rootElement);

                //1. 设置账号
                Input.Click(appElement, false);
                Input.Clipboard(qq);
                Input.Ctrl_V();

                //2. 设置密码
                appElement = rootElement.FindFirst(TreeScope.Descendants, new PropertyCondition(AutomationElement.NameProperty, "密码"));
                Input.Click(appElement, false);
                Input.KeybdClick(Input.VK_BACK);

                char[] cpws = pws.ToCharArray();
                for (int i = 0; i < cpws.Length; ++i)
                {
                    //键盘按键事件(小写)
                    //检查大小写是否开启w
                    if (Console.CapsLock)
                        Input.KeybdClick((byte)Keys.CapsLock);

                    Input.KeybdClick((byte)Input.VkKeyScan(cpws[i]));
                }

                //点击登陆 
                condition[0] = new PropertyCondition(AutomationElement.NameProperty, "登   录");
                condition[1] = new PropertyCondition(AutomationElement.NameProperty, "安  全  登  录");

                time.Start(new TimeSpan(0, 0, 30));
                appElement = null;
                while (appElement == null)
                {
                    if (time.End())
                    {
                        SetError(ErrorType.LoginException, "登陆超时：获取登录按钮失败");
                        return null;
                    }

                    appElement = rootElement.FindFirst(TreeScope.Descendants, new OrCondition(condition));

                    if (appElement == null)
                        System.Threading.Thread.Sleep(500);
                }
                Input.Click(appElement, false);

                return rootElement;
            }
            catch (Exception e)
            {
                Debug.OnException(e);
                return null;
            }
        }

        AutomationElement WaitLogin(string qq, string pws, string nickname)
        {
            try
            {
                AutomationElementCollection items = AutomationElement.RootElement.FindAll(TreeScope.Children,
                    new PropertyCondition(AutomationElement.NameProperty, "QQ"));
                for (int i = 0; i < items.Count; ++i)
                {
                    AutomationElementCollection items2 = items[i].FindAll(TreeScope.Descendants,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
                    for (int j = 0; j < items2.Count; ++j)
                    {
                        LegacyIAccessiblePattern pattern = (LegacyIAccessiblePattern)items2[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                        if (pattern != null)
                        {
                            //通过昵称来确定登陆是否成功,可鞥不太准确,最好是QQ号
                            if (pattern.Current.Description == nickname)
                                return items[i];
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

        public static void LoginOut(AutomationElement appElement)
        {
            Time time = new Time();
            time.Start(new TimeSpan(0, 3, 0));
            while (true)
            {
                try
                {
                    if (time.End())
                    {
                        return;
                    }

                    AutomationElementCollection items = appElement.FindAll(TreeScope.Children,
                        new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC371));
                    for (int i = 0; i < items.Count; ++i)
                    {
                        AutomationElementCollection item = items[i].FindAll(TreeScope.Children,
                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                        for (int j = 0; j < item.Count; ++j)
                        {
                            LegacyIAccessiblePattern pattern = (LegacyIAccessiblePattern)item[j].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                            if (pattern != null)
                            {
                                if (pattern.Current.Description == "关闭")
                                {
                                    Input.ShowFrame(appElement);
                                    Input.Click(item[j], true);

                                    System.Threading.Thread.Sleep(100);

                                    //关闭UI可能出现提示面盘
                                    AutomationElement tip = appElement.FindFirst(TreeScope.Children,
                                        new PropertyCondition(AutomationElement.NameProperty, "提示"));
                                    if (tip != null)
                                    {
                                        item = tip.FindAll(TreeScope.Descendants,
                                            new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                                        for (int k = 0; k < item.Count; ++k)
                                        {
                                            pattern = (LegacyIAccessiblePattern)item[k].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                                            if (pattern != null)
                                            {
                                                if (pattern.Current.Description == "关闭")
                                                {
                                                    Input.ShowFrame(appElement);
                                                    Input.Click(item[k], true);
                                                }
                                            }
                                            
                                        }
                                    }

                                    return;
                                }
                            }
                        }
                    }
                }
                catch (Exception)
                { }
            }
        }


        //关闭所有QQ
        //应用程序
        public static void CloseQQ()
        {
            AutomationElement rootElement = null;
            while (rootElement==null)
            {
                rootElement = AutomationElement.RootElement.FindFirst(TreeScope.Children,
                    new PropertyCondition(AutomationElement.NameProperty, "Windows 任务管理器"));
                if (rootElement == null)
                {
                    Process.Start(@"C:\WINDOWS\system32\taskmgr.exe");
                }

                System.Threading.Thread.Sleep(500);
            }
            Input.ShowFrame(rootElement);

            AutomationElement element = rootElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "应用程序"));
            Input.Click(element,false);


            Condition[] condition = new Condition[2];
            condition[0] = new PropertyCondition(AutomationElement.NameProperty, "QQ");
            condition[1] = new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC357);

            AutomationElement over = rootElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "结束任务(E)"));

            while (true)
            {
                Input.ShowFrame(rootElement);

                element = rootElement.FindFirst(TreeScope.Descendants, new AndCondition(condition));
                if (element == null)
                    break;

                Input.Click(element, false);
                System.Threading.Thread.Sleep(1000);
                Input.Click(over,false);

                System.Threading.Thread.Sleep(2000);
            }

            Process[] p = Process.GetProcessesByName("taskmgr");
            for (int i = 0; i < p.Length; ++i)
            {
                try
                {
                    p[i].CloseMainWindow();
                    p[i].Kill();
                    p[i].Dispose();
                }
                catch (Exception) { }
            }
        }

        //进程
        public static string CloseQQProcess()
        {
            Time time = new Time();
            time.Start(new TimeSpan(0, 2, 0));
            AutomationElement rootElement = null;
            while (rootElement == null)
            {
                if (time.End())
                {
                    return "启动任务管理器超时";
                }

                rootElement = AutomationElement.RootElement.FindFirst(TreeScope.Children,
                    new PropertyCondition(AutomationElement.NameProperty, "Windows 任务管理器"));
                if (rootElement == null)
                {
                    Process.Start(@"C:\WINDOWS\system32\taskmgr.exe");
                }

                System.Threading.Thread.Sleep(500);
            }
            Input.ShowFrame(rootElement);

            AutomationElement element = rootElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "进程"));
            Input.Click(element, false);


            Condition[] condition = new Condition[2];
            condition[0] = new PropertyCondition(AutomationElement.NameProperty, "QQ.exe *32");
            condition[1] = new PropertyCondition(AutomationElement.NameProperty, "QQ.exe *64");

            AutomationElement over = rootElement.FindFirst(TreeScope.Descendants,
                new PropertyCondition(AutomationElement.NameProperty, "结束进程(E)"));

            time = new Time();
            time.Start(new TimeSpan(0, 2, 0));
            while (true)
            {
                if (time.End())
                {
                    return "关闭QQ进程超时";
                }

                Input.ShowFrame(rootElement);

                element = rootElement.FindFirst(TreeScope.Descendants, new OrCondition(condition));
                if (element == null)
                    break;

                Input.Click(element, false);
                System.Threading.Thread.Sleep(1000);
                Input.Click(over, false);
                System.Threading.Thread.Sleep(1000);
                Input.KeybdClick((byte)Keys.Enter);

                System.Threading.Thread.Sleep(2000);
            }

            Process[] p = Process.GetProcessesByName("taskmgr");
            for (int i = 0; i < p.Length; ++i)
            {
                try
                {
                    p[i].CloseMainWindow();
                    p[i].Kill();
                    p[i].Dispose();
                }
                catch (Exception) { }
            }

            return null;
        }
    }
}
