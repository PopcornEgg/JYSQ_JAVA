package com.yxkj.jyb;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yxkj.jyb.BJBDataMgr.My;
import com.yxkj.jyb.ForumDataMgr.ForumPostItem;
import com.yxkj.jyb.ForumDataMgr.ForumThreadItem;
import com.yxkj.jyb.Utils.HttpCommon;
import com.yxkj.jyb.Utils.HttpUtils;
import com.yxkj.jyb.Utils.NetWorkStateDetector;
import com.yxkj.jyb.Utils.UserUtils;
import com.yxkj.jyb.Utils.uiUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.ta.annotation.TAInject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class QuestionView extends Activity
{
	private static int prePullCount = 10;
	private static String curTid = "";
	private static boolean isshowsoftinput = false;
	private static ForumThreadItem curForumThreadItem = null;
	public  static QuestionView sActivity;
	
	private PullToRefreshListView mPullRefreshListView;
	private List<ForumPostItem> listPosts = null;
	ListView mActualListView = null;
	private MyAdapterPost mAdapter;
	private EditText mEdit = null;
	boolean isPulling = false;//是否正在拉取
	private ForumPostItem lastPostitem = null;
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.question_info);
        sActivity = this;
        
        curForumThreadItem = ForumDataMgr.getThread_By_Tid(curTid);
		if(curForumThreadItem == null)
			asynGetOneThread(curTid);
		
        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pulllv);
        mPullRefreshListView.setMode(Mode.BOTH);  
        mActualListView = mPullRefreshListView.getRefreshableView();
		mAdapter = new MyAdapterPost(this);
		mActualListView.setAdapter(mAdapter);
		mEdit = (EditText)findViewById(R.id.editText);
		uiUtils.setEmptyView(sActivity, "加载失败，该提问可能已被删除", mActualListView);
		
		findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				GlobalUtility.Func.hideSoftInput(sActivity, mEdit);
				sActivity.finish();
			}  
		});
		
		if(listPosts != null)
			listPosts.clear();
        updateListView();
        
        findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!NetWorkStateDetector.isConnectingToInternet())
				{
					ShowToast("当前无网络");
					return;
				}
				
				if(!UserUtils.DataUtils.isLogined())
				{
					UserLogin.showLogin(sActivity,new CallBackInterface() {
			            @Override
			            public void exectueMethod(Object p) {
			            }
			        });
				}
				else
				{
					String rp = mEdit.getText().toString();
					if(rp.isEmpty()){
						ShowToast("回复内容不能为空");
						return;
					}
					asynPostReply(rp, 0, "");
					mEdit.setText("");
				}
				GlobalUtility.Func.hideSoftInput(sActivity, mEdit);
			}  
		});   
		
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>(){
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(curForumThreadItem == null)
					return;
				asynGetPostList(curForumThreadItem.tid, prePullCount, 0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(curForumThreadItem == null)
					return;
				if(listPosts != null)
					asynGetPostList(curForumThreadItem.tid, 10, listPosts.size());
				else
					asynGetPostList(curForumThreadItem.tid, 10, 0);
			}
		});
	  
		findViewById(R.id.sendcar).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				GlobalUtility.Func.hideSoftInput(sActivity, mEdit);
				if(!UserUtils.DataUtils.isLogined())
				{
					UserLogin.showLogin(sActivity,new CallBackInterface() {
			            @Override
			            public void exectueMethod(Object p) {
			            	CameraCroperAct.show(sActivity);
			            }
			        });
				}
				else
					CameraCroperAct.show(sActivity);
			}  
		});
    } 
    @Override  
    protected void onStart() {  
    	super.onStart();
    	if(isshowsoftinput)
          	GlobalUtility.Func.showSoftInput(this, mEdit);
    	else
          	GlobalUtility.Func.hideSoftInput(this, mEdit);
    }
    
    @SuppressLint("DefaultLocale") private void asynGetOneThread(String tid)
	{
		LoadingBox.showBox(this, "加载中...");
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumThreadOneUrl);
		params.put("tid",tid);
		params.put("tauthorexd","1");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("null")){
					return;
				}
				try {
					
					JSONObject json = new JSONObject(content);
					ForumThreadItem tdata = new ForumThreadItem();
					tdata.tid = json.getString("tid");
	                tdata.fid = json.getString("fid");
	                tdata.author = json.getString("author");
	                tdata.authorid = json.getString("authorid");
	                tdata.subject = json.getString("subject");
	                tdata.dateline = json.getLong("dateline");
	                tdata.replies = json.getInt("replies");
	                tdata.realname = json.getString("realname");
	                tdata.gender = json.getInt("gender");
	                tdata.credit = json.getInt("credit");
	                tdata.adoptpid = json.getInt("adoptpid");
	                tdata.attachment = json.getInt("attachment");
	                ForumDataMgr.AddThread(tdata);
	                
					QuestionView.curForumThreadItem = tdata;
					updateListView();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
    
    @TAInject
	private void asynGetPostList(String tid, int count, int start)
	{
		if(isPulling)
			return;
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostListUrl);
		params.put("tid", tid);
		params.put("start", Integer.toString(start));
		params.put("count", Integer.toString(count));
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				isPulling = false;
				mPullRefreshListView.onRefreshComplete();
				
				if(content.contains("null/r/n")){
					return;
				}
				try {
					JSONArray jsonArray = new JSONArray(content);
					for(int i=0;i<jsonArray.length();i++){
						JSONObject json = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			            //Toast.makeText(context, (String)json.getString("subject"), Toast.LENGTH_SHORT).show();
						ForumPostItem pitem = new ForumPostItem();
						pitem.tid = json.getString("tid");
						pitem.pid = json.getString("pid");
						pitem.author = json.getString("author");
						pitem.authorid = json.getString("authorid");
						pitem.subject = json.getString("subject");
						pitem.message = GlobalUtility.Func.hexStr2Str(json.getString("message"));
						pitem.dateline = json.getInt("dateline");
						pitem.realname = json.getString("realname");
						pitem.gender = json.getInt("gender");
						pitem.attachment = json.getInt("attachment");
						if(!json.getString("first").equals("1"))
							ForumDataMgr.addPost(pitem);
					}
					updateListView();
				} catch (JSONException e) {
					mPullRefreshListView.onRefreshComplete();
					e.printStackTrace();
				}
			}
			@Override  
	        public void onFailure(String error) {  
	            // 上传失败后要做到工作  
				//error.
			//	Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
				isPulling = false;
	        }  
		});
	}
	@SuppressWarnings("unchecked")
	private void updateListView(){
		if(curForumThreadItem == null)
			return;
		listPosts = ForumDataMgr.getPosts(curForumThreadItem.tid);
		if( listPosts == null || listPosts.size() == 0 ){
			asynGetPostList(curForumThreadItem.tid, prePullCount, 0);
		}
		if( listPosts != null && listPosts.size() > 0 )
			Collections.sort(listPosts);
		mAdapter.setData(curForumThreadItem, listPosts);
	}
	public void ShowToast(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	private void asynPostReply(String str,Integer attachment,String url)
	{
		if(curForumThreadItem == null)
			return;
		if(attachment != 2)
			LoadingBox.showBox(QuestionView.this, "提交中...");
		lastPostitem = new ForumPostItem();
		lastPostitem.tid = curForumThreadItem.tid;
		lastPostitem.pid = "-1";//临时ID
		lastPostitem.author = UserUtils.DataUtils.get("username");
		lastPostitem.authorid = UserUtils.DataUtils.get("uid");
		lastPostitem.subject = url;
		lastPostitem.message = str;
		lastPostitem.realname = UserUtils.DataUtils.get("realname");
		lastPostitem.gender = Integer.parseInt(UserUtils.DataUtils.get("gender")) ;
		lastPostitem.dateline = System.currentTimeMillis() / 1000;
		lastPostitem.attachment = attachment;
		ForumDataMgr.addPost(lastPostitem);
		updateListView();
		ListView lv = mPullRefreshListView.getRefreshableView();
		lv.setSelection(lv.getCount() - 1);
		
		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.ForumPostNewReplyUrl);
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		params.put("fid", curForumThreadItem.fid);
		params.put("tid", curForumThreadItem.tid);
		params.put("message", str);
		params.put("typeid", "0");
		params.put("tag", "");
		params.put("subject", url);
		params.put("touid", curForumThreadItem.authorid);
		params.put("realname", UserUtils.DataUtils.getNickName());
		params.put("attachment", attachment.toString());
		
		curForumThreadItem.replies ++;
		FragmentPage2.updateThreadReply();
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				String pid = content.replace("\r\n", "");
				pid = pid.substring(1);
				int ipid = Integer.parseInt(pid);
				if(ipid >= 0)
				{
					ForumDataMgr.delPostInDic(lastPostitem);
					lastPostitem.pid = pid;
					ForumDataMgr.addPostInDic(lastPostitem);
					ShowToast("回复成功");
				}
				else
					ShowToast("回复失败");
			}
			@Override  
	        public void onFailure(String error) {  
				ShowToast("回复失败");
				LoadingBox.hideBox();
	        }  
		});
	}
    
    @Override
   	public void onResume()
   	{
   		super.onResume();
   		if(CameraCroperAct.sOutBitmap != null){
   			uplaod(CameraCroperAct.sOutBitmap);
   			CameraCroperAct.sOutBitmap = null;//用完记得设置
   		}
   	}
    private void uplaod(Bitmap bm){
    	
    	if( bm == null){
    		return;
    	}
    	LoadingBox.showBox(QuestionView.this, "提交中...");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] datas = baos.toByteArray();
	    ImageDataMgr.sUpLoadMgr.addLoad(datas, 1, mLoadHandler);
    }
    private void asynCreatePostData(String tid){

    	if(curForumThreadItem == null)
			return;
    	if(!UserUtils.DataUtils.isLogined()){
			GlobalUtility.Func.ShowToast("您尚未登录！");
			return;
		}

    	My.ThreadItem threadItem = My.getThreadItem(tid);
		if(threadItem == null){
			return;
		}
		int raelpos = mAdapter.curSelectedId;
		if(raelpos < 0 || raelpos >= listPosts.size()){
			return;
		}
		ForumPostItem fpitem = listPosts.get(raelpos);
		if(!My.PostItem.checkNextIdx(fpitem.pid)){
			GlobalUtility.Func.ShowToast("已加入了我的知识库");
			return;
		}
		
		StringBuilder sb = new StringBuilder("");
		sb.append(curForumThreadItem.subject);
		sb.append(String.format("[hr]%s" , curForumThreadItem.message/*.isEmpty() ? " " : curForumThreadItem.message*/));
		sb.append(String.format("[hr]%s" , fpitem.attachment == 2 ?fpitem.subject : fpitem.message));
		String newmsgs = sb.toString();

		HttpCommon.postParams params = new HttpCommon.postParams(GlobalUtility.Config.BJB_PostMyPostDataUrl);
		
		params.put("uid", UserUtils.DataUtils.get("uid"));
		params.put("username", UserUtils.DataUtils.get("username"));
		params.put("password", UserUtils.DataUtils.get("password"));
		
		params.put("subtype", Integer.toString(threadItem.subtype));
		params.put("subname", threadItem.subname);
		params.put("message", newmsgs);
		
		String _realtid = threadItem.tid;
		params.put("tid", _realtid);
		params.put("fromtid", tid);
		
		String _realpid = My.PostItem.getStrNextIdx();
		params.put("pid", _realpid);
		params.put("frompid", fpitem.pid);
		
		My.PostItem pitem = new My.PostItem();
		pitem.tid = _realtid;
    	pitem.pid = _realpid;
    	pitem.message = newmsgs;
    	pitem.dateline = System.currentTimeMillis();
    	pitem.from = fpitem.pid;
		My.addPostItem(pitem);
		
		LoadingBox.showBox( this, "添加中...");
		HttpUtils.post(this,  params, new HttpCommon.HandlerInterface()
		{
			@Override
			public void onSuccess(String content)
			{
				LoadingBox.hideBox();
				if(content.contains("__succ__")){
					GlobalUtility.Func.ShowToast("加入我的知识库成功");
				}
				else{
					GlobalUtility.Func.ShowToast("已加入了我的知识库");
				}
			}
			@Override  
	        public void onFailure(String error) {  
				LoadingBox.hideBox();
	        }  
		});
	}
    @Override 
	protected void onDestroy(){
		super.onDestroy();
		sActivity = null;
		curTid = "";
		curForumThreadItem = null;
	}
    static public void show(Context _c,String _tid, boolean _isshow){
		if(_c != null){
			curTid = _tid;
			isshowsoftinput = _isshow;
			Intent intent = new Intent(_c, QuestionView.class);
			_c.startActivity(intent);
		}
	}
	public LoadHandler mLoadHandler = new LoadHandler();
    class LoadHandler extends Handler  
    {  
        /** 
         * 接受子线程传递的消息机制 
         */  
        @Override  
        public void handleMessage(Message msg)  
        {  
            super.handleMessage(msg);  
            int what = msg.what;  
            
            switch (what)  
            {                  
                case 1:  {  
                	int tp = msg.arg1;
                	if(tp == 1){
                		String rp = mEdit.getText().toString();
    					mEdit.setText("");
                		asynPostReply(rp, 2, GlobalUtility.Config.ImageMainUrl + msg.obj.toString());
                	}
                	break;
                }  
                case 2:  {  
                	updateListView();
                	break;
                }  
                case 3:  {  
                	asynCreatePostData(msg.obj.toString());
                	break;
                }  
            }  
        }  
    }  
}