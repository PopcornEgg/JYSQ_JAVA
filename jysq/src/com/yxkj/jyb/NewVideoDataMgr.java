package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewVideoDataMgr {
	
	@SuppressWarnings("rawtypes")
	public static class Item implements Comparable
	{
		public String id;
		public String title;
		public int type;
		public int playcount = 0;
		public int price = 0;
		public String img;
		public String url;
		public long dateline;
		
		@Override
	    public int compareTo(Object o)
	    {
			Item sdto = (Item)o;
            if(this.dateline > sdto.dateline)
                return -1;  
            else if(this.dateline < sdto.dateline)
            	return 1;  
            else
                return 0;  
	    }
	}

    static Map<String, Item> mapItems = new HashMap<String, Item>();
    static List<Item> listItems = new ArrayList< Item>();
    
    static public void addItem(Item item)
    {
        if (item == null)
            return;
        
        if (mapItems.containsKey(item.id)){
        	mapItems.put(item.id, item);
        }
        else{
        	mapItems.put(item.id, item);
        	listItems.add(item);
        }
    }
    static public Item getItem( String id )
    {
    	if(mapItems.containsKey(id))
    		return mapItems.get(id);
    	return null;
    }
    static public List< Item> getItems( )
    {
    	return listItems;
    }
    static public void addPlayCount( String id , int count)
    {
    	if(mapItems.containsKey(id))
    		mapItems.get(id).playcount+=count;
    }
}
