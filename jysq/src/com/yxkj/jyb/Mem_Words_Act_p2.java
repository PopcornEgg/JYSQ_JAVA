package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Mem_Words_Act_p2 extends Activity{
	
	static final String TAG = "Mem_Number_Act_p2";
	static public String[][] ansContents = null;
	
	private Button time = null;
	private Timer mTimer = new Timer();
	private List<View> mViewList = new ArrayList<View>();//把需要滑动的页卡添加到这个list中
	private EditText[][] mEditTexts = null;
	private ViewPager mViewPager = null;//viewpager 
	private int curPageCount = 0;
	private int curPageIdx = 0;
	private int curGroupCount = 0;
	private int curSelectedIdx = 0;
	private TextView mPageIdx = null;
	private Set<Integer> initedView = new HashSet<Integer>();
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mem_words_act_p2);
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Mem_Words_Act_p2.this.finish();
			}  
		});
		
        findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				showScore();
				Mem_Words_Act_p2.this.finish();
			}  
		}); 
        
        findViewById(R.id.pre).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(curPageIdx > 0){
					curPageIdx--;
					setCurPage(curPageIdx);
					mViewPager.setCurrentItem(curPageIdx);
				}
			}  
		}); 
        findViewById(R.id.next).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(curPageIdx < curPageCount - 1){
					curPageIdx++;
					setCurPage(curPageIdx);
					mViewPager.setCurrentItem(curPageIdx);
				}
			}  
		}); 
        
        time = (Button)findViewById(R.id.time);
		mPageIdx = (TextView)findViewById(R.id.pageidx);
        randomNumbers(Mem_Words_Act_Main.difficulty);
        initViewList();
        setCurPage(0);
        curTime = 0;
		mTimer.schedule(newTimerTask(), 0, 1000);       // timeTask 
		
		LoadingBox.hideBox();
    }  
	private void randomNumbers(int count){
		int onecount = Mem_Words_Act_Main.oneGroupCount;
		curGroupCount = count / onecount;
		ansContents = new String[curGroupCount][onecount];
		for(int i=0;i<curGroupCount;i++){
			for(int j=0;j<onecount;j++){
				ansContents[i][j] = "";
			}
		}
		curPageCount = (int)Math.ceil((double)curGroupCount / 2.0);
		mEditTexts = new EditText[curGroupCount][onecount];
	}
	private void initViewList(){
		
		LayoutInflater lf = LayoutInflater.from(this);  
		for(int i=0;i<curPageCount;i++){
			View _v = lf.inflate(R.layout.mem_words_act_p2_page, null);  
			mViewList.add(_v);
		}
		PagerAdapter pagerAdapter = new PagerAdapter() {  
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1) {  
                return arg0 == arg1;  
            }  
            @Override  
            public int getCount() {  
                return mViewList.size();  
            }  
            @Override  
            public void destroyItem(ViewGroup container, int position,  Object object) {  
                container.removeView(mViewList.get(position));  
            }  
            @Override  
            public int getItemPosition(Object object) {  
                return super.getItemPosition(object);  
            }  
            @Override  
            public Object instantiateItem(ViewGroup container, int position) {  
            	View view = mViewList.get(position);
                container.addView(view);  
                //这个需要注意，我们是在重写adapter里面实例化button组件的，如果你在onCreate()方法里这样做会报错的。
                initPage(position, view);
               // Log.d(TAG, "position = " + position);
                return view;  
            }  
        };  
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);  
        mViewPager.addOnPageChangeListener(new OnPageChangeListener(){
        	public void onPageScrolled(int arg0, float arg1, int arg2){
        	}
			public void onPageSelected(int arg0){
				setCurPage(arg0);
        	}
			public void onPageScrollStateChanged(int arg0){
			}
        });
	}
	private void initPage(int _page, View _view){
		if(initedView.contains(_page))
			return;
		initedView.add(_page);

		int onecount = Mem_Words_Act_Main.oneGroupCount;
		int idx = _page*2;
		
		String strTitle = String.format("%d-%d", idx*onecount + 1, (idx + 1) * onecount);
		TextView title1 = (TextView)_view.findViewById(R.id.title1);
		title1.setText(strTitle);

		ArrayList<View> numbers = new ArrayList<View> ();
		_view.findViewsWithText(numbers, "number", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		
		for(int i=0;i<onecount;i++){
			EditText dt = (EditText)numbers.get(i);
			//if(dt.getOnFocusChangeListener() != null)
			//	break;
			mEditTexts[idx][i] = dt;
			dt.setTag(i);
			//dt.setText(ansContents[idx][i].toString());
			dt.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					setNextFocus();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			
			dt.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
			    @Override  
			    public void onFocusChange(View v, boolean hasFocus) {  
			    	if(hasFocus) {
					// 此处为得到焦点时的处理内容
			    		curSelectedIdx = Integer.parseInt( v.getTag().toString());
					} else {
					// 此处为失去焦点时的处理内容
					}
			    }
			});
		}
		//默认选中第一个
		if(_page == 0){
			InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
	        mEditTexts[0][0].requestFocus();
		}
		
		idx++;
		if(curGroupCount > idx){
			_view.findViewById(R.id.ll2).setVisibility(View.VISIBLE);
			strTitle = String.format("%d-%d", idx*onecount + 1, (idx + 1) * onecount);
			TextView title2 = (TextView)_view.findViewById(R.id.title2);
			title2.setText(strTitle);
			for(int i=0;i<onecount;i++){
				EditText dt = (EditText)numbers.get(i + onecount);
				//if(dt.getOnFocusChangeListener() != null)
				//	break;
				dt.setTag(i + onecount);
				mEditTexts[idx][i] = dt;
				//dt.setText(ansContents[idx][i].toString());
				dt.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						setNextFocus();
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
					@Override
					public void afterTextChanged(Editable s) {
					}
				});
				dt.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
				    @Override  
				    public void onFocusChange(View v, boolean hasFocus) {  
				    	if(hasFocus) {
						// 此处为得到焦点时的处理内容
				    		curSelectedIdx = Integer.parseInt( v.getTag().toString());
						} else {
						// 此处为失去焦点时的处理内容
						}
				    }
				});
			}
		}else{
			_view.findViewById(R.id.ll2).setVisibility(View.GONE);
		}
	}
	private void setNextFocus(){
		curSelectedIdx++;
		EditText dt = null;
		int idx = curPageIdx*2;
		int onecount = Mem_Words_Act_Main.oneGroupCount;
		if(curSelectedIdx >= onecount*2)
			curSelectedIdx = 0;
		if(curSelectedIdx >= onecount){
			idx++;
			if(curGroupCount > idx){
				dt = mEditTexts[idx][curSelectedIdx - onecount];
			}else{
				curSelectedIdx = 0;
				dt = mEditTexts[idx-1][curSelectedIdx];
			}
		}else{
			dt = mEditTexts[idx][curSelectedIdx];
		}
		dt.requestFocus();
	}
	private void setCurPage(int _page){
		curPageIdx = _page;
		updatePageIdx(_page + 1);
	}
	private void updatePageIdx(int _page){
		String str = String.format("%d/%d", _page, curPageCount);
		mPageIdx.setText(str);
	}
	TimerTask timertask = null;
	public static int curTime = 0;
	private void cancelTimer(){
		if(timertask != null)
			timertask.cancel();
		timertask = null;
	}
	private TimerTask newTimerTask(){
		cancelTimer();
		timertask = new TimerTask() {
	        @Override    
	        public void run() {    
	            runOnUiThread(new Runnable() {    
	                @Override    
	                public void run() {    
	                    if(time != null){
	                    	curTime++;
	                    	String strTime = GlobalUtility.Func.second2Hour(curTime);
	                    	time.setText(strTime);
	                    }
	                }    
	            });    
	        }    
	    };  
	    return timertask;
	}
	private void showScore(){
		int onecount = Mem_Words_Act_Main.oneGroupCount;
		String buffer = "";
		for(int i=0;i<curGroupCount;i++){
			for(int j=0;j<onecount;j++){
				if( mEditTexts[i][j] != null)
					buffer = mEditTexts[i][j].getText().toString();
				else
					buffer="";
				ansContents[i][j] = buffer;
			}
		}
		
		int allCount = curGroupCount * onecount;
		int rightCount = 0;
		for(int i=0;i<curGroupCount;i++){
			for(int j=0;j<onecount;j++){
				if(Mem_Words_Act_p1.memContents[i][j].equals(ansContents[i][j])){
					rightCount++;
				}
			}
		}
		
		String strTxt1 = String.format("记忆%d个数字", allCount);
		String strTxt2 = String.format("耗时%s", GlobalUtility.Func.second2Hour(curTime + Mem_Words_Act_p1.curTime));
		String strTxt3 = String.format("正确率%.1f%%", (float)rightCount / (float)allCount * 100.0f);
		Mem_Score_Act.show(Mem_Words_Act_p2.this, Mem_Words_Act_Main.scoreFrom,
				Mem_Words_Act_Main.scoreTitle, strTxt1, strTxt2, strTxt3);
	}
	
	static public void show(Context _c ){
		if(_c != null){
			LoadingBox.showBox(_c, "生成数据...");
			Intent intent = new Intent(_c, Mem_Words_Act_p2.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		cancelTimer();
	}
	@Override
	public void onResume()
	{
		super.onResume();
	//	cancelTimer();
	}
}