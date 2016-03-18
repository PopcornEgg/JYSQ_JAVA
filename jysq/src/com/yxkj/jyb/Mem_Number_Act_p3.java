package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Mem_Number_Act_p3 extends Activity{
	
	private Integer[][] memNumbers = null;
	private Integer[][] ansNumbers = null;
	
	private List<View> mViewList = new ArrayList<View>();//把需要滑动的页卡添加到这个list中
	private ViewPager mViewPager = null;//viewpager 
	private int curPageCount = 0;
	private int curPageIdx = 0;
	private TextView mPageIdx = null;
	private Set<Integer> initedView = new HashSet<Integer>();
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mem_number_act_p3);
        
        memNumbers = Mem_Number_Act_p1.memNumbers;
        ansNumbers = Mem_Number_Act_p2.ansNumbers;
        if(memNumbers == null || ansNumbers == null)
        	return;
        if(memNumbers.length != ansNumbers.length)
        	return;
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Mem_Number_Act_p3.this.finish();
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
        
		mPageIdx = (TextView)findViewById(R.id.pageidx);
        randomNumbers(Mem_Number_Act_Main.difficulty);
        initViewList();
        setCurPage(0);
        
		LoadingBox.hideBox();
    }  
	private void randomNumbers(int count){
		curPageCount = count / Mem_Number_Act_Main.oneGroupCount;
	}
	private void initViewList(){
		
		LayoutInflater lf = LayoutInflater.from(this);  
		for(int i=0;i<curPageCount;i++){
			View _v = lf.inflate(R.layout.mem_number_act_p3_page, null);  
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
		int onecount = Mem_Number_Act_Main.oneGroupCount;
		
		String strTitle = String.format("%d-%d", _page*onecount+ 1, (_page + 1) * onecount);
		TextView range = (TextView)_view.findViewById(R.id.range);
		range.setText(strTitle);

		ArrayList<View> numbers = new ArrayList<View> ();
		_view.findViewsWithText(numbers, "number", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		
		//答案
		for(int i=0;i<onecount;i++){
			((TextView)numbers.get(i)).setText(memNumbers[_page][i].toString());
		}
		//我的答案
		for(int i=0;i<onecount;i++){
			TextView tv = ((TextView)numbers.get(i+onecount));
			if(ansNumbers[_page][i] < 0){
				tv.setText("");
			}else{
				tv.setText(ansNumbers[_page][i].toString());
				tv.setTextColor(ansNumbers[_page][i] == memNumbers[_page][i] ? 
						this.getResources().getColor(R.color.def_font_color_black):
							this.getResources().getColor(R.color.def_font_color_red));
			}
		}
	}
	private void setCurPage(int _page){
		curPageIdx = _page;
		updatePageIdx(_page + 1);
	}
	private void updatePageIdx(int _page){
		String str = String.format("%d/%d", _page, curPageCount);
		mPageIdx.setText(str);
	}
	static public void show(Context _c ){
		if(_c != null){
			LoadingBox.showBox(_c, "生成数据...");
			Intent intent = new Intent(_c, Mem_Number_Act_p3.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
	@Override
	public void onResume()
	{
		super.onResume();
	//	cancelTimer();
	}
}