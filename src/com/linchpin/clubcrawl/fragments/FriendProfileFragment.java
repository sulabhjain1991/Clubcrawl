package com.linchpin.clubcrawl.fragments;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.DialogProfilePic;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.beans.BeanListProfilePics;
import com.linchpin.clubcrawl.beans.BeanProfilePics;
import com.linchpin.clubcrawl.beans.BeanUserInfo;
import com.linchpin.clubcrawl.helper.ConstantUtil;

public class FriendProfileFragment extends ParentFragment {

	private View view;
	private String id;
	private BeanUserInfo beanUserInfo;
	private BeanListProfilePics beanListProfilePics;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.friend_profile_fragment, container, false);
		id = getArguments().getString("id");
		
		if(ConstantUtil.isNetworkAvailable(getActivity()))
		{
			new GetFriendData(getActivity()).execute();
		}
		else
			Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

		ImageView ivBack = (ImageView) view.findViewById(R.id.ivBack);
		ivBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

		ImageView ivSendText = (ImageView) view.findViewById(R.id.ivSendText);
		ivSendText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SendTextMessage fr = new SendTextMessage();
				FragmentManager fm = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();

				fragmentTransaction.add((R.id.frame), fr,"SendTextMessage");
				fragmentTransaction.addToBackStack("SendTextMessage");
				fragmentTransaction.commit();

			}
		});
		return view;
	}

	//Task for get gallery data
	public class GetFriendData extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog pd;
		Context con;
		private String message;

		public GetFriendData(Context con)
		{
			this.con = con;
		}

		@Override
		protected void onPreExecute() 
		{
			pd = ProgressDialog.show(con, null, "Loading...");
			pd.setContentView(R.layout.progress_layout);

			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params)
		{
			String url = "";
			Gson gson = new Gson();
			url = ConstantUtil.userProfileDetail + id;
			String responseString = ConstantUtil.http_connection(url);

			if(responseString != null && !responseString.equals(""))
			{
				beanUserInfo = gson.fromJson(responseString, BeanUserInfo.class);
				message = "ProfilePics";
				String url_pics = ConstantUtil.profilePicsUrl+"userid="+id;
				String responseStringPics = ConstantUtil.http_connection(url_pics);
				if(responseStringPics != null && !responseStringPics.equals(""))
				{
					beanListProfilePics = gson.fromJson(responseStringPics, BeanListProfilePics.class);
				}

			}
			else
				message = "UserInfo";

			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			if(pd.isShowing())
			{
				pd.dismiss();
			}
			Message myMessage = new Message(); 
			myMessage.obj = message;
			myHandler.sendMessage(myMessage);
			super.onPostExecute(result);

		}

	}
	private Handler myHandler = new Handler() 
	{

		public void handleMessage(Message msg)
		{


			if (msg.obj.toString().equalsIgnoreCase("UserInfo"))
			{

				Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();


			}
			else
			{
				if(beanListProfilePics == null )
					Toast.makeText(getActivity(), getString(R.string.connectionerror), Toast.LENGTH_LONG).show();
				else
				{
					setProfileInfo();
					if(beanListProfilePics.getStatus().equals("true") )
					{
						List<BeanProfilePics> beanProfilePics = beanListProfilePics.getResult();
						
							LinearLayout llPics = (LinearLayout)view.findViewById(R.id.llPics);
							if(llPics.getChildCount()>0)
								llPics.removeAllViews();
							for(int i = 0;i<beanProfilePics.size();i++)
							{
								LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View v = inflater.inflate(R.layout.custom_friend_profile_pic, null);
								llPics.addView(v);

								//set picture on each imageView
								setPictures(v,beanProfilePics.get(i).getImage(),i);

								//set On click listener on cross button

							}
						
						//						Toast.makeText(getActivity(), getString(R.string.strSuccessLogin), Toast.LENGTH_LONG).show();
						//						Intent i=new Intent(getActivity(),MainScreen.class);
						//						i.putExtra("loginInfo", beanLoginInfo.getUserdata());
						//						startActivity(i);
						//						finish();

					}

				}
			}
			
			


		}
	};
	
	protected void setPictures(View v,final String url,int id)
	{
		final AQuery aq = new AQuery(view);
		LinearLayout.LayoutParams params = new LayoutParams(
				ConstantUtil.screenWidth*40/100, ConstantUtil.screenWidth * 40 / 100);

		RelativeLayout rl = (RelativeLayout)v;
		rl.setLayoutParams(params);
		ImageView ivPic = (ImageView) rl.getChildAt(0);
		ivPic.setId(id);
		ivPic.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(getActivity(),DialogProfilePic.class);
				i.putExtra("picUrl", ConstantUtil.profilePicBaseUrl+url);
				startActivity(i);
				
				
			}
		});

		Bitmap bm;
		try {
			ivPic.setScaleType(ScaleType.FIT_XY);

			ImageOptions options = new ImageOptions();

			Bitmap icon = BitmapFactory.decodeResource(getResources(),
					R.drawable.no_img_jst_yet);
			options.preset = icon;
			options.targetWidth = ConstantUtil.screenWidth*40/100;


			aq.id(ivPic.getId()).image(ConstantUtil.profilePicBaseUrl+url,options);


		} catch (Exception e) {
			bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.no_img_jst_yet);
			ivPic.setImageBitmap(bm);
		}

	}
	
	protected void setProfileInfo()
	{
		TextView tvGender = (TextView) view.findViewById(R.id.tvGender);
		TextView tvAge = (TextView) view.findViewById(R.id.tvAge);
		TextView tvMaritalStatus = (TextView) view.findViewById(R.id.tvMarritalStatus);
		tvGender.setText(beanUserInfo.getUser().getGender());
		tvAge.setText(beanUserInfo.getUser().getAge());
		tvMaritalStatus.setText(beanUserInfo.getUser().getMarital_status());
		LinearLayout llFriendProfileComments = (LinearLayout)view.findViewById(R.id.llFriendProfileComments);
		if(llFriendProfileComments.getChildCount()>0)
			llFriendProfileComments.removeAllViews();
		for(int i = 0;i<beanUserInfo.getUser().getPost().size();i++)
		{
			LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(R.layout.adapter_user_post, null);
			TextView tvTime = (TextView) v.findViewById(R.id.tvTime);
			TextView tvMsg = (TextView) v.findViewById(R.id.tvMsg);
			tvTime.setText(beanUserInfo.getUser().getPost().get(i).getCreated_at());
			tvMsg.setText(beanUserInfo.getUser().getPost().get(i).getStatus());
			llFriendProfileComments.addView(v);

			//set picture on each imageView

			//set On click listener on cross button

		}
		setProfileImage();
		
	}

	// set profile image
		private void setProfileImage() {
			ImageView ivProfileImage = (ImageView) view
					.findViewById(R.id.ivProfileImage);
			AQuery aq = new AQuery(ivProfileImage);

			setWidthAndHeightOfImage();
			Bitmap bm;
			try {
				ImageOptions options = new ImageOptions();
				Bitmap icon = BitmapFactory.decodeResource(getResources(),
						R.drawable.no_img_jst_yet);
				options.preset = icon;
				
				aq.id(R.id.ivProfileImage).image(
						ConstantUtil.profilePicBaseUrl
								+ beanUserInfo.getUser().getProfile_pic(),
						options);
			} catch (Exception e) {
				bm = BitmapFactory.decodeResource(getResources(),
						R.drawable.no_img_jst_yet);
				ivProfileImage.setImageBitmap(bm);
			}
		}
		
		//set width and Height of profile Image
		private void setWidthAndHeightOfImage() {
			ImageView photo = (ImageView) view.findViewById(R.id.ivProfileImage);
			ConstantUtil.getScreen_Height(getActivity());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ConstantUtil.screenWidth * 30 / 100,
					ConstantUtil.screenWidth * 30 / 100);
			photo.setLayoutParams(params);
			params.topMargin=10;
			params.gravity = Gravity.CENTER_HORIZONTAL;

		}

	@Override
	public void onDestroy() 
	{
		RelativeLayout rlHeader = (RelativeLayout) getActivity().findViewById(R.id.header); 
		rlHeader.setVisibility(View.VISIBLE);
		super.onDestroy();
	}
	
	@Override
	public void onResume()
	{
		RelativeLayout rlHeader = (RelativeLayout) getActivity().findViewById(R.id.header); 
		rlHeader.setVisibility(View.GONE);
		super.onResume();
	}


}
