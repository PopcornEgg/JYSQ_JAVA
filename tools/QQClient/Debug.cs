using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QQClient
{
    public class Debug
    {
        public static System.Action<string> OnLog = null;
        public static void OnException(Exception e)
        {
            LogLine(e.Message);
            LogLine(e.Source);
            LogLine(e.StackTrace);
        }

        public static void LogLine(string msg)
        {
            if (OnLog != null) OnLog(msg);
            else Console.WriteLine(msg);
        }

        public static void Log(string msg)
        {
            if (OnLog != null) OnLog(msg);
            else Console.Write(msg);
        }

        public static void LogError(string msg)
        {
            if (OnLog != null) OnLog(msg);
            else Console.Write(msg);
        }
    }

    class Time
    {
        DateTime end;
        public Time() { }
        public Time(TimeSpan t) { end = DateTime.Now + t; }
        public void Start(TimeSpan t) { end = DateTime.Now + t; }
        public bool End() { return DateTime.Now > end; }
    }
}
