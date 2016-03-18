package com.yxkj.jyb;

import com.yxkj.jyb.ui.CropImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ImageCroper extends Activity{

	private CropImageView mView = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.imagecroper);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				ImageCroper.this.finish();
			}  
		});
		
		mView = (CropImageView) findViewById(R.id.cropimage);
		  //设置资源和默认长宽
		  mView.setDrawable(getResources().getDrawable(R.drawable.button1), 500,500);
		  //调用该方法得到剪裁好的图片
		//  
		  
		  findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Bitmap mBitmap= mView.getCropImage();
					ImageView looker = (ImageView)findViewById(R.id.looker);
					looker.setImageBitmap(mBitmap);
				}  
			});
		
//		LayoutParams lp = new LayoutParams(480, 600);  
//		 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);  
//		 CropImageView   my = new CropImageView(this, bitmap);  
//	        LinearLayout layout = new LinearLayout(this);  
//	  
//	        layout.addView(my);  
//	        layout.setTop(50); 
//	        
//	        this.addContentView(layout, lp);  
    }  
	
	
	static public void show(Context _c){
		if(_c != null){
			Intent intent = new Intent(_c, ImageCroper.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
	}
}