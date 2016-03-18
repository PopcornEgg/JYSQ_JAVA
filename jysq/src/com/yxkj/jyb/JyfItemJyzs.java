package com.yxkj.jyb;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;

public class JyfItemJyzs extends BaseAdapter {
	public static int imgs[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
	private Context context = null;
	private View videoStudy = null;
	private List<ForumThreadItem> curListThreads = null;

	public JyfItemJyzs(Context _c) {
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
			return 1;

		return curListThreads.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if( position == 0 )
			return videoStudy;

		// TODO Auto-generated method stub
		return curListThreads.get(position - 1);
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
		
		if( position == 0)
		{
			if( videoStudy == null)
			{
				videoStudy = LayoutInflater.from(context).inflate(R.layout.jyf_item_video,null);
				
				VideoStudyHolder vsh = new VideoStudyHolder();
				vsh.protxt = (TextView)videoStudy.findViewById(R.id.protxt);
				vsh.probar = (ProgressBar)videoStudy.findViewById(R.id.progressBar1);
				videoStudy.setTag(vsh);
			}
			
			VideoStudyHolder vsh = (VideoStudyHolder)videoStudy.getTag();
			int per = VideoStudyList.getAllStudyTime();
			vsh.protxt.setText(per + "%");
			vsh.probar.setMax(100);
			vsh.probar.setProgress(per);

			return videoStudy;
		}
		
		ForumThreadItem item = curListThreads.get(position-1);
		if(item == null)
			return null;

		ItemHolder ih = null;
		if(convertView == null || convertView == videoStudy )
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.fragment_jyf_item,null);
			if(convertView == null)
				return null;

			ih = new ItemHolder();
			ih.img = (ImageView)convertView.findViewById(R.id.img);
			ih.lable = (TextView)convertView.findViewById(R.id.info);
			ih.date = (TextView)convertView.findViewById(R.id.date);
			convertView.setTag(ih);
		}
		else
			ih = (ItemHolder)convertView.getTag();
		
		ih.img.setImageResource(imgs[item.imgid%imgs.length]);
        ih.lable.setText(item.subject);
        ih.date.setText(GlobalUtility.Func.timeStamp2Date(item.dateline));
        return convertView;
	}
	static class ItemHolder
    {
    	public TextView lable;
    	public TextView date;
		public ImageView img;
    }
	
	static class VideoStudyHolder
	{
		public TextView protxt;
		public ProgressBar probar;
	}
}
