package com.yxkj.jyb;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LoadingBox {
	static AlertDialog sAlertDialog = null;
	static public void showBox(Context _c, String txt){
		if(_c != null && sAlertDialog == null){
			
			View layout = LayoutInflater.from(_c).inflate(R.layout.loadingbox,null);
			TextView tv  = (TextView)layout.findViewById(R.id.textShow);
			tv.setText(txt);
		    Builder bl =  new AlertDialog.Builder(_c);
		    bl.setView(layout)
		   		.setInverseBackgroundForced(true)
		   		.setCancelable(false);
		    sAlertDialog = bl.show();
		    sAlertDialog.setOnDismissListener(new OnDismissListener(){
		    	 public void onDismiss(DialogInterface dialog){
		    		 sAlertDialog = null;
		    	 }
		    });
		}
	}
	static public void hideBox(){
		if(sAlertDialog != null){
			sAlertDialog.dismiss();
//			/sAlertDialog = null;
		}
	}
}