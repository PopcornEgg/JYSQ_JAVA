package com.yxkj.jyb;

import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.viewpagerindicator.TabPageIndicator;

public class FragmentPage_bjb extends Fragment{
	
	private static final String[] CONTENT = new String[] { "֪ʶ��", "�ҵ�֪ʶ"};

	public static Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_bjb, null);
		context = container.getContext();

		view.getContext().setTheme(R.style.Theme_PageIndicatorDefaults);
		//@style/Theme.PageIndicatorDefaults

		//ViewPager��adapter
        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getChildFragmentManager());

        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        
        //�������Ҫ��ViewPager���ü�������indicator���þ�����
        indicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				//Toast.makeText(context, CONTENT[arg0], Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
        indicator.setVisibility(1);
        return view;
	}
	
    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	String _name = CONTENT[position % CONTENT.length];
        	if(_name.equals("֪ʶ��"))
        		return FragmentPage_bjb_sysitem.newInstance(_name);
        	else if(_name.equals("�ҵ�֪ʶ"))
        		return FragmentPage_bjb_myitem.newInstance(_name);
        	return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
    
	@Override
	public void onResume()
	{
		super.onResume();
		Plugins.onResume(context);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		Plugins.onPause(context);
	}
}
