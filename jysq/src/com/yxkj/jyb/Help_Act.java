package com.yxkj.jyb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Help_Act extends Activity{

	static int sIdx = -1;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.help_act);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Help_Act.this.finish();
			}  
		});
		
		TabsMgr.helpInfo hi = TabsMgr.helpInfo.getItem(sIdx);
		if(hi == null){
			return;
		}
		
		TextView title = (TextView)findViewById(R.id.title);
		title.setText(hi.title);
		TextView info = (TextView)findViewById(R.id.info);
		info.setText(hi.info);
    }  
	
	static public void show(Context _c,int idx){
		sIdx = idx;
		if(_c != null){
			Intent intent = new Intent(_c, Help_Act.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sIdx = -1;
	}
}