package com.linchpin.clubcrawl.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.linchpin.clubcrawl.interfaces.IMessanger;

public class ParentFragment extends Fragment
{
	protected IMessanger activityCallback;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			activityCallback = (IMessanger) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ToolbarListener");
		}
	}
	
	
}
