package com.yxkj.jyb;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ThreadsFilterDialog extends AlertDialog {

	public ThreadsFilterDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setCustomDialog();
	}
	 private void setCustomDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.threadsfilter, null);
        //title = (TextView) view.findViewById(R.id.title);
        //editText = (EditText) view.findViewById(R.id.number);
        //positiveButton = (Button) view.findViewById(R.id.positiveButton);
        //negativeButton = (Button) view.findViewById(R.id.negativeButton);
        super.setContentView(view);
    }
}
