package com.yxkj.jyb;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NormalDlg {
	static AlertDialog sAlertDialog = null;
	static CallBackInterface sCallBack = null;
	static public void show(Context _c, String title, String info, CallBackInterface cb){
		if(_c != null){
			
			View layout = LayoutInflater.from(_c).inflate(R.layout.normaldlg,null);
			
			TextView txttitle  = (TextView)layout.findViewById(R.id.title);
			txttitle.setText(title);
			TextView txtinfo = (TextView)layout.findViewById(R.id.info);
			txtinfo.setText(info);
			
			sCallBack = cb;
			
			layout.findViewById(R.id.ok).setOnClickListener( new OnClickListener() {
				public void onClick(View v) {
					if(sAlertDialog != null){
						sAlertDialog.dismiss();
					}
					if(sCallBack != null)
						sCallBack.exectueMethod(null);
				}  
		    });
			layout.findViewById(R.id.cancel).setOnClickListener( new OnClickListener() {
				public void onClick(View v) {
					if(sAlertDialog != null){
						sAlertDialog.dismiss();
					}
				}  
		    });
				
		    Builder bl =  new Builder(_c);
		    bl.setView(layout);
		   	bl.setInverseBackgroundForced(true);
		   	
		    sAlertDialog = bl.show();
			sAlertDialog.setOnDismissListener(new OnDismissListener() {
		          @Override
		          public void onDismiss(DialogInterface dialog) {
		        	  sAlertDialog = null;
		        	  sCallBack = null;
		          }
	        });
		}
	}
	static public void hideBox(){
		if(sAlertDialog != null){
			sAlertDialog.dismiss();
			sAlertDialog = null;
		}
	}
}