package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FollowingListAdapter;
import com.linchpin.clubcrawl.beans.BeanFollowingInfo;
import com.linchpin.clubcrawl.beans.BeanListFollowing;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class FollowingListFragment extends ParentFragment implements Result{
	ImageView ivBack;
	ListView lvFollowing;
	private final int	FOLLOWING	= 1;
	FollowingListAdapter adapter;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.following_list, container,false);
		setTitleBar();
		initAll();
		setAllListeners();
		networkCall();
		return view;
	}


	private void initAll() {
		// TODO Auto-generated method stub
		ivBack=(ImageView) view.findViewById(R.id.ivBack);
		lvFollowing=(ListView) view.findViewById(R.id.lvFollowing);

	}

	private void setAllListeners() {
		// TODO Auto-generated method stub

	}
	private void networkCall() {
		// TODO Auto-generated method stub
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{


			NetworkTask networkTask = new NetworkTask(getActivity(), FOLLOWING);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(FollowingListFragment.this);
			networkTask.execute(getUrl());

		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();
	}

	private String getUrl() {
		// TODO Auto-generated method stub
		AppPreferences appPref = new AppPreferences(getActivity());
		String url;
		if(getArguments().getString("clickable").equals("true"))
			url = ConstantUtil.followingListUrl + "user_id="+ appPref.getUserId()+"&follow_type=user";
		else
			url = ConstantUtil.followingListUrl + "user_id="+ getArguments().getString("id")+"&follow_type=user";
		return url;


	}



	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (id) {
		case FOLLOWING:
			try
			{
				JSONObject jsonObject = new JSONObject(object);
				if(!jsonObject.equals("") && jsonObject!=null){

					JSONArray array = null;

					try {
						array = jsonObject.optJSONArray("followings");
						List<BeanFollowingInfo> FollowersInfo = new ArrayList<BeanFollowingInfo>();
						BeanListFollowing listFollowers=new BeanListFollowing();
						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanFollowingInfo beanFollowingInfo = gson.fromJson(obj,
									BeanFollowingInfo.class);
							FollowersInfo.add(beanFollowingInfo);




							/*JSONObject jObject=array.getJSONObject(i);
							String user_id=jObject.optString("user_id").toString();
							String first_name=jObject.optString("first_name").toString();
							String last_name=jObject.optString("last_name").toString();
							String profile_pic=jObject.optString("profile_pic").toString();
							String follow_status=jObject.optString("follow_status").toString();
							new BeanFollowersInfo().setUser_id(user_id);*/



						}
						listFollowers.setFollowsName(FollowersInfo);
						adapter=new FollowingListAdapter(getActivity(), listFollowers,getArguments().getString("clickable"));
						lvFollowing.setAdapter(adapter);

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

	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.editfollowing));
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
	@Override
	public void onPause() {
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)(getActivity()));
		super.onPause();
	}
}
