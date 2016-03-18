using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Automation;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;

#region QQ客户端一些通用的接口
namespace QQClient
{
    public class Tools
    {

        public static AutomationElement getQQClientByNickName(string nickname)
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
                            {
                                return items[i];
                            }
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

        //关闭好友界面
        public static void closeNormalUI(AutomationElement appElement)
        {
            if (appElement != null)
            {
                Input.ShowFrame(appElement);
                //关闭
                List<Condition> conditions = new List<Condition>();
                conditions.Clear();
                conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350));
                conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "按钮"));
                AutomationElementCollection items = appElement.FindAll(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
                for (int i = 0; i < items.Count; ++i)
                {
                    LegacyIAccessiblePattern pattern =
                             (LegacyIAccessiblePattern)items[i].GetCurrentPattern(LegacyIAccessiblePattern.Pattern);
                    if (pattern != null && (pattern.Current.Description == "关闭" || pattern.Current.Description == "取消"))
                    {
                        Input.Click(items[i], false);
                        break;
                    }
                }
            }
        }

        //激活所有QQ窗口
        public static void OpenAllQQWindow()
        {
            List<Condition> conditions = new List<Condition>();
            conditions.Clear();
            conditions.Add(new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC365));
            conditions.Add(new PropertyCondition(AutomationElement.NameProperty, "用户升级的通知区域"));
            conditions.Add(new PropertyCondition(AutomationElement.LocalizedControlTypeProperty, "工具栏"));

            AutomationElement appElement =  AutomationElement.RootElement.FindFirst(TreeScope.Descendants, new AndCondition(conditions.ToArray()));
            if( appElement != null )
            {
                AutomationElementCollection items = appElement.FindAll(TreeScope.Children, 
                    new PropertyCondition(AutomationElement.ControlTypeProperty, 0xC350) );
                for (int i = 0; i < items.Count; ++i)
                {
                   // if (items[i].Current.Name.Contains("QQ:"))
                    {
                        for (int j = 0; j < 10; ++j)
                        {
                            Input.Click(items[i], true);
                            System.Threading.Thread.Sleep(10);
                        }
                    }
                }
            }
        }

        //获取本机IP
        public static string GetIP()
        {
            System.Net.IPHostEntry ipe = System.Net.Dns.GetHostEntry(System.Net.Dns.GetHostName());
            System.Net.IPAddress ipa = ipe.AddressList[1];
            
            return ipa.ToString();
        }
    }
}
#endregion
