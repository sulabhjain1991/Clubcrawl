package com.linchpin.clubcrawl.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.R;

public class ComposePost extends Fragment
{
	View	view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_post, container, false);
		// set the font for whole class
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		return view;
	}
}
