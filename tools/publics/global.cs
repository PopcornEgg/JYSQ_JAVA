using System.Windows.Forms;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Linq;
using System.Text;
using System.IO;
using System.Collections.Specialized;
using System.Net;
using System.Web;

namespace utitls {

    public static class Func
    {
        static public void putPassWord(PostData nvc)
        {
            if (nvc != null)
                nvc.Add("pw", Config.UPLOADPASSWORD);
        }

        public static string HttpPostData(string url, int timeOut, NameValueCollection stringDict)
        {
            string responseContent;
            var memStream = new MemoryStream();
            var webRequest = (HttpWebRequest)WebRequest.Create(url);
            // 边界符
            var boundary = DateTime.Now.Ticks.ToString("x");
            // 设置属性
            webRequest.Method = "POST";
            webRequest.Timeout = timeOut;
            webRequest.ContentType = "multipart/form-data; boundary=" + boundary;
            var buffer = new byte[1024];
            // 写入字符串的Key
            var stringKeyHeader = "\r\n--" + boundary + "\r\nContent-Disposition: form-data; name=\"{0}\"\r\n\r\n{1}";
            foreach (byte[] formitembytes in from string key in stringDict.Keys
                                             select string.Format(stringKeyHeader, key, stringDict[key])
                                                 into formitem
                                                 select Encoding.UTF8.GetBytes(formitem))
            {
                memStream.Write(formitembytes, 0, formitembytes.Length);
            }
            // 写入最后的结束边界符
            webRequest.ContentLength = memStream.Length;
            var requestStream = webRequest.GetRequestStream();
            memStream.Position = 0;
            var tempBuffer = new byte[memStream.Length];
            memStream.Read(tempBuffer, 0, tempBuffer.Length);
            memStream.Close();
            requestStream.Write(tempBuffer, 0, tempBuffer.Length);
            requestStream.Close();
            var httpWebResponse = (HttpWebResponse)webRequest.GetResponse();
            using (var httpStreamReader = new StreamReader(httpWebResponse.GetResponseStream(),
                                                            Encoding.GetEncoding("utf-8")))
            {
                responseContent = httpStreamReader.ReadToEnd();
            }
            httpWebResponse.Close();
            webRequest.Abort();
            return responseContent;
        }

        //获取我的QQ号
        static public String getMyQQ(WebBrowser wb)
        {
            if (wb == null)
                return "";
            foreach (HtmlElement he in wb.Document.GetElementsByTagName("a"))
            {
                string cname = he.GetAttribute("className");
                if (cname == "user-home")
                {
                    String home = he.GetAttribute("href");
                    home = home.Replace("http://user.qzone.qq.com/", "");
                    home = home.Replace("/main", "");
                    return home;
                }
            }
            return "";
        }

        public static string MyUrlEncode(string str)
        {
            return HttpUtility.UrlEncode(str, Encoding.UTF8);
        }

        public static string MyUrlDeCode(string str)
        {
            return HttpUtility.UrlDecode(str, Encoding.UTF8);
        }
        public static string getDBDateTime( )
        {
            return DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
        }
    }

    public static class Config
    {
        public const String UPLOADPASSWORD = "yxkj_myqq_5201314";
#if DEBUG
        public const String ROOT_URL = "http://192.168.31.243/myqq/main.php?module=";
        //public const String ROOT_URL = "http://bbs.jiyisq.cn/myqq/main.php?module=";
#else
        public const String ROOT_URL = "http://bbs.jiyisq.cn/myqq/main.php?module=";
#endif
        public const String INSERT_ZONE_URL = ROOT_URL + "zone_insert";
        public const String UPDATE_ZONE_URL = ROOT_URL + "zone_update";
        public const String GETLIST_ZONE_URL = ROOT_URL + "zone_getlist";
        public const String GETLISTFEILD_ZONE_URL = ROOT_URL + "zone_getlistfeild";
        public const String LOCKQQ_ZONE_URL = ROOT_URL + "zone_lockqq";
        public const String UNLOCKEQQ_ZONE_URL = ROOT_URL + "zone_unlockqq";
        //public const String UPDATE_USERINFO_URL = ROOT_URL + "field";
    }
    public class PostData : NameValueCollection
    {
        public void Add(string name, int value)
        {
            Add(name, value.ToString());
        }
        public void Add(string name, float value)
        {
            Add(name, value.ToString());
        }
        public void Add(string name, bool value)
        {
            Add(name, value.ToString());
        }
        public void Add(string name, double value)
        {
            Add(name, value.ToString());
        }
    }

    public class updateDBinfoThread
    {
        private PostData data = null;
        private String url = null;

        public updateDBinfoThread(PostData _d, String _u)
        {
            data = _d;
            url = _u;
        }
        public void Proc()
        {
            Func.putPassWord(data);
            if (data != null)
            {
                string sttuas = Func.HttpPostData(url, 10000, data);
                if (sttuas.Contains("_succ_"))
                {
                }
            }
        }

