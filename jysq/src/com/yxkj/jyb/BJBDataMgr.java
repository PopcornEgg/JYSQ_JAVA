package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BJBDataMgr {
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
//系统笔记本数据
	
	static public class Sys {
		//科目
		@SuppressWarnings("rawtypes")
		public static class SubjectItem implements Comparable
		{
			public String fup;
			public String fid;
			public String name;
			public int threads = 0;
			public long dateline;
			
			@Override
		    public int compareTo(Object o)
		    {
				SubjectItem sdto = (SubjectItem)o;
	            if(this.dateline > sdto.dateline)
	                return -1;  
	            else if(this.dateline < sdto.dateline)
	            	return 1;  
	            else
	                return 0;  
		    }
		}

	    static Map<String, SubjectItem> allSubjectItem = new HashMap<String, SubjectItem>();
	    static Map<String, List< SubjectItem>> kindSubjectItem = new HashMap<String, List< SubjectItem>>();
	    
	    static public void addSubjectItem(SubjectItem item)
	    {
	        if (item == null)
	            return;
	        
	        if (allSubjectItem.containsKey(item.fid)){
	        	allSubjectItem.put(item.fid, item);
	        }
	        else{
	        	allSubjectItem.put(item.fid, item);
	            //根据fup分类 list
	        	List<SubjectItem> ls = null;
	        	if(kindSubjectItem.containsKey(item.fup))
	        		ls = kindSubjectItem.get(item.fup);
	        	else{
	        		ls = new ArrayList<SubjectItem>();
	        		kindSubjectItem.put(item.fup, ls);
	        	}
	        	ls.add(item);
	        }
	    }
	    static public List< SubjectItem> getSubjectItems(String fup)
	    {
	    	if(kindSubjectItem.containsKey(fup))
        		return kindSubjectItem.get(fup);
	    	return null;
	    }
	    
	    //主题
	    @SuppressWarnings("rawtypes")
		public static class ThreadItem implements Comparable
		{
			public String fid;
			public String tid;
			public String name;
			public int replies = 0;
			public long dateline;
			
			@Override
		    public int compareTo(Object o)
		    {
				ThreadItem sdto = (ThreadItem)o;
	            if(this.dateline > sdto.dateline)
	                return -1;  
	            else if(this.dateline < sdto.dateline)
	            	return 1;  
	            else
	                return 0;  
		    }
		}
	    
	    static Map<String, ThreadItem> allThreadItem= new HashMap<String, ThreadItem>();
	    static Map<String, List< ThreadItem>> kindThreadItem = new HashMap<String, List< ThreadItem>>();
	    
	    static public void addThreadItem(ThreadItem item)
	    {
	        if (item == null)
	            return;
	        
	        if (allThreadItem.containsKey(item.tid)){
	        	allThreadItem.put(item.tid, item);
	        }
	        else{
	        	allThreadItem.put(item.tid, item);
	            //根据fid分类 list
	        	List<ThreadItem> ls = null;
	        	if(kindThreadItem.containsKey(item.fid))
	        		ls = kindThreadItem.get(item.fid);
	        	else{
	        		ls = new ArrayList<ThreadItem>();
	        		kindThreadItem.put(item.fid, ls);
	        	}
	        	ls.add(item);
	        }
	    }
	    static public List< ThreadItem> getThreadItems(String fid)
	    {
	    	if(kindThreadItem.containsKey(fid))
        		return kindThreadItem.get(fid);
	    	return null;
	    }
	    static public ThreadItem getThreadItem(String tid)
	    {
	    	if(allThreadItem.containsKey(tid))
        		return allThreadItem.get(tid);
	    	return null;
	    }
	    
	    //回答
	    @SuppressWarnings("rawtypes")
		public static class PostItem implements Comparable
		{
	    	public String tid;
			public String pid;
			public String subject;
			public String message;
			public int first = 0;
			public long dateline;
			
			@Override
		    public int compareTo(Object o)
		    {
				PostItem sdto = (PostItem)o;
	            if(this.dateline > sdto.dateline)
	                return -1;  
	            else if(this.dateline < sdto.dateline)
	            	return 1;  
	            else
	                return 0;  
		    }
		}
	    
	    static Map<String, PostItem> allPostItem= new HashMap<String, PostItem>();
	    static Map<String, List< PostItem>> kindPostItem = new HashMap<String, List< PostItem>>();
	    
	    static public void addPostItem(PostItem item)
	    {
	        if (item == null)
	            return;
	        
	        if (allPostItem.containsKey(item.pid)){
	        	allPostItem.put(item.pid, item);
	        }
	        else{
	        	allPostItem.put(item.pid, item);
	            //根据fid分类 list
	        	List<PostItem> ls = null;
	        	if(kindPostItem.containsKey(item.tid))
	        		ls = kindPostItem.get(item.tid);
	        	else{
	        		ls = new ArrayList<PostItem>();
	        		kindPostItem.put(item.tid, ls);
	        	}
	        	ls.add(item);
	        }
	    }
	    static public List< PostItem> getPostItems(String tid)
	    {
	    	if(kindPostItem.containsKey(tid))
        		return kindPostItem.get(tid);
	    	return null;
	    }
	}
///////////////////////////////////////////////////////////////////////////////////////////////////
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
//我的笔记本数据
	static public class My {
	    @SuppressWarnings("rawtypes")
		public static class PostItem implements Comparable
		{
	    	static HashSet<String> fromsMap = new HashSet<String>();
	    	static Integer curIdx = 0;
	    	public String tid;
			public String pid;
			public String message;
			public long dateline;
			public String from = "0";
			
			public PostItem clone(){
				My.PostItem pitem = new My.PostItem();
				pitem.tid = this.tid;
		    	pitem.pid = this.pid;
		    	pitem.message = this.message;
		    	pitem.dateline = System.currentTimeMillis();
		    	pitem.from = "0";
		    	return pitem;
			}
			
			@Override
		    public int compareTo(Object o){
				PostItem sdto = (PostItem)o;
	            if(this.dateline > sdto.dateline)
	                return -1;  
	            else if(this.dateline < sdto.dateline)
	            	return 1;  
	            else
	                return 0;  
		    }
			static public void setMaxIdx(int val){
				curIdx = val < curIdx ? curIdx : val;
		    }
			static public String getStrNextIdx( ){
				curIdx++;
				return curIdx.toString();
		    }
			static public boolean checkNextIdx(String _formpid ){
				for (Map.Entry<String, PostItem> entry : allPostItem.entrySet()) {  
					PostItem item = entry.getValue();
					if(!item.from.equals("0") && item.from.equals(_formpid)){
	        			return false;
	        		}
				}  
				return true;
		    }
		}
	    @SuppressWarnings("rawtypes")
		public static class ThreadItem implements Comparable
		{
	    	static public Integer curIdx = 0;
			public int subtype;//科目
			public String tid;
			public String subname;
			public long dateline;
			public int count = 0;
			public String from = "0";
			
			@Override
		    public int compareTo(Object o){
				ThreadItem sdto = (ThreadItem)o;
	            if(this.dateline > sdto.dateline)
	                return -1;  
	            else if(this.dateline < sdto.dateline)
	            	return 1;  
	            else
	                return 0;  
		    }
			public Integer getCount(){
				if(allPostItemList.containsKey(tid)){
					return allPostItemList.get(tid).size();
				}
				return 0;
		    }
			static public void setMaxIdx(int val){
				curIdx = val < curIdx ? curIdx : val;
		    }
			static public String getStrIdx( ){
				return curIdx.toString();
		    }
			static public String getStrNextIdx( String _formtid ){
				String tid = checkNextIdx(_formtid);
				if(tid.isEmpty()){
					curIdx++;
					return curIdx.toString();
				}else{
					return tid;
				}
		    }
			static public String checkNextIdx(String _formtid ){
				for(int i=0;i<allThreadItemList.size();i++){
					ThreadItem item = allThreadItemList.get(i);
	        		if(!item.from.equals("0") && item.from.equals(_formtid)){
	        			return allThreadItemList.get(i).tid;
	        		}
	        	}
				return "";
		    }
		}
	    
	    static Map<String, ThreadItem> allThreadItem= new HashMap<String, ThreadItem>();
	    static List<ThreadItem> allThreadItemList= new ArrayList<ThreadItem>();
	    
	    static Map<String, PostItem> allPostItem= new HashMap<String, PostItem>();
	    static Map<String,  List<PostItem>> allPostItemList= new HashMap<String, List<PostItem>>();
	    
	    static public void addThreadItem(ThreadItem item)
	    {
	    	if(item == null)
	    		return;
	        if(allThreadItem.containsKey(item.tid)){
	        	allThreadItem.put(item.tid, item);
	        }
	        else{
	        	allThreadItem.put(item.tid, item);
	        	allThreadItemList.add(item);
	        	ThreadItem.setMaxIdx(Integer.parseInt(item.tid) );
	        }
	    }
	    static public void delThreadItem(String _tid)
	    {
	        if(allThreadItem.containsKey(_tid)){
	        	allThreadItem.remove(_tid);
	        	int tag = -1;
	        	for(int i=0;i<allThreadItemList.size();i++){
	        		if(allThreadItemList.get(i).tid.equals(_tid)){
	        			tag = i;
	        			break;
	        		}
	        	}
	        	if(tag >= 0)
	        		allThreadItemList.remove(tag);
	        	
	        	if(allPostItemList.containsKey(_tid)){
	        		List<PostItem> ls = allPostItemList.get(_tid);
		        	for(int i=0;i < ls.size();i++){
		        		allPostItem.remove(ls.get(i).pid);
		        	}
	        	}
	        	allPostItemList.remove(_tid);
	        }
	    }
	    
	    static public void addPostItem(PostItem item)
	    {
	        if(allPostItem.containsKey(item.pid)){
	        	allPostItem.put(item.pid, item);
	        }
	        else{
	        	PostItem.setMaxIdx(Integer.parseInt(item.pid) );
	        	allPostItem.put(item.pid, item);
	        	List<PostItem> ls = null;
	        	if(allPostItemList.containsKey(item.tid))
	        		ls = allPostItemList.get(item.tid);
	        	else{
	        		ls = new ArrayList<PostItem>();
	        		allPostItemList.put(item.tid, ls);
	        	}
	        	ls.add(item);
	        }
	    }
	    static public boolean delPostItem(String _pid)
	    {
	        if(allPostItem.containsKey(_pid)){
	        	PostItem pitem = allPostItem.get(_pid);
	        	allPostItem.remove(_pid);
	        	String _tid = pitem.tid;
	        	if(allPostItemList.containsKey(_tid)){
	        		List<PostItem> ls = allPostItemList.get(_tid);
	        		int tag = -1;
		        	for(int i=0;i < ls.size();i++){
		        		if(ls.get(i).pid.equals(_pid)){
		        			tag = i;
		        			break;
		        		}
		        	}
		        	if(tag >= 0)
		        		ls.remove(tag);
//		        	if(ls.size() == 0){
//		        		delThreadItem(_tid);
//		        		return true;
//		        	}
	        	}
	        }
	        return false;
	    }
	   
	    static public ThreadItem getThreadItem(String _tid)
	    {
	        if(allThreadItem.containsKey(_tid)){
	        	return allThreadItem.get(_tid);
	        }
	        return null;
	    }
	    static public List< ThreadItem > getThreadItems( )
	    {
	    	return allThreadItemList;
	    } 
	    static public List< PostItem> getPostItems(String tid)
	    {
	    	if(allPostItemList.containsKey(tid)){
	    		return allPostItemList.get(tid);
	        }
	    	return null;
	    } 
	
	    //当用户登出时调用
	    static public void clear(){
	    	allThreadItem.clear();
	    	allThreadItemList.clear();
	    	allPostItem.clear();
	    	allPostItemList.clear();
	    }
	}
//
}
