package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.List;

import com.yxkj.jyb.R;
import com.yxkj.jyb.BJBDataMgr.My;

import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class bjb_MyChooseGroup_Dlg  {
	
	static private Handler sHandler = null;
	static private Context sContext = null;
	static private AlertDialog sDialog = null;
	static private int sWhat = 1;
	static private void initVariable(View layout)
	{
		layout.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bjb_MyCreateGroup_Act.show(sContext, sHandler, sWhat);
				sDialog.dismiss();
			}
		});
		
		ListView lv = (ListView)layout.findViewById(R.id.lv);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
    			List< My.ThreadItem > lvdatas = My.getThreadItems();
    			if(position >=0 && position < lvdatas.size()){
    				if(sHandler != null){
    					Message msg = new Message();
    					msg.what = sWhat;
    					msg.obj = lvdatas.get(position).tid;
    					sHandler.sendMessage(msg);
    				}
    				sDialog.dismiss();
    			}
    		}
    	});
		
		List< My.ThreadItem > lvdatas = My.getThreadItems();
		List<String> namels = new ArrayList<String>();
    	for(int i=0;i<lvdatas.size();i++){
    		namels.add(lvdatas.get(i).subname);
    	}
		lv.setAdapter(new ArrayAdapter<String>(sContext, R.layout.fragmentpage_bjb_sysitem,namels));
	}
	
	static public void show(Context _c, Handler _hl, int _what) {  
		
		sContext = _c;
		sHandler = _hl;
		sWhat = _what;
        Builder bl =  new Builder(sContext);
        View layout = LayoutInflater.from(sContext).inflate(R.layout.bjb_mychoosegroup_dlg,null);
		initVariable(layout);
        bl.setView(layout);
	   	bl.setInverseBackgroundForced(true);
	   	sDialog = bl.show();
	   	sDialog.setOnDismissListener(new OnDismissListener() {
	          @Override
	          public void onDismiss(DialogInterface dialog) {
	        	  sDialog = null;
	        	  //sHandler = null;
	        	  sContext = null;
	          }
      });
	}  
}
