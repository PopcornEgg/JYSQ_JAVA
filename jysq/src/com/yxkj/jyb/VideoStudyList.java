package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

import com.tencent.qcload.playersdk.util.VideoInfo;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;

public class VideoStudyList extends Activity
{
	static class VideoItem
	{
		int uid = 0;
		int img;
		String lable;
		String url;
		public Integer alltime = 1;
		public Integer studytime = 0;
		public VideoItem(int _t, int _img, String _lable, String _url){
			this.alltime = _t;
			this.img = _img;
			this.lable = _lable;
			this.url = _url;
		}
	}
	
	static String sLocalDataName = "videostudy";
	static boolean isInited = false;
	static public List<VideoItem> mDatas = new ArrayList<VideoItem>(){{
		add(new VideoItem(	94,   R.drawable.study_0	,	"视频介绍"	,					"117a1d6e215e11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1498, R.drawable.study_1	,	"第一章 启动右脑"	,				"6a30437a215e11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2351, R.drawable.study_2	,	"第二章 记忆力展示和测验"	,		"69de17d6216211e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2074, R.drawable.study_3	,	"第三章 记忆锁链法训练（上）"	,	"356d64c8216911e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1565, R.drawable.study_3	,	"第四章 记忆锁链法训练（下）"	,	"7c6f399a216f11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1837, R.drawable.study_4	,	"第五章 初级大脑格式化"	,		"9cc6983c217411e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2075, R.drawable.study_4	,	"第六章 中级大脑格式化"	,		"95c8b1dc217911e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2667, R.drawable.study_4	,	"第七章 高级大脑格式化（上）"	,	"e97806ca217e11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2139, R.drawable.study_4	,	"第八章 高级大脑格式化（下）"	,	"bbeef482218511e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1799, R.drawable.study_5	,	"第九章 记忆长串数字练习"	,		"21678036218b11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1601, R.drawable.study_6	,	"第十章 记忆的传统与科学"	,		"aea5003c218f11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2217, R.drawable.study_7	,	"第十一章 偏难词组记忆（上）"	,	"b9661c64219311e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1895, R.drawable.study_7	,	"第十二章 偏难词组记忆（下）"	,	"2195869e219911e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1488, R.drawable.study_8	,	"第十三章 人像人名电话记忆运用",	"8f7bd15a219d11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1868, R.drawable.study_9	,	"第十四章 句子记忆训练（上）"	,	"3431e5ba21a111e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2283, R.drawable.study_9	,	"第十五章 句子记忆训练（下）"	,	"b50b0a4621a511e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1639, R.drawable.study_10	,	"第十六章 英语单词记忆训练（上）",	"3773656e21ab11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	2188, R.drawable.study_10	,	"第十七章 英语单词记忆训练（中）",	"282e1b4a21af11e5ba3a4152c36ae4c8"	));
		add(new VideoItem(	1990, R.drawable.study_10	,	"第十八章 英语单词记忆训练（下）",	"7855105621b411e5ba3a4152c36ae4c8"	));
	}};

	static public String initStudyList(){
		if(isInited)
			return "";
		isInited = true;
		String getlist = "";
		for(Integer i=0;i<mDatas.size();i++){
			VideoItem item = mDatas.get(i);
			item.uid = i;
			String strTime = GlobalUtility.Func.getLocalData(sLocalDataName, i.toString());
			if(strTime.isEmpty()){
				if(getlist.isEmpty()){
					getlist = "data" + i;
				}
				else{
					getlist = getlist + ",data" + i;
				}
			}
			else{
				item.studytime = Integer.parseInt(strTime);
			}
		}
		return getlist;
	}
	static public void setStudyListStudyTime(Integer _uid, Integer _st, boolean isneedupload){
		if(_uid >= 0 && _uid < mDatas.size()){
			VideoItem item = mDatas.get(_uid);
			item.studytime = _st;
			GlobalUtility.Func.saveLocalData(sLocalDataName, _uid.toString(), item.studytime.toString());
			if(isneedupload){//上传到服务器
				if(sVideoStudyList != null){
					sVideoStudyList.asynSaveStudyList("data"+_uid, item.studytime.toString());
				}
			}
			updateListView();
		}
	}
	static public void updateListView(){
		if(sVideoStudyList != null){
			sVideoStudyList._updateListView();
		}
	}
	static public int getAllStudyTime(){
		int alltime = 0;
		int studytime = 0;
		for(Integer i=0;i<mDatas.size();i++){
			VideoItem item = mDatas.get(i);
			alltime += item.alltime;
			studytime += item.studytime;
		}
		
		return (int)((float)studytime / (float)alltime * 100.0f);
	}
	
	static VideoStudyList sVideoStudyList = null;
	MyAdapter mMyAdapter = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.video_list);
        sVideoStudyList = this;
        
        ListView listView = (ListView)findViewById(R.id.voido_list);
        
        mMyAdapter = new MyAdapter(this.getBaseContext());
        mMyAdapter.SetVideoItem(getData());
        listView.setAdapter(mMyAdapter);
        listView.setOnItemClickListener(new OnItemClickListenerImpl());
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				sVideoStudyList.finish();
			}  
		});
        findViewById(R.id.help).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Help_Act.show(sVideoStudyList, 0);
			}  
		});
    }
	private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			if(position < mDatas.size()){
				final VideoItem vitem = mDatas.get(position);
				
				List<VideoInfo> videos = new ArrayList<VideoInfo>();
				VideoInfo v1=new VideoInfo(); 
		        v1.description="标清"; 
		        v1.type=VideoInfo.VideoType.MP4; 
		        v1.url = "http://8731.vod.myqcloud.com/8731_" + vitem.url + ".f20.mp4";
		        //v1.url="http://4500.vod.myqcloud.com/4500_d754e448e74c11e4ad9e37e079c2b389.f20.mp4?vkey=693D66AF23164CA4741745A2FE9675DCC4493BF10CF724CBE3769CB237121DAB55F3D494AC2C6DB7&ocid=12345"; 
		        videos.add(v1); 
		        
		        VideoInfo v2=new VideoInfo(); 
		        v2.description="手机"; 
		        v2.type=VideoInfo.VideoType.MP4; 
		        v2.url = "http://8731.vod.myqcloud.com/8731_" + vitem.url + ".f10.mp4"; 
		        videos.add(v2); 
		        
		        VideoInfo v3=new VideoInfo(); 
		        v3.description="高清"; 
		        v3.type=VideoInfo.VideoType.MP4; 
		        v3.url = "http://8731.vod.myqcloud.com/8731_" + vitem.url + ".f0.mp4"; 
		        videos.add(v3); 
		        
		        VideoPlayer.play(sVideoStudyList, vitem.uid, videos, vitem.studytime >= vitem.alltime ? 0 : vitem.studytime, new CallBackInterface() {
		            @Override
		            public void exectueMethod(Object p) {
		            	String[] params = p.toString().split(",");
		            	if(params.length == 2){
		            		int studyTime = Integer.parseInt(params[1].toString()) ;
		            		int uid = Integer.parseInt(params[0].toString()) ;
		            		setStudyListStudyTime(uid, studyTime, true);
		            	}
		            }
		        });
			}
		}
	}
	
	public void asynSaveStudyList(String savelist, String savevals)
	{
		if(!UserUtils.DataUtils.isLogined() || !NetWorkStateDetector.isConnectingToInternet())
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumSaveVideoStudyUrl);
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("savelist", savelist);
		params.put("savevals", savevals);
		
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				if(!content.contains("__succ__")){
					
				}
			}
			@Override  
	        public void onFailure(String error) {  
	        }  
		});
	}
	
	 @Override
    public void finish(){
    	super.finish();
    	sVideoStudyList = null;
    }
	
    private List<VideoItem> getData()
    {
        return mDatas;
    }
	
	void _updateListView(){
		 mMyAdapter.SetVideoItem(getData());
	}
	
	public class MyAdapter extends BaseAdapter{
		 
        private LayoutInflater mInflater;
        private List<VideoItem> mData; 
         
        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        
        public void SetVideoItem(List<VideoItem> items)
        {
        	this.mData = items;
        	notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
        	if( mData == null )
        		return 0;
        	
            // TODO Auto-generated method stub
            return mData.size();
        }
 
        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }
 
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	if( mData == null || mData.size() < 1 )
        		return null;
        	
        	VideoItemHolder holder = null;
            if (convertView == null)
            {
                holder=new VideoItemHolder();  
                 
                convertView = mInflater.inflate(R.layout.video_list_item, null);
                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.lable = (TextView)convertView.findViewById(R.id.lable);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                holder.pro = (ProgressBar)convertView.findViewById(R.id.progressBar1);
                convertView.setTag(holder);
                 
            }else {
                 
                holder = (VideoItemHolder)convertView.getTag();
            }
             
            VideoItem vi = mData.get(position);
            holder.img.setImageResource(vi.img);
            holder.lable.setText(vi.lable);
            long hours = vi.alltime / (60*60);
			long mins = vi.alltime / 60 % (60);
			long secs = vi.alltime % (60);
			if(hours > 0 )
				holder.time.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
			else
				holder.time.setText(String.format("%02d:%02d", mins, secs));
            int pre = (int)((float)vi.studytime / (float)vi.alltime * 100.0f);
            holder.pro.setProgress(pre);
             
            return convertView;
        }
        
        public final class VideoItemHolder
        {
            public ImageView img;
            public TextView lable;
            public TextView time;
            public ProgressBar pro;
        }
    }
     
}