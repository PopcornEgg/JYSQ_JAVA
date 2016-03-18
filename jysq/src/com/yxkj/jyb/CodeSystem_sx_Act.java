package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.yxkj.jyb.ImageDataMgr.DLItem;
import com.yxkj.jyb.TabsMgr.codesysdata;
import com.yxkj.jyb.TabsMgr.codesysdataTab;
import com.yxkj.jyb.ui.MyScaleAnimation;
import com.yxkj.jyb.ui.NoScrollViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;

public class CodeSystem_sx_Act extends Activity
{
	private View mView1, mView2, mView3, mView4;//需要滑动的页卡 
	private NoScrollViewPager mViewPager;//viewpager 
	private List<View> viewList;//把需要滑动的页卡添加到这个list中  
	private Activity mActivity;
	int selectedTime = 0;
	Timer mTimer = new Timer();
	public List<codesysdata> mCodeDataList = new ArrayList<codesysdata>();
	public Map<String, codesysdata> mChangedCodeDataMap = new HashMap<String, codesysdata>();
	
	@SuppressWarnings("serial")
	final static List<String[]> selectedRanges = new ArrayList<String[]>(){{
		//000-09
		String[] p1 = new String[]{"000","00","01","02","03","04","05","06","07","08","09"};
		add(p1);
		//0-49
		String[] p2 = new String[50];
		add(p2);
		for(Integer i=0; i < 50; i++){
			p2[i] = i.toString();
		}
		//50-99
		String[] p3 = new String[50];
		add(p3);
		for(Integer i=50; i < 100; i++){
			p3[i-50] = i.toString();
		}
		//0-99
		String[] p4 = new String[100];
		add(p4);
		for(Integer i=0; i < 100; i++){
			p4[i] = i.toString();
		}
		//全部
		String[] p5 = new String[111];
		add(p5);
		for(Integer i=0; i < p1.length; i++){
			p5[i] = p1[i];
		}
		for(Integer i=p1.length; i < p4.length + p1.length; i++){
			p5[i] = i.toString();
		}
	}};
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.codesystem_sx_act);
        mActivity = this;
		
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mActivity.finish();
			}  
		});
		
		TextView title=(TextView)findViewById(R.id.title);
		title.setText("编码熟悉:" + CodeSystem_Fra.getCurTypeName());
		
		getLayoutInflater();
		LayoutInflater lf = LayoutInflater.from(this);  
		mView1 = lf.inflate(R.layout.codesystem_sx_page1, null);  
		mView2 = lf.inflate(R.layout.codesystem_sx_page2, null);  
		mView3 = lf.inflate(R.layout.codesystem_sx_page3, null);
		mView4 = lf.inflate(R.layout.codesystem_sx_page4, null);
  
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中  
        viewList.add(mView1);  
        viewList.add(mView2);  
        viewList.add(mView3);  
        viewList.add(mView4);  
  
		PagerAdapter pagerAdapter = new PagerAdapter() {  
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1) {  
                return arg0 == arg1;  
            }  
            @Override  
            public int getCount() {  
                return viewList.size();  
            }  
            @Override  
            public void destroyItem(ViewGroup container, int position,  Object object) {  
                container.removeView(viewList.get(position));  
            }  
            @Override  
            public int getItemPosition(Object object) {  
                return super.getItemPosition(object);  
            }  
            @Override  
            public Object instantiateItem(ViewGroup container, int position) {  
            	View view = viewList.get(position);
                container.addView(view);  
                
                //这个需要注意，我们是在重写adapter里面实例化button组件的，如果你在onCreate()方法里这样做会报错的。
                if(position == 0)
                	initPage1(view);
                else if(position == 1)
                	initPage2(view);
                else if(position == 2)
                	initPage3(view);
                else if(position == 3)
                	initPage4(view);
                
                return view;  
            }  
        };  
        mViewPager = (NoScrollViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);  
        mViewPager.addOnPageChangeListener(new OnPageChangeListener(){
        	public void onPageScrolled(int arg0, float arg1, int arg2){
        	}
			public void onPageSelected(int arg0){
        		if(arg0 == 2){
        			mTimer.schedule(newTask(), 0, 1000);       // timeTask 
        			nextTask();
        		}
        		else{
        			cancelTask();
        			if(arg0 == 3){
        				showScore();
        			}
        		}
        	}
			public void onPageScrollStateChanged(int arg0){
			}
        });
    }  
	TimerTask task = null;
	private void cancelTask(){
		if(task != null)
			task.cancel();
		task = null;
	}
	private TimerTask newTask(){
		cancelTask();
		task = new TimerTask() {
	        @Override    
	        public void run() {    
	   
	            runOnUiThread(new Runnable() {      // UI thread    
	                @Override    
	                public void run() {    
	                    if(txtTime != null){
	                    	String strTime = GlobalUtility.Func.second2Min(selectedTime);
	                    	txtTime.setText(strTime);
	                    	selectedTime--;
	                    	if(selectedTime < 0){
	                    		timeOver();
	                    	}
	                    }
	                }    
	            });    
	        }    
	    };  
	    return task;
	}
    
	public LoadHandler mLoadHandler = new LoadHandler();
    class LoadHandler extends Handler  
    {  
        /** 
         * 接受子线程传递的消息机制 
         */  
        @SuppressLint("NewApi")
		@Override  
        public void handleMessage(Message msg)  
        {  
            super.handleMessage(msg);  
            int what = msg.what;  
            switch (what)  
            {                  
                case 1:  
                {  
                	DLItem dlitem = (DLItem)msg.obj;
                	if(dlitem != null){
                		String url = dlitem.url;
                		ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
            			if(imginfo != null){
            			    int tag = Integer.parseInt(dlitem.tag.toString());
                    		if(tag == 0){
                    			headTitle.setBackground(new BitmapDrawable(mActivity.getResources(), imginfo.bitmap));
                    		}else{
                    			tag--;
                    			page3Btns[tag].setBackground(new BitmapDrawable(mActivity.getResources(), imginfo.bitmap));
                    			//page3Btns[tag].setImageBitmap(imginfo.bitmap);
                    		}
            			}
            			else
            				Log.i("jiyibang_FragmentPage2:handleMessage", "下载成功;加载失败 : " + url);
                	}
                	else
                		Log.i("jiyibang_FragmentPage2:handleMessage", "DLItem == null");
                }  
            }  
        }  
    }  
    int curRightIdx = 0;
    int[] curCodeIdxs = new int[6];
    long setAnswerTime = 0;
    static int maxShowtypes = 2;
    private void nextTask(){

    	//出题方式 0编码 1名字 2图片
    	List<Integer> showtypes = GlobalUtility.Func.getRandomNumbers(maxShowtypes);
    	int showtp = new Random().nextInt(maxShowtypes);
    	showtypes.remove(showtp);
    	
    	//生成正确答案
    	curRightIdx = new Random().nextInt(page3Btns.length);
    	int curRightCodeIdx = new Random().nextInt(mCodeDataList.size());
    	List<Integer> numlist = GlobalUtility.Func.getRandomNumbers(mCodeDataList.size());
    	numlist.remove(curRightCodeIdx);//移除正确答案
    	
    	//设置问题
    	codesysdata rightcode = mCodeDataList.get(curRightCodeIdx);
        if (showtp == 0){
        	scaleQuestion(rightcode.cname, 1);
        }else if(showtp == 1){
        	scaleQuestion(rightcode.name, 1);
        }
        else if(showtp == 2){
        	String url = GlobalUtility.Config.ImageMainUrl + rightcode.image +"/scaling";
        	scaleQuestion(url, 0);
        }
    	//设置答案
    	showtp = showtypes.get(new Random().nextInt(showtypes.size()));
    	for(int i=0; i<page3Btns.length; i++){
    		 //生成不重读答案
    		if(i == curRightIdx){
    			SetCode(i, showtp, rightcode);
    			curCodeIdxs[i] = curRightCodeIdx;
    		}else{
    			int answerIdx =new Random().nextInt(numlist.size());
	            int sIdx = numlist.get(answerIdx);
	            numlist.remove(answerIdx);
	    		SetCode(i, showtp, mCodeDataList.get(sIdx));
	    		curCodeIdxs[i] = sIdx;
    		}
    	}
    	setAnswerTime = System.currentTimeMillis();
    }
   //设置编码显示
    // cname 是否显示名字，如果不显示名字，那么显示它的标签图像
    private void SetCode(int idx, int showtp, codesysdata code)
    {
    	 if (showtp == 0){
    		 scaleAnswer(idx, code.cname, 1);
         }else if(showtp == 1){
        	 scaleAnswer(idx, code.name, 1);
         }
         else if(showtp == 2){
        	 String url = GlobalUtility.Config.ImageMainUrl + code.image +"/scaling";
         	 scaleAnswer(idx, url, 0);
         }
    }
    private void scaleAnswer(int idx, String url, int iscname){
        MyScaleAnimation back_scale = new MyScaleAnimation(1, 0f, 1, 1f,  
            Animation.RELATIVE_TO_SELF, 0.5f,  
            Animation.RELATIVE_TO_SELF, 0.5f); 
        back_scale.iarg0 = idx;
        back_scale.iarg1 = iscname;
        back_scale.sarg0 = url;
        back_scale.setDuration(150);
        back_scale.setAnimationListener(new Animation.AnimationListener() {  
            @Override  
            public void onAnimationStart(Animation animation) {  
            }  
            @Override  
            public void onAnimationRepeat(Animation animation) {  
            }  
            @SuppressLint("NewApi")
			@Override  
            public void onAnimationEnd(Animation animation) {  
            	MyScaleAnimation front_scale = new MyScaleAnimation(0, 1f, 1, 1f,  
    	            Animation.RELATIVE_TO_SELF, 0.5f,  
    	            Animation.RELATIVE_TO_SELF, 0.5f);
            	front_scale.setDuration(150);
            	 
            	MyScaleAnimation sanim = (MyScaleAnimation)animation;
            	int idx = sanim.iarg0;
            	int iscname = sanim.iarg1;
            	String url = sanim.sarg0;
            	
            	if(iscname > 0){
            		page3Btns[idx].setText(url);
                	page3Btns[idx].setBackgroundResource(R.color.font_color_gray);
            	}
            	else{
            		page3Btns[idx].setText("");
            		ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
        			if(imginfo != null){
        				page3Btns[idx].setBackground(new BitmapDrawable(mActivity.getResources(), imginfo.bitmap));
        			}
        			else{
        				page3Btns[idx].setBackgroundResource(R.color.font_color_gray);
        				ImageDataMgr.sDownLoadMgr.addLoad(url, idx + 1, 1, mLoadHandler);
        			}
            	}
            	
    			page3Btns[idx].startAnimation(front_scale);  
            }  
        });  
        page3Btns[idx].startAnimation(back_scale);  
    }
    private void scaleQuestion(String url, int iscname){
    	MyScaleAnimation back_scale = new MyScaleAnimation(1, 0f, 1, 1f,  
                Animation.RELATIVE_TO_SELF, 0.5f,  
                Animation.RELATIVE_TO_SELF, 0.5f); 
            back_scale.iarg0 = iscname;
            back_scale.sarg0 = url;
            back_scale.setDuration(150);
            back_scale.setAnimationListener(new Animation.AnimationListener() {  
                @Override  
                public void onAnimationStart(Animation animation) {  
                }  
                @Override  
                public void onAnimationRepeat(Animation animation) {  
                }  
                @SuppressLint("NewApi")
				@Override  
                public void onAnimationEnd(Animation animation) {  
                	MyScaleAnimation front_scale = new MyScaleAnimation(0, 1f, 1, 1f,  
        	            Animation.RELATIVE_TO_SELF, 0.5f,  
        	            Animation.RELATIVE_TO_SELF, 0.5f);
                	front_scale.setDuration(150);
                	 
                	MyScaleAnimation sanim = (MyScaleAnimation)animation;
                	int iscname = sanim.iarg0;
                	String url = sanim.sarg0;
                	
                	if(iscname > 0){
                		headTitle.setText(url);
                    	headTitle.setBackgroundResource(R.color.font_color_gray);
                	}
                	else{
                		headTitle.setText("");
                		ImageDataMgr.ImageItem imginfo = ImageDataMgr.sImageCacheMgr.get(url);
            			if(imginfo != null){
            				headTitle.setBackground(new BitmapDrawable(mActivity.getResources(), imginfo.bitmap));
            			}
            			else{
            				headTitle.setBackgroundResource(R.color.font_color_gray);
            				ImageDataMgr.sDownLoadMgr.addLoad(url, 0, 1, mLoadHandler);
            			}
                	}
                	
        			headTitle.startAnimation(front_scale);  
                }  
            });  
            headTitle.startAnimation(back_scale);  
    }
    private void timeOver(){
    	cancelTask();
    	mViewPager.setCurrentItem(3, true);
    }
    private void showScore(){
		codesysdataTab _codesysdataTab = TabsMgr.codesysdataTab.getItem_by_Name(CodeSystem_Fra.getCurTypeName());
		List<codesysdata> list = new ArrayList<codesysdata>();
		for(Map.Entry<String, codesysdata> entry:mChangedCodeDataMap.entrySet()){ 
		     list.add(entry.getValue());
		} 
		//计算成绩
		float newtime = 0;
		float oldtime = 0;
		
        for( int i = 0; i < list.size(); ++i )
        {
        	codesysdata newdata = list.get(i);
        	newtime += newdata.time;
        	codesysdata olddata = _codesysdataTab.mItemMap.get(newdata.cname);
        	oldtime += olddata.time;
        	olddata.time = newdata.time;
        }
        //存储本地
        _codesysdataTab._Save();
        
        String s = "";
        if(oldtime > newtime)
        	s = String.format("-%.02f" , (oldtime - newtime) / list.size());
        else
        	s = String.format("+%.02f" , (newtime - oldtime) / list.size());
        String ret = String.format("训练前:%.02f\n训练后:%.02f\n成 绩:%s", oldtime / list.size(), newtime / list.size(), s);
        mScoreText.setText(ret);
        
		MyAdapterPage4 page4Adapter = new MyAdapterPage4(CodeSystem_sx_Act.this);
		page4Adapter.setData(list);
		mScoreList.setAdapter(page4Adapter);
    }
	private void initPage1(View view){
		OnClickListener clickl = new OnClickListener() {
			public void onClick(View v) {
				selectedTime = Integer.parseInt(v.getTag().toString()); 
				if(CodeSystem_Fra.getCurTypeName() == "数字"){
					mViewPager.setCurrentItem(1, true);
				}
				else if(CodeSystem_Fra.getCurTypeName() == "字母"){
					String[] sels = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
					codesysdataTab _codesysdataTab = TabsMgr.codesysdataTab.getItem_by_Name(CodeSystem_Fra.getCurTypeName());
					if(_codesysdataTab == null)
						Log.i("CodeSystem_sx_Act","CodeSystem_sx_Act._codesysdataTab == null");
					mCodeDataList.clear();
					for(int i=0; i<sels.length; i++){
						if(_codesysdataTab.mItemMap.containsKey(sels[i]))
							mCodeDataList.add(_codesysdataTab.mItemMap.get(sels[i]));
					}
					mViewPager.setCurrentItem(2, true);
				}
			}
		};
      	view.findViewById(R.id.Button01).setOnClickListener(clickl);
      	view.findViewById(R.id.Button02).setOnClickListener(clickl);
      	view.findViewById(R.id.Button03).setOnClickListener(clickl);
      	view.findViewById(R.id.Button04).setOnClickListener(clickl);
      	view.findViewById(R.id.Button05).setOnClickListener(clickl);
	}
	private void initPage2(View view){
		OnClickListener clickl = new OnClickListener() {
			public void onClick(View v) {
				int idx = Integer.parseInt(v.getTag().toString()); 
				String[] sels = selectedRanges.get(idx);
				codesysdataTab _codesysdataTab = TabsMgr.codesysdataTab.getItem_by_Name(CodeSystem_Fra.getCurTypeName());
				if(_codesysdataTab == null)
					Log.i("CodeSystem_sx_Act","CodeSystem_sx_Act._codesysdataTab == null");
				mCodeDataList.clear();
				for(int i=0; i<sels.length; i++){
					if(_codesysdataTab.mItemMap.containsKey(sels[i]))
						mCodeDataList.add(_codesysdataTab.mItemMap.get(sels[i]));
				}
				mViewPager.setCurrentItem(2, true);
			}
		};
      	view.findViewById(R.id.Button01).setOnClickListener(clickl);
      	view.findViewById(R.id.Button02).setOnClickListener(clickl);
      	view.findViewById(R.id.Button03).setOnClickListener(clickl);
      	view.findViewById(R.id.Button04).setOnClickListener(clickl);
      	view.findViewById(R.id.Button05).setOnClickListener(clickl);
	}
	Button[] page3Btns = new Button[6];
	Button headTitle = null;
	TextView txtTime = null;
	private void initPage3(View view){
		
		txtTime = (TextView)view.findViewById(R.id.time);
		String strTime = GlobalUtility.Func.second2Min(selectedTime);
    	txtTime.setText(strTime);
    	
		OnClickListener clickl = new OnClickListener() {
			public void onClick(View v) {
				int idx = Integer.parseInt(v.getTag().toString()); 
				codesysdata code = mCodeDataList.get(curCodeIdxs[idx]);
				codesysdata changecode = null;
				if(mChangedCodeDataMap.containsKey(code.cname))
					changecode = mChangedCodeDataMap.get(code.name);
				else{
					changecode = new codesysdata(code);
					mChangedCodeDataMap.put(code.name, changecode);
				}
				float taketime = (float)(System.currentTimeMillis() - setAnswerTime) / 1000.0f;
				if(curRightIdx == idx){//正确
					changecode.time = (changecode.time  + taketime) / 2.0f;
					nextTask();
				}else{
					changecode.time = (changecode.time  + 10.0f) / 2.0f;
				}
			}
		};
		
		page3Btns[0] = (Button)view.findViewById(R.id.Button01);
		page3Btns[0].setOnClickListener(clickl);
		//LayoutParams lp1 = btn1.getLayoutParams();
		//lp1.height = lp1.width;
		page3Btns[1] = (Button)view.findViewById(R.id.Button02);
		page3Btns[1].setOnClickListener(clickl);
		page3Btns[2] = (Button)view.findViewById(R.id.Button03);
		page3Btns[2].setOnClickListener(clickl);
		page3Btns[3] = (Button)view.findViewById(R.id.Button04);
		page3Btns[3].setOnClickListener(clickl);
		page3Btns[4] = (Button)view.findViewById(R.id.Button05);
		page3Btns[4].setOnClickListener(clickl);
		page3Btns[5] = (Button)view.findViewById(R.id.Button06);
		page3Btns[5].setOnClickListener(clickl);

		headTitle = (Button)view.findViewById(R.id.headTitle);
	}
	
	ListView mScoreList = null;
	TextView mScoreText = null;
	private void initPage4(View view){
		mScoreList = (ListView)view.findViewById(R.id.list);
        mScoreText = (TextView)view.findViewById(R.id.score);
	}
	public class MyAdapterPage4 extends BaseAdapter{
        //用来接收传递过来的Context上下文对象
	    private Context context;
	    public List<codesysdata> mDataList = null;
	    //构造函数
	    public MyAdapterPage4(Context context){
	        this.context = context;
	    }
	    public void setData(List<codesysdata> _d){
	    	mDataList = _d;
			notifyDataSetChanged();
		}
	    @Override
	    public int getCount() {
	        return mDataList!=null ? mDataList.size() : 0;
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
	    //未优化的getView，这部分可以使用recycle()释放内存、或者BitmapFacotry.Options缩小，或者软引用，或者控制图片资源大小等等很多方法，找时间专门写
	    @SuppressLint("InflateParams") @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	
	    	if( context == null || mDataList == null)
				return null;
	    	
	    	if( convertView == null){
	    		convertView = LayoutInflater.from(context).inflate(R.layout.codesystem_sx_page4_item,null);   
	    		Page4ImteHolder vh = new Page4ImteHolder();
	    		vh.text01 = (TextView)convertView.findViewById(R.id.TextView01);
	    		vh.text02 = (TextView)convertView.findViewById(R.id.TextView02);
	    		vh.text03 = (TextView)convertView.findViewById(R.id.TextView03);
	    		vh.text04 = (TextView)convertView.findViewById(R.id.TextView04);
	    		convertView.setTag(vh);
	    	}
	    	
	    	codesysdata data = mDataList.get(position);
	    	if(data == null)
	    		 return convertView;
	    	Page4ImteHolder vh = (Page4ImteHolder)convertView.getTag();
	    	
	    	vh.text01.setText(data.cname);
	    	vh.text02.setText(data.name);
	    	vh.text03.setText(String.format("%.02f", data.time));
	    	vh.text04.setText(String.format("%.02f", 10.0f - data.time));
	        return convertView;
	    }
	    public class Page4ImteHolder
	    {
	    	public TextView text01;
	    	public TextView text02;
	    	public TextView text03;
	    	public TextView text04;
	    }
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		cancelTask();
		if(mTimer != null)
			mTimer.cancel();
	}
	public static void show(Context _c){
		if(_c != null){
			Intent intent = new Intent(_c, CodeSystem_sx_Act.class);
			_c.startActivity(intent);
		}
	}
}