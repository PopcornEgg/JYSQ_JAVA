package com.yxkj.jyb;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutApp extends Activity{
	private Activity mActivity;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.about_app);
        mActivity = this;
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mActivity.finish();
			}  
		});
	}
	static public void showSelf(Context _c){
		if(_c != null){
			Intent intent = new Intent(_c, AboutApp.class);
			_c.startActivity(intent);
		}
	}
}