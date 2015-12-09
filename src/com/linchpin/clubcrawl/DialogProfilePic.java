package com.linchpin.clubcrawl;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.androidquery.AQuery;
import com.linchpin.clubcrawl.fragments.MyProfilePicsFragments;
import com.linchpin.clubcrawl.fragments.ParentFragment;

public class DialogProfilePic extends ParentFragment
{
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.activity_dialog_profile_pic, container,false);
		String picUrl = getArguments().getString("picUrl");
		ImageView ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
		AQuery aq = new AQuery(ivProfilePic);
		Bitmap bm = aq.getCachedImage(picUrl);
		MyProfilePicsFragments.fromDialog = 1;

		if(bm != null)
		{
			ivProfilePic.setImageBitmap(bm);
		}

		return view;
	}	

}



