package com.yxkj.jyb;

import java.util.List;
import java.util.Random;

import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapterThread extends BaseAdapter {

	static String[] listHelps = new String[]{
		"求大神帮忙解决一下，怎么记忆！",
		"求助小伙伴，看看这个怎么记忆！",
		"求小伙伴，我保证好好学习！！",
		"谢谢大家啦！！",
	};
	static public String getListHelps(boolean isImg){
		int idx = new Random().nextInt(listHelps.length);
		if(isImg)
			return "<font color='#808080'>" + listHelps[idx] + "</font><br>";
		else
			return "<font color='#808080'>" + listHelps[idx] + "</font><br><br>";
	}
	private Context context = null;
	private List<ForumThreadItem> mListData = null;
	public MyAdapterThread(Context _c) {
		context = _c;
	}
	public void setData(List<ForumThreadItem> _d){
		mListData = _d;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mListData == null)
			return 0;
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if( context == null || mListData == null)
			return null;
		ForumThreadItem item = mListData.get(position);
		if(item == null)
			return null;
		ViewHolder vh = null;
		if(convertView == null )
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.fragmentpage2_item,null);
			if(convertView == null)
				return null;
			vh = new ViewHolder();
			vh.head = (ImageButton)convertView.findViewById(R.id.head);
			vh.reply = (Button)convertView.findViewById(R.id.reply);
			vh.img = (ImageView)convertView.findViewById(R.id.img);
			vh.name = (TextView)convertView.findViewById(R.id.name);
			vh.subname = (TextView)convertView.findViewById(R.id.subname);
			vh.time = (TextView)convertView.findViewById(R.id.time);
			vh.question = (TextView)convertView.findViewById(R.id.question);
			vh.ansnum = (TextView)convertView.findViewById(R.id.ansnum);
			vh.credit = (TextView)convertView.findViewById(R.id.credit);
			convertView.setTag(vh);
			
			vh.reply.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					ForumThreadItem item = mListData.get(position);
					if(item == null)
						return;
					QuestionView.show(context, item.tid, true);
				}
			});
			
			vh.head.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					ForumThreadItem item = mListData.get(position);
					if(item == null)
						return;
					OtherUserInfo.show(context, item.authorid, item.author, item.realname, item.gender);
				}
			});
			
			vh.img.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					ForumThreadItem item = mListData.get(position);
					if(item == null)
						return;
					PhotoViewer.sUrl = item.subject;
					PhotoViewer.sForumThreadItem = item;
					PhotoViewer.sType = 1;
					PhotoViewer.show(context);
				}
			});
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		if(item.attachment == 0){
			vh.img.setVisibility(View.INVISIBLE);
			LayoutParams params = vh.img.getLayoutParams();
		    params.height=1;  
			vh.question.setText(Html.fromHtml(item.exInfo + item.subject));
		}
		else if(item.attachment == 2){
			vh.question.setText(Html.fromHtml(item.exInfo));
			vh.img.setVisibility(View.VISIBLE);
			
			String url = item.subject + "/scaling";
			ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
			if(imginfo != null){
				LayoutParams params = vh.img.getLayoutParams();  
			    params.height= GlobalUtility.Func.dip2px(100.0f);  
			    vh.img.setImageBitmap(imginfo.bitmap);
			}
			else{
				vh.img.setImageResource(R.color.font_color_gray);
				ImageDataMgr.sDownLoadMgr.addLoad(url, vh.img, 1, FragmentPage2.sFragmentPage2.mLoadHandler);
			}
		}
		
        vh.img.setTag(position);
        vh.head.setImageResource(GlobalUtility.Func.getHeadIconBySex(item.gender));
        if(item.realname.isEmpty())
			vh.name.setText(item.author);
		else
			vh.name.setText(item.realname);

        
        vh.ansnum.setText(item.replies + "回答");
        vh.time.setText(GlobalUtility.Func.timeStamp2Recently(item.dateline));
        vh.reply.setTag(position);
        vh.head.setTag(position);
        vh.credit.setText(Integer.toString(item.credit));
        vh.subname.setText(com.yxkj.jyb.FragmentPage2.getCurSubName(item.fid));
        
        return convertView;
	}
	
	static public class ViewHolder
    {
    	public ImageButton head;
    	public Button reply;
    	public TextView name;
    	public TextView subname;
    	public TextView time;
    	public TextView ansnum;
    	public TextView question;
    	public TextView credit;
		public ImageView img;
    }
}
