package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumDataMgr {
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
//主题数据
	public static class ForumThreadItem implements Comparable
	{
	    public String fid;
	    public String tid;
	    public String author;
	    public String authorid;
	    public String subject = "";
	    public String message = "";
	    public long dateline;
	    public int replies = 0;
	    public String realname;//昵称
	    public int gender;//性别
	    public int credit = 0;//积分
	    public Integer adoptpid = -1;//采纳帖子
	    public String exInfo = "";//额外的信息
	    public Integer imgid = 1;
	    public Integer attachment = 0;
	    
	    static int idx = 0;
	    ForumThreadItem()
	    {
	    	imgid = ++idx;
	    	if( idx > 0xfffffff)
	    		idx = 0;
	    }
	    @Override
	    public int compareTo(Object o)
	    {
	    	ForumThreadItem sdto = (ForumThreadItem)o;
            if(this.dateline > sdto.dateline)
                return -1;  
            else if(this.dateline < sdto.dateline)
            	return 1;  
            else
                return 0;  
	    }
	}
	
	static Map<String, ForumThreadItem> dicAllThreads = new HashMap<String, ForumThreadItem>();
    static List<ForumThreadItem> listAllThreads = new ArrayList<ForumThreadItem>();

    static Map<String, Map<String, ForumThreadItem>> dicKindThreads = new HashMap<String, Map<String, ForumThreadItem>>();
    static Map<String, List< ForumThreadItem>> listKindThreads = new HashMap<String, List< ForumThreadItem>>();
    
    static public void AddThread(ForumThreadItem item)
    {
        if (item == null)
            return;
        
        if (dicAllThreads.containsKey(item.tid)){
        	ForumThreadItem curitem = dicAllThreads.get(item.tid);
        	item.exInfo = curitem.exInfo;
            dicAllThreads.put(item.tid, item);
        }
        else{
        	item.exInfo = MyAdapterThread.getListHelps(item.attachment == 2);
        	dicAllThreads.put(item.tid, item);
        	listAllThreads.add(item);

            //根据fid分类 dic
        	Map<String, ForumThreadItem> dicFids = dicKindThreads.get(item.fid);
            if (dicFids == null){
                dicFids = new HashMap<String, ForumThreadItem>();
                dicKindThreads.put(item.fid, dicFids);
            }
            dicFids.put(item.tid, item);
            //根据fid分类 list
            List<ForumThreadItem> listFids = listKindThreads.get(item.fid);
            if (listFids == null){
                listFids = new ArrayList<ForumThreadItem>();
                listKindThreads.put(item.fid, listFids);
            }
            listFids.add(item);
        }
    }
    static public List<ForumThreadItem> GetThreads_By_Fid(String fid)
    {
    	if(listKindThreads.containsKey(fid))
    		return listKindThreads.get(fid);
    	else
    		return null;
    }
    static public List<ForumThreadItem> GetThreads_By_Fids(String fids)
    {
        List<ForumThreadItem> retlistFids = new ArrayList<ForumThreadItem>();
        String[] ps = fids.split(",");
        for(int i=0;i<ps.length;i++)
        {
            List<ForumThreadItem> listFids = listKindThreads.get(ps[i]);
            if (listFids != null)
            {
                retlistFids.addAll(listFids);
            }
        }
        return retlistFids;
    }
    static public List<ForumThreadItem> GetTid_By_Fid(String fid)
    {
        return listKindThreads.get(fid);
    }
    static public ForumThreadItem getThread_By_Tid(String tid)
    {
    	if(dicAllThreads.containsKey(tid))
    		return dicAllThreads.get(tid);
    	else 
    		return null;
    }
    static public void addThreadReply(String tid, int count)
    {
    	if(dicAllThreads.containsKey(tid))
    	{
    		ForumThreadItem item = dicAllThreads.get(tid);
    		item.replies += count;
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////
//回复数据
    public static class ForumPostItem implements Comparable
	{
		public String tid;
	    public String pid;
	    public String author;
	    public String authorid;
	    public String subject;
	    public String message;
	    public long   dateline;
	    public String realname;//昵称
	    public int gender;//性别
	    public Integer attachment = 0;
	    
	    @Override
	    public int compareTo(Object o)
	    {
	    	ForumPostItem sdto = (ForumPostItem)o;
            if(this.dateline > sdto.dateline)
                return 1;  
            else if(this.dateline < sdto.dateline)
            	return -1;  
            else
                return 0;  
	    }
	}
    static Map<String, List<ForumPostItem>> listAllPosts = new HashMap<String, List<ForumPostItem>>();
    static Map<String, ForumPostItem> dicAllPosts = new HashMap<String, ForumPostItem>();
    static public void addPost(ForumPostItem item)
    {
        if (item == null)
            return;
        if(dicAllPosts.containsKey(item.pid))
        	return;
        dicAllPosts.put(item.pid, item);
    	List<ForumPostItem> l = listAllPosts.get(item.tid);
    	if(l == null)
    		l = new ArrayList<ForumPostItem>();
    	l.add(item);
    	listAllPosts.put(item.tid, l);
    }
    static public void addPostInDic(ForumPostItem item)
    {
        if (item == null)
            return;
        if(dicAllPosts.containsKey(item.pid))
        	return;
        dicAllPosts.put(item.pid, item);
    }
    static public List<ForumPostItem> getPosts(String tid)
    {
    	return listAllPosts.get(tid);
    }
    static public void delPostInDic(ForumPostItem item)
    {
        if(!dicAllPosts.containsKey(item.pid))
        	return;
        dicAllPosts.remove(item.pid);
    }
    static public void delPost(ForumPostItem item)
    {
        if(!dicAllPosts.containsKey(item.pid))
        	return;
        dicAllPosts.remove(item.pid);
    	List<ForumPostItem> l = listAllPosts.get(item.tid);
    	if(l != null)
    	{
    		int pos = -1;
    		for(int i=0;i<l.size();i++){
    			ForumPostItem pi = l.get(i);
    			if(pi.pid.equals(item.pid)){
    				pos = i;
    				break;
    			}
    		}
    		if(pos >= 0)
    			l.remove(pos);
    	}
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
//自己的问答
    static Map<String, ForumThreadItem> dicMyThreads = new HashMap<String, ForumThreadItem>();
    static List<ForumThreadItem> listMyThreads = new ArrayList<ForumThreadItem>();
    static public void addMyThread(ForumThreadItem item)
    {
        if (item == null)
            return;
        if (dicMyThreads.containsKey(item.tid)){
        	dicMyThreads.put(item.tid, item);
        }
        else{
        	dicMyThreads.put(item.tid, item);
        	listMyThreads.add(item);
        }
    }
    static public List<ForumThreadItem> getMyThreads()
    {
    	return listMyThreads;
    }
    static Map<String, ForumPostItem> dicMyPosts = new HashMap<String, ForumPostItem>();
    static List<ForumPostItem> listMyPosts = new ArrayList<ForumPostItem>();
    static public void addMyPost(ForumPostItem item)
    {
        if (item == null)
            return;
        if (dicMyPosts.containsKey(item.pid)){
        	dicMyPosts.put(item.pid, item);
        }
        else{
        	dicMyPosts.put(item.pid, item);
        	listMyPosts.add(item);
        }
    }
    static public List<ForumPostItem> getMyPosts()
    {
    	return listMyPosts;
    }
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
//个人消息数据
    static Map<String, ForumNotificationItem> dicAllNotification = new HashMap<String, ForumNotificationItem>();
    static List<ForumNotificationItem> listAllNotification = new ArrayList<ForumNotificationItem>();
    static Map<String, List< ForumNotificationItem>> listKindNotification = new HashMap<String, List< ForumNotificationItem>>();
	public static class ForumNotificationItem implements Comparable
	{
		public String id;
		public String from_id;
		public String from_idtype;
		public String uid;
		public String type;
		public Integer isnew;
		public String author;
		public String authorid;
		public String note;
		public long dateline;
		
		@Override
		public int compareTo(Object o)
		{
			ForumNotificationItem sdto = (ForumNotificationItem)o;
			if(this.dateline > sdto.dateline)
				return -1;  
			else if(this.dateline < sdto.dateline)
				return 1;  
			else
				return 0;  
		}
	}
	static public ForumNotificationItem getMyNotification_by_id(String id)
    {
		if(dicAllNotification.containsKey(id))
			return dicAllNotification.get(id);
		return null;
    }
	static public List<ForumNotificationItem> getAllMyNotifications()
    {
    	return listAllNotification;
    }
	static public List<ForumNotificationItem> getMyNotifications_by_type(String tp)
    {
		if(listKindNotification.containsKey(tp))
			return listKindNotification.get(tp);
		return null;
    }
	static public void addMyNotification(ForumNotificationItem item)
    {
        if (item == null)
            return;
        
        if (dicAllNotification.containsKey(item.id)){
        	dicAllNotification.put(item.id, item);
        }
        else{
        	dicAllNotification.put(item.id, item);
        	listAllNotification.add(item);

            //根据fid分类 list
            List<ForumNotificationItem> lt = listKindNotification.get(item.type);
            if (lt == null){
            	lt = new ArrayList<ForumNotificationItem>();
                listKindNotification.put(item.type, lt);
            }
            lt.add(item);
        }
    }
}
