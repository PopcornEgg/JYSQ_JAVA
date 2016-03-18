package com.yxkj.jyb.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;

public class MyScaleAnimation extends ScaleAnimation {

	public int iarg0 = -1;
	public int iarg1 = -1;
	public String sarg0 = "";
	public String sarg1 = "";

	public MyScaleAnimation(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScaleAnimation(float fromX, float toX, float fromY, float toY) {
		super(fromX, toX, fromY, toY);
	}

	public MyScaleAnimation(float fromX, float toX, float fromY, float toY,float pivotX, float pivotY) {
		super(fromX, toX, fromY, toY, pivotX, pivotY);
	}

	public MyScaleAnimation(float fromX, float toX, float fromY, float toY,int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
		super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,pivotYValue);
	}
}
/*
//通过AnimationUtils得到动画配置文件(/res/anim/back_scale.xml)  
MyScaleAnimation animation = AnimationUtils.loadAnimation(CodeSystem_sx_Act.this, R.anim.back_scale);  
animation.
animation.setAnimationListener(new Animation.AnimationListener() {  
    @Override  
    public void onAnimationStart(Animation animation) {  
    }  
    @Override  
    public void onAnimationRepeat(Animation animation) {  
    }  
    @Override  
    public void onAnimationEnd(Animation animation) {  
//        if(bool){  
//            imgView.setImageResource(R.drawable.back);  
//            bool = false;  
//        }else {  
//            imgView.setImageResource(R.drawable.front);  
//            bool = true;  
//        }  
//        //通过AnimationUtils得到动画配置文件(/res/anim/front_scale.xml),然后在把动画交给ImageView  
//        target.startAnimation(AnimationUtils.loadAnimation(CodeSystem_sx_Act.this, R.anim.front_scale));  
    }  
});  
*/