package com.linchpin.clubcrawl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linchpin.clubcrawl.R;

public class SendTextMessage extends ParentFragment {

	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.send_text_message, container, false);
		
		TextView tvCenterHeaderText = (TextView) view.findViewById(R.id.tvCenterHeaderText);
		tvCenterHeaderText.setText(" ");
		
		ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
		ivBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
		
		
		
		return view;
	}
}
