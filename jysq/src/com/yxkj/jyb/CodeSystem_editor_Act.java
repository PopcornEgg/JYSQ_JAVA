package com.yxkj.jyb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yxkj.jyb.TabsMgr.codesysdata;
import com.yxkj.jyb.TabsMgr.codesysdataTab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class CodeSystem_editor_Act extends Activity{
	
	private ScrollView mListView = null;
	private codesysdata curcodesysdata = null;
	private Handler mHandler = null;
	
	static private int sPosition = 0;
	static private String sType = "";
	static private int maxItemcount = 10;
	static private int[] RLItemsId = new int[]{R.id.item01, R.id.item02,R.id.item03,R.id.item04,R.id.item05,
		R.id.item06,R.id.item07,R.id.item08,R.id.item09,R.id.item10};
	static private int[] EditItemsId = new int[]{R.id.name01, R.id.EditText09, R.id.EditText08, R.id.EditText07, R.id.EditText06,
		R.id.EditText05, R.id.EditText04, R.id.EditText03, R.id.EditText02, R.id.EditText01};
	static private int[] BtnItemsId = new int[]{ R.id.del01, R.id.ImageButton09, R.id.ImageButton08, R.id.ImageButton07, R.id.ImageButton06,
		 R.id.ImageButton05, R.id.ImageButton04, R.id.ImageButton03, R.id.ImageButton02, R.id.ImageButton01};
	
	private boolean isChangeed = false;
	private View[] mRLItems = new View[maxItemcount];
	private EditText[] mEditItems = new EditText[maxItemcount];
	private List<String> mDataItems  = new ArrayList<String>();
	
	@Override  
    protected void onCreate(Bundle savedInstanceState)
	{  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.codesystem_editor_act);
        
        ImageButton btn_back=(ImageButton)findViewById(R.id.back);
		btn_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				CodeSystem_editor_Act.this.finish();
			}  
		});
		findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				updateDataItems();
				StringBuilder sb = new StringBuilder("");
				for(int i=0;i<mDataItems.size();i++){
					String s = mDataItems.get(i);
					if(!s.isEmpty()){
						if(i == 0)
							sb.append(s);
						else 
							sb.append("," + s);
					}
				}
				String str = sb.toString();
				if(!str.isEmpty()){
					curcodesysdata.name = str;
					codesysdataTab tab = codesysdataTab.getItem_by_Name(sType);
					tab._Save();
				}
				CodeSystem_editor_Act.this.finish();
			}  
		});
		findViewById(R.id.add).setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//bjb_MyPostData_PopMenu.show(CodeSystem_editor_Act.this, v, subtid, mHandler);
				if(mDataItems.size() >= maxItemcount){
					GlobalUtility.Func.ShowToast(String.format("最多只能添加%d个标签", maxItemcount));
				}else{
					updateDataItems();
					mDataItems.add("");
					updateListView();
				}
			}  
		});
		
		mListView = (ScrollView)findViewById(R.id.sv);
		//LinearLayout ll = (LinearLayout)findViewById(R.id.ll);
		//ll.addView(LayoutInflater.from(this).inflate(R.layout.codesystem_editor_item,null));

		codesysdataTab tab = codesysdataTab.getItem_by_Name(sType);
		if (tab != null){
			curcodesysdata = tab.mItemList.get(sPosition);
			if (curcodesysdata != null) {
				TextView cname =(TextView)findViewById(R.id.cname); 
				cname.setText(curcodesysdata.cname);
				
				mDataItems.clear();
				String[] names = curcodesysdata.name.split(",");
				mDataItems.addAll(Arrays.asList(names));
			}
		}
		mHandler = new Handler() {  
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1: {
						break;
					}
					case 2: {
						break;
					}
				}
			}
		};
		
		initViews();
    }  
	private void initViews(){
		for(int i=0;i<maxItemcount;i++){
			mRLItems[i] = findViewById(RLItemsId[i]);
			mEditItems[i] = (EditText)findViewById(EditItemsId[i]);
			//GlobalUtility.Func.hideSoftInput(this, mEditItems[i]);
			ImageButton btn = (ImageButton)findViewById(BtnItemsId[i]);
			btn.setTag(i);
			btn.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					//bjb_MyPostData_PopMenu.show(CodeSystem_editor_Act.this, v, subtid, mHandler);
					if(mDataItems.size() > 1){
						int idx = Integer.parseInt(v.getTag().toString()); 
						updateDataItems();
						mDataItems.remove(idx);
						updateListView();
					}else{
						GlobalUtility.Func.ShowToast("最后一个标签不能删除");
					}
				}  
			});
		}
		updateListView();
	}
	private void updateListView(){
		for(int i=0;i<maxItemcount;i++){
			if(i < mDataItems.size()){
				mRLItems[i].setVisibility(View.VISIBLE);
				mEditItems[i].setText(mDataItems.get(i));
			}else{
				mRLItems[i].setVisibility(View.INVISIBLE);
			}
		}
    }
	private void updateDataItems(){
		for(int i=0;i<maxItemcount;i++){
			if(i < mDataItems.size()){
				mDataItems.set(i, mEditItems[i].getText().toString());
			}
		}
    }
	
	static public void show(Context _c,int pos ,String tp){
		if(_c != null){
			sPosition = pos;
			sType = tp;
			Intent intent = new Intent(_c, CodeSystem_editor_Act.class);
			_c.startActivity(intent);
		}
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();
		sPosition = 0;
		sType = "";
	}
	@Override
	public void onResume()
	{
		super.onResume();
	}
}