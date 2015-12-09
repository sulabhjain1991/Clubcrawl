package com.linchpin.clubcrawl.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.linchpin.clubcrawl.adapters.VenuesListAdapter;
import com.linchpin.clubcrawl.beans.BeanListVenues;
import com.linchpin.clubcrawl.beans.BeanVenuesInfo;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class VenuesListFragment extends ParentFragment implements Result{

	TextView tvBack;
	ListView lvVenuesList;
	final int Venues=1;
	VenuesListAdapter adapter;
	View view;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.following_venues_list, container,false);
		setTitleBar();
		initAll();
		setAllListeners();
		networkCall();

		return view;
	}

	

	private void setTitleBar()
	{

		TextView tvTitle = (TextView) getActivity().findViewById(R.id.tvLocation);
		tvTitle.setText(getString(R.string.editfollowingvenues));
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

		lvVenuesList=(ListView) view.findViewById(R.id.lvVenuesList);
	}
	private void setAllListeners() {
		// TODO Auto-generated method stub
		/*tvBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	*/
		
		if(getArguments().getString("clickable").equals("false"))
		{
			lvVenuesList.setOnItemClickListener(null);
		}
		
		
		
		
		
	}

	private void networkCall() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if (ConstantUtil.isNetworkAvailable(getActivity()))
		{


			NetworkTask networkTask = new NetworkTask(getActivity(), Venues);
			networkTask.setProgressDialog(true, "Requesting");
			networkTask.exposePostExecute(VenuesListFragment.this);
			networkTask.execute(getUrl());

		}
		else Toast.makeText(getActivity(), getString(R.string.internetFailure), Toast.LENGTH_LONG).show();



	}






	private String getUrl() {
		// TODO Auto-generated method stub
		String url;
		AppPreferences appPref = new AppPreferences(getActivity());
		if(getArguments().getString("clickable").equals("true"))
		{
		 url = ConstantUtil.venuesListUrl + "user_id="
				+ appPref.getUserId();
		}
		else 
		{
			url=ConstantUtil.venuesListUrl+"user_id="+getArguments().getString("id");
		}
		return url;

	}
@Override
public void onResume() {
	// TODO Auto-generated method stub
	ImageView ivNotification = (ImageView) getActivity().findViewById(R.id.ivMenu);
	ivNotification.setBackgroundResource(R.drawable.pre_btn);
	//ivNotification.setOnClickListener((MainScreen) getActivity());
	ivNotification.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 FragmentManager fm = getActivity().getSupportFragmentManager();
		
	    fm.popBackStack();
	
		}
	});

	super.onResume();
}


@Override
public void onPause() {
	// TODO Auto-generated method stub
	ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
	ivMenu.setBackgroundResource(R.drawable.menuicon);
	ivMenu.setOnClickListener((MainScreen) getActivity());
	super.onPause();
}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (id) {
		case Venues:
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
						array = jsonObject.optJSONArray("VenueDetails");
						List<BeanVenuesInfo> VenuesInfo = new ArrayList<BeanVenuesInfo>();
						BeanListVenues listVenues=new BeanListVenues();
						for(int i=0;i<array.length();i++)
						{
							String obj = array.getString(i);
							BeanVenuesInfo beanFollowersInfo = gson.fromJson(obj,
									BeanVenuesInfo.class);
							VenuesInfo.add(beanFollowersInfo);

						}
						listVenues.setVenueDetails(VenuesInfo);
						adapter=new VenuesListAdapter(getActivity(), listVenues, getArguments().getString("clickable"));
						lvVenuesList.setAdapter(adapter);

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




}
