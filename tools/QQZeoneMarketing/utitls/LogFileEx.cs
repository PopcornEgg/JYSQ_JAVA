using System;
using System.Collections.Generic;
using System.Threading;
using System.Linq;
using System.Text;
using System.IO;
//过程的执行失败等日志
namespace utitls
{
    public class LogFileEx
    {
        const int tickTime = 1000;

        static string folderName = "LogFileEx";
        static public bool createFolder(String sPath){
            if (!Directory.Exists(sPath))
                return Directory.CreateDirectory(sPath) != null;
            else
                return true;
        }

        static object mLock = new object();
        static Dictionary<String, LogColumns> mLogDic = new Dictionary<String, LogColumns>();

        public class LogRow
        {
            public List<String> ls = new List<string>();
            public override string ToString()
            {
                StringBuilder sb = new StringBuilder("");
                foreach (String str in ls)
                {
                    sb.Append(str + "\t");
                }
                return sb.ToString();
            }
            public void add(String str)
            {
                ls.Add(str);
            }
            public void setls(List<String> _l)
            {
                this.ls = _l;
            }
        }
        public class LogColumns
        {
            public bool isDirty = false;
            public String filename;
            public List<LogRow> rows = new List<LogRow>();
            public LogColumns(String fn)
            {
                filename = fn;
            }
            public override string ToString()
            {
                StringBuilder sb = new StringBuilder("");
                foreach (LogRow row in rows)
                {
                    sb.Append(row.ToString() + "\n");
                }
                isDirty = false;
                return sb.ToString();
            }
            public void add(LogRow row)
            {
                rows.Add(row);
                isDirty = true;
            }
        }
        public static void add(String filename, LogRow row)
        {
            lock (mLock)
            {
                if (mLogDic.ContainsKey(filename))
                {
                    mLogDic[filename].add(row);
                }
                else
                {
                    LogColumns logcol = new LogColumns(filename);
                    logcol.add(row);
                    mLogDic.Add(filename, logcol);
                }
            }
        }

        static Thread thread = null;
        static bool exitThread = true;
        public static void start()
        {
            exitThread = false;
            thread = new Thread(new ThreadStart(_do));
            thread.Start();
        }

        public static void stop()
        { 
            exitThread = true;
        }

        private static void _do()
        {
            while (true)
            {
                if (exitThread)
                    break;

                System.Threading.Thread.Sleep(tickTime);
                
                lock (mLock)
                {
                    try
                    {
                        foreach (LogColumns lc in mLogDic.Values)
                        {
                            if (lc.isDirty)
                            {
                                if (!createFolder(folderName))
                                    continue;
                                String pathName = folderName + "\\" + lc.filename;
                                FileStream aFile = new FileStream(pathName, FileMode.Create);
                                StreamWriter sw = new StreamWriter(aFile);
                                sw.Write(lc.ToString());
                                sw.Close();
                            }
                        }
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
