package com.linchpin.clubcrawl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linchpin.clubcrawl.R;

public class InboxFragment extends ParentFragment{

	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_inbox, container, false);
		
		return view;
	}
}
