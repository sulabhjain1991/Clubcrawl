package com.linchpin.clubcrawl.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface IMessanger
{
	public void onSendMessage(Fragment fr,Bundle b,String fragmentName);

}
