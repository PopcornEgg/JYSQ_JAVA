package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.ui.LineGridView;

public class JyfItemJyxl extends BaseAdapter {

	public static int topicons[] = {R.drawable.img_jyxlbtn_0,R.drawable.img_jyxlbtn_1,R.drawable.img_jyxlbtn_2};
	public static String topnames[] = {"Œ“µƒ±‡¬Î"," ˝◊÷º«“‰","¥ ª„º«“‰"};
	public static int imgs[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
	
	private Context context = null;
	private View topView = null;
	private List<ForumThreadItem> curListThreads = null;

	public JyfItemJyxl(Context _c) {
		context = _c;
	}
	public void setData(List<ForumThreadItem> _d){
		curListThreads = _d;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		if(curListThreads == null)
			return 1;

		return curListThreads.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if( position == 0 )
			return topView;

		return curListThreads.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<Map<String, Object>> getTopDatas(){        
		List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        for(int i=0;i<topicons.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", topicons[i]);
            map.put("text", topnames[i]);
            data_list.add(map);
        }
        return data_list;
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( context == null || curListThreads == null)
			return null;
		
		if( position == 0){
			
			if( topView == null){
				topView = LayoutInflater.from(context).inflate(R.layout.jyf_item_codesystem,null);
				TopHolder vsh = new TopHolder();
				vsh.lgv = (LineGridView)topView.findViewById(R.id.lgv);
				topView.setTag(vsh);
			}
			TopHolder vsh = (TopHolder)topView.getTag();
			//–¬Ω®  ≈‰∆˜
	        String [] from ={"image","text"};
	        int [] to = {R.id.image,R.id.text};
	        SimpleAdapter sim_adapter = new SimpleAdapter(context, getTopDatas(), R.layout.linegridview_item, from, to);
	        //≈‰÷√  ≈‰∆˜
	        vsh.lgv.setAdapter(sim_adapter);
	        vsh.lgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	@Override
	        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long position) {
	        		switch((int)position){
	        		case 0:
	        			CodeSystem_FraAct.show(context);
	        			break;
	        		case 1:
	        			Mem_Number_Act_Main.show(context);
	        			break;
	        		case 2:
	        			Mem_Words_Act_Main.show(context);
	        			break;
	        		}
	        	}
        	});
			return topView;
		}
		
		ForumThreadItem item = curListThreads.get(position-1);
		if(item == null)
			return null;

		BodyHolder ih = null;
		if(convertView == null || convertView == topView ){
			
			convertView = LayoutInflater.from(context).inflate(R.layout.fragment_jyf_item,null);
			if(convertView == null)
				return null;

			ih = new BodyHolder();
			ih.img = (ImageView)convertView.findViewById(R.id.img);
			ih.lable = (TextView)convertView.findViewById(R.id.info);
			ih.date = (TextView)convertView.findViewById(R.id.date);
			convertView.setTag(ih);
		}
		else
			ih = (BodyHolder)convertView.getTag();
		
		ih.img.setImageResource(imgs[item.imgid%imgs.length]);
        ih.lable.setText(item.subject);
        ih.date.setText(GlobalUtility.Func.timeStamp2Date(item.dateline));
        
        return convertView;
	}
	
	static class TopHolder
	{
		public LineGridView lgv = null;
	}
	
	static class BodyHolder
    {
    	public TextView lable;
    	public TextView date;
		public ImageView img;
    }
}
