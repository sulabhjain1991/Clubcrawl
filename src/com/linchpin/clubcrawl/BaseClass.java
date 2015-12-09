package com.linchpin.clubcrawl;

import android.content.Context;
import android.view.View;
import com.linchpin.clubcrawl.helper.FontHelper;

public class BaseClass
{
	
	 public void setFont(View v,Context con)
	{
		FontHelper.applyFont(con, v, "fonts/font.ttf");
	}
}
