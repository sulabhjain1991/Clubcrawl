package com.linchpin.clubcrawl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.linchpin.clubcrawl.BaseClass;
import com.linchpin.clubcrawl.MenuFragment;
import com.linchpin.clubcrawl.R;
import com.linchpin.clubcrawl.adapters.VenueFeedAdapter;
import com.linchpin.clubcrawl.beans.BeanListVenueFeed;
import com.linchpin.clubcrawl.beans.BeanResponse;
import com.linchpin.clubcrawl.helper.AppPreferences;
import com.linchpin.clubcrawl.helper.ConstantUtil;
import com.linchpin.clubcrawl.helper.NetworkTask;
import com.linchpin.clubcrawl.helper.NetworkTask.Result;

public class ViewFeedFragment extends ParentFragment implements OnClickListener,Result
{
	private RelativeLayout	layout;
	String clubId;
	private View view;
	private BeanListVenueFeed beanListVenueFeed;
	private BeanResponse responseBean;
	private final int DETAIL=0;

	@Override
	public void onResume()
	{
		layout = (RelativeLayout) getActivity().findViewById(R.id.normal_pan);
		layout.findViewById(R.id.overview_pan).setVisibility(View.VISIBLE);
		TextView overview = (TextView) layout.findViewById(R.id.viewfeed);
		overview.setTextColor(getResources().getColor(R.color.background_color));
		layout.findViewById(R.id.overview).setOnClickListener(this);
		((TextView) layout.findViewById(R.id.overview)).setTextColor(getResources().getColor(R.color.black));
		layout.findViewById(R.id.tvLocation).setVisibility(View.GONE);
		layout.findViewById(R.id.ivSearch).setVisibility(View.GONE);
		layout.findViewById(R.id.ivComposePost).setVisibility(View.GONE);
		super.onResume();
	}

	@Override
	public void onPause()
	{
		layout.findViewById(R.id.overview_pan).setVisibility(View.GONE);
		layout.findViewById(R.id.tvLocation).setVisibility(View.VISIBLE);
		layout.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
		layout.findViewById(R.id.ivComposePost).setVisibility(View.VISIBLE);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		MenuFragment.currentsel = "ViewFeedFragment";
		view = inflater.inflate(R.layout.fragment_venue_feed, container,
				false);

		clubId = getArguments().getString("clubId");
		BaseClass bc = new BaseClass();
		bc.setFont(view.findViewById(R.id.mainLayout), getActivity());



		if (ConstantUtil.isNetworkAvailable(getActivity())) {
			AppPreferences appPref = new AppPreferences(getActivity());

			StringBuilder sb = new StringBuilder();
			sb.append(ConstantUtil.userVenueFeedsList);
			sb.append("venue_id=").append(clubId);
			sb.append("&user_id=").append(appPref.getUserId());
			sb.append("&curpage=").append("0");
			sb.append("&perpage=").append("10");
			NetworkTask feedTask = new NetworkTask(getActivity(), DETAIL);
			feedTask.setProgressDialog(true);
			feedTask.exposePostExecute(ViewFeedFragment.this);
			feedTask.execute(sb.toString());
			//new FeedsTask(getActivity(), url, url1).execute();
		} else
			Toast.makeText(getActivity(), getString(R.string.internetFailure),
					Toast.LENGTH_LONG).show();


		return view;

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.overview:
			activityCallback.onSendMessage(new ClubDetailFragment(),getArguments(), "ClubDetailFragment");

			break;
		default:
			break;
		}
	}

	@Override
	public void resultfromNetwork(String object, int id, int arg1, String arg2) {
		switch (id) {
		case DETAIL:
			if(object != null && !object.equals(""))
			{
			Gson gson = new Gson();
			beanListVenueFeed = gson.fromJson(object, BeanListVenueFeed.class);
			if(beanListVenueFeed == null || beanListVenueFeed.getPost() == null)
			{
				responseBean = gson.fromJson(object, BeanResponse.class);
				Toast.makeText(getActivity(), responseBean.getMessage(), Toast.LENGTH_SHORT).show();
			}
			else
			{
			ListView lvFeeds = (ListView) view.findViewById(R.id.lvFeedList);
			lvFeeds.setAdapter(new VenueFeedAdapter(getActivity(), beanListVenueFeed));
			}
			}
			else
				Toast.makeText(getActivity(),
						getString(R.string.connectionerror),
						Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		
		
	}
}
