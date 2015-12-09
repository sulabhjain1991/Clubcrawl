package com.linchpin.clubcrawl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.linchpin.clubcrawl.MainScreen;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;
import com.linchpin.clubcrawl.jobjects.events.EventsDetail;

public class TicketDetail extends ParentFragment implements Result,OnClickListener
{
	View	view;
	TextView	tvPrice, tvName, tvDescription, tvType;
	ImageView	ivEventImage;

	private void findAllviews()
	{
		tvPrice = (TextView) view.findViewById(R.id.tvPrices);
		tvName = (TextView) view.findViewById(R.id.tvName);
		tvDescription = (TextView) view.findViewById(R.id.tvDescription);
		tvType = (TextView) view.findViewById(R.id.tvType);
		ivEventImage = (ImageView) view.findViewById(R.id.ivEventImage);

	}

	ImageView	menue, search, composepost;
	TextView	location;
	ImageView ivCenterImage;
	@Override
	public void onResume()
	{
	
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.pre_btn);
		ivMenu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				getFragmentManager().popBackStack();;
			}
		});		
		location=(TextView) ((MainScreen) getActivity()).findViewById(R.id.tvLocation);
		location.setVisibility(View.GONE);
		ivCenterImage=(ImageView) ((MainScreen) getActivity()).findViewById(R.id.ivImage);
		//location.setText("");
				ivCenterImage.setVisibility(View.VISIBLE);
		//location.setOnClickListener(null);
		//location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cc_logo90x150, 0, 0, 0);
		((MainScreen) getActivity()).findViewById(R.id.ivSearch).setVisibility(View.GONE);
		((MainScreen) getActivity()).findViewById(R.id.ivComposePost).setVisibility(View.GONE);
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.event_detail, container, false);
		findAllviews();
		
		NetworkTask networkTask = new NetworkTask(getActivity(), 1000);
		networkTask.setProgressDialog(true);
		networkTask.exposePostExecute(TicketDetail.this);
		networkTask.execute(ConstantUtil.baseUrl + "api/skiddleEvent/?id=" + getArguments().getString("clubId"));
		return view;
	}

	@Override
	public void onPause()
	{
		ivCenterImage.setVisibility(View.GONE);
		location.setVisibility(View.VISIBLE);
		ImageView ivMenu = (ImageView) getActivity().findViewById(R.id.ivMenu);
		ivMenu.setImageResource(R.drawable.menuicon);
		ivMenu.setOnClickListener((MainScreen)(getActivity()));
		location.setOnClickListener(((MainScreen) getActivity()));
		location.setCompoundDrawables(null, null, null, null);
	
		
		((MainScreen) getActivity()).findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
		((MainScreen) getActivity()).findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);

		super.onPause();
	}
	private void setDetails(EventsDetail events)
	{
		tvPrice.setText(events.getResults().getEntryprice());
		tvName.setText(events.getResults().getEventname());
		tvDescription.setText(events.getResults().getDescription());
		tvType.setText(getArguments().getString("eventType"));
		AQuery aQuery = new AQuery(getView());
		aQuery.id(ivEventImage).image(events.getResults().getImageurl());
	}

	@Override
	public void resultfromNetwork(String result, int id, int arg1, String arg2)
	{
		Gson gson = new Gson();

		switch (id)
		{
			case 1000:			
				EventsDetail events = gson.fromJson(result, EventsDetail.class);
				if (events.getError().equals("0"))
				{
					setDetails(events);
				}				
				break;
			default:
				break;
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.ivMenu:
				getActivity().onBackPressed();
				break;

			default:
				break;
		}
		
	}

}
