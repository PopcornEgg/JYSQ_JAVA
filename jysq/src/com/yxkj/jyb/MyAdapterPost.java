package com.yxkj.jyb;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ta.annotation.TAInject;
import com.yxkj.jyb.ForumDataMgr.ForumPostItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.UserUtils;

public class MyAdapterPost extends BaseAdapter{
        //用来接收传递过来的Context上下文对象
    private Activity context;
    private View item1;
    private List<ForumPostItem> listPosts = null;
    private ForumThreadItem curForumThreadItem = null;
    public int curSelectedId = 0;
    //构造函数
    public MyAdapterPost(Activity context)
    {
        this.context = context;
    }
    public void setData(ForumThreadItem _cft, List<ForumPostItem> _d){
    	curForumThreadItem = _cft;
    	listPosts = _d;
		notifyDataSetChanged();
	}
    public boolean isShowEmptyView() {
    	if(listPosts != null && listPosts.size() > 0)
    		return false;
    	return true;
    }
   @Override
    public int getCount() {
    	if(curForumThreadItem == null)
    		return 0;
    	int count = 1;
    	if(isShowEmptyView())
    		count++;//显示一个空提示
		else
    		count += listPosts.size();
        return count;
    }

    @Override
    public Object getItem(int position) {
        //根据选中项返回索引位置
        return position;
    }