        static public void start(String url, PostData data)
        {
            try
            {
                updateDBinfoThread tt = new updateDBinfoThread(data, url);
                Thread thread = new Thread(new ThreadStart(tt.Proc));
                thread.Start();
            }
            catch (Exception)
            {
            }
        }
    }
    public class SQLGetData
    {
        static public String URL = Config.ROOT_URL + "common_get";
        private String tab;
        private String order = "";
        private String desc = "";
        private String start = "";
        private String count = "";
        public SQLGetData(String _t)
        {
            this.tab = _t;
        }
        public void setInfo(String _o,String _d,String _s, String _c)
        {
            this.order = _o;
            this.desc = _d;
            this.start = _s;
            this.count = _c;
        }
        Dictionary<String, Object> dicTags = new Dictionary<string, object>();
        public void addTag(String _k, Object _v)
        {
            if (!string.IsNullOrEmpty(_k))
            {
                if (dicTags.ContainsKey(_k))
                    dicTags[_k] = _v;
                else
                    dicTags.Add(_k, _v);
            }
        }
        List<String> lsFeilds = new List<String>();
        public void addFeild(String _v)
        {
            if (!string.IsNullOrEmpty(_v))
            {
                bool has = false;
                foreach (string str in lsFeilds)
                {
                    if (str == _v)
                    {
                        has = true;
                        break;
                    }
                }
                if (!has)
                    lsFeilds.Add(_v);
            }
        }
        public PostData getPostData()
        {
            PostData data = new PostData();
            Func.putPassWord(data);
            data.Add("tab", tab);
            if (!String.IsNullOrEmpty(order) && !String.IsNullOrEmpty(desc))
            {
                data.Add("order", order);
                data.Add("desc", desc);
            }
            if (!String.IsNullOrEmpty(start) && !String.IsNullOrEmpty(count))
            {
                data.Add("start", start);
                data.Add("count", count);
            }

            String tags = "";
            foreach (string key in dicTags.Keys)
            {
                Object obj = dicTags[key];
                if (obj is int)
                {
                    if (string.IsNullOrEmpty(tags))
                        tags = String.Format(" {0}={1} ", key, obj.ToString());
                    else
                        tags += String.Format(" AND {0}={1} ", key, obj.ToString());
                }
                else if (obj is string)
                {
                    if (string.IsNullOrEmpty(tags))
                        tags = String.Format(" {0}='{1}' ", key, obj.ToString());
                    else
                        tags += String.Format(" AND {0}='{1}' ", key, obj.ToString());
                }
            }
            data.Add("tags", tags);

            String fields="";
            foreach (string str in lsFeilds)
            {
                if (string.IsNullOrEmpty(fields))
                    fields = str;
                else
                    fields += "," + str;
            }
            data.Add("fields", fields);
            return data;
        }
    }
    public class SQLUpdateData
    {
        static public String URL = Config.ROOT_URL + "common_update";
        private String tab;
        private String isnew = "0";
        public SQLUpdateData(String _t)
        {
            this.tab = _t;
        }
        public void setInfo(String _is)
        {
            this.isnew = _is;
        }
        Dictionary<String, Object> dicTags = new Dictionary<string, object>();
        public void addTag(String _k, Object _v)
        {
            if (!string.IsNullOrEmpty(_k))
            {
                if (dicTags.ContainsKey(_k))
                    dicTags[_k] = _v;
                else
                    dicTags.Add(_k, _v);
            }
        }
        Dictionary<String, Object> dicFeilds = new Dictionary<string, object>();
        public void addFeild(String _k, Object _v)
        {
            if (!string.IsNullOrEmpty(_k))
            {
                if (dicFeilds.ContainsKey(_k))
                    dicFeilds[_k] = _v;
                else
                    dicFeilds.Add(_k, _v);
            }
        }
        
        public PostData getPostData()
        {
            PostData data = new PostData();
            Func.putPassWord(data);
            data.Add("tab", tab);
            data.Add("isnew", isnew);

            String tags = "";
            foreach (string key in dicTags.Keys)
            {
                Object obj = dicTags[key];
                if (obj is int)
                {
                    if (string.IsNullOrEmpty(tags))
                        tags = String.Format(" {0}={1} ", key, obj.ToString());
                    else
                        tags += String.Format(" AND {0}={1} ", key, obj.ToString());
                }
                else if (obj is string)
                {
                    if (string.IsNullOrEmpty(tags))
                        tags = String.Format(" {0}='{1}' ", key, obj.ToString());
                    else
                        tags += String.Format(" AND {0}='{1}' ", key, obj.ToString());
                }
            }
            data.Add("tags", tags);

            String fieldsupdate = "";
            foreach (string key in dicFeilds.Keys)
            {
                Object obj = dicFeilds[key];
                if (obj is int)
                {
                    if (string.IsNullOrEmpty(fieldsupdate))
                        fieldsupdate = String.Format(" {0}={1} ", key, obj.ToString());
                    else
                        fieldsupdate += String.Format(" AND {0}={1} ", key, obj.ToString());
                }
                else if (obj is string)
                {
                    if (string.IsNullOrEmpty(fieldsupdate))
                        fieldsupdate = String.Format(" {0}='{1}' ", key, obj.ToString());
                    else
                        fieldsupdate += String.Format(" AND {0}='{1}' ", key, obj.ToString());
                }
            }
            data.Add("fieldsupdate", fieldsupdate);

            String fieldsinsert1 = "";
            String fieldsinsert2 = "";
            foreach (string key in dicFeilds.Keys)
            {
                Object obj = dicFeilds[key];
                //key
                if (string.IsNullOrEmpty(fieldsinsert1))
                    fieldsinsert1 = String.Format("{0}", key);
                else
                    fieldsinsert1 += String.Format(",{0}", key);
               
                //value
                if (obj is int)
                {
                    if (string.IsNullOrEmpty(fieldsinsert2))
                        fieldsinsert2 = String.Format("{0}", obj.ToString());
                    else
                        fieldsinsert2 += String.Format(",{0}", obj.ToString());
                }
                else if (obj is string)
                {
                    if (string.IsNullOrEmpty(fieldsinsert2))
                        fieldsinsert2 = String.Format("'{0}'", obj.ToString());
                    else
                        fieldsinsert2 += String.Format(",'{0}'", obj.ToString());
                }
            }
            data.Add("fieldsinsert", String.Format(" {0} values {1} ", fieldsinsert1, fieldsinsert2));

            return data;
        }
    }
}
