package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yxkj.jyb.BJBDataMgr.My;


import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class bjb_MyPostData_PopMenu {

	static Context sContext = null;
	static PopupWindow sPopupWindow = null;
	static Handler sHandler = null;
	static String sTid = "";
	static public void show(Context _c, View view, String tid, Handler hl) {

		sContext = _c;
		sTid = tid;
		sHandler = hl;
        // һ���Զ���Ĳ��֣���Ϊ��ʾ������
		View contentView = LayoutInflater.from(_c).inflate(R.layout.bjb_mypostdata_popmenu, null);
		
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        sPopupWindow = popupWindow;

        popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
        });

        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
        // �Ҿ���������API��һ��bug
       //ʵ����һ��ColorDrawable��ɫΪ��͸��
    	ColorDrawable dw = new ColorDrawable(0x00000000);
    	//����SelectPicPopupWindow��������ı���
    	popupWindow.setBackgroundDrawable(dw);
    	
    	popupWindow.setAnimationStyle(R.style.PopMenuAnim);
    	
    	//popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
    	//popupWindow.showAsDropDown(view);
    	//�õ�mView����Ļ�е�����  
    	//int [] pos = new int[2];  
    	//view.getLocationOnScreen(pos);  
    	int offsetY = view.getHeight();
    	int offsetX = view.getWidth();  
    	popupWindow.showAtLocation(view,Gravity.RIGHT | Gravity.TOP, offsetX, offsetY);
    	
    	//MainTabActivity.setBackgroundAlpha(0.5f);
    	 //popWindow��ʧ��������
    	popupWindow.setOnDismissListener(new OnDismissListener() {
	          @Override
	          public void onDismiss() {
	        	  //MainTabActivity.setBackgroundAlpha(1.0f);
	        	  sPopupWindow = null;
	        	  sContext = null;
	          }
        });
                  
    	ArrayList<View> btns = new ArrayList<View> ();
    	contentView.findViewsWithText(btns, "btns", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		for(int i=0;i<btns.size();i++)
		{
			Button _btn = (Button)btns.get(i);
			_btn.setTag(i);
			_btn.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
					int i = Integer.parseInt(v.getTag().toString()) ;
					if(i == 0){//ɾ��
						NormalDlg.show(sContext, "��ʾ","��ȷ��Ҫ���÷��������ȫ��ɾ����",new CallBackInterface() {
				            @Override
				            public void exectueMethod(Object p) {
				            	if (sHandler != null){
				            		Message msg = new Message();
				            		msg.what = 1;
				            		msg.obj = sTid;
				            		sHandler.sendMessage(msg);
				            	}
				            }
				        });
					}
					else if(i == 1){//���
						bjb_MyPostDataEdit_Act.show(sContext, My.getThreadItem(sTid), null, null);
					}
					sPopupWindow.dismiss();
				}
			});
		}
    }
}