package com.yxkj.jyb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Mem_Score_Act extends Activity{
	
	static private int    from = 0;
	static private String title = "";
	static private String txt1 = "";
	static private String txt2 = "";
	static private String txt3 = "";
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mem_score_act);
        
        findViewById(R.id.back).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Mem_Score_Act.this.finish();
			}  
		});
		
		findViewById(R.id.check).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(from == 1){//Êý×Ö¼ÇÒä
					Mem_Number_Act_p3.show(Mem_Score_Act.this);
				}else if(from == 2){//´Ê»ã¼ÇÒä
					Mem_Words_Act_p3.show(Mem_Score_Act.this);
				}
			}  
		});
		
		((TextView)findViewById(R.id.title)).setText(title);
		((TextView)findViewById(R.id.txt1)).setText(txt1);
		((TextView)findViewById(R.id.txt2)).setText(txt2);
		((TextView)findViewById(R.id.txt0)).setText(txt3);
    }  
	
	
	static public void show(Context _c,int _f,String _title, String _t1,String _t2,String _t3){
		if(_c != null){
			from = _f;
			title = _title;
			txt1 = _t1;
			txt2 = _t2;
			txt3 = _t3;
			Intent intent = new Intent(_c, Mem_Score_Act.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
}