    @Override
    public long getItemId(int position) {
        //根据选中项id返回索引位置
        return position;
    }
    boolean isPulling = false; 
    @TAInject
	private void asynPostAdoptPost(String getuid, String pid, String tid)
	{
		if(isPulling)
			return;
		isPulling = true;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumAdoptPostUrl);
		params.put("getuid", getuid);
		params.put("pid", pid);
		params.put("tid", tid);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		HttpUtils.post(context,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isPulling = false;
				
				if(content.contains("__null__")){
					return;
				}
				try {
					JSONObject json = new JSONObject(content);
					ForumThreadItem titem = ForumDataMgr.getThread_By_Tid(json.getString("tid"));
					if(titem != null){
						titem.adoptpid = Integer.parseInt(json.getString("pid"));
						notifyDataSetChanged();
						GlobalUtility.Func.ShowToast("采纳成功");
					}
				} catch (JSONException e) {
					isPulling = false;
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				isPulling = false;
	        }  
		});
	}
    //未优化的getView，这部分可以使用recycle()释放内存、或者BitmapFacotry.Options缩小，或者软引用，或者控制图片资源大小等等很多方法，找时间专门写
    @SuppressLint("InflateParams") @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	if( context == null || curForumThreadItem == null)
			return null;
    	
    	if( position == 0)
    	{
    		if( item1 == null)
    		{
    			item1 = LayoutInflater.from(context).inflate(R.layout.question_info_item1,null);
    			
    			ViewHolder vh = new ViewHolder();
        		vh.head = (ImageButton)item1.findViewById(R.id.head);
        		vh.img = (ImageView)item1.findViewById(R.id.img);
        		vh.name = (TextView)item1.findViewById(R.id.name);
        		vh.subname = (TextView)item1.findViewById(R.id.subname);
        		vh.time = (TextView)item1.findViewById(R.id.time);
        		vh.question = (TextView)item1.findViewById(R.id.question);
        		item1.setTag(vh);
        		
        		vh.head.setOnClickListener(new View.OnClickListener(){
    				public void onClick(View v) {
    					ForumThreadItem item = (ForumThreadItem)(v.getTag());
    					if(item == null)
    						return;
    					OtherUserInfo.show(context, item.authorid, item.author, item.realname, item.gender);
    				}
    			});
        		vh.img.setOnClickListener(new View.OnClickListener(){
    				public void onClick(View v) {
    					PhotoViewer.sUrl = curForumThreadItem.subject;
    					PhotoViewer.sForumThreadItem = curForumThreadItem;
    					PhotoViewer.sType = 1;
    					PhotoViewer.show(context);
    				}
    			});
    		}
    		ViewHolder vh = (ViewHolder)item1.getTag();
    		setImageViewThread(vh);
            vh.head.setImageResource(GlobalUtility.Func.getHeadIconBySex(curForumThreadItem.gender));
            if(curForumThreadItem.realname.isEmpty())
            	vh.name.setText("楼主 " + curForumThreadItem.author);
    		else
    			vh.name.setText("楼主 " + curForumThreadItem.realname);
            vh.time.setText(GlobalUtility.Func.timeStamp2Recently(curForumThreadItem.dateline));
            vh.head.setTag(curForumThreadItem);
            vh.subname.setText(ThreadsFilter.getSubNameByFid(curForumThreadItem.fid));
    		return item1;
    	}
    	else if( convertView == item1 || convertView == null )
    	{
			convertView = LayoutInflater.from(context).inflate(R.layout.question_info_item2,null);   
    		ViewHolder vh = new ViewHolder();
    		vh.head = (ImageButton)convertView.findViewById(R.id.head);
    		vh.adopt = (Button)convertView.findViewById(R.id.adopt);
    		vh.img = (ImageView)convertView.findViewById(R.id.img);
    		vh.name = (TextView)convertView.findViewById(R.id.name);
    		vh.time = (TextView)convertView.findViewById(R.id.time);
    		vh.bjbget = (Button)convertView.findViewById(R.id.getbjb);
    		vh.question = (TextView)convertView.findViewById(R.id.question);
    		vh.fullinfo = convertView.findViewById(R.id.fullinfo);
    		vh.emptyinfo = (TextView)convertView.findViewById(R.id.emptyinfo);
    		convertView.setTag(vh);
    		
    		vh.bjbget.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					curSelectedId = v.getId();
					bjb_MyChooseGroup_Dlg.show(context, QuestionView.sActivity.mLoadHandler, 3);
				}
			});
    		vh.head.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					ForumPostItem item = (ForumPostItem)(v.getTag());
					if(item == null)
						return;
					OtherUserInfo.show(context, item.authorid, item.author, item.realname, item.gender);
				}
			});
    		vh.adopt.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				String uid = UserUtils.DataUtils.get("uid");
    				if(uid.equals(curForumThreadItem.authorid)){
    				
    					ForumPostItem pitem = (ForumPostItem)v.getTag();
    					if(pitem == null || uid.equals(pitem.authorid)){
    						return;
    					}
    					asynPostAdoptPost(pitem.authorid, pitem.pid, pitem.tid);
    				}
    			}  
    		});
    		vh.img.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					ForumPostItem item = listPosts.get(position);
					if(item == null)
						return;
					PhotoViewer.sUrl = item.subject;
					PhotoViewer.sForumPostItem = item;
					PhotoViewer.sType = 2;
					PhotoViewer.show(context);
				}
			});
    		
//    		if(position == 1){
//        		item2 = convertView;
//        		if(isShowEmptyView()){
//        			emptyitem = LayoutInflater.from(context).inflate(R.layout.emptylistview_item,null);
//        			TextView txt = (TextView)emptyitem.findViewById(R.id.info);
//        			txt.setText("学霸，挑战一下这个问题吧！");
//        			return emptyitem;
//        		}
//        	}
    	}
    	
