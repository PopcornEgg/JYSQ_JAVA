package com.yxkj.jyb.ui;

import com.yxkj.jyb.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

public class LineGridView extends GridView{
    public LineGridView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public LineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    //
//    @Override
//    protected void dispatchDraw(Canvas canvas){
//        super.dispatchDraw(canvas);
//        int childCount = getChildCount();
//        if(childCount <= 0)
//        	return;
//        View localView1 = getChildAt(0);
//        int column = getWidth() / localView1.getWidth();
//        Paint localPaint;
//        localPaint = new Paint();
//        localPaint.setStyle(Paint.Style.STROKE);
//        localPaint.setColor(getContext().getResources().getColor(R.color.font_color_gray_small2));
//        for(int i = 0;i < childCount;i++){
//            View cellView = getChildAt(i);
//            if((i + 1) % column == 0){
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }else if((i + 1) > (childCount - (childCount % column))){
//                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }else{
//                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
//            }
//        }
//        if(childCount % column != 0){
//            for(int j = 0 ;j < (column-childCount % column) ; j++){
//                View lastView = getChildAt(childCount - 1);
//                canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(), lastView.getRight() + lastView.getWidth()* j, lastView.getBottom(), localPaint);
//            }
//        }
//    }
    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
        int childCount = getChildCount();
        if(childCount <= 0)
        	return;
        View localView1 = getChildAt(0);
        int column = super.getNumColumns();//getWidth() / localView1.getWidth();
        int itemwidth  = getWidth() / column;
        int itemheight = localView1.getHeight();
        Paint localPaint;
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setColor(getContext().getResources().getColor(R.color.font_color_gray_small2));
        int rowumn = (int)Math.ceil((double)childCount/(double)column);
        for(int i = 0;i < rowumn;i++){
        	for(int j = 1;j < column;j++){
        		int idx = j+i*column;
        		if(idx<childCount){
        			canvas.drawLine(itemwidth*j, itemheight*i, itemwidth*j, itemheight*(i+1), localPaint);
        		}else
        			break;
        	}
        }
    }
}