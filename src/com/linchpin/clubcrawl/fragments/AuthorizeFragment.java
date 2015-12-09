package com.linchpin.clubcrawl.fragments;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.DoinBackgroung;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class AuthorizeFragment extends ParentFragment implements Result,DoinBackgroung, OnClickListener
{
	View		view;
	String		bcode, clubLogo;
	public  static boolean isSucceed=false;
	private final static int CHECK_BARCODE=0x001;
	ImageView	ivVanue;
	TextView	tvAuthoriseOffer;
	private String barCodeNo;
	private final int IMAGE_FILE=3;
private File f;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		view = inflater.inflate(R.layout.authorisation, container, false);


		bcode = getArguments().getString("bcode");
		clubLogo = getArguments().getString("club_logo");
		BaseClass bc = new BaseClass();
		AQuery aq=new AQuery(getActivity());
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());
		ivVanue = (ImageView) view.findViewById(R.id.ivVenuePic);
		tvAuthoriseOffer = (TextView) view.findViewById(R.id.tvAuthoriseOffer);
		Button btnTweet = (Button) view.findViewById(R.id.btnTweet);
		btnTweet.setOnClickListener(this);
		tvAuthoriseOffer.setOnClickListener(this);
		if(isSucceed)
			tvAuthoriseOffer.setVisibility(View.GONE);
		NetworkTask networkTask=new NetworkTask(getActivity(),CHECK_BARCODE);
		networkTask.setProgressDialog(true);
		networkTask.exposePostExecute(AuthorizeFragment.this);
		String uid = null;
		if(MenuFragment.beanUserInfo.getUser().getId() != null && !(MenuFragment.beanUserInfo.getUser().getId().equals("")))
				uid = MenuFragment.beanUserInfo.getUser().getId();
		else
			uid = MenuFragment.beanUserInfo.getUser().getFbid();
		networkTask.execute(ConstantUtil.baseUrl+"club/getBarCodeDetail?bcode="+bcode+"&uid="+uid);
		Bitmap bm;
		try
		{
			ImageOptions options = new ImageOptions();
			Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defult);
			options.preset = icon;
			aq.id(ivVanue).image(ConstantUtil.inviteLogoImageBaseUrl+clubLogo, options);
		}
		catch (Exception e)
		{
			bm = BitmapFactory.decodeResource(getResources(), R.drawable.defult);
			ivVanue.setImageBitmap(bm);
		}
		setTitleBar();
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{
		}
		return view;
	}

	@Override
	public void onPause()
	{
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);

		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener(((MainScreen) getActivity()));
		super.onPause();
	}

	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.authorization));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
				0, //top
				0, //right
				0//bottom
				);
		getActivity().findViewById(R.id.ivComposePost).setVisibility(View.GONE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.pre_btn);
		getActivity().findViewById(R.id.ivSearch).setVisibility(View.GONE);
		TextView tvEdit = (TextView) getActivity().findViewById(R.id.tvEditProfile);
		tvEdit.setText(getString(R.string.strEdit));
		tvEdit.setVisibility(View.GONE);
		ivMenu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				getActivity().onBackPressed();
			}
		});

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tvAuthoriseOffer:
			///club/doauthorisecode?code=<generated_bcode>&uniquecode=<code_entered_by_user>
			
			Bundle b = new Bundle();
		
			b.putString("barCode", bcode);
			
			activityCallback.onSendMessage(new AuthorizeNextFragment(), b,
					"AuthorizeNextFragment");

			break;
		case R.id.btnTweet:
			NetworkTask networkTask = new NetworkTask(getActivity(), IMAGE_FILE, 0, ConstantUtil.inviteLogoImageBaseUrl+clubLogo);
			networkTask.exposeDoinBackground(AuthorizeFragment.this);
			networkTask.exposePostExecute(AuthorizeFragment.this);
			networkTask.execute(ConstantUtil.inviteLogoImageBaseUrl+clubLogo);
		break;
		default:
			break;
		}
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2)
	{
		switch (id)
		{
		
		case CHECK_BARCODE:
			try
			{
				JSONObject jsonObject=new JSONObject(object);
				if(jsonObject.has("barcode_number"))
				{
					barCodeNo = jsonObject.getString("barcode_number");
					tvAuthoriseOffer.setVisibility(View.VISIBLE);

				}
			}
			catch (Exception e) 
			{
				// TODO: handle exception
			}
			break;
		case IMAGE_FILE:
			shareOnTwitter();
			break;
		default:
			break;
		}

	}
	
	private void shareOnTwitter()
	{
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		   shareIntent.setType("*/*");
		   
		   shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Enjoy your night");
		 
		   shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f) );

		   PackageManager pm = getActivity().getPackageManager();
		   List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
		   boolean isTwitter = false;
		     for (final ResolveInfo app : activityList) 
		      {
		        if ((app.activityInfo.name).contains("twitter"))
		          {
		        	isTwitter = true;
		             final ActivityInfo activity = app.activityInfo;
		             final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
		             shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		             shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		             shareIntent.setComponent(name);
		             getActivity().startActivity(shareIntent);
		            break;
		          }
		        }
		     if(!isTwitter)
		     {
		    	 String appPackageName = "com.twitter.android";
		    	 getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); 
		     }
	
	}

	@Override
	public String doInBackground(Integer id, String... params) {
	switch (id) {
	case IMAGE_FILE:
		 f = ConstantUtil.String_to_File(params[0]);
		break;

	default:
		break;
	}
		return null;
	}

}
