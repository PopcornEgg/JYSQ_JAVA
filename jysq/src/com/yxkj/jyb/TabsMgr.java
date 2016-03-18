package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.yxkj.jyb.FileTools.AssetReader;
import com.yxkj.jyb.FileTools.BaseReader;
import com.yxkj.jyb.FileTools.LocalReader;

public class TabsMgr {

	//需要预加载的放这里面
	static public void init() {
		
	}
	static public class codesystypeTab {
		//data
		public int tpid = 0;
		public String tpname = "";
		public boolean edit = false;
		//static
		public static boolean tabInited = false;
		public static List<codesystypeTab> sData = new ArrayList<codesystypeTab>();
		
	    public static void Load()
	    {
	    	tabInited = true;
	    	sData.clear();
	    	AssetReader reader = new AssetReader("codesystype.txt");
	        for (int i = 0; i < reader.recordCount(); ++i)
	        {
	        	codesystypeTab cfg = new codesystypeTab();
	            cfg.tpid = reader.getInt(i, "tpid");
	            cfg.tpname = reader.getString(i, "tpname");
	            cfg.edit = reader.getBoolean(i, "edit");
	            sData.add(cfg);
	        }
	    }
	    public static List<codesystypeTab> getData()
	    {
	        if (!tabInited)
	            Load();
	        return sData;
	    }
	    public static codesystypeTab getItem(int _idx)
	    {
	        if (!tabInited)
	            Load();
	        if (_idx >= 0 && _idx < sData.size()){
	            return sData.get(_idx);
	        }
	        return null;
	    }
	    public static int getCount()
	    {
	        if (!tabInited)
	            Load();
	        return sData.size();
	    }
	}
	
	static public class codesysdata {
		public int id = 0;
		public String cname = "";
		public String name = "";
		public String image = "";
		public String desc = "";
		public float time = 10.0f;
		
		public codesysdata(){}
		public codesysdata(codesysdata _data){
			this.id = _data.id;
			this.cname = _data.cname;
			this.name = _data.name;
			this.image = _data.image;
			this.desc = _data.desc;
			this.time = _data.time;
		}
		public String toString(){
			return String.format("%d\t%s\t%s\t%s\t%s\t%.02f\n", id, cname, name, image, desc, time);
		}
	}
	static public class codesysdataTab {
		//data
		public String mTabName = "codesysdata_";
		public List<codesysdata> mItemList = new ArrayList<codesysdata>();
		public Map<String, codesysdata> mItemMap = new HashMap<String, codesysdata>();
		public void _Load(){
			//优先读取本地
			BaseReader reader = null;
			if(FileTools.LocalReader.exists(mTabName))
				reader = new LocalReader(mTabName);
			else
				reader = new AssetReader(mTabName);
			
	        for (int i = 0; i < reader.recordCount(); ++i){
	        	codesysdata data = new codesysdata();
	        	data.id = reader.getInt(i, "id");
	        	data.cname = reader.getString(i, "cname");
	        	data.name = reader.getString(i, "name");
	        	data.image = reader.getString(i, "image");
	        	data.desc = reader.getString(i, "desc");
	        	data.time = reader.getFloat(i, "time");
	        	mItemList.add(data);
	        	mItemMap.put(data.cname, data);
	        }
		}
		public void _Save(){
			String str = "$id\tcname\tname\timage\tdesc\ttime\n";
			for (int idx = 0; idx < mItemList.size(); ++idx){
				str += mItemList.get(idx).toString();
			}
			FileTools.LocalReader.write(mTabName, str);
		}
		
		//static
		private static boolean tabInited = false;
		private static Map<String, codesysdataTab> sDataName = new HashMap<String, codesysdataTab>();
		private static Map<Integer, codesysdataTab> sDataId = new HashMap<Integer, codesysdataTab>();
		
