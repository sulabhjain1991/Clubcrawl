package com.linchpin.clubcrawl.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

public class MyTextView extends AutoCompleteTextView{
private Context context;
	public MyTextView(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
	    	InputMethodManager imm = (InputMethodManager)context.getSystemService(
	    		      Context.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(getWindowToken(), 0);
	        return true;
	    }
	    return super.onKeyPreIme(keyCode, event);
	}

}
