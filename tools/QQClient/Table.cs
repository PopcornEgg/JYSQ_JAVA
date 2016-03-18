using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QQClient
{
    //这个文件主要管理于服务器数据互交
    public class Table
    {
        //更新账号数据
        public static void UpdateTable_accountex(string qq, string[,] values)
        {
            try
            {
                String URL = utitls.Config.ROOT_URL + "accountex_update";
                utitls.PostData nvc = new utitls.PostData();
                utitls.Func.putPassWord(nvc);
                nvc.Add("qq", qq);

                for (int i = 0; i < values.Length / 2; ++i)
                    nvc.Add(values[i, 0], values[i, 1]);

                string sttuas = utitls.Func.HttpPostData(URL, 10000, nvc);
                if (!sttuas.Contains("_succ_"))
                {
                    Debug.LogError("【登录失败】上报数据异常");
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }

        //更新好友表
        public static void UpdateTable_friend(string qq, string[,] values)
        {
            try
            {
                String URL = utitls.Config.ROOT_URL + "friend_update";
                utitls.PostData nvc = new utitls.PostData();
                utitls.Func.putPassWord(nvc);
                nvc.Add("qq", qq);

                for (int i = 0; i < values.Length / 2; ++i)
                    nvc.Add(values[i, 0], values[i, 1]);

                if (nvc != null)
                {
                    string sttuas = utitls.Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas.Contains("_succ_"))
                    {
                        Debug.Log(qq + sttuas);
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }

        
        //更新用户服务器
        public static void UpdateTable_user(string qq, string[,] values)
        {
            //上报到服务器
            try
            {
                utitls.PostData nvc = new utitls.PostData();
                utitls.Func.putPassWord(nvc);
                nvc.Add("qq", qq);

                for (int i = 0; i < values.Length / 2; ++i)
                    nvc.Add(values[i, 0], values[i, 1]);

                if (nvc != null)
                {
                    string sttuas = utitls.Func.HttpPostData(utitls.Config.UNLOCKEQQ_ZONE_URL, 10000, nvc);
                    if (sttuas != null)
                    {
                        if (sttuas.Contains("_succ_"))
                        {
                            Debug.Log("上报到服务器 " + sttuas);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }

        //更新用户服务器
        public static void UpdateTable_accountex_log(string qq, string[,] values)
        {
            //上报到服务器
            try
            {
                utitls.PostData nvc = new utitls.PostData();
                utitls.Func.putPassWord(nvc);
                nvc.Add("qq", qq);

                for (int i = 0; i < values.Length / 2; ++i)
                    nvc.Add(values[i, 0], values[i, 1]);

                if (nvc != null)
                {
                    string URL = utitls.Config.ROOT_URL + "accountex_log_update";
                    string sttuas = utitls.Func.HttpPostData(URL, 10000, nvc);
                    if (sttuas != null)
                    {
                        if (sttuas.Contains("_succ_"))
                        {
                            Debug.Log("上报到服务器 " + sttuas);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }
    }
}