//    	
//    	if(position == 1){
//    		convertView = item2;
//    	}
    	
    	ViewHolder vh = (ViewHolder)convertView.getTag();
    	
    	if(isShowEmptyView()){
    		vh.emptyinfo.setVisibility(View.VISIBLE);
    		vh.fullinfo.setVisibility(View.INVISIBLE);
    	}else{
    		ForumPostItem pitem = listPosts.get(position - 1);
        	if(pitem == null)
        		 return convertView;
        	
    		vh.emptyinfo.setVisibility(View.INVISIBLE);
    		vh.fullinfo.setVisibility(View.VISIBLE);
    		
    		vh.img.setTag(position - 1);
        	setImageViewPost(pitem, vh);
            vh.head.setImageResource(GlobalUtility.Func.getHeadIconBySex(pitem.gender));
            if(pitem.realname != null && pitem.realname.isEmpty())
            	vh.name.setText(position + "楼 " + pitem.author);
    		else
    			vh.name.setText(position + "楼 " + pitem.realname);
           // vh.question.setText(pitem.message);
            vh.question.setText(Html.fromHtml(pitem.message));
            vh.time.setText(GlobalUtility.Func.timeStamp2Recently(pitem.dateline));
            vh.head.setTag(pitem);
            
            vh.adopt.setTag(pitem);
            String uid = UserUtils.DataUtils.get("uid");
            if(curForumThreadItem.adoptpid >= 0){
            	if(pitem.pid.equals(curForumThreadItem.adoptpid.toString())){
            		vh.adopt.setText("已采纳");
            		vh.adopt.setVisibility(View.VISIBLE );
            	}
            	else{
            		vh.adopt.setVisibility(View.INVISIBLE );
            	}
            }
            else{
            	vh.adopt.setText("采纳");
            	if(uid.equals(curForumThreadItem.authorid)){
            		vh.adopt.setVisibility(uid.equals(pitem.authorid)? View.INVISIBLE : View.VISIBLE );
            	}
            	else{
            		 vh.adopt.setVisibility(View.INVISIBLE );
            	}
            }
            vh.bjbget.setId(position - 1);
            //vh.adopt.setVisibility(curForumThreadItem.pid >= 0 ? View.VISIBLE : View.INVISIBLE );
    	}
    	
        return convertView;
    }
    private void setImageViewThread(ViewHolder vh){
    	
    	if(curForumThreadItem.attachment == 0){
			vh.img.setVisibility(View.INVISIBLE);
			LayoutParams params = vh.img.getLayoutParams();
		    params.height=1;  
		    vh.img.setLayoutParams(params);
			vh.question.setText(Html.fromHtml(curForumThreadItem.exInfo + curForumThreadItem.subject));
		}
		else if(curForumThreadItem.attachment == 2){
			vh.question.setText(Html.fromHtml(curForumThreadItem.exInfo));
			vh.img.setVisibility(View.VISIBLE);
			
			String url = curForumThreadItem.subject + "/scaling";
			ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
			if(imginfo != null){//肯定存在
				LayoutParams params = vh.img.getLayoutParams();  
			    params.height= GlobalUtility.Func.dip2px(90.0f); 
			    vh.img.setLayoutParams(params);
			    vh.img.setImageBitmap(imginfo.bitmap);
			}
			else{
				vh.img.setImageResource(R.color.font_color_gray);
				ImageDataMgr.sDownLoadMgr.addLoad(url, vh.img, 2, QuestionView.sActivity.mLoadHandler);
			}
		}
    }
    private void setImageViewPost(ForumPostItem item, ViewHolder vh){
    	
    	if(item.attachment == 0){
			vh.img.setImageDrawable(null);
			LayoutParams params = vh.img.getLayoutParams();
		    params.height=1;  
		    vh.img.setLayoutParams(params);
			vh.img.setVisibility(View.INVISIBLE);
		}
		else if(item.attachment == 2){
			vh.img.setVisibility(View.VISIBLE);
			String url = item.subject + "/scaling";
			ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
			if(imginfo != null){
				LayoutParams params = vh.img.getLayoutParams();  
				params.height= GlobalUtility.Func.dip2px(80.0f); 
			    vh.img.setImageBitmap(imginfo.bitmap);
			    vh.img.setLayoutParams(params);
			}
			else{
				vh.img.setImageResource(R.color.font_color_gray);
				ImageDataMgr.sDownLoadMgr.addLoad(url, vh.img, 2, QuestionView.sActivity.mLoadHandler);
			}
		}
    }
    static class ViewHolder
    {
    	public ImageButton head;
    	public Button adopt;
    	public Button bjbget;
    	public TextView name;
    	public TextView subname;
    	public TextView time;
		public TextView question;
		public ImageView img;
		public View fullinfo;
		public TextView emptyinfo;
    }
}
