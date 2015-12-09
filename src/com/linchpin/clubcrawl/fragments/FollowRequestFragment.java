package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.FollowRequestAdapter;
import com.linchpin.clubcrawl.beans.BeanFollowRequestInfo;
import com.linchpin.clubcrawl.beans.BeanFollowRequestList;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class FollowRequestFragment extends ParentFragment implements Result{

	View view;
	ListView lvFollowRequestList;
	final int FOLLOWERS=1;
	FollowRequestAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.fragment_follow_req_list, container,false);
		setTitleBar();
		initAll();
		networkCall();




		return view;
	}


	private void networkCall() {
		// TODO Auto-generated method stub
		if(ConstantUtil.isNetworkAvailable(getActivity())){
			NetworkTask networkTask=new NetworkTask(getActivity(), FOLLOWERS);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(FollowRequestFragment.this);
			networkTask.execute(getUrl());
		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

	}


	private String getUrl() {
		// TODO Auto-generated method stub
		AppPreferences appPref = new AppPreferences(getActivity());
		String url = ConstantUtil.followersRequestListUrl +"follow_type_id="+appPref.getUserId()+"&follow_type=user";
		return url;
	}


	@SuppressLint("NewApi")
	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.editfollowersRequest));
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
		lvFollowRequestList=(ListView) view.findViewById(R.id.lvFollowRequestList);
	}


	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub

		Gson gson=new Gson();

		switch (id) {
		case FOLLOWERS:
			if(object!=null || !object.equals(""))
			{
				JSONArray array = null;
			try {
				
				JSONObject jsonObject = new JSONObject(object);

				try 
				{
				array=jsonObject.optJSONArray("list");
				List<BeanFollowRequestInfo> FollowersRequestInfo = new ArrayList<BeanFollowRequestInfo>();
				BeanFollowRequestList listFollowers=new BeanFollowRequestList();
				for(int i=0;i<array.length();i++)
				{
					String obj = array.getString(i);
					BeanFollowRequestInfo followRequestInfo=gson.fromJson(obj,
							BeanFollowRequestInfo.class);
					FollowersRequestInfo.add(followRequestInfo);
				}
				listFollowers.setList(FollowersRequestInfo);
					adapter=new FollowRequestAdapter(getActivity(), listFollowers);
					lvFollowRequestList.setAdapter(adapter);
				} 
				
				catch (Exception e) {
				
					
					
				}
				
				
			} catch (Exception e) {
				// TODO: handle exception
			}




			}
			else
			{
				Toast.makeText(getActivity(), "Error from server ", Toast.LENGTH_SHORT).show();
			}

			break;

		default:
			break;
		}

	}








}
