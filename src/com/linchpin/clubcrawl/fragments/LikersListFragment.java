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
import com.linchpin.clubcrawl.adapters.LikersListAdapter;
import com.linchpin.clubcrawl.beans.BeanLikersInfo;
import com.linchpin.clubcrawl.beans.BeanListLikers;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class LikersListFragment extends ParentFragment implements Result{

	TextView tvBack;
	ListView lvLikers;
	final int LIKERS=1;
	String feedId;
	View view;

	LikersListAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view=inflater.inflate(R.layout.likers_list, container,false);
		
		Bundle bundle =this.getArguments();
		feedId=bundle.getString("feedId");
		setTitleBar();
		initAll();
		setClickListeners();
		networkCall();
		return view;

	}

	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.editlikers));
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
	
		lvLikers=(ListView) view.findViewById(R.id.lvLikers);
	}
	private void setClickListeners() {
		// TODO Auto-generated method stub
	/*	tvBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});*/
	}

	private void networkCall() {
		// TODO Auto-generated method stub
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{


			NetworkTask networkTask = new NetworkTask(getActivity(), LIKERS);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(LikersListFragment.this);
			networkTask.execute(getUrl());

		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();

	}



	private String getUrl() {
		// TODO Auto-generated method stub
		AppPreferences appPref = new AppPreferences(getActivity());
		String url = ConstantUtil.likersListUrl+"like_id="+feedId+"&" + "user_id="
				+ appPref.getUserId();
		return url;


	}



	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (id) {
		case LIKERS:
			try
			{
				JSONObject jsonObject = new JSONObject(object);
				/*("status"))
				{
					String message=jsonObject.optString("message").toString();
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
				}
				else*/ if(!jsonObject.equals("") && jsonObject!=null){

					JSONArray array = null;

					try {
						array = jsonObject.optJSONArray("likeUserName");
						List<BeanLikersInfo> LikersInfo = new ArrayList<BeanLikersInfo>();
						BeanListLikers listLikers=new BeanListLikers();
						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanLikersInfo beanLikersInfo = gson.fromJson(obj,
									BeanLikersInfo.class);
							LikersInfo.add(beanLikersInfo);




							/*JSONObject jObject=array.getJSONObject(i);
							String user_id=jObject.optString("user_id").toString();
							String first_name=jObject.optString("first_name").toString();
							String last_name=jObject.optString("last_name").toString();
							String profile_pic=jObject.optString("profile_pic").toString();
							String follow_status=jObject.optString("follow_status").toString();
							new BeanFollowersInfo().setUser_id(user_id);*/



						}
						listLikers.setLikeUserName(LikersInfo);
						adapter=new LikersListAdapter(getActivity(), listLikers);
						lvLikers.setAdapter(adapter);

					} catch (Exception e) {
						// TODO: handle exception
					}					

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
