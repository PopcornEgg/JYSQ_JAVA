package com.yxkj.jyb;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;

public class JyfItemSjxy extends BaseAdapter {
	public static int imgs[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
	private Context context = null;
	private List<ForumThreadItem> curListThreads = null;

	public JyfItemSjxy(Context _c) {
		context = _c;
	}
	public void setData(List<ForumThreadItem> _d){
		curListThreads = _d;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(curListThreads == null)
			return 0;

		return curListThreads.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return curListThreads.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if( context == null || curListThreads == null)
			return null;
		
		ForumThreadItem item = curListThreads.get(position);
		if(item == null)
			return null;

		BodyHolder ih = null;
		if(convertView == null ){
			
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
	}
	
	static class BodyHolder
    {
    	public TextView lable;
    	public TextView date;
		public ImageView img;
    }
}
