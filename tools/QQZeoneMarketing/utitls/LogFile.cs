using System;
using System.Collections.Generic;
using System.Threading;
using System.Linq;
using System.Text;
using System.IO;

//单纯的错误，异常日志用
namespace utitls {

    public class LogFile
    {
        static object mLock = new object();
        static List<Log> mLogList = new List<Log>();
        public class Log
        {
            public String form;
            public String log;
            public int lv;

            public override string ToString()
            {
                return string.Format("{0}\t{1}\t{2}\n",form,log,lv);
            }
        }
        static private String header = "来源\t内容\t等级\n";
        static private int LOGLEVEL = -1;
        public static void add(String _l, String _f, int _lv)
        {
            lock (mLock)
            {
                Log log = new Log();
                log.form = _f;
                log.lv = _lv;
                log.log  = _l;
                mLogList.Add(log);
            }
        }
        public static void add(String _l, String _f)
        {
            lock (mLock)
            {
                Log log = new Log();
                log.form = _f;
                log.lv = -1;
                log.log = _l;
                mLogList.Add(log);
            }
        }
        public static void add(String _l)
        {
            lock (mLock)
            {
                Log log = new Log();
                log.form = "null";
                log.lv = -1;
                log.log = _l;
                mLogList.Add(log);
            }
        }
        public static void start()
        {
            exitThread = false;
            Thread thread = new Thread(new ThreadStart(_do));
            thread.Start();
        }
        public static void stop()
        {
            exitThread = true;
        }

        static bool exitThread = true;
        private static void _do()
        {
            while (true)
            {
                if (exitThread)
                    break;

                System.Threading.Thread.Sleep(1000);

                lock (mLock)
                {
                    try
                    {
                        if (mLogList.Count == 0)
                            break;
                        
                        
                        FileStream aFile = new FileStream("Log.txt", FileMode.Create);
                        StreamWriter sw = new StreamWriter(aFile);

                        sw.Write(header);
                        foreach (Log l in mLogList)
                        {
                            if (l.lv >= LOGLEVEL)
                                sw.Write(l.ToString());
                        }
                        //                     sw.WriteLine("Hello to you.");
                        sw.Close();
                        mLogList.Clear();
                    }
                    catch (IOException ex)
                    {
                        Console.WriteLine(ex.ToString());
                        return;
                    }
                }
            }
            
            
        } 
    }
}
