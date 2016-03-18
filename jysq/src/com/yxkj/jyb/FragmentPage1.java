package com.yxkj.jyb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("InflateParams") public class FragmentPage1 extends Fragment{
	public static Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = container.getContext();
		View view = inflater.inflate(R.layout.fragment_serach, null);	
		Button button=(Button)view.findViewById(R.id.textSearch);
		button.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(context, Page1_searchview.class);
				//intent.putExtra("curTid", item.tid);
				startActivity(intent);
			}
		});
		view.findViewById(R.id.my).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				MyThreads.show(context);
			}
		});
		
		return view;		
	}
	@Override
	public void onResume()
	{
		super.onResume();
		Plugins.onResume(context);
//		
//		if(CameraCroperAct.sOutBitmap != null){
//			testview.setImageBitmap(CameraCroperAct.sOutBitmap);
//		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		Plugins.onPause(context);
	}
	

}
