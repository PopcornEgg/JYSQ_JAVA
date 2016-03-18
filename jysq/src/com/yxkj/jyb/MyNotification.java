package com.yxkj.jyb;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class MyNotification extends FragmentActivity
{
	private Activity mActivity;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mynotification);
        mActivity = this;
		
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mActivity.finish();
			}  
		});
    }  
   
	public static void show(Context _c){
		if(_c != null){
			Intent intent = new Intent(_c, MyNotification.class);
			_c.startActivity(intent);
		}
	}
}