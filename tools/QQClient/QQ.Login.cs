using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QQClient
{
    public partial class QQ
    {
        //登录失败
        void OnLoginFail(string qq, ErrorType errorType, string errorDesc)
        {
            try
            {
                Debug.LogError(string.Format("【登录失败】 {0} {1}上报数据", qq,errorDesc));

                string error = errorDesc;
                switch (errorType)
                {
                    case ErrorType.SecurityCode:
                        Table.UpdateTable_accountex(qq, new string[,] { { "state", "需验证码" }, { "allow_logn", "0" } });
                        break;
                    case ErrorType.PasswordError:
                        Table.UpdateTable_accountex(qq, new string[,] { { "state", "密码错误" } });
                        break;
                    case ErrorType.ProtectedMode:
                        Table.UpdateTable_accountex(qq, new string[,] { { "state", "保护模式" }, { "allow_logn", "0" } });
                        break;
                }
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }
    }
}