	    public static void Load()
	    {
	    	tabInited = true;
	    	sDataName.clear();
	    	sDataId.clear();
	    	for (int idx = 0; idx < codesystypeTab.getCount(); ++idx){
	    		codesystypeTab tpcfg = codesystypeTab.getItem(idx);
	    		codesysdataTab cfg = new codesysdataTab();
	    		sDataName.put(tpcfg.tpname, cfg);
	    		sDataId.put(tpcfg.tpid, cfg);
	    		cfg.mTabName = "codesysdata_" + tpcfg.tpid + ".txt";
	    		cfg._Load();
	    	}
	    }
	    public static Map<String, codesysdataTab> getDataName()
	    {
	        if (!tabInited)
	            Load();
	        return sDataName;
	    }
	    public static Map<Integer, codesysdataTab> getDataId()
	    {
	        if (!tabInited)
	            Load();
	        return sDataId;
	    }
	    public static codesysdataTab getItem_by_Name(String _idx)
	    {
	        if (!tabInited)
	            Load();
	        if(sDataName.containsKey(_idx))
	            return sDataName.get(_idx);
	        return null;
	    }
	    public static codesysdataTab getItem_by_Id(int _idx)
	    {
	        if (!tabInited)
	            Load();
	        if(sDataId.containsKey(_idx))
	            return sDataId.get(_idx);
	        return null;
	    }
	    public static int getCount()
	    {
	        if (!tabInited)
	            Load();
	        return sDataName.size();
	    }
	}

	static public class helpInfo {
		//data
		public int id = 0;
		public String title = "";
		public String info  = "";
		//static
		public static boolean tabInited = false;
		public static List<helpInfo> sData = new ArrayList<helpInfo>();
		
	    public static void Load()
	    {
	    	tabInited = true;
	    	sData.clear();
	    	AssetReader reader = new AssetReader("helpinfo.txt");
	        for (int i = 0; i < reader.recordCount(); ++i)
	        {
	        	helpInfo cfg = new helpInfo();
	            cfg.id = reader.getInt(i, "id");
	            cfg.title = reader.getString(i, "title");
	            cfg.info  = reader.getString(i, "info");
	            sData.add(cfg);
	        }
	    }
	    public static helpInfo getItem(int _idx)
	    {
	        if (!tabInited)
	            Load();
	        if (_idx >= 0 && _idx < sData.size()){
	            return sData.get(_idx);
	        }
	        return null;
	    }
	    public static int getCount()
	    {
	        if (!tabInited)
	            Load();
	        return sData.size();
	    }
	}
	static public class memWords {
		//data
		public int id = 0;
		public int type = 0;
		public String name = "";
		//static
		public static boolean tabInited = false;
		public static List<memWords> sData = new ArrayList<memWords>();
		public static Map<Integer, List<memWords>> sTypeData = new HashMap<Integer, List<memWords>>();
		
	    public static void Load()
	    {
	    	tabInited = true;
	    	sData.clear();
	    	sTypeData.clear();
	    	AssetReader reader = new AssetReader("mem_words.txt");
	        for (int i = 0; i < reader.recordCount(); ++i)
	        {
	        	memWords cfg = new memWords();
	            cfg.id = reader.getInt(i, "id");
	            cfg.type = reader.getInt(i, "type");
	            cfg.name  = reader.getString(i, "name");
	            sData.add(cfg);
	            List<memWords> ls = null;
	            if(sTypeData.containsKey(cfg.type)){
	            	ls = sTypeData.get(cfg.type);
	            }else{
	            	ls = new ArrayList<memWords>();
	            }
	            ls.add(cfg);
	        }
	    }
	    public static memWords getItem(int _idx)
	    {
	        if (!tabInited)
	            Load();
	        if (_idx >= 0 && _idx < sData.size()){
	            return sData.get(_idx);
	        }
	        return null;
	    }
	    public static List<memWords> getItemByType(int _tp)
	    {
	        if (!tabInited)
	            Load();
	        if(_tp == 0)
	        	return sData;
	        if(sTypeData.containsKey(_tp)){
	    	   return sTypeData.get(_tp);
	        }
	        return null;
	    }
	    public static List<String> getNamesByTypeRandom(int _tp, int _count)
	    {
	        if (!tabInited)
	            Load();
	        List<String> des = new ArrayList<String>();
	        List<memWords> src = null;
	        if(_tp == 0)
	        	src = sData;
	        if(sTypeData.containsKey(_tp)){
	        	src = sTypeData.get(_tp);
	        }
	        if(src != null){
	        	for(int i=0; i<_count; i++){
	        		int idx = new Random().nextInt(src.size());
	        		des.add(src.get(idx).name);
	        	}
	        }
	        return des;
	    }
	    public static int getCount()
	    {
	        if (!tabInited)
	            Load();
	        return sData.size();
	    }
	}
}
