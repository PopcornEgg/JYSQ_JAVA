package com.yxkj.jyb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Mem_Words_Act_Main extends Activity{
	
	static public final int scoreFrom = 2;
	static public final String scoreTitle = "¥ ª„º«“‰";
	
	static public final int oneGroupCount = 20;
	public static final String[] listNames = {"20","40","80","160","320"};
	static public int difficulty = 0;
	static public int type = 0;
	
	private View[] listIVs = null; 
	//private ScrollView mListView = null;
	private TextView diff = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mem_words_act_main);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Mem_Words_Act_Main.this.finish();
			}  
		});
		
        Button button=(Button)findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Mem_Words_Act_p1.show(Mem_Words_Act_Main.this);
			}  
		});   
		
		diff = (TextView)findViewById(R.id.diff);
		//mListView = (ScrollView)findViewById(R.id.sv);
		LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		listIVs = new View[listNames.length];
		for(int i=0;i<listNames.length;i++){
			View _v = LayoutInflater.from(this).inflate(R.layout.mem_words_act_main_item,null);
			_v.setTag(i);
			_v.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					int idx = Integer.parseInt(v.getTag().toString());
					difficulty = Integer.parseInt(listNames[idx]);
					diff.setText(listNames[idx]);
					updateIVs(idx);
				} 
			});
			listIVs[i] = _v.findViewById(R.id.img);
			((TextView)_v.findViewById(R.id.txt)).setText(listNames[i]);
			_v.findViewById(R.id.sp).setVisibility(i==0 ? View.GONE : View.VISIBLE);
			ll.addView(_v);
		}
		difficulty = Integer.parseInt(listNames[0]);
		diff.setText(listNames[0]);
		updateIVs(0);
		
		//µπº∆ ±
//		new CountDownTimer(30000, 1000) {
//		   public void onTick(long millisUntilFinished) {
//		   }
//		   public void onFinish() {
//		   }
//		}.start();
    }  
	private void updateIVs(int idx){
		for(int i=0;i<listIVs.length;i++){
			listIVs[i].setVisibility(i==idx ? View.VISIBLE : View.INVISIBLE);
		}
	}
	static public void show(Context _c ){
		if(_c != null){
			Intent intent = new Intent(_c, Mem_Words_Act_Main.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
}