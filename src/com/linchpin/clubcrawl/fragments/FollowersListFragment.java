package com.linchpin.clubcrawl.fragments;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FollowersListAdapter;
import com.linchpin.clubcrawl.beans.BeanFollowersInfo;
import com.linchpin.clubcrawl.beans.BeanListFollowers;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class FollowersListFragment extends ParentFragment implements Result{

	//ImageView ivBack;
	private final int	FOLLOWERS	= 1;
	ListView lvFollowers;
	FollowersListAdapter adapter;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.followers_list, container,false);
		setTitleBar();
		initAll();
		setAllListeners();
		networkCall();
		return view;

	}

	@SuppressLint("NewApi")
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.editfollowers));
		tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, //left
				0, //top
				0, //right
				0//bottom
		);

		ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivComposePost);
		ivNotification.setVisibility(View.GONE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.pre_btn);
		ImageView ivSearch = (ImageView) getActivity().findViewById(R.id.ivSearch);
		ivSearch.setVisibility(View.GONE);
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


	private void initAll() {
		// TODO Auto-generated method stub
		//ivBack=(ImageView) view.findViewById(R.id.ivBack);
		lvFollowers=(ListView) view.findViewById(R.id.lvFollowers);
	}
	private void setAllListeners() {
		// TODO Auto-generated method stub
		/*ivBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	finish();
				//overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
			}
		});*/



	}
	private void networkCall() {
		// TODO Auto-generated method stub
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{


			NetworkTask networkTask = new NetworkTask(getActivity(), FOLLOWERS);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(FollowersListFragment.this);
			networkTask.execute(getUrl());

		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

	}

	private String getUrl() {
		// TODO Auto-generated method stub
		AppPreferences appPref = new AppPreferences(getActivity());
		String url;
		if(getArguments().getString("clickable").equals("true"))
		 url = ConstantUtil.followersListUrl +"follow_type_id="+appPref.getUserId()+"&follow_type=user";
		else
			 url = ConstantUtil.followersListUrl +"follow_type_id="+getArguments().getString("id")+"&follow_type=user";
		return url;


	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (id) {
		case FOLLOWERS:
			try
			{
				JSONObject jsonObject = new JSONObject(object);
				if(!jsonObject.equals("") && jsonObject!=null){

					JSONArray array = null;

					try {
						array = jsonObject.optJSONArray("followers");
						List<BeanFollowersInfo> FollowersInfo = new ArrayList<BeanFollowersInfo>();
						BeanListFollowers listFollowers=new BeanListFollowers();
						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanFollowersInfo beanFollowersInfo = gson.fromJson(obj,
									BeanFollowersInfo.class);
							FollowersInfo.add(beanFollowersInfo);





						}
						listFollowers.setFollowsName(FollowersInfo);
						adapter=new FollowersListAdapter(getActivity(), listFollowers, getArguments().getString("clickable"));
						lvFollowers.setAdapter(adapter);

					} catch (Exception e) {
						// TODO: handle exception
					}					

				}
				else {

					Toast.makeText(getActivity(), getResources().getString(R.string.internetFailure), Toast.LENGTH_SHORT).show();
				}


			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			break;

		default:
			break;
		}


	}
	@Override
	public void onPause() {
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)(getActivity()));
		super.onPause();
	}
	

}
