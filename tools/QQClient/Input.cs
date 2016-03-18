using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Automation;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.Drawing;

namespace QQClient
{
    class Input
    {
        public static void ShowFrame(AutomationElement appElement)
        {
            try
            {
                IntPtr hWnd = new IntPtr(appElement.Current.NativeWindowHandle);

                WindowRect rect = new WindowRect();
                GetWindowRect(hWnd, out rect);
                SetWindowPos(hWnd, (IntPtr)HWND_TOPMOST, rect.Left, rect.Top, rect.Right - rect.Left, rect.Bottom - rect.Top, 0);
            }
            catch (Exception e)
            {
                Debug.OnException(e);
            }
        }

        public static void Click(AutomationElement appElement, bool doub)
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Location.X + rect.Size.Width * 0.5f, rect.Location.Y + rect.Height * 0.5f);
                Click(pos, doub, false);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void Click(AutomationElement appElement, int ofsx, int ofsy, bool doub)
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Left + ofsx, rect.Top + ofsy);
                Click(pos, doub, false);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void ClickRight(AutomationElement appElement, bool doub)
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Location.X + rect.Size.Width - rect.Height * 0.5f, rect.Location.Y + rect.Height * 0.5f);
                Click(pos, doub, false);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void MoveRight(AutomationElement appElement )
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Location.X + rect.Size.Width - rect.Height * 0.5f, rect.Location.Y + rect.Height * 0.5f);

                SetCursorPos((int)pos.X, (int)pos.Y);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void Click_MouseRight(AutomationElement appElement, bool doub)
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Location.X + rect.Size.Width * 0.5f, rect.Location.Y + rect.Height * 0.5f);
                Click(pos, doub, true);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void Click_MouseRight(AutomationElement appElement, int ofsx, int ofsy, bool doub)
        {
            try
            {
                Rect rect = appElement.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Left + ofsx, rect.Top + ofsy);
                Click(pos, doub, true);
            }
            catch (Exception e)
            {
                Debug.LogLine(e.Message);
                Debug.LogLine(e.Source);
                Debug.LogLine(e.StackTrace);
            }
        }

        public static void Click(System.Windows.Point pos, bool doub, bool isMouseRight)
        {
            System.Drawing.Point _pos = Control.MousePosition;

            SetCursorPos((int)pos.X, (int)pos.Y);

            if (isMouseRight)
            {
                mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                mouse_event(MOUSEEVENTF_RIGHTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                //双击
                if (doub)
                {
                    mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                    mouse_event(MOUSEEVENTF_RIGHTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                }
            }
            else
            {
                mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                mouse_event(MOUSEEVENTF_LEFTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                //双击
                if (doub)
                {
                    mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                    mouse_event(MOUSEEVENTF_LEFTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                }
            }

            SetCursorPos((int)_pos.X, (int)_pos.Y);
        }

        public static void ClickItem(AutomationElement appElement, string name, bool doub)
        {
            System.Windows.Automation.Condition condition = new PropertyCondition
                (AutomationElement.NameProperty, name);

            AutomationElement appElement2 = appElement.FindFirst(TreeScope.Descendants, condition);


            string state = appElement2.Current.ItemStatus;
            if (appElement2 != null)
            {

                AutomationPattern[] ab = appElement2.GetSupportedPatterns();
                AutomationProperty[] ap = appElement2.GetSupportedProperties();

                
                object temp;

                if (appElement2.TryGetCurrentPattern(ValuePattern.Pattern, out temp))
                {
                    ValuePattern ipItem = temp as ValuePattern;
                }

                if (appElement2.TryGetCurrentPattern(SelectionItemPattern.Pattern, out temp))
                {
                    SelectionItemPattern ipItem = temp as SelectionItemPattern;
                }


                Rect rect = appElement2.Current.BoundingRectangle;
                System.Windows.Point pos = new System.Windows.Point(rect.Location.X + rect.Size.Width * 0.5f, rect.Location.Y + rect.Height * 0.5f);
                SetCursorPos((int)pos.X, (int)pos.Y);

                mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                mouse_event(MOUSEEVENTF_LEFTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                //双击
                if (doub)
                {
                    mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                    mouse_event(MOUSEEVENTF_LEFTUP | MOUSEEVENTF_ABSOLUTE, (int)pos.X, (int)pos.Y, 0, 0);
                }
            }
        }

        public static void Clipboard(string str)
        {
            while (true)
            {
                try
                {
                    System.Windows.Forms.Clipboard.Clear();
                    System.Windows.Forms.Clipboard.SetText(str);
                    return;
                }
                catch (Exception) {
                    System.Threading.Thread.Sleep(500);
                }
            }
        }

        public static void Ctrl_V()
        {
            // Ctrl + v
            keybd_event(VK_CTRL, 0, 0, 0); //模拟先按下Shift键
            keybd_event(VK_V, 0, 0, 0); // 在没有弹出来的情况下按左键盘的数字“2”按键
            keybd_event(VK_V, 0, KEYEVENTF_KEYUP, 0); //然后松开键盘
            keybd_event(VK_CTRL, 0, KEYEVENTF_KEYUP, 0); //然后松开键盘
        }

        public static void Ctrl_A()
        {
            // Ctrl + a
            keybd_event(VK_CTRL, 0, 0, 0); //模拟先按下Shift键
            keybd_event(VK_A, 0, 0, 0); // 在没有弹出来的情况下按左键盘的数字“2”按键
            keybd_event(VK_A, 0, KEYEVENTF_KEYUP, 0); //然后松开键盘
            keybd_event(VK_CTRL, 0, KEYEVENTF_KEYUP, 0); //然后松开键盘
        }

        public static void KeybdClick(byte key)
        {
            keybd_event(key, 0, 0, 0); //然后松开键盘
            keybd_event(key, 0, KEYEVENTF_KEYUP, 0); //然后松开键盘
        }
        
        public static bool GetKeyState(System.Windows.Forms.Keys keys)
        {
            return ((GetKeyState((int)keys) & 0x8000) != 0) ? true : false;
        }

        public const int HWND_TOP = 0;
        public const int HWND_BOTTOM = 1;
        public const int HWND_TOPMOST = -1;
        public const int HWND_NOTOPMOST = -2;


        [DllImport("user32.dll")]
        public static extern IntPtr SetWindowPos(IntPtr hWnd, IntPtr hWndInsertAfter, int x, int y, int cx, int cy, uint wFlags);
        [DllImport("user32.dll")]
        public static extern bool GetWindowRect(IntPtr hWnd, out WindowRect lpRect);

        [DllImport("User32.dll")]
        private static extern void SendMessage(IntPtr hwnd, int msg, int wParam, int lParam);


 
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        public static extern void SetCursorPos(int x, int y);

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        public static extern bool GetCursorPos(out System.Windows.Point pt);

        [System.Runtime.InteropServices.DllImport("user32.dll")]
        private static extern int mouse_event(int dwFlags, int dx, int dy, int cButtons, int dwExtraInfo);
        [System.Runtime.InteropServices.DllImport("user32.dll")]
        public static extern void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);  //导入模拟键盘的方法

        // Long，欲测试的虚拟键键码。对字母、数字字符（A-Z、a-z、0-9），用它们实际的ASCII值  
        [System.Runtime.InteropServices.DllImport("user32.dll", EntryPoint = "GetKeyState")]
        public static extern int GetKeyState( int nVirtKey);  

        [DllImport("user32.dll")]
        public static extern Keys VkKeyScan(char ch); 


        //移动鼠标 
        const int MOUSEEVENTF_MOVE = 0x0001;
        //模拟鼠标左键按下 
        const int MOUSEEVENTF_LEFTDOWN = 0x0002;
        //模拟鼠标左键抬起 
        const int MOUSEEVENTF_LEFTUP = 0x0004;
        //模拟鼠标右键按下 
        const int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        //模拟鼠标右键抬起 
        const int MOUSEEVENTF_RIGHTUP = 0x0010;
        //模拟鼠标中键按下 
        const int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
        //模拟鼠标中键抬起 
        const int MOUSEEVENTF_MIDDLEUP = 0x0040;
        //标示是否采用绝对坐标 
        const int MOUSEEVENTF_ABSOLUTE = 0x8000;


        public const int VK_CTRL = 0x0011;
        public const int VK_A = 0x0041;
        public const int VK_V = 0x0056;
        public const int VK_BACK = 0x0008; //退格键

        //按键弹起
        public const int KEYEVENTF_KEYUP = 0x02;
    }

    public struct WindowRect
    {
        public int Left;
        public int Top;
        public int Right;
        public int Bottom;
    }
}